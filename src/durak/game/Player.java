package durak.game;

public class Player {

	private IPlayer back;
	private int  id;
	private Hand hand = new Hand();

	public Player(IPlayer back, int id) {
		this.back = back;
		this.id = id;
	}
}
