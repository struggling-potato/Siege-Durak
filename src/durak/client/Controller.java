package durak.client;

import durak.game.*;
import durak.game.graphics.Buttons;
import durak.game.graphics.ClientView;

import java.util.ArrayList;
import java.util.List;

public class Controller implements IPlayer, IController {

	private final Player       player    = new Player();
	private final IView        view      = new ClientView(this);
	private       List<Player> opponents = new ArrayList<>();
	private       IGame        game;
	private       PlayerState  currentPlayerState;
	private       Table        table;

	public Controller(IGame game) {
		this.game = game;
		currentPlayerState = PlayerState.STATE_INVALID;
		game.registerPlayer(this);
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
//		view.setButtonState(Buttons.BUTTON_PASS, true);
	}

	@Override
	public void defendYourself() {
		System.out.println(player.getId() + " defendYourself");
		currentPlayerState = PlayerState.STATE_DEFEND;
		view.drawStringState("Отбивайтесь");
		view.setCardsState(true);
		view.setButtonState(Buttons.BUTTON_GIVEUP, true);
	}

	@Override
	public void tossCards() {
		currentPlayerState = PlayerState.STATE_TOSS;
		view.drawStringState("Подкидывайте");
		view.setCardsState(true);
		view.setButtonState(Buttons.BUTTON_PASS, true);
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
		currentPlayerState = PlayerState.STATE_WAIT;
		view.setCardsState(false);
		view.drawStringState("Ожидание хода");
		view.setButtonState(Buttons.BUTTON_PASS, false);

	}

	@Override
	public void onGameStarted() {
		currentPlayerState = PlayerState.STATE_WAIT;
		view.drawStringState("Ожидание хода");
	}

	public IView getView() {
		return view;
	}

	@Override
	public void onGameFinished(int loserId) {
		if (loserId == -1) {
			view.drawStringState("Игра окончена. Ничья!");
		}
		if (player.getId() == loserId) {
			view.drawStringState("Игра окончена. Вы проиграли!");
		}
		else {
			view.drawStringState("Игра окончена. Вы победили!");
		}
	}

	@Override
	public void currentOpponentsList(ArrayList<Player> opponents) {
		this.opponents = opponents;
		view.drawPlayers(opponents);
	}

	@Override
	public void onPlayerDisconnected() {
		currentPlayerState = PlayerState.STATE_INVALID;
		view.drawStringState("Вы вышли из игры");
	}

	@Override
	public void onButtonPressed(Buttons buttonId) {
		switch (buttonId) {
			case BUTTON_START:
				System.out.println("start pressed");
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
		System.out.println(player.getId() + " onCardClicked " + cardIdx + " " + currentPlayerState);
		if (currentPlayerState == PlayerState.STATE_MOVE) {
			game.throwCard(player.getId(), player.getCard(cardIdx));
			view.drawStringState("Ожидание противника");
		}
		else if (currentPlayerState == PlayerState.STATE_DEFEND) {
			chosenCard = new Card(player.getCard(cardIdx));
			view.setCardsState(false);
		}
		else if (currentPlayerState == PlayerState.STATE_TOSS) {
			game.tossCard(player.getId(), player.getCard(cardIdx));
			view.drawStringState("Ожидание противника");
		}
	}

	@Override
	public void onCardsClicked(List<Integer> cardsIdx) {
		if (currentPlayerState == PlayerState.STATE_MOVE) {
			List<Card> cards=new ArrayList<>();
			for (Integer cardIdx: cardsIdx){
				cards.add(player.getCard(cardIdx));
			}
			game.throwCards(player.getId(), cards);
			view.drawStringState("Ожидание противника");
		}
		else if (currentPlayerState == PlayerState.STATE_TOSS) {
			List<Card> cards=new ArrayList<>();
			for (Integer cardIdx: cardsIdx){
				cards.add(player.getCard(cardIdx));
			}
			game.tossCards(player.getId(), cards);
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
