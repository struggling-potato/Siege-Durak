package durak.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class Pair {
	private Card           bot;
	private Optional<Card> top;

	public ArrayList<Card> getCards() {
		ArrayList<Card> pair= new ArrayList<Card>();
		pair.add(bot);
		if (top.isPresent())
			pair.add(top.get());
		return pair;
	}
}

