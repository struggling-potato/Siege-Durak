package durak.game;

import java.util.ArrayList;

public class Hand {
	private ArrayList<Card> cards = new ArrayList<>();

	public void addCard(Card card) {
		cards.add(card);
	}

	public ArrayList<Card> getCards() {
		return cards;
	}
}
