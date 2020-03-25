package durak.game;

public interface IPlayer {

    void handOut(Hand hand); //твои карты

    void makeMove(); //ходи

    void defendYourself(); //бейся

    void tossCards(); //подкидывай

    void currentTable(Table table); //текущий стол

    void onPlayerRegistered(int playerId);

    void endMove(); //закончить ход

    void onGameStarted(); //игра начата-играй

    void onGameFinished(); //игра окончена

    void onPlayerDisconnected();
}
