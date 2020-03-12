package durak.game;

public class Card implements Comparable {
	private Suit suit;
	private Rank rank;
	private int  ownerId;

	public Card(Suit suit, Rank rank) {
		this.suit = suit;
		this.rank = rank;
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
}
