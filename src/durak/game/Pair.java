package durak.game;

import java.util.ArrayList;
import java.util.Optional;

public class Pair {
	private Card           bot;
	private Optional<Card> top;

	public Pair(Card bot, Card top) {
		this.bot = bot;
		this.top = Optional.of(top);
	}

	public ArrayList<Card> getCards() {
		ArrayList<Card> pair = new ArrayList<>();
		pair.add(bot);
		top.ifPresent(pair::add);
		return pair;
	}
}

