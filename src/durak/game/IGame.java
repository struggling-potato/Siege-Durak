package durak.game;

public interface IGame {

	void throwCard(int playerId, Card card); //походить

	void tossCard(int playerId, Card card); //подбросить

	void beatCard(int playerId, Pair pair); //отбить

    void passTossing(int playerId); //хватит

	void giveUpDefence(int playerId); //взять

	void registerPlayer(IPlayer player);

	void exitGame(int playerId);

	void startGame(int playerId); //начать игру

	void getOpponentsList(int playerId); //получить список противников

}
