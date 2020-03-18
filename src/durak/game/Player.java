package durak.game;

import java.io.Serializable;

public class Player implements IPlayer, Serializable {
	private IGame game;
	private int   id;
	private Hand  hand = new Hand();

	public void printHand() {
		for (Card card : hand.getCards()) {
			System.out.println("{" + card.getSuit() + ":" + card.getRank() + "}");
		}
	}

	public Player() {

	}

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

	}

	@Override
	public void defendYourself() {

	}

	@Override
	public void tossCards() {

	}

	@Override
	public void currentTable(Table table) {

	}

	@Override
	public void onPlayerRegistered(int playerId) {
		System.out.println("onPlayerRegistered playerId: " + playerId);
		id = playerId;
	}

	@Override
	public void endMove() {

	}
}
