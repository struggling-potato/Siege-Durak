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

    }

    @Override
    public void tossCards(int playerId, List<Card> cards) {

    }

    private Timer timer = new Timer(true);
    private boolean timeOut;

    @Override
    public void throwCard(int playerId, Card card) {
        System.out.println(playerId + " throwCard {" + card + "}");
        PlayerState curState = idToState.get(playerId);
        Hand        curHand  = idToHand.get(playerId);
        if (checkStateAndCard(curState, curHand, card, null)) {
            IPlayer curIPlayer = playerIdToIPlayer.get(playerId);
            Player  curPlayer  = playerIdToPlayer.get(playerId);
            curHand.getCards().remove(card);
            table.getThrownCard().add(new Pair(card));
            for (var player : playerIdToIPlayer.values()) {
                if (player.equals(curIPlayer)) {
                    player.handOut(curHand);
                    curPlayer.handOut(curHand);
                }
                player.currentTable(table);
                if (idToState.get(IPlayerToPlayerId.get(player)) == PlayerState.STATE_DEFEND)
                    player.defendYourself();
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
            for (var player : playerIdToIPlayer.values()) {
                if (!player.equals(curPlayer)) {
                    idToState.replace(IPlayerToPlayerId.get(player), PlayerState.STATE_TOSS);
                }
                retry(IPlayerToPlayerId.get(player));
            }
        } else {
            retry(playerId);
        }
    }

    @Override
    public void passTossing(int playerId) {
        System.out.println(playerId + " passTossing");
        PlayerState playerState = idToState.get(playerId);
        if (playerState == PlayerState.STATE_TOSS) {
            idToState.replace(playerId, PlayerState.STATE_WAIT);
            playerIdToIPlayer.get(playerId).endMove();
            // TODO: Если все пасанули, то игра продолжается. Начинается следующий ход
        }
    }

    private boolean giveUp;

    private void retry(int playerId) {
        IPlayer curIPlayer = playerIdToIPlayer.get(playerId);
        Hand    curHand    = idToHand.get(playerId);
        curIPlayer.currentTable(table);
        curIPlayer.currentOpponentsList(players.stream()
                                               .filter(player -> player.getId() != playerId)
                                               .collect(Collectors.toCollection(ArrayList::new)));
        curIPlayer.handOut(curHand);
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
                curIPlayer.onPlayerDisconnected();
                // TODO: Удалить игрока
            }
        }
    }

    @Override
    public void tossCard(int playerId, Card card) {
        System.out.println(playerId + " tossCard {" + ", " + card + "}");
        PlayerState curState = idToState.get(playerId);
        Hand        curHand  = idToHand.get(playerId);
        if (checkStateAndCard(curState, curHand, card, null)) {
            IPlayer curIPlayer = playerIdToIPlayer.get(playerId);
            Player  curPlayer  = playerIdToPlayer.get(playerId);
            curHand.getCards().remove(card);
            table.getThrownCard().add(new Pair(card));
            for (var player : playerIdToIPlayer.values()) {
                if (player.equals(curIPlayer)) {
                    player.handOut(curHand);
                    curPlayer.handOut(curHand);
                }
                player.currentTable(table);
            }
        }
        else {
            retry(playerId);
        }
    }

    @Override
    public void giveUpDefence(int playerId) {
        System.out.println(playerId + "giveUpDefence");
        if (giveUp) return;
        // TODO: Проблема с таймером, добавить поддержку нескольких карт в throwCard и tossCard
        Hand curHand      = idToHand.get(playerId);
        var  cardsOnTable = table.getThrownCard();

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
            playerIdToIPlayer.put(currentId, player);
            IPlayerToPlayerId.put(player, currentId);
            idToHand.put(currentId, new Hand());
            idToState.put(currentId, PlayerState.STATE_WAIT);
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

    @Override
    public void startGame(int playerId) {

    }

    @Override
    public boolean waitCondition() {
        return iPlayers.size() >= 2;
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
        return iPlayers.size() == 0 ? -1 : curMoveIdx % iPlayers.size();
    }

    public boolean nextMoveCondition() {
        boolean stateWaitAll = idToState.values().stream().filter(state -> state == PlayerState.STATE_WAIT).count() ==
                               iPlayers.size() - 1;
        System.out.println("nextMoveCondition timeOut " + timeOut);
        System.out.println("nextMoveCondition stateWaitAll " + stateWaitAll);
        return timeOut || (stateWaitAll);
    }

    @Override
    public void start() {
        System.out.println("Game started");
        System.out.println("Players: " + iPlayers);

        Deck deck = new Deck();
        table = new Table(deck);

        System.out.println("Trump in this game:");
        System.out.println(deck.getTrump().getSuit());

        for (int playerIdx = 0; playerIdx < iPlayers.size(); ++playerIdx) {

            IPlayer iPlayer = iPlayers.get(playerIdx);
            Player  player  = players.get(playerIdx);
            Hand    hand    = new Hand();

            if (playerIdx != 1) {
                for (int i = 0; i < 6; i++) {
                    hand.addCard(deck.takeCardFromDeck());
                }
            }

            int playerId = IPlayerToPlayerId.get(iPlayers.get(getMovingPlayerIdx(playerIdx)));

            idToHand.put(playerId, hand);
            iPlayer.handOut(hand);
            player.handOut(hand);
            System.out.println(playerId + " player cards from deck: ");
            for (Card card : hand.getCards()) {
                System.out.println("{" + card.getSuit() + ":" + card.getRank() + "}");
            }
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
                    idToState.put(curId, PlayerState.STATE_WAIT);
                }
                retry(curId);
            }
            synchronized (iPlayers) {
                setTimeOut(50000);
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
                idToState.put(curId, PlayerState.STATE_WAIT);
                retry(curId);
            }

            var  cardsOnTable = table.getThrownCard();
            if (giveUp) {
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
                curMoveIdx++;
                giveUp = false;
            }
            else {
                for (var pair : cardsOnTable) {
                    table.getDump().getCards().add(pair);
                }
                table.getThrownCard().clear();
            }

            for (int i = 0; i < iPlayers.size(); ++i) {
                IPlayer iPlayer  = iPlayers.get(getMovingPlayerIdx(i));
                Player  player   = players.get(getMovingPlayerIdx(i));
                int     playerId = IPlayerToPlayerId.get(iPlayer);
                Hand    curHand  = idToHand.get(playerId);
                while (curHand.getCards().size() < 6)
                    curHand.addCard(table.getDeck().takeCardFromDeck());

                idToHand.put(playerId, curHand);
                iPlayer.handOut(curHand);
                player.handOut(curHand);
            }

            curMoveIdx++;
        }
    }

    private boolean timerCanceled;

    int getMovingPlayerIdx(int offset) {
        return iPlayers.size() == 0 ? -1 : (curMoveIdx + offset) % iPlayers.size();
    }

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
            timer.schedule(new TimerTask() {
                               @Override
                               public void run() {
                                   synchronized (iPlayers) {
                                       timeOut = true;
                                       iPlayers.notify();
                                   }
                               }
                           }
                    , millisecondsTimeOut);
        }
    }
}