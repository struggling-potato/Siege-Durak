package durak.game;

import java.io.Serializable;
import java.util.Objects;

public class Card implements Comparable, Serializable {
	private Suit suit;
	private Rank rank;
	private int  ownerId;

	public Card(Suit suit, Rank rank) {
		this.suit = suit;
		this.rank = rank;
	}

	public Card(Card card) {
		this.suit = card.suit;
		this.rank = card.rank;
	}

	public Suit getSuit() {
		return suit;
	}

	public Rank getRank() {
		return rank;
	}

	private int getOwnerId() {
		return ownerId;
	}

	private void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public int compareTo(Object o) {
		Card card   = (Card) o;
		int  result = this.getSuit().compareTo(card.getSuit());
		if (result == 0)
			result = this.getRank().compareTo(card.getRank());
		return result;
	}

	@Override
	public String toString() {
		return "Card{" +
		       "suit=" + suit +
		       ", rank=" + rank +
		       '}';
	}

	@Override
	public int hashCode() {
		return Objects.hash(suit, rank, ownerId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Card card = (Card) o;
		return ownerId == card.ownerId &&
		       suit == card.suit &&
		       rank == card.rank;
	}
}
