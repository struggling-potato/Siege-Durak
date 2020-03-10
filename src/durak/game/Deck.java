package durak.game;

import java.util.*;

public class Deck {
	private Trump trump = new Trump();
	private ArrayList<Card> cards = new ArrayList<Card>();
	private ArrayList<Integer> keysOfCards = new ArrayList<Integer>();
	private CardsDictionary cardsdictionary = new CardsDictionary();
	private Integer currentCardIndex;

	public Deck() {
		currentCardIndex = 1;
	}

	public ArrayList<Card> getCardsFromDeck() {
		return this.cards;
	}

	public Card getCurrentCardFromDeck() {
		Card currentCard = cards.get(currentCardIndex);
		currentCardIndex++;
		return currentCard;
	};

	public void setCardsToDeck(ArrayList<Card> cards) {
		this.cards = cards;
	}

	public Trump getTrump() {
		return this.trump;
	}

	public void setTrump(Trump trump) {
		this.trump = trump;
	}

	public void generateDeck() {
		HashMap<Integer, Card> dictionary = cardsdictionary.createDictionary();
		for (int i=1; i <= 36; i++){
			keysOfCards.add(i);
		}
		Collections.shuffle(keysOfCards);
		for (int i=0; i < keysOfCards.size(); i++) {
			cards.add(dictionary.get(keysOfCards.get(i)));
		}
		int keyOfTrump = 1 + (int) (Math.random() * 4);
		switch (keyOfTrump) {
			case 1: {
				trump.setSuit(Suit.SUIT_HEARTS);
				break;
			}
			case 2: {
				trump.setSuit(Suit.SUIT_PIKES);
				break;
			}
			case 3: {
				trump.setSuit(Suit.SUIT_CLOVERS);
				break;
			}
			case 4: {
				trump.setSuit(Suit.SUIT_TILES);
				break;
			}
		}
	}
}
