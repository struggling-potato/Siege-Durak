package durak.game;

import java.util.ArrayList;
import java.util.HashMap;

public class Game implements IGame {
	private int currentId = 0;

	private Table table;

	private ArrayList<IPlayer> players = new ArrayList<>(6);

	private HashMap<Integer, IPlayer> map = new HashMap<>();

	@Override
	public void throwCard(int playerId, Card card) {

	}

	@Override
	public void tossCard(int playerId, Card card) {

	}

	@Override
	public void beatCard(int playerId, Pair pair) {

	}

	@Override
	public void passTossing(int playerId) {

	}

	@Override
	public void giveUpDefence(int playerId) {

	}

	@Override
	public void registerPlayer(IPlayer player) {
		int id = currentId;
		players.add(player);
		map.put(currentId++, player);
		System.out.println("Player " + id + " registered");
		player.onPlayerRegistered(id);
	}

	@Override
	public void exitGame(int playerId) {
		if (map.containsKey(playerId)) {
			IPlayer iPlayer = map.get(playerId);
			players.remove(iPlayer);
			map.remove(playerId);
			System.out.println("Player " + playerId + " exited");
		}
		else {
			System.out.println("Player " + playerId + " not found");
		}

	}

	@Override
	public void startGame(int playerId) {

	}

	@Override
	public void getOpponentsList(int playerId) {

	}
}