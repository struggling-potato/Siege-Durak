package durak;

import durak.game.Game;
import durak.game.Player;

public class Client {

    public static void main(String[] args) {
        Game   game    = new Game();
        Player player1 = new Player(game);
        Player player2 = new Player(game);

        game.exitGame(player1.getId());
        game.exitGame(player2.getId());
    }
}
