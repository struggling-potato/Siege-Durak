package durak;

import durak.game.Game;
import durak.game.Player;

public class Server {

	public static void main(String[] args) throws InterruptedException {
		Game game = new Game();

		Player player1 = new Player();
		Player player2 = new Player();

		player1.register(game);
		player2.register(game);

		game.start();

		game.exitGame(player1.getId());
		game.exitGame(player2.getId());
	}
}
