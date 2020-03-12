package durak.game.graphics.utils;

import durak.game.Card;

public class CardToImage {
	public static String getCartImageUrl(Card card) {
		String suit, rank;

		switch (card.getSuit()){
			case SUIT_HEARTS: {
				suit = "hearts";
				break;
			}
			case SUIT_PIKES: {
				suit = "pikes";
				break;
			}
			case SUIT_TILES: {
				suit = "tiles";
				break;
			}
			case SUIT_CLOVERS: {
				suit = "clovers";
				break;
			}
			default: {
				suit = "";
				break;
			}
		}

		switch (card.getRank()) {
			case RANK_6: {
				rank = "6";
				break;
			}
			case RANK_7: {
				rank = "7";
				break;
			}
			case RANK_8: {
				rank = "8";
				break;
			}
			case RANK_9: {
				rank = "9";
				break;
			}
			case RANK_10: {
				rank = "10";
				break;
			}
			case RANK_J: {
				rank = "J";
				break;
			}
			case RANK_K: {
				rank = "K";
				break;
			}
			case RANK_Q: {
				rank = "Q";
				break;
			}
			case RANK_A: {
				rank = "A";
				break;
			}
			default: {
				rank = "";
				break;
			}
		}
		return "assets/" + suit + "_" + rank + ".png";
	}
}
