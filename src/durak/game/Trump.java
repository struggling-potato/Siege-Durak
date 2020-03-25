package durak.game;

import java.io.Serializable;

public class Trump implements Serializable {
	private Suit suit;

	public Suit getSuit() {
		return suit;
	}

	public void setSuit(Suit suit) {
		this.suit = suit;
	}

	@Override
	public String toString() {
		return "Trump{" +
		       "suit=" + suit +
		       '}';
	}
}
