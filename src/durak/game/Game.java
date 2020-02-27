package durak.game;

import durak.Client;

import java.util.ArrayList;
import java.util.HashMap;

public class Game implements IGame {
    private int currentId = 0;

    private game.Table table;

    private ArrayList<Player> players = new ArrayList<>(6);

    private HashMap<Integer, Player> map = new HashMap<>();

    public int addUser(IPlayer iPlayer) {
        Player player1 = new Player(iPlayer, currentId);
        players.add(player1);
        map.put(currentId, player1);
        int id = currentId++;
        return id;
    }

    public static void main(String[] args) {
        Game game = new Game();
        Client client = new Client();
        Client client1 = new Client();

        client.register(game);
        client1.register(game);
        client.foo();
        client1.foo();
    }

    @Override
    public int ping(int id, int val) {
        System.out.println(id);
        System.out.println(val);
        return val;
    }
}
