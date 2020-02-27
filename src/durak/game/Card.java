package durak.game;

public class Card {
	private Suit suit;
	private Rank rank;

	public Card(Suit s, Rank r) {
		suit = s;
		rank = r;
	}

	public Suit getSuit() {
		return suit;
	}

	public Rank getRank() {
		return rank;
	}
}
