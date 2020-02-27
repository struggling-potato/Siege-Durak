package durak.game;

public interface IGame {

    void throwCard(int playerId, Card card);

    void tossCard(int playerId, Card card);

    void beatCard(int playerId, Pair pair);

    void passTossing(int playerId);

    void giveUpDefence(int playerId);

    int registerPlayer(IPlayer player);

    void exitGame(int playerId);
}
