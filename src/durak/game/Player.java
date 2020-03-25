package durak.game;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements IPlayer, Serializable {
	private transient IGame game;
	private int   id;
	private Hand  hand = new Hand();

	public void printHand() {
		for (Card card : hand.getCards()) {
			System.out.println("{" + card.getSuit() + ":" + card.getRank() + "}");
		}
	}

	public Player() {

	}

	public Card getCard(int index){
		return hand.getCards().get(index);
	}

	// Для отрисовки руки опонента нужна либо сама рука, либо количество карт в руке. Поскольку по API договорились
	// передавать игроков, то добавлю этот метод сюда
	public int getHandSize() { return hand.getCards().size(); }

	public void register(IGame game) {
		this.game = game;
		game.registerPlayer(this);
	}

	public int getId() {
		return id;
	}

	@Override
	public void handOut(Hand hand) {
		System.out.println("handOut " + hand);
		this.hand = hand;
	}

	@Override
	public void makeMove() {
		game.throwCard(id, hand.getCards().get(0));
	}

	@Override
	public void defendYourself() {

	}

	@Override
	public void tossCards() {

	}

	@Override
	public void currentTable(Table table) {
		System.out.println(id + " currentTable " + table);
	}

	@Override
	public void onPlayerRegistered(int playerId) {
		System.out.println("onPlayerRegistered playerId: " + playerId);
		id = playerId;
	}

	@Override
	public void endMove() {

	}

	@Override
	public void onGameStarted() {

	}

	@Override
	public void onGameFinished(int loserId) {

	}

	@Override
	public void currentOpponentsList(ArrayList<Player> opponents) {

	}

	@Override
	public void onPlayerDisconnected() {

	}
}
