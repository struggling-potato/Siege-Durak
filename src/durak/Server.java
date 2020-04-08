package durak;

import durak.communication.Connector;
import durak.server.Game;

import java.io.IOException;

public class Server {

	public static void main(String[] args) throws InterruptedException, IOException {
		Game      game      = new Game();
		Connector connector = new Connector();
		connector.registerServer(game, 1488);

		game.waitAndStart();
	}
}