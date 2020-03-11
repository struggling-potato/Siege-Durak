package durak.game;

public class ServerPlayer {

	private IPlayer iPlayer;
	private Player  player;

	public ServerPlayer(IPlayer iPlayer, Player player) {
		this.iPlayer = iPlayer;
		this.player = player;
	}

	public IPlayer getiPlayer() {
		return iPlayer;
	}

	public Player getPlayer() {
		return player;
	}
}
