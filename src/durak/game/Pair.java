package game;

import java.util.Optional;

public class Pair {
	private Card           bot;
	private Optional<Card> top;

	public Card[] getCards() {
		if (top.isEmpty())
			return new Card[]{bot};
		else
			return new Card[]{bot, top.get()};
	}
}
