package durak.game;

import java.util.ArrayList;

public interface IPlayer {

    void handOut(Hand hand); //твои карты

    void makeMove(); //ходи

    void defendYourself(); //бейся

    void tossCards(); //подкидывай

    void currentTable(Table table); //текущий стол

    void onPlayerRegistered(int playerId);

    void endMove(); //закончить ход

    void onGameStarted(); //игра начата-играй

    void onGameFinished(int loserId); //игра окончена id дурака или -1 если ничья

    void currentOpponentsList(ArrayList<Player> opponents); //список проивников

    void onPlayerDisconnected();
}
