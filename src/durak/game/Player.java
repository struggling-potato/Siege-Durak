package durak.game;

import durak.client.Controller;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements IPlayer, Serializable {
	private IGame game;
	private int   id;

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	private Hand       hand;
	private Controller controller;

	public void printHand() {
		for (Card card : hand.getCards()) {
			System.out.println("{" + card.getSuit() + ":" + card.getRank() + "}");
		}
	}

	public Player(Controller controller) {
		hand = new Hand();
		this.controller = controller;
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

	@Override
	public void onGameStarted() {

	}

	@Override
	public void onGameFinished() {

	}

	@Override
	public void opponentsList(ArrayList<Player> opponents) {

	}
}
