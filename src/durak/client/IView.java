package durak.client;

import durak.game.Hand;
import durak.game.Player;
import durak.game.Table;

import java.util.ArrayList;

public interface IView {

	void drawTable(Table table); //стол

	void drawStringState(String state); //строка состояния

	void drawHandOut(Hand hand); //твои карты

	void drawPlayers(ArrayList<Player> players); //противники

	void setButtonState(int buttonId, String title, boolean visible);
}
