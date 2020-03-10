package durak.game;

import java.util.HashMap;

public class CardsDictionary {

	public HashMap<Integer, Card> dictionary = new HashMap<Integer, Card>();

	CardsDictionary() {
	}

	public HashMap<Integer, Card> createDictionary() {
		dictionary.put(1, new Card(Suit.SUIT_HEARTS, Rank.RANK_6));
		dictionary.put(2, new Card(Suit.SUIT_PIKES, Rank.RANK_6));
		dictionary.put(3, new Card(Suit.SUIT_CLOVERS, Rank.RANK_6));
		dictionary.put(4, new Card(Suit.SUIT_TILES, Rank.RANK_6));
		dictionary.put(5, new Card(Suit.SUIT_HEARTS, Rank.RANK_7));
		dictionary.put(6, new Card(Suit.SUIT_PIKES, Rank.RANK_7));
		dictionary.put(7, new Card(Suit.SUIT_CLOVERS, Rank.RANK_7));
		dictionary.put(8, new Card(Suit.SUIT_TILES, Rank.RANK_7));
		dictionary.put(9, new Card(Suit.SUIT_HEARTS, Rank.RANK_8));
		dictionary.put(10, new Card(Suit.SUIT_PIKES, Rank.RANK_8));
		dictionary.put(11, new Card(Suit.SUIT_CLOVERS, Rank.RANK_8));
		dictionary.put(12, new Card(Suit.SUIT_TILES, Rank.RANK_8));
		dictionary.put(13, new Card(Suit.SUIT_HEARTS, Rank.RANK_9));
		dictionary.put(14, new Card(Suit.SUIT_PIKES, Rank.RANK_9));
		dictionary.put(15, new Card(Suit.SUIT_CLOVERS, Rank.RANK_9));
		dictionary.put(16, new Card(Suit.SUIT_TILES, Rank.RANK_9));
		dictionary.put(17, new Card(Suit.SUIT_HEARTS, Rank.RANK_10));
		dictionary.put(18, new Card(Suit.SUIT_PIKES, Rank.RANK_10));
		dictionary.put(19, new Card(Suit.SUIT_CLOVERS, Rank.RANK_10));
		dictionary.put(20, new Card(Suit.SUIT_TILES, Rank.RANK_10));
		dictionary.put(21, new Card(Suit.SUIT_HEARTS, Rank.RANK_J));
		dictionary.put(22, new Card(Suit.SUIT_PIKES, Rank.RANK_J));
		dictionary.put(23, new Card(Suit.SUIT_CLOVERS, Rank.RANK_J));
		dictionary.put(24, new Card(Suit.SUIT_TILES, Rank.RANK_J));
		dictionary.put(25, new Card(Suit.SUIT_HEARTS, Rank.RANK_Q));
		dictionary.put(26, new Card(Suit.SUIT_PIKES, Rank.RANK_Q));
		dictionary.put(27, new Card(Suit.SUIT_CLOVERS, Rank.RANK_Q));
		dictionary.put(28, new Card(Suit.SUIT_TILES, Rank.RANK_Q));
		dictionary.put(29, new Card(Suit.SUIT_HEARTS, Rank.RANK_K));
		dictionary.put(30, new Card(Suit.SUIT_PIKES, Rank.RANK_K));
		dictionary.put(31, new Card(Suit.SUIT_CLOVERS, Rank.RANK_K));
		dictionary.put(32, new Card(Suit.SUIT_TILES, Rank.RANK_K));
		dictionary.put(33, new Card(Suit.SUIT_HEARTS, Rank.RANK_A));
		dictionary.put(34, new Card(Suit.SUIT_PIKES, Rank.RANK_A));
		dictionary.put(35, new Card(Suit.SUIT_CLOVERS, Rank.RANK_A));
		dictionary.put(36, new Card(Suit.SUIT_TILES, Rank.RANK_A));
		return dictionary;
	}

	public HashMap<Integer, Card> getDictionary() {
		return dictionary;
	}

}
