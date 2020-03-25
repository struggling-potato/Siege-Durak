package durak.game;

import java.io.Serializable;
import java.util.ArrayList;

public class Dump implements Serializable {
	private ArrayList<Pair> cards = new ArrayList<>();

	@Override
	public String toString() {
		return "Dump{" +
		       cards.size() + " card pairs=" + cards +
		       '}';
	}
}
