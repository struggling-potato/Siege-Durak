package durak.game;

import java.io.Serializable;
import java.util.ArrayList;

public class Pair implements Serializable {
	private Card bot;
	private Card top;

	public Pair(Card bot, Card top) {
		this.bot = bot;
		this.top = top;
	}

	public Pair(Card card) {
		bot = card;
	}

	public ArrayList<Card> getCards() {
		ArrayList<Card> pair = new ArrayList<>();
		pair.add(bot);
		if (null != top)
			pair.add(top);
		return pair;
	}
}

