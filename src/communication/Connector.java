package communication;

import utils.Actor;

public class Connector {
	public Actor connect(String ip, int port) {
		return new ActorConnection();
	}
}
