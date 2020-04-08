package durak.communication;

import durak.game.Card;
import durak.game.IGame;
import durak.game.IPlayer;
import durak.game.Pair;

import java.util.ArrayList;
import java.util.List;

class GameDummy implements IGame {
	private Connector connector;
	private boolean   isDisconnected = false;

	GameDummy(Connector connector) {
		this.connector = connector;
	}

	@Override
	public void throwCard(int playerId, Card card) {
		Message message = (info) -> info.getGame().ifPresent((game -> game.throwCard(playerId, card)));
		sendMessageToGame(message);
	}

	@Override
	public void throwCards(int playerId, List<Card> cards) {
		Message message = (info) -> info.getGame().ifPresent((game -> game.throwCards(playerId, cards)));
		sendMessageToGame(message);
	}

	private void sendMessageToGame(Message message) {
		if (!isDisconnected)
			connector.sendMessageToGame(this, message);
	}

	@Override
	public void tossCard(int playerId, Card card) {
		Message message = (info) -> info.getGame().ifPresent((game -> game.tossCard(playerId, card)));
		sendMessageToGame(message);
	}

	@Override
	public void tossCards(int playerId, List<Card> cards) {
		Message message = (info) -> info.getGame().ifPresent((game -> game.tossCards(playerId, cards)));
		sendMessageToGame(message);
	}

	@Override
	public void beatCard(int playerId, Pair pair) {
		Message message = (info) -> info.getGame().ifPresent((game -> game.beatCard(playerId, pair)));
		sendMessageToGame(message);
	}

	@Override
	public void passTossing(int playerId) {
		Message message = (info) -> info.getGame().ifPresent((game -> game.passTossing(playerId)));
		sendMessageToGame(message);
	}

	@Override
	public void giveUpDefence(int playerId) {
		Message message = (info) -> info.getGame().ifPresent((game -> game.giveUpDefence(playerId)));
		sendMessageToGame(message);
	}

	@Override
	public void registerPlayer(IPlayer player) {
		int dummyId = connector.getPlayerDummyId(player);
		Message message = (info) -> info.getGame().ifPresent((game -> {
			PlayerDummy playerDummy = info.getConnector().playerDummyById(game, dummyId, info);
			game.registerPlayer(playerDummy);
		}));
		connector.tirePlayerWithDummy(this, player, dummyId);

		sendMessageToGame(message);
	}

	@Override
	public void exitGame(int playerId) {
		Message message = (info) -> info.getGame().ifPresent((game -> game.exitGame(playerId)));
		sendMessageToGame(message);
	}

	@Override
	public void startGame(int playerId) {
		Message message = (info) -> info.getGame().ifPresent((game -> game.startGame(playerId)));
		sendMessageToGame(message);
	}

	void onCleanUp(ArrayList<IPlayer> players) {
		isDisconnected = true;

		players.forEach(IPlayer::onPlayerDisconnected);
		connector.cleanUpDummiesOnGameDummyCleanUp(players);
	}
}
