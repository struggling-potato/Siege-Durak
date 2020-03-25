package durak.client;

import durak.game.*;
import durak.game.graphics.Buttons;
import durak.game.graphics.ClientView;

import java.util.ArrayList;
import java.util.HashMap;

public class Controller implements IPlayer, IController {

	private final Player            player;
	private final ClientView        view;
	private       Game              game;
	private       PlayerState       currentPlayerState;
	private       Table             table;
	private       ArrayList<Player> opponents;

	private Controller(Game game) {
		player = new Player();
		this.game = game;
		view = new ClientView();
		opponents = new ArrayList<>();
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
		view.setButtonState(Buttons.BUTTON_PASS, true);
	}

	@Override
	public void defendYourself() {
		currentPlayerState = PlayerState.STATE_DEFEND;
		view.drawStringState("Отбивайтесь");
		view.setCardsState(true);
	}

	@Override
	public void tossCards() {
		currentPlayerState = PlayerState.STATE_TOSS;
		view.drawStringState("Подкидывайте");
		view.setCardsState(true);
	}

	@Override
	public void currentTable(Table table) {
		this.table = table;
		view.drawTable(table);
	}

	@Override
	public void onPlayerRegistered(int playerId) {
		player.onPlayerRegistered(playerId);
		view.setButtonState(Buttons.BUTTON_START, true);
	}

	@Override
	public void endMove() {
		currentPlayerState=PlayerState.STATE_WAIT;
		view.setCardsState(false);
		view.drawStringState("Ожидание хода");

	}

	@Override
	public void onGameStarted() {
		currentPlayerState = PlayerState.STATE_WAIT;
		view.drawStringState("Ожидание хода");
	}

	@Override
	public void onGameFinished() {
		currentPlayerState=PlayerState.STATE_INVALID;
		view.drawStringState("Вы вышли из игры");
	}

	@Override
	public void currentOpponentsList(ArrayList<Player> opponents) {
		this.opponents = opponents;
		view.drawPlayers(opponents);
	}

	@Override
	public void onButtonPressed(Buttons buttonId) {
		switch (buttonId) {
			case BUTTON_START:
				view.setButtonState(Buttons.BUTTON_START, false);
				view.drawStringState("Ожидание противников");
				game.startGame(player.getId());
				break;
			case BUTTON_PASS:
				view.setButtonState(Buttons.BUTTON_PASS, false);
				game.passTossing(player.getId());
				view.setCardsState(false);
				break;
			case BUTTON_GIVEUP:
				view.setButtonState(Buttons.BUTTON_GIVEUP, false);
				game.giveUpDefence(player.getId());
				view.setCardsState(false);
				break;
			case BUTTONS_EXITGAME:
				view.setButtonState(Buttons.BUTTONS_EXITGAME, false);
				game.exitGame(player.getId());
		}
	}

	private Card chosenCard;

	@Override
	public void onCardClicked(int cardIdx) {
		if (currentPlayerState == PlayerState.STATE_MOVE) {
			game.throwCard(player.getId(), player.getCard(cardIdx));
			view.drawStringState("Ожидание противника");
		}
		if (currentPlayerState == PlayerState.STATE_DEFEND) {
			chosenCard = new Card(player.getCard(cardIdx));
			view.setCardsState(false);
		}
		if(currentPlayerState==PlayerState.STATE_TOSS){
			game.tossCard(player.getId(), player.getCard(cardIdx));
			view.drawStringState("Ожидание противника");
		}
	}

	@Override
	public void onTableClicked(int pairIdx) {
		Pair chosenPair = table.getThrownCard().get(pairIdx);
		if (chosenPair.isOpen()) {
			game.beatCard(player.getId(), new Pair(chosenPair.getBottomCard(), chosenCard));
		}
	}
}
