package durak.game;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;

public class Deck implements Serializable {
	private Trump            trump = new Trump();
	private ArrayDeque<Card> cards;

	public Deck() {
		ArrayList<Card> cards = new ArrayList<>(36);
		for (var suit : Suit.values()) {
			if (suit.equals(Suit.SUIT_INVALID))
				continue;
			for (var rank : Rank.values()) {
				if (rank.equals(Rank.RANK_INVALID))
					continue;
				cards.add(new Card(suit, rank));
			}
		}

		Collections.shuffle(cards);
		this.cards = new ArrayDeque<>(cards);
		trump.setSuit(this.cards.getLast().getSuit());
	}

	public ArrayDeque<Card> getCards() {
		return cards;
	}

	public Card takeCardFromDeck() {
		return cards.removeLast();
	}

	public Trump getTrump() {
		return this.trump;
	}

	@Override
	public String toString() {
		return "Deck{" +
		       "trump=" + trump +
		       ", cards=" + cards +
		       '}';
	}
}
