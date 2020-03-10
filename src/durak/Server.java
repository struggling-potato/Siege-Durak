package durak;

import durak.game.*;

public class Server {

	public static void main(String[] args) throws InterruptedException {
		Game game = new Game();

		Player player1 = new Player(game);
		Player player2 = new Player(game);

		Deck deck = new Deck();
		deck.generateDeck();

		System.out.println("Trump in this game:");
		System.out.println(deck.getTrump().getSuit());

		Hand hand1 = new Hand();
		Hand hand2 = new Hand();

		for (int i = 0; i < 6; i++) {
			hand1.addCard(deck.getCurrentCardFromDeck());
		}

		for (int i = 0; i < 6; i++) {
			hand2.addCard(deck.getCurrentCardFromDeck());
		}

		player1.handOut(hand1);
		player2.handOut(hand2);

		System.out.println("0 player cards from deck: ");
		player1.printHand();

		System.out.println("1 player cards from deck: ");
		player2.printHand();

		game.exitGame(player1.getId());
		game.exitGame(player2.getId());
	}
}
