package durak.game;

public class Player implements IPlayer {
	private IGame game;
	private int   id;
	private Hand  hand = new Hand();

	public void printHand() {
		for (Card card : hand.getCards()) {
			System.out.println("{" + card.getSuit() + ":" + card.getRank() + "}");
		}
	}

	public Player(IGame game) {
		this.game = game;
		id        = game.registerPlayer(this);
	}

	public int getId() {
		return id;
	}

	@Override
	public void handOut(Hand hand) {
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
	public void endMove() {

	}
}
