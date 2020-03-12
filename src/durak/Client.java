package durak;

import durak.game.*;
import durak.game.graphics.ClientView;

public class Client {

    public static void main(String[] args) {
        ClientView clientView = new ClientView("Siege-Durak");
        clientView.setTrump(new Card(Suit.SUIT_HEARTS, Rank.RANK_6));
//        Game   game    = new Game();
//        Player player1 = new Player(game);
//        Player player2 = new Player(game);
//
//        Hand hand1 = new Hand();
//        Hand hand2 = new Hand();
//        hand1.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_6));
//        hand1.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_J));
//        hand1.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_Q));
//
//        hand2.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_K));
//        hand2.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_A));
//        hand2.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_7));
//
//        player1.handOut(hand1);
//        player2.handOut(hand2);
//
//        player1.printHand();
//        player2.printHand();
//
//        game.exitGame(player1.getId());
//        game.exitGame(player2.getId());
    }
}
