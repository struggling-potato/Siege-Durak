package durak.client;

import durak.game.*;
import durak.game.graphics.ClientView;

import java.util.ArrayList;
import java.util.HashMap;

public class Controller implements IPlayer, IController {

	private final Player      player;
	private final ClientView  view;
	private       Game        game;
	private       PlayerState currentPlayerState;
	private       Table       table;

	private final static HashMap<Integer, String> BUTTON_MAP = new HashMap<>() {
		{
			BUTTON_MAP.put(0, "Начать игру");

			//TODO: Виталий, поменяй название)))
			BUTTON_MAP.put(1, "Хватит");


		}
	};

	private Controller(Game game) {
		player = new Player();
		this.game = game;
		view = new ClientView();
		currentPlayerState = PlayerState.STATE_INVALID;
	}

	@Override
	public void handOut(Hand hand) {
		player.handOut(hand);
		view.drawHandOut(hand);
	}

	@Override
	public void makeMove() {
		currentPlayerState = PlayerState.STATE_MOVE;
		view.drawStringState("Ваш ход");
		view.setCardsState(true);
		view.setButtonState(1, BUTTON_MAP.get(1), true);
	}

	@Override
	public void defendYourself() {
		currentPlayerState = PlayerState.STATE_DEFEND;
		view.drawStringState("Отбивайтесь");
		view.setCardsState(true);

	}

	@Override
	public void tossCards() {
	}

	@Override
	public void currentTable(Table table) {
		this.table = table;
		view.drawTable(table);
		game.getOpponentsList(player.getId());
	}

	@Override
	public void onPlayerRegistered(int playerId) {
		player.onPlayerRegistered(playerId);
		view.setButtonState(0, BUTTON_MAP.get(0), true);
	}

	@Override
	public void endMove() {

	}

	@Override
	public void onGameStarted() {
		currentPlayerState = PlayerState.STATE_WAIT;
		view.drawStringState("Ожидание хода");
	}

	@Override
	public void onGameFinished() {

	}

	@Override
	public void opponentsList(ArrayList<Player> opponents) {
		view.drawPlayers(opponents);
	}

	@Override
	public void onButtonPressed(int buttonId) {
		switch (buttonId) {
			case 0:
				view.setButtonState(0, BUTTON_MAP.get(0), false);
				view.drawStringState("Ожидание противников");
				game.startGame(player.getId());
				break;
			case 1:
				view.setButtonState(1, BUTTON_MAP.get(1), false);
				game.passTossing(player.getId());
				view.setCardsState(false);
		}
	}

	private Card chosenCard;

	@Override
	public void onCardClicked(int cardIdx) {
		if (currentPlayerState == PlayerState.STATE_MOVE) {
			game.throwCard(player.getId(), player.getCard(cardIdx));
			view.drawStringState("Ожидание противника");
		}
		if (currentPlayerState == PlayerState.STATE_TOSS) {
			chosenCard = new Card(player.getCard(cardIdx));
			view.setCardsState(false);
		}
	}

	@Override
	public void onTableClicked(int pairIdx) {
		Pair chosenPair= table.getThrownCard().get(pairIdx);
		if (chosenPair.getCards().size() == 1) {
			chosenPair.getCards().add(chosenCard);
			game.beatCard(player.getId(),chosenPair);
		}

	}
}
