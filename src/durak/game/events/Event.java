package durak.game.events;

public interface Event {
	/*
	 *  General event class
	 */

	interface GameEvent extends Event {
		/*
		 * Superclass for game events
		 */
		//TODO: subclasses attack, defend, toss, wait, beat, pass
	}
}
