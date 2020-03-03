package durak.game;

public interface IPlayer {

    void handOut(Hand hand);

    void makeMove();

    void defendYourself();

    void tossCards();

    void currentTable(Table table);

    void endMove();
}
