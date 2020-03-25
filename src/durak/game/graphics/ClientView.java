package durak.game.graphics;

import durak.client.IView;
import durak.game.Hand;
import durak.game.Player;
import durak.game.Table;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientView implements IView {

	private final static HashMap<Buttons, String> BUTTON_MAP = new HashMap<>();
	static {
		BUTTON_MAP.put(Buttons.BUTTON_START, "Начать игру");
		BUTTON_MAP.put(Buttons.BUTTON_PASS, "Закончить ход");
		BUTTON_MAP.put(Buttons.BUTTON_GIVEUP, "Взять карты");
		BUTTON_MAP.put(Buttons.BUTTONS_EXITGAME, "Выйти из игры");
	}

	@Override
	public void drawTable(Table table) {

	}

	@Override
	public void drawStringState(String state) {

	}

	@Override
	public void drawHandOut(Hand hand) {

	}

	@Override
	public void drawPlayers(ArrayList<Player> players) {

	}

	@Override
	public void setButtonState(Buttons buttonId, boolean visible) {

	}

	@Override
	public void setCardsState(boolean clicked) {

	}
}
