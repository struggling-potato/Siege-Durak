package durak.communication;

import durak.game.*;

import java.util.ArrayList;

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
		int remoteId = connector.translatePlayerIdToRemotePlayerId(dummyPlayerId);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(remoteId);
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
		int remoteId = connector.translatePlayerIdToRemotePlayerId(dummyPlayerId);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(remoteId);
			player.makeMove();
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void defendYourself() {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		int remoteId = connector.translatePlayerIdToRemotePlayerId(dummyPlayerId);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(remoteId);
			player.defendYourself();
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void tossCards() {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		int remoteId = connector.translatePlayerIdToRemotePlayerId(dummyPlayerId);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(remoteId);
			player.tossCards();
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void currentTable(Table table) {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		int remoteId = connector.translatePlayerIdToRemotePlayerId(dummyPlayerId);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(remoteId);
			player.currentTable(table);
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void onPlayerRegistered(int playerId) {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		id = playerId;
		int remoteId = connector.translatePlayerIdToRemotePlayerId(dummyPlayerId);
		Message message =
				(info) -> {
					IPlayer player = info.getConnector().playerByDummyId(remoteId);
					player.onPlayerRegistered(playerId);
				};
		sendMessageToPlayer(message);
	}

	@Override
	public void endMove() {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		int remoteId = connector.translatePlayerIdToRemotePlayerId(dummyPlayerId);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(remoteId);
			player.endMove();
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void onGameStarted() {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		int remoteId = connector.translatePlayerIdToRemotePlayerId(dummyPlayerId);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(remoteId);
			player.onGameStarted();
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void onGameFinished(int loserId) {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		int remoteId = connector.translatePlayerIdToRemotePlayerId(dummyPlayerId);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(remoteId);
			player.onGameFinished(loserId);
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void currentOpponentsList(ArrayList<Player> opponents) {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		int remoteId = connector.translatePlayerIdToRemotePlayerId(dummyPlayerId);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(remoteId);
			player.currentOpponentsList(opponents);
		};
		sendMessageToPlayer(message);
	}

	@Override
	public void onPlayerDisconnected() {
		int dummyPlayerId = connector.getPlayerDummyId(this);
		int remoteId = connector.translatePlayerIdToRemotePlayerId(dummyPlayerId);
		Message message = (info) -> {
			IPlayer player = info.getConnector().playerByDummyId(remoteId);
			player.onPlayerDisconnected();
			info.getConnector().cleanUpDummyOnPlayerDisconnected(remoteId);
		};
		sendMessageToPlayer(message);
	}

	void onCleanUp(IGame serverGame) {
		isDisconnected = true;
		serverGame.exitGame(id);
	}
}
