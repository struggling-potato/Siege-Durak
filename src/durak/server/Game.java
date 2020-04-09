package durak.server;

import durak.game.*;

import java.util.*;
import java.util.stream.Collectors;

interface ServerGame {

    boolean waitCondition();

    void waitAndStart();

    void start();
}

public class Game implements IGame, ServerGame {
    int curMoveIdx = 0;
    private int                           currentId         = 0;
    private Table                         table;
    private ArrayList<IPlayer>            iPlayers          = new ArrayList<>(6);
    private ArrayList<Player>             players           = new ArrayList<>(6);
    private HashMap<Integer, IPlayer>     playerIdToIPlayer = new HashMap<>();
    private HashMap<Integer, Player>      playerIdToPlayer  = new HashMap<>();
    private HashMap<IPlayer, Integer>     IPlayerToPlayerId = new HashMap<>();
    private HashMap<Integer, Hand>        idToHand          = new HashMap<>();
    private HashMap<Integer, PlayerState> idToState         = new HashMap<>();

    @Override
    public void throwCards(int playerId, List<Card> cards) {
        System.out.println(playerId + " throwCard {" + cards + "}");
        cards = cards.stream().sorted().distinct().collect(Collectors.toCollection(ArrayList::new));
        PlayerState curState = idToState.get(playerId);
        Hand        curHand  = idToHand.get(playerId);
        ArrayList<Card> validCards = cards.stream()
                                          .filter(card -> checkStateAndCard(curState, curHand, card, null))
                                          .collect(Collectors.toCollection(ArrayList::new));
        if (validCards.size() == cards.size()) {
            Player  curPlayer  = playerIdToPlayer.get(playerId);

            validCards.forEach(card -> {
                curHand.getCards().remove(card);
                table.getThrownCard().add(new Pair(card));
            });

            curPlayer.handOut(curHand);
            idToState.replace(playerId, PlayerState.STATE_TOSS);

            for (var player : playerIdToIPlayer.values()) {
                retry(IPlayerToPlayerId.get(player));
            }
        }
        else {
            retry(playerId);
        }
    }

    @Override
    public void tossCards(int playerId, List<Card> cards) {
        System.out.println(playerId + " tossCard {" + ", " + cards + "}");
        cards = cards.stream().sorted().distinct().collect(Collectors.toCollection(ArrayList::new));
        PlayerState curState = idToState.get(playerId);
        Hand        curHand  = idToHand.get(playerId);
        ArrayList<Card> validCards = cards.stream()
                                          .filter(card -> checkStateAndCard(curState, curHand, card, null))
                                          .collect(Collectors.toCollection(ArrayList::new));
        if (validCards.size() == cards.size()) {
            Player  curPlayer  = playerIdToPlayer.get(playerId);

            validCards.forEach(card -> {
                curHand.getCards().remove(card);
                table.getThrownCard().add(new Pair(card));
            });

            curPlayer.handOut(curHand);

            for (var player : playerIdToIPlayer.values()) {
                retry(IPlayerToPlayerId.get(player));
            }
        }
        else {
            retry(playerId);
        }
    }

    private Timer timer = new Timer(true);
    private boolean timeOut;

    @Override
    public void throwCard(int playerId, Card card) {
        System.out.println(playerId + " throwCard {" + card + "}");
        PlayerState curState = idToState.get(playerId);
        Hand        curHand  = idToHand.get(playerId);
        if (checkStateAndCard(curState, curHand, card, null)) {
            Player  curPlayer  = playerIdToPlayer.get(playerId);
            curHand.getCards().remove(card);
            table.getThrownCard().add(new Pair(card));
            curPlayer.handOut(curHand);
            idToState.replace(playerId, PlayerState.STATE_TOSS);

            for (var player : playerIdToIPlayer.values()) {
                retry(IPlayerToPlayerId.get(player));
            }
        }
        else {
            retry(playerId);
        }
    }

