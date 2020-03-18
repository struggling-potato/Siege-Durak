package durak.game;

import java.util.ArrayList;

public class Table {
	private Dump dump;
	private Deck deck;

	private ArrayList<Pair> thrownCard = new ArrayList<>();

	public Dump getDump() {
		return dump;
	}

	public Deck getDeck() {
		return deck;
	}

	public ArrayList<Pair> getThrownCard() {
		return thrownCard;
	}
}
