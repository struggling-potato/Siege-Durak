package durak.game;

import java.io.Serializable;
import java.util.ArrayList;

public class Table implements Serializable {
	private Dump dump = new Dump();
	private Deck deck;

	private ArrayList<Pair> thrownCard = new ArrayList<>();

	Table(Deck deck) {
		this.deck = deck;
	}

	public Dump getDump() {
		return dump;
	}

	public Deck getDeck() {
		return deck;
	}

	public ArrayList<Pair> getThrownCard() {
		return thrownCard;
	}

	void setDeck(Deck deck) {
		this.deck = deck;
	}

	@Override
	public String toString() {
		return "Table{" +
		       thrownCard.size() + " thrownCard pairs=" + thrownCard +
		       ", dump=" + dump +
		       ", deck=" + deck +
		       '}';
	}
}
