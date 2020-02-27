package durak;

import durak.game.Game;
import durak.game.IPlayer;

public class Client implements IPlayer {

    private Game game = null;
    private int id = -1;

    public void register(Game game) {
        this.game = game;

        id = game.addUser(this);
    }

    @Override
    public void foo() {
        if (game != null) {
            System.out.println(
                    game.ping(id, 123)
            );
        }
    }
}
