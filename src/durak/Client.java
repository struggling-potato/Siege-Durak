package durak;

import durak.ai.Bot;
import durak.client.Controller;
import durak.communication.Connector;
import durak.communication.ServerInfo;
import durak.game.IGame;

import durak.game.*;
import durak.game.graphics.Buttons;
import durak.game.graphics.ClientView;

import java.util.ArrayList;

import java.io.IOException;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        Connector connector = new Connector();
        IGame     game      = connector.connectToServer(new ServerInfo("localhost", 1488)).get();

        Controller controller = new Controller(game);
        Bot player1 = new Bot();
        player1.register(game);
//        Bot player2 = new Bot();
//        player2.register(game);
    }
}