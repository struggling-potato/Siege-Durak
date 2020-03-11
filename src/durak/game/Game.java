package durak.game;

import java.util.ArrayList;
import java.util.HashMap;

interface ServerGame {

	boolean waitCondition();

	void waitAndStart();

	void start();
}

public class Game implements IGame, ServerGame {
	int curMoveIdx = 0;
	private int                           currentId         = 0;
	private Table                         table;
	private ArrayList<IPlayer>            players           = new ArrayList<>(6);
	private HashMap<Integer, IPlayer>     playerIdToIPlayer = new HashMap<>();
	private HashMap<IPlayer, Integer>     IPlayerToPlayerId = new HashMap<>();
	private HashMap<Integer, Hand>        idToHand          = new HashMap<>();
	private HashMap<Integer, PlayerState> idToState         = new HashMap<>();

	@Override
	public void throwCard(int playerId, Card card) {
		System.out.println("throwCard {" + playerId + ", " + card + "}");
		PlayerState curState = idToState.get(playerId);
		Hand        curHand  = idToHand.get(playerId);
		if (checkStateAndCard(curState, curHand, card, null)) {
			IPlayer curPlayer = playerIdToIPlayer.get(playerId);
			curHand.getCards().remove(card);
			table.getThrownCard().add(new Pair(card));
			for (var player : playerIdToIPlayer.values()) {
				if (player.equals(curPlayer)) {
					player.handOut(curHand);
				}
				player.currentTable(table);
			}
		}
	}

	boolean checkStateAndCard(PlayerState current, Hand currentHand, Card checkingCard, Pair pair) {
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
				return table.getThrownCard().stream().anyMatch((p) -> p.getCards().stream().anyMatch((c) -> c.getRank() == checkingCard.getRank()));
			}
			case STATE_WAIT:
			default: {
				return false;
			}
		}
	}

	@Override
	public void tossCard(int playerId, Card card) {
		System.out.println("tossCard {" + playerId + ", " + card + "}");
		PlayerState curState = idToState.get(playerId);
		Hand        curHand  = idToHand.get(playerId);
		if (checkStateAndCard(curState, curHand, card, null)) {
			IPlayer curPlayer = playerIdToIPlayer.get(playerId);
			curHand.getCards().remove(card);
			table.getThrownCard().add(new Pair(card));
			for (var player : playerIdToIPlayer.values()) {
				if (player.equals(curPlayer)) {
					player.handOut(curHand);
				}
				player.currentTable(table);
			}
		}
	}

	@Override
	public void beatCard(int playerId, Pair pair) {
		System.out.println("beatCard {" + playerId + ", " + pair + "}");
		PlayerState curState = idToState.get(playerId);
		Hand        curHand  = idToHand.get(playerId);
		if (checkStateAndCard(curState, curHand, null, pair)) {
			IPlayer curPlayer = playerIdToIPlayer.get(playerId);
			curHand.getCards().remove(pair.getTopCard());
			table.getThrownCard().removeIf((p) -> p.getBottomCard().equals(pair.getBottomCard()));
			table.getThrownCard().add(pair);
			for (var player : playerIdToIPlayer.values()) {
				if (player.equals(curPlayer)) {
					player.handOut(curHand);
				}
				player.currentTable(table);
			}
		}
	}

	@Override
	public void passTossing(int playerId) {
		System.out.println("passTossing {" + playerId + "}");
		idToState.replace(playerId, PlayerState.STATE_WAIT);
		idToState.replace(++currentId, PlayerState.STATE_MOVE);
		idToState.replace(++currentId, PlayerState.STATE_DEFEND);
	}

	@Override
	public void giveUpDefence(int playerId) {
		System.out.println("giveUpDefence {" + playerId + "}");

		Hand curHand      = idToHand.get(playerId);
		var  cardsOnTable = table.getThrownCard();
		for (var pair : cardsOnTable) {
			var cards = pair.getCards();

			for (var card : cards) {
				curHand.addCard(card);
			}
		}
		table.getThrownCard().clear();

		idToState.replace(playerId, PlayerState.STATE_WAIT);
		currentId += 2;
		idToState.replace(currentId, PlayerState.STATE_MOVE);
		idToState.replace(++currentId, PlayerState.STATE_DEFEND);
	}

	@Override
	public void registerPlayer(IPlayer player) {
		int id = currentId;

		synchronized (players) {
			players.add(player);
			playerIdToIPlayer.put(currentId, player);
			IPlayerToPlayerId.put(player, currentId);
			idToHand.put(currentId, new Hand());
			idToState.put(currentId, PlayerState.STATE_WAIT);
			currentId++;
			System.out.println("Player " + id + " registered");
			player.onPlayerRegistered(id);
			System.out.println("waitCondition: " + waitCondition());
			if (waitCondition()) {
				synchronized (players) {
					players.notify();
				}
			}
		}
	}

	@Override
	public void exitGame(int playerId) {
		if (playerIdToIPlayer.containsKey(playerId)) {
			IPlayer iPlayer = playerIdToIPlayer.get(playerId);
			players.remove(iPlayer);
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
		return players.size() >= 2;
	}

	@Override
	public void waitAndStart() {
		synchronized (players) {
			while (!waitCondition()) {
				System.out.println("!waitCondition()");
				try {
					players.wait();
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
		return players.size() == 0 ? -1 : curMoveIdx % players.size();
	}

	int getMovingPlayerIdx(int offset) {
		return players.size() == 0 ? -1 : (curMoveIdx + offset) % players.size();
	}

	@Override
	public void start() {
		System.out.println("Game started");
		System.out.println("Players: " + players);
		Deck deck = new Deck();
		table = new Table(deck);

		table.setDeck(deck);

		System.out.println("Trump in this game:");
		System.out.println(deck.getTrump().getSuit());

		for (int playerIdx = 0; playerIdx < players.size(); ++playerIdx) {
			IPlayer player = players.get(playerIdx);
			Hand    hand   = new Hand();

			for (int i = 0; i < 6; i++) {
				hand.addCard(deck.takeCardFromDeck());
			}

			int playerId = IPlayerToPlayerId.get(players.get(getMovingPlayerIdx(playerIdx)));

			idToHand.put(playerId, hand);
			player.handOut(hand);
			System.out.println(playerId + " player cards from deck: ");
			for (Card card : hand.getCards()) {
				System.out.println("{" + card.getSuit() + ":" + card.getRank() + "}");
			}
		}

		while (true) {
			int moveId = IPlayerToPlayerId.get(players.get(getMovingPlayerIdx()));
			idToState.put(moveId, PlayerState.STATE_MOVE);
			playerIdToIPlayer.get(moveId).makeMove();
			int defId = IPlayerToPlayerId.get(players.get(getMovingPlayerIdx(1)));
			for (var it = playerIdToIPlayer.keySet().iterator(); it.hasNext(); ) {
				int     curId  = it.next();
				IPlayer player = playerIdToIPlayer.get(curId);
				if (defId == curId) {
					idToState.put(curId, PlayerState.STATE_DEFEND);
					playerIdToIPlayer.get(curId).defendYourself();
				}
				else if (moveId != curId) {
					idToState.put(curId, PlayerState.STATE_WAIT);
					playerIdToIPlayer.get(curId).endMove();
				}
			}
			curMoveIdx++;
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}