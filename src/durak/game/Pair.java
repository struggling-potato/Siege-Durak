package durak.game;

import java.util.ArrayList;
import java.util.Optional;

public class Pair {
	private Card           bot;
	private Optional<Card> top;

	public ArrayList<Card> getCards() {
		ArrayList<Card> pair = new ArrayList<>();
		pair.add(bot);
		top.ifPresent(pair::add);
		return pair;
	}
}

