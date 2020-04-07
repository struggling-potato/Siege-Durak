package durak.game;

import java.util.List;

public interface IGame {

	void throwCard(int playerId, Card card); //походить

	void throwCards(int playerId, List<Card> cards); //походить

	void tossCard(int playerId, Card card); //подбросить

	void tossCards(int playerId, List<Card> cards); //подбросить

	void beatCard(int playerId, Pair pair); //отбить

    void passTossing(int playerId); //хватит

	void giveUpDefence(int playerId); //взять

	void registerPlayer(IPlayer player);

	void exitGame(int playerId);

	void startGame(int playerId); //начать игру

}
