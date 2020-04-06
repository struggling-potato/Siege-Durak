package durak;

import durak.communication.Connector;
import durak.game.Game;

import java.io.IOException;

public class Server {

	public static void main(String[] args) throws InterruptedException, IOException {
		Game      game      = new Game();
		Connector connector = new Connector();
		connector.registerServer(game, 1488);

		Thread.sleep(10000);

		game.waitAndStart();
	}
}