package durak.client;

public interface IController {

	void buttonPressed(int buttonId);

	void clickTable(int pairIdx, boolean mode);

	void clickCard(int pairIdx, boolean mode);
}