package durak.communication;

import durak.game.Hand;
import durak.game.IGame;
import durak.game.IPlayer;
import durak.game.Table;

class PlayerDummy implements IPlayer {
	private Connector connector;
	private int       id;
	private boolean   isDisconnected = false;

	PlayerDummy(Connector connector) {
		this.connector = connector;
	}

	@Override
	public void handOut(Hand hand) {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(dummyPlayerId);
			player.handOut(hand);
		};
		sendMessageToPlayer(message);
	}

	private void sendMessageToPlayer(Message message) {
		if (!isDisconnected)
			connector.sendMessageToPlayer(this, message);
	}

	@Override
	public void makeMove() {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(dummyPlayerId);
			player.makeMove();
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void defendYourself() {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(dummyPlayerId);
			player.defendYourself();
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void tossCards() {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(dummyPlayerId);
			player.tossCards();
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void currentTable(Table table) {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(dummyPlayerId);
			player.currentTable(table);
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void onPlayerRegistered(int playerId) {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		id = playerId;
		Message message =
				(info) -> {
					IPlayer player = info.getConnector().playerByDummyId(dummyPlayerId);
					player.onPlayerRegistered(playerId);
				};
		sendMessageToPlayer(message);
	}

	@Override
	public void endMove() {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(dummyPlayerId);
			player.endMove();
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void onGameStarted() {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(dummyPlayerId);
			player.onGameStarted();
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void onGameFinished() {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(dummyPlayerId);
			player.onGameFinished();
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void onPlayerDisconnected() {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(dummyPlayerId);
			player.onPlayerDisconnected();
			info.getConnector().cleanUpDummyOnPlayerDisconnected(dummyPlayerId);
		};
		sendMessageToPlayer(message);
	}

	void onCleanUp(IGame serverGame) {
		isDisconnected = true;
		serverGame.exitGame(id);
	}
}
