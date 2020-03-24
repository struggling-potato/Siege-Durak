package durak.client;

public interface IController {

	void onButtonPressed(int buttonId);
	
	void onCardClicked(int cardIdx);

	void onTableClicked(int pairIdx);
}
