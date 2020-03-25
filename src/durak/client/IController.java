package durak.client;

import durak.game.graphics.Buttons;

public interface IController {

	void onButtonPressed(Buttons buttonIdx);
	
	void onCardClicked(int cardIdx);

	void onTableClicked(int pairIdx);
}