    boolean checkStateAndCard(PlayerState current, Hand currentHand, Card checkingCard, Pair pair) {
        try {
            switch (current) {
                case STATE_MOVE: {
                    return currentHand.getCards().stream().anyMatch((c) -> c.equals(checkingCard));
                }
                case STATE_DEFEND: {
                    if (!table.getThrownCard()
                              .stream()
                              .anyMatch((p) -> p.isOpen() && p.getBottomCard().equals(pair.getBottomCard())))
                        return false;
                    return !pair.isOpen()
                           && currentHand.getCards().stream().anyMatch((c) -> c.equals(pair.getTopCard()))
                           && pair.isValidPair(table.getDeck().getTrump());
                }
                case STATE_TOSS: {
                    return table.getThrownCard()
                                .stream()
                                .anyMatch((p) -> p.getCards()
                                                  .stream()
                                                  .anyMatch((c) -> c.getRank() == checkingCard.getRank()));
                }
                case STATE_WAIT:
                default: {
                    return false;
                }
            }
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void beatCard(int playerId, Pair pair) {
        System.out.println(playerId + " beatCard { " + pair + " }");
        PlayerState curState = idToState.get(playerId);
        Hand        curHand  = idToHand.get(playerId);
        if (checkStateAndCard(curState, curHand, null, pair)) {
            IPlayer curPlayer = playerIdToIPlayer.get(playerId);
            curHand.getCards().remove(pair.getTopCard());
            table.getThrownCard().removeIf((p) -> p.getBottomCard().equals(pair.getBottomCard()));
            table.getThrownCard().add(pair);
            if (table.getDeck().getCards().isEmpty() && curHand.getCards().isEmpty()) {
                idToState.replace(playerId, PlayerState.STATE_INVALID);
                synchronized (iPlayers) {
                    iPlayers.notify();
                }
            }
            for (var player : playerIdToIPlayer.values()) {
                if (!player.equals(curPlayer)) {
                    if (idToState.get(IPlayerToPlayerId.get(player)) != PlayerState.STATE_INVALID)
                        idToState.replace(IPlayerToPlayerId.get(player), PlayerState.STATE_TOSS);
                }
                retry(IPlayerToPlayerId.get(player));
            }
        } else {
            retry(playerId);
        }
        if (nextMoveCondition()) {
            synchronized (iPlayers) {
                iPlayers.notify();
            }
        }
    }

    @Override
    public void passTossing(int playerId) {
        System.out.println(playerId + " passTossing");
        PlayerState playerState = idToState.get(playerId);
        if (playerState == PlayerState.STATE_TOSS) {
            idToState.replace(playerId, PlayerState.STATE_WAIT);
            playerIdToIPlayer.get(playerId).endMove();

            if (nextMoveCondition())
                synchronized (iPlayers) {
                    iPlayers.notify();
                }
        }
    }

    private boolean giveUp;

    @Override
    public void tossCard(int playerId, Card card) {
        System.out.println(playerId + " tossCard {" + ", " + card + "}");
        PlayerState curState = idToState.get(playerId);
        Hand        curHand  = idToHand.get(playerId);
        if (checkStateAndCard(curState, curHand, card, null)) {
            Player curPlayer = playerIdToPlayer.get(playerId);
            curHand.getCards().remove(card);
            table.getThrownCard().add(new Pair(card));
            curPlayer.handOut(curHand);

            for (var player : playerIdToIPlayer.values()) {
                retry(IPlayerToPlayerId.get(player));
            }
        }
        else {
            retry(playerId);
        }
    }    @Override
    public boolean waitCondition() {
        return iPlayers.size() >= 2;
    }

    private void retry(int playerId) {
        IPlayer curIPlayer = playerIdToIPlayer.get(playerId);
        Player curPlayer = playerIdToPlayer.get(playerId);
        Hand    curHand    = idToHand.get(playerId);
        curIPlayer.currentTable(table);
        curIPlayer.currentOpponentsList(players.stream()
                                               .filter(player -> player.getId() != playerId)
                                               .collect(Collectors.toCollection(ArrayList::new)));
        curIPlayer.handOut(curHand);
        curPlayer.handOut(curHand);
        if (curHand.getCards().isEmpty()) {
            idToState.replace(playerId, PlayerState.STATE_WAIT);
        }
        switch (idToState.get(playerId)) {
            case STATE_WAIT: {
                curIPlayer.endMove();
                break;
            }
            case STATE_DEFEND: {
                curIPlayer.defendYourself();
                break;
            }
            case STATE_MOVE: {
                curIPlayer.makeMove();
                break;
            }
            case STATE_TOSS: {
                curIPlayer.tossCards();
                break;
            }
            case STATE_INVALID:
            default: {
                return;
//                curIPlayer.onPlayerDisconnected();
                // TODO: Удалить игрока
            }
        }
    }

    @Override
    public void giveUpDefence(int playerId) {
        System.out.println(playerId + "giveUpDefence");
        if (giveUp) return;
        // TODO: Проблема с таймером, добавить поддержку нескольких карт в throwCard и tossCard

        synchronized (iPlayers) {
            for (int i = 0; i < iPlayers.size(); ++i) {
                IPlayer player      = iPlayers.get(getMovingPlayerIdx(i));
                int     curPlayerId = IPlayerToPlayerId.get(player);
                if (curPlayerId == playerId) {
                    player.endMove();
                }
                else {
                    player.tossCards();
                }
            }
            giveUp = true;
            setTimeOut(5000);
        }

//		idToState.replace(playerId, PlayerState.STATE_WAIT);
//		currentId += 2;
//		idToState.replace(currentId, PlayerState.STATE_MOVE);
//		idToState.replace(++currentId, PlayerState.STATE_DEFEND);
    }

    @Override
    public void registerPlayer(IPlayer player) {
        int id = currentId;

        synchronized (iPlayers) {
            iPlayers.add(player);
            playerIdToIPlayer.put(id, player);
            IPlayerToPlayerId.put(player, id);
            idToHand.put(id, new Hand());
            if (!isGameStarted)
                idToState.put(id, PlayerState.STATE_WAIT);
            else
                idToState.put(id, PlayerState.STATE_INVALID);
            currentId++;
            Player newPlayer = new Player();
            newPlayer.onPlayerRegistered(id);
            players.add(newPlayer);
            playerIdToPlayer.put(id, newPlayer);
            System.out.println("Player " + id + " registered");

            player.onPlayerRegistered(id);
            System.out.println("waitCondition: " + waitCondition());
            if (waitCondition()) {
                synchronized (iPlayers) {
                    iPlayers.notify();
                }
            }
        }
    }

    @Override
    public void exitGame(int playerId) {
        if (playerIdToIPlayer.containsKey(playerId)) {
            IPlayer iPlayer = playerIdToIPlayer.get(playerId);
            iPlayers.remove(iPlayer);
            playerIdToIPlayer.remove(playerId);
            System.out.println("Player " + playerId + " exited");
        }
        else {
            System.out.println("Player " + playerId + " not found");
        }

    }

    Integer startGameCount = 0;

    @Override
    public void startGame(int playerId) {
        synchronized (iPlayers) {
            startGameCount++;
            if (startGameCount >= iPlayers.size() >> 1)
                iPlayers.notify();
        }
    }

    private boolean isGameStarted;

    @Override
    public void start() {
        synchronized (iPlayers) {
            while (startGameCount < iPlayers.size() >> 1) {
                try {
                    iPlayers.wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isGameStarted = true;
        }

        System.out.println("Game started");
        System.out.println("Players: " + iPlayers);

        Deck deck = new Deck();
        table = new Table(deck);

        System.out.println("Trump in this game:");
        System.out.println(deck.getTrump().getSuit());

//        idToState.put(2, PlayerState.STATE_INVALID);

        System.out.println("activePlayersCount " + activePlayersCount());
        for (int playerIdx = 0; playerIdx < activePlayersCount(); ++playerIdx) {

//            IPlayer iPlayer = iPlayers.get(playerIdx);
//            Player  player  = players.get(playerIdx);
            Hand    hand    = new Hand();

//            if (IPlayerToPlayerId.get(iPlayer) != 2) {
                for (int i = 0; i < 6; i++) {
                    hand.addCard(deck.takeCardFromDeck());
                }
//            }

            int playerId = IPlayerToPlayerId.get(iPlayers.get(getMovingPlayerIdx(playerIdx)));

            idToHand.put(playerId, hand);
//            iPlayer.handOut(hand);
//            player.handOut(hand);
            System.out.println(playerId + " player cards from deck: ");
            for (Card card : hand.getCards()) {
                System.out.println("{" + card.getSuit() + ":" + card.getRank() + "}");
            }
        }

        for (var iPlayer: iPlayers) {
            iPlayer.onGameStarted();
        }

        while (!iPlayers.isEmpty()) {
            int moveId = IPlayerToPlayerId.get(iPlayers.get(getMovingPlayerIdx()));
            idToState.put(moveId, PlayerState.STATE_MOVE);
            int defId = IPlayerToPlayerId.get(iPlayers.get(getMovingPlayerIdx(1)));
            idToState.put(defId, PlayerState.STATE_DEFEND);
            for (var it = playerIdToIPlayer.keySet().iterator(); it.hasNext(); ) {
                int curId = it.next();
                if (moveId != curId &&
                    defId != curId) {
                    if (idToState.get(curId) != PlayerState.STATE_INVALID)
                        idToState.put(curId, PlayerState.STATE_WAIT);
                }
                retry(curId);
            }
            synchronized (iPlayers) {
                setTimeOut(60000);
                while (!nextMoveCondition()) {
                    try {
                        iPlayers.wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            for (var it = playerIdToIPlayer.keySet().iterator(); it.hasNext(); ) {
                int curId = it.next();
                if (idToState.get(curId) != PlayerState.STATE_INVALID)
                    idToState.put(curId, PlayerState.STATE_WAIT);
                retry(curId);
            }

            var  cardsOnTable = table.getThrownCard();
            if (giveUp || (timeOut && table.getThrownCard().stream().filter(pair -> pair.isOpen()).count() != 0)) {
                IPlayer iPlayer  = iPlayers.get(getMovingPlayerIdx(1));
                Player  player   = players.get(getMovingPlayerIdx(1));
                int     playerId = IPlayerToPlayerId.get(iPlayer);
                Hand    iPlayerHand  = idToHand.get(playerId);
                for (var pair : cardsOnTable) {
                    var cards = pair.getCards();

                    for (var card : cards) {
                        iPlayerHand.addCard(card);
                    }
                }
                player.handOut(iPlayerHand);
                table.getThrownCard().clear();
                incPlayerMoveIdx();
                giveUp = false;
            }
            else {
                for (var pair : cardsOnTable) {
                    table.getDump().getCards().add(pair);
                }
                table.getThrownCard().clear();
            }

            for (int i = 0; i < activePlayersCount(); ++i) {
                if (table.getDeck().getCards().isEmpty())
                    break;
                IPlayer iPlayer  = iPlayers.get(getMovingPlayerIdx(i));
                Player  player   = players.get(getMovingPlayerIdx(i));
                int     playerId = IPlayerToPlayerId.get(iPlayer);
                Hand    curHand  = idToHand.get(playerId);
                while (idToState.get(playerId) != PlayerState.STATE_INVALID && !table.getDeck().getCards().isEmpty() &&
                       curHand.getCards().size() < 6)
                    curHand.addCard(table.getDeck().takeCardFromDeck());

                idToHand.put(playerId, curHand);
                iPlayer.handOut(curHand);
                player.handOut(curHand);
            }

            int playersCount = activePlayersCount();
            if (playersCount >= 1) {
                incPlayerMoveIdx();
            }

            if (playersCount <= 1) {
                int loserId = playersCount == 0 ? -1: getMovingPlayerIdx();
                for (var iPlayer: iPlayers) {
                    iPlayer.onGameFinished(loserId);
                }
                try {
                    Thread.sleep(60000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    @Override
    public void waitAndStart() {
        synchronized (iPlayers) {
            while (!waitCondition()) {
                System.out.println("!waitCondition()");
                try {
                    iPlayers.wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("start()");
        start();
    }

    int getMovingPlayerIdx() {
        return getMovingPlayerIdx(0);
    }



    public boolean nextMoveCondition() {
        boolean stateWaitAll = idToState.values().stream().filter(state -> state == PlayerState.STATE_WAIT).count() ==
                               iPlayers.size() - 1 -
                               idToState.values().stream().filter(state -> state == PlayerState.STATE_INVALID).count();
        boolean noOpenCards = table.getThrownCard().stream().filter(pair -> pair.isOpen()).count() == 0;
        System.out.println("nextMoveCondition timeOut " + timeOut);
        System.out.println("nextMoveCondition stateWaitAll " + stateWaitAll);
        return timeOut || (noOpenCards && stateWaitAll);
    }

    void incPlayerMoveIdx() {
        curMoveIdx++;
        int curPlayerId = IPlayerToPlayerId.get(iPlayers.get(getMovingPlayerIdx()));
        while (idToState.get(curPlayerId) != PlayerState.STATE_INVALID &&
               idToHand.get(curPlayerId).getCards().isEmpty())
            curMoveIdx++;
    }

    int activePlayersCount() {
        if (!table.getDeck().getCards().isEmpty())
            return (int) (iPlayers.size() - idToState.values()
                                                     .stream()
                                                     .filter(playerState -> playerState == PlayerState.STATE_INVALID)
                                                     .count());

        return (int) idToHand.values().stream().filter(hand -> !hand.getCards().isEmpty()).count();
    }

    private boolean timerCanceled;

    int getMovingPlayerIdx(int offset) {
        boolean emptyDeck = table.getDeck().getCards().isEmpty();
        for (int i = 0; i < iPlayers.size(); ++i) {
            int idx = iPlayers.size() == 0 ? -1 : (curMoveIdx + offset + i) % iPlayers.size();
            if (idx < 0)
                return idx;
            int playerId = IPlayerToPlayerId.get(iPlayers.get(idx));
            boolean emptyHand = idToHand.get(playerId).getCards().isEmpty();
            if (idToState.get(playerId) == PlayerState.STATE_INVALID || (emptyDeck && emptyHand))
                continue;
            return idx;
        }
        return -1;
    }

    private int timerChecksum = 0;

    private void setTimeOut(int millisecondsTimeOut) {
        synchronized (timer) {
            System.out.println("setTimeOut " + millisecondsTimeOut);
            try {
                timer.cancel();
                timer.purge();
            }
            catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
            timeOut = false;
            Timer timer = new Timer(true);
            ++timerChecksum;
            int currentTimerChecksum = timerChecksum;
            timer.schedule(new TimerTask() {
                               @Override
                               public void run() {
                                   synchronized (iPlayers) {
                                       if (currentTimerChecksum != timerChecksum)
                                           return;
                                       timeOut = true;
                                       iPlayers.notify();
                                   }
                               }
                           }
                    , millisecondsTimeOut);
        }
    }
}