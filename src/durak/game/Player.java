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

	}

	@Override
	public void onPlayerRegistered(int playerId) {
		id = playerId;
	}

	@Override
	public void endMove() {

	}
}
