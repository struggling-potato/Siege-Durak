package durak.game;

public class Player implements IPlayer {
	private IGame game;
	private int   id;
	private Hand  hand = new Hand();

	public Player(IGame game) {
		this.game = game;
		id        = game.registerPlayer(this);
	}

	public int getId() {
		return id;
	}

	@Override
	public void handOut(Hand hand) {

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
