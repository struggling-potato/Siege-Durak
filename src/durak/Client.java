package durak;

import durak.ai.Bot;
import durak.client.Controller;
import durak.communication.Connector;
import durak.communication.ServerInfo;
import durak.game.IGame;

import durak.game.*;
import durak.game.graphics.Buttons;
import durak.game.graphics.ClientView;

import java.util.ArrayList;

import java.io.IOException;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        Connector connector = new Connector();
        IGame     game      = connector.connectToServer(new ServerInfo("localhost", 1488)).get();

        Bot player1 = new Bot();
        player1.register(game);
//        Bot player2 = new Bot();
//        player2.register(game);
//
//        Thread.sleep(100000);

        Controller controller = new Controller(game);
//        ClientView clientView = ((ClientView) controller.getView());
//	    clientView.setTrump(new Card(Suit.SUIT_HEARTS, Rank.RANK_6));
//	    clientView.drawStringState("Новый стейт");
//	    Hand hand1 = new Hand();
//	    hand1.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_6));
//	    hand1.addCard(new Card(Suit.SUIT_CLOVERS, Rank.RANK_J));
//	    hand1.addCard(new Card(Suit.SUIT_PIKES, Rank.RANK_Q));
//	    clientView.drawHandOut(hand1);
//	    clientView.setButtonState(Buttons.BUTTON_GIVEUP, true);
//	    Player player1 = new Player();
//	    Player player2 = new Player();
//	    Hand hand2 = new Hand();
//	    Hand hand3 = new Hand();
//	    hand2.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_6));
//	    hand2.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_J));
//	    hand2.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_Q));
//	    hand3.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_K));
//	    hand3.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_A));
//	    hand3.addCard(new Card(Suit.SUIT_HEARTS, Rank.RANK_7));
//	    hand3.addCard(new Card(Suit.SUIT_PIKES, Rank.RANK_Q));
//	    player1.handOut(hand2);
//	    player2.handOut(hand3);
//	    ArrayList<Player> players = new ArrayList<>();
//	    players.add(player1);
//	    players.add(player2);
//	    clientView.drawPlayers(players);
//	    Deck deck = new Deck();
//	    hand1.addCard(deck.takeCardFromDeck());
//	    hand1.addCard(deck.takeCardFromDeck());
//	    hand1.addCard(deck.takeCardFromDeck());
//	    clientView.drawHandOut(hand1);
//	    Table           table       = new Table(deck);
////	    ArrayList<Pair> thrownCards = new ArrayList<>();
//	    table.getThrownCard().add(new Pair(new Card(Suit.SUIT_HEARTS, Rank.RANK_K), new Card(Suit.SUIT_PIKES, Rank.RANK_Q)));
//        table.getThrownCard().add(new Pair(new Card(Suit.SUIT_HEARTS, Rank.RANK_Q), new Card(Suit.SUIT_PIKES, Rank.RANK_A)));
////	    table.setThrownCards(thrownCards);  // Если хотите глянуть, то добавьте сеттер в класс Table или придумайте другой способ сформировать пары
//	    clientView.drawTable(table);
    }
}