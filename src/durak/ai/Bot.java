package durak.ai;

import durak.game.*;

public class Bot implements IPlayer {
	/* AI class that sends commands to ClientController */
	private int id;
	private IGame game;
	private Hand  hand = new Hand();
	private Trump trump = new Trump();

	public Bot (IGame game) {
		this.game = game;
		id        = game.registerPlayer(this);
		trump.setSuit(Suit.SUIT_PIKES);
	}

	public int getId() {
		return id;
	}

	public void printHand() {
		for (Card card : hand.getCards()) {
			System.out.println("{" + card.getSuit() + ":" + card.getRank() + "}");
		}
	}

	private Card findMin(){
		Card MinCard = hand.getCards().get(0);
		for ( Card card : hand.getCards()){
			if ((card.getRank().compareTo(MinCard.getRank()) < 0) && (card.getSuit()!= trump.getSuit())) {
				MinCard=card;
			}
		}
		return MinCard;
	}
	private Card findMinAnswer(Card ThrownCard){
		Card MinCard = ThrownCard;
		boolean haveAnswer=false;
		for ( Card card : hand.getCards()){
			if ((card.getRank().compareTo(ThrownCard.getRank())> 0)&&(card.getSuit()==ThrownCard.getSuit())){
				MinCard=card;
				haveAnswer=true;
			}
		}
		if ((haveAnswer == false)&&(ThrownCard.getSuit()!=trump.getSuit())) {
			for (Card card : hand.getCards()) {
				if (card.getSuit() == trump.getSuit()) {
					MinCard = card;
				}
			}
		}
		return MinCard;
	}
	private Card AttackRule1() {
		Card AttackCard=findMin();
		return AttackCard;
	}
	private Card DefendRule1(Card card) {
		Card DefendCard=findMinAnswer(card);
		return DefendCard;
	}
	private Card ThrowRule1() {
		Card ThrownCard=findMin();
		return ThrownCard;
	}


	@Override
	public void handOut(Hand hand) {
		this.hand=hand;
	}

	@Override
	public void makeMove() {
		Card card= AttackRule1();
		System.out.println("{" + card.getSuit() + ":" + card.getRank() + "}");
	}

	@Override
	public void defendYourself() {
		Card card= DefendRule1(new Card(Suit.SUIT_TILES,Rank.RANK_10));
		System.out.println("{" + card.getSuit() + ":" + card.getRank() + "}");
	}

	@Override
	public void tossCards() {
		Card card= ThrowRule1();
		System.out.println("{" + card.getSuit() + ":" + card.getRank() + "}");
	}

	@Override
	public void currentTable(Table table) {
		//trump=table.getTrump();

	}

	@Override
	public void endMove() {

	}

}
