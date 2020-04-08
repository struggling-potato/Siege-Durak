package durak.game.playerscache;

import durak.game.Player;

import java.util.ArrayList;

public interface PlayersRepository {

	ArrayList<Player> get();

	void update(ArrayList<Player> players);
}

class PlayersRepositoryFactory {

	public static PlayersRepository create() {
		return new PlayersRepositoryImpl();
	}
}

class PlayersRepositoryImpl implements PlayersRepository {

	private ArrayList<Player> players;

	@Override
	public ArrayList<Player> get() {
		return players;
	}

	@Override
	public void update(ArrayList<Player> players) {
		this.players = players;
	}
}