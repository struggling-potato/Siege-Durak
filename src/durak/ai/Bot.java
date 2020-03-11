package durak.ai;

import durak.game.*;

import java.util.ArrayList;
import java.util.Optional;

public class Bot implements IPlayer {
	/* AI class that sends commands to ClientController */
	private int   id;
	private IGame game;
	private Hand  hand = new Hand();

	private Table table = new Table();

	public Bot(IGame game) {
		this.game = game;
		game.registerPlayer(this);
		Trump trump = new Trump();
		trump.setSuit(Suit.SUIT_CLOVERS);
		Deck deck = new Deck();
		deck.setTrump(trump);
		table.setDeck(deck);
	}

	public int getId() {
		return id;
	}

	public void printHand() {
		for (Card card : hand.getCards()) {
			System.out.println(card);
		}
	}

	private int countSuit(Suit suit) {
		ArrayList<Card> OneSuit =
				hand.filter((card) -> card.getSuit().equals(suit));
		return OneSuit.size();
	}

	private int countRank(Rank rank) {
		ArrayList<Card> OneRank =
				hand.filter((card) -> card.getRank().equals(rank));
		return OneRank.size();
	}

	private ArrayList<Card> getMinEverySuit() {
		ArrayList<Card> MyHand    = hand.getCards();
		ArrayList<Card> EverySuit = new ArrayList<>();
		MyHand.sort(null);
		EverySuit.add(MyHand.get(0));
		for (int i = 0; i < MyHand.size() - 1; i++) {
			if (MyHand.get(i).getSuit() != MyHand.get(i + 1).getSuit())
				EverySuit.add(MyHand.get(i + 1));
		}
		return EverySuit;
	}

	private ArrayList<Card> findMinPair() {
		ArrayList<Card> OneRankCards = new ArrayList<>();
		ArrayList<Card> EndRankCards = new ArrayList<>();
		for (Card firstCard : hand.getCards()) {
			OneRankCards.add(firstCard);
			for (Card secondCard : hand.getCards()) {
				if ((firstCard.getRank().compareTo(secondCard.getRank()) == 0) && (firstCard != secondCard)) {
					OneRankCards.add(secondCard);
				}
			}
			if ((OneRankCards.size() >= 2) && ((OneRankCards.get(0).getRank().compareTo(Rank.RANK_Q)) < 0))
				if (EndRankCards.size() == 0) {
					EndRankCards.addAll(OneRankCards);
				}
				else if (OneRankCards.get(0).getRank().compareTo(EndRankCards.get(0).getRank()) < 0) {
					EndRankCards.clear();
					EndRankCards.addAll(OneRankCards);
				}
			OneRankCards.clear();
		}
		return EndRankCards;
	}

	private Card findMin() {
		ArrayList<Card> EverySuits  = getMinEverySuit();
		Card            MinCard     = EverySuits.get(0);
		Card            MinSaveCard = MinCard;
		boolean         canSave     = false;
		Card            MinTrump    = MinCard;
		if (countSuit(MinSaveCard.getSuit()) > 1)
			canSave = true;
		for (Card card : EverySuits) {
			if (card.getRank().compareTo(MinCard.getRank()) < 0) {
				if (card.getSuit() == table.getDeck().getTrump().getSuit())
					MinTrump = card;
				else if ((countSuit(card.getSuit()) > 1) && (card.getRank().compareTo(MinSaveCard.getRank()) < 0)) {
					MinSaveCard = card;
					canSave = true;
				}
				else
					MinCard = card;
			}
		}
		if (canSave)
			return MinSaveCard;
		if (MinCard.getSuit() == MinTrump.getSuit())
			return MinTrump;
		return MinCard;
	}


	private Optional<Card> findMinToss(Card TossCard) {
		Optional<Card> MinCard = Optional.empty();
		for (Card card : hand.getCards()) {
			if ((card.getRank().compareTo(TossCard.getRank()) == 0) &&
			    (card.getSuit() != table.getDeck().getTrump().getSuit())) {
				MinCard = Optional.of(card);
			}
		}
		return MinCard;
	}

	private Optional<Card> findMinAnswer(Card ThrownCard) {
		Optional<Card> MinCard = Optional.empty();
		for (Card card : hand.getCards()) {
			if ((card.getRank().compareTo(ThrownCard.getRank()) > 0) && (card.getSuit() == ThrownCard.getSuit())) {
				MinCard = Optional.of(card);
			}
		}
		if ((MinCard.isEmpty()) && (ThrownCard.getSuit() != table.getDeck().getTrump().getSuit())) {
			ArrayList<Card> trumps =
					hand.filter((card) -> card.getSuit().equals(table.getDeck().getTrump().getSuit()));
			if (trumps.isEmpty())
				return MinCard;
			trumps.sort(null);
			MinCard = Optional.of(trumps.get(0));
		}
		return MinCard;
	}


	@Override
	public void handOut(Hand hand) {
		this.hand = hand;
	}

	@Override
	public void makeMove() {
		ArrayList<Card> Pair = findMinPair();
		if (Pair.size() < 2) {
			Card card = findMin();
			System.out.println(card);
			game.throwCard(id, card);
		}
		else {
			for (Card card : Pair) {
				System.out.println(card);
				game.throwCard(id, card);
			}
		}
	}

	@Override
	public void defendYourself() {
		findMinAnswer(new Card(Suit.SUIT_TILES, Rank.RANK_10))
				.ifPresentOrElse((card) -> {
					System.out.println(card);
					game.beatCard(id, new Pair(new Card(Suit.SUIT_TILES, Rank.RANK_10), card));
				}, () -> {
					System.out.println("Take it");
					game.giveUpDefence(id);
				});
	}

	@Override
	public void tossCards() {
		findMinToss(new Card(Suit.SUIT_PIKES, Rank.RANK_8))
				.ifPresentOrElse((card) -> {
					System.out.println(card);
					game.tossCard(id, card);
				}, () -> {
					System.out.println("{Nothing to toss}");
					game.passTossing(id);
				});
	}

	@Override
	public void currentTable(Table table) {
		this.table = table;
	}

	@Override
	public void onPlayerRegistered(int playerId) {
		id = playerId;
	}

	@Override
	public void endMove() {

	}
}
