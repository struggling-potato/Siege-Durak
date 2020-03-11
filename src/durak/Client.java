package durak;

import durak.game.*;

public class Client {

    public static void main(String[] args) {
        Game   game    = new Game();
        Player player1 = new Player();
        Player player2 = new Player();

        player1.register(game);
        player2.register(game);

        Hand hand1 = new Hand();
        Hand hand2 = new Hand();
        hand1.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_6));
        hand1.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_J));
        hand1.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_Q));

        hand2.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_K));
        hand2.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_A));
        hand2.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_7));

        player1.handOut(hand1);
        player2.handOut(hand2);

        player1.printHand();
        player2.printHand();

        game.exitGame(player1.getId());
        game.exitGame(player2.getId());
    }
}
