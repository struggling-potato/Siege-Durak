package durak.client;

public interface IController {

	void onButtonPressed(int buttonId);

	void onClickTable(int pairIdx, boolean mode);

	void onClickCard(int pairIdx, boolean mode);
}