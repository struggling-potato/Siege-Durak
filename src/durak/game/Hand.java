package durak.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Hand implements Serializable {
	private ArrayList<Card> cards = new ArrayList<>();

	public void addCard(Card card) {
		cards.add(card);
	}

	public ArrayList<Card> getCards() {
		return cards;
	}

	public ArrayList<Card> filter(Predicate<Card> predicate) {
		return cards.stream().filter(predicate).collect(Collectors.toCollection(ArrayList::new));
	}

	public void sort() {
		cards.sort(null);
	}

	@Override
	public String toString() {
		return "Hand{" +
		       cards.size() + " cards=" + cards +
		       '}';
	}
}
