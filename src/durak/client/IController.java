package durak.client;

public interface IController {

	void onButtonPressed(int buttonId);
	
	void onCardClicked(int pairIdx, boolean mode);

	void onTableClicked(int pairIdx, boolean mode);
}
