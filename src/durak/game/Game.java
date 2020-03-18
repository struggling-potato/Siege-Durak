package durak.game;

import durak.client.states.State;

import java.util.ArrayList;
import java.util.HashMap;

interface ServerGame {

	void start();
}

public class Game implements IGame, ServerGame {
	int curMoveIdx = 0;
	private int                       currentId  = 0;
	private Table                     table;
	private ArrayList<IPlayer>        players    = new ArrayList<>(6);
	private HashMap<Integer, IPlayer> map        = new HashMap<>();
	private HashMap<IPlayer, Integer> reverseMap = new HashMap<>();
	private HashMap<Integer, Hand>    idToHand   = new HashMap<>();
	private HashMap<Integer, State>   idToState  = new HashMap<>();

	@Override
	public void throwCard(int playerId, Card card) {
		State curState = idToState.get(playerId);
		if (curState.equals(State.STATE_MOVE)) {
			Hand curHand = idToHand.get(playerId);
			if (curHand.getCards().stream().anyMatch((c) -> c.equals(card))) {
				curHand.getCards().remove(card);
				idToHand.put(currentId, curHand);
				table.getThrownCard().add(new Pair(card));
				for (var it = map.keySet().iterator(); it.hasNext(); ) {
					int     curId  = it.next();
					IPlayer player = map.get(curId);
					if (playerId == curId) {
						player.handOut(curHand);
					}
					player.currentTable(table);
				}
			}
		}
	}

	@Override
	public void tossCard(int playerId, Card card) {

	}

	@Override
	public void beatCard(int playerId, Pair pair) {

	}

	@Override
	public void passTossing(int playerId) {

	}

	@Override
	public void giveUpDefence(int playerId) {

	}

	@Override
	public void registerPlayer(IPlayer player) {
		int id = currentId;

		ServerPlayer serverPlayer = new ServerPlayer(player, new Player());
		players.add(player);


		map.put(currentId, player);
		reverseMap.put(player, currentId);
		idToHand.put(currentId, new Hand());
		idToState.put(currentId, State.STATE_WAIT);

		currentId++;
		System.out.println("Player " + id + " registered");
		player.onPlayerRegistered(id);
	}

	@Override
	public void exitGame(int playerId) {
		if (map.containsKey(playerId)) {
			IPlayer iPlayer = map.get(playerId);
			players.remove(iPlayer);
			map.remove(playerId);
			System.out.println("Player " + playerId + " exited");
		}
		else {
			System.out.println("Player " + playerId + " not found");
		}

	}

	@Override
	public void start() {
		Deck deck = new Deck();

		System.out.println("Trump in this game:");
		System.out.println(deck.getTrump().getSuit());

		for (var player : players
		) {
			Hand hand = new Hand();

			for (int i = 0; i < 6; i++) {
				hand.addCard(deck.takeCardFromDeck());
			}

			int playerId = reverseMap.get(players.get(curMoveIdx));

			idToHand.put(playerId, hand);
			player.handOut(hand);
			System.out.println(playerId + " player cards from deck: ");
			for (Card card : hand.getCards()) {
				System.out.println("{" + card.getSuit() + ":" + card.getRank() + "}");
			}
		}

		int moveId = reverseMap.get(players.get(curMoveIdx));
		idToState.put(moveId, State.STATE_MOVE);
		map.get(moveId).makeMove();
		int defId = reverseMap.get(players.get(curMoveIdx + 1));
		for (var it = map.keySet().iterator(); it.hasNext(); ) {
			int     curId  = it.next();
			IPlayer player = map.get(curId);
			if (defId == curId) {
				idToState.put(curId, State.STATE_DEFEND);
				map.get(curId).defendYourself();
			}
			else if (moveId != curId) {
				idToState.put(curId, State.STATE_WAIT);
				map.get(curId).endMove();
			}
		}

	}
}
