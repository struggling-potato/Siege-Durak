package durak.client;

import durak.game.graphics.Buttons;

import java.util.List;

public interface IController {

	void onButtonPressed(Buttons buttonIdx);
	
	void onCardClicked(int cardIdx);

	void onCardsClicked(List<Integer> cardsIdx);

	void onTableClicked(int pairIdx);
}
