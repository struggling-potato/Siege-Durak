package durak.ai;

import durak.game.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class Bot implements IPlayer {
	/* AI class that sends commands to ClientController */
	private int   id;
	private IGame game;
	private Hand  hand = new Hand();

	private Deck                   checkDeck          = new Deck();
	private Table                  table              = new Table(checkDeck);
	private HashMap<Card, Integer> cardIntegerHashMap = new HashMap<>();
	private Trump                  trump              = new Trump();

	public Bot() {
		for (Card card : checkDeck.getCards()) {
			cardIntegerHashMap.put(card, cardIntegerHashMap.getOrDefault(card, 0) + 1);
		}
	}

	public void register(IGame game) {
		this.game = game;
		game.registerPlayer(this);
	}

	public int getId() {
		return id;
	}

	private int countSuit(Suit suit) {
		ArrayList<Card> OneSuit =
				hand.filter((card) -> card.getSuit().equals(suit));
		return OneSuit.size();
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

	@Override
	public void handOut(Hand hand) {
		System.out.println(id + " handOut " + hand);
		this.hand = hand;

	}

	@Override
	public void makeMove() {
		System.out.println(id + " makeMove");
		for (Card card : hand.getCards()) {
			cardIntegerHashMap.put(card, cardIntegerHashMap.getOrDefault(card, 0) + 1);
		}
		if (getInGameCards().size() <= 12) {
			findCardWithNoAnswer().ifPresent((card) -> {
				System.out.println(card);
				game.throwCard(id, card);
			});
		}
		else {
			ArrayList<Card> someCards = findMinPair();
			if (someCards.size() < 2) {
				Card card = findMin();
				System.out.println(card);
				game.throwCard(id, card);
			}
			else {
				for (Card card : someCards) {
					System.out.println(card);
					game.throwCard(id, card);
				}
			}
		}
	}

	private ArrayList<Card> getInGameCards() {
		ArrayList<Card> cards = new ArrayList<>();
		for (Card card : checkDeck.getCards()) {
			if (cardIntegerHashMap.get(card) < 2)
				cards.add(card);
		}
		return cards;
	}

	private Optional<Card> findCardWithNoAnswer() {
		Optional<Card> resultCard = Optional.empty();
		for (Card card : hand.getCards()) {
			if ((!findHypotheticalAnswer(card)) && (card.getSuit() != trump.getSuit())) {
				resultCard = Optional.of(card);
				return resultCard;
			}
		}
		return resultCard;
	}

	private Card findMin() {
		ArrayList<Card> EverySuits  = getMinEverySuit();
		Card            MinCard     = EverySuits.get(0);
		Card            MinSaveCard = MinCard;
		boolean         canSave     = false;
		Card            MinTrump    = MinCard;
		if ((countSuit(MinSaveCard.getSuit()) > 1) && (MinSaveCard.getRank().compareTo(Rank.RANK_9) > 0))
			canSave = true;
		for (Card card : EverySuits) {
			if (card.getSuit() == trump.getSuit())
				MinTrump = card;
			else if ((countSuit(card.getSuit()) > 1) && (card.getRank().compareTo(MinSaveCard.getRank()) < 0) &&
			         (card.getRank().compareTo(Rank.RANK_9) > 0)) {
				MinSaveCard = card;
				canSave = true;
			}
			else if (card.getRank().compareTo(MinCard.getRank()) < 0)
				MinCard = card;
		}
		if ((canSave) && (MinCard.getRank().compareTo(Rank.RANK_9) > 0))
			return MinSaveCard;
		if (MinCard.getSuit() == MinTrump.getSuit())
			return MinTrump;
		return MinCard;
	}

	private boolean findHypotheticalAnswer(Card attackCard) {
		for (Card card : getInGameCards()) {
			if ((card.getSuit() == attackCard.getSuit()) && (card.getRank().compareTo(attackCard.getRank()) > 0))
				return true;
		}
		return false;
	}

	@Override
	public void defendYourself() {
		System.out.println(id + " defendYourself");
		boolean isToss = false;
		for (Pair pair : table.getThrownCard()) {
			if (pair.isOpen()) {
				var opt = findMinAnswer(pair.getBottomCard());
				if (opt.isPresent())
					isToss = true;
					opt.ifPresent((card) -> {
							System.out.println(card);
							game.beatCard(id, new Pair(pair.getBottomCard(), card));
						});
			}
		}
		if (!isToss) {
			System.out.println(id + " Take it");
			game.giveUpDefence(id);
		}
	}

	private Optional<Card> findMinAnswer(Card ThrownCard) {
		Optional<Card> MinCard = Optional.empty();
		for (Card card : hand.getCards()) {
			if ((card.getRank().compareTo(ThrownCard.getRank()) > 0) && (card.getSuit() == ThrownCard.getSuit())) {
				MinCard = Optional.of(card);
			}
		}
		if ((MinCard.isEmpty()) && (ThrownCard.getSuit() != trump.getSuit())) {
			ArrayList<Card> trumps =
					hand.filter((card) -> card.getSuit().equals(trump.getSuit()));
			if (trumps.isEmpty())
				return MinCard;
			trumps.sort(null);
			MinCard = Optional.of(trumps.get(0));
		}
		return MinCard;
	}

	@Override
	public void tossCards() {
		System.out.println(id + " tossCards");
		boolean isToss = false;
		for (Pair pair : table.getThrownCard()) {
			for (Card thrownCard : pair.getCards()) {
				var opt = findMinToss(thrownCard);
				if (opt.isPresent())
					isToss = true;
				opt.ifPresent((card) -> {
					System.out.println(card);
					game.tossCard(id, card);
				});
			}
		}
		if (!isToss) {
			System.out.println("{Nothing to toss}");
			game.passTossing(id);
		}
	}

	private Optional<Card> findMinToss(Card TossCard) {
		Optional<Card> MinCard = Optional.empty();
		for (Card card : hand.getCards()) {
			if ((card.getRank().compareTo(TossCard.getRank()) == 0) &&
			    (card.getSuit() != trump.getSuit())) {
				MinCard = Optional.of(card);
			}
		}
		return MinCard;
	}

	@Override
	public void currentTable(Table table) {
		System.out.println(id + " currentTable: " + table);
		this.table = table;
		this.trump = table.getDeck().getTrump();
		for (Pair pair : table.getDump().getCards()) {
			for (Card card : pair.getCards()) {
				cardIntegerHashMap.put(card, cardIntegerHashMap.getOrDefault(card, 0) + 1);
			}
		}
	}

	@Override
	public void onPlayerRegistered(int playerId) {
		id = playerId;
		System.out.println(id + " onPlayerRegistered");
	}

	@Override
	public void endMove() {
		System.out.println(id + " Turn is over");
	}

	@Override
	public void onGameStarted() {
		System.out.println(id + " onGameStarted");
	}

	@Override
	public void onGameFinished(int loserId) {
		System.out.println(id + " onGameFinished " + loserId);
	}

	@Override
	public void currentOpponentsList(ArrayList<Player> opponents) {
		System.out.println(id + " currentOpponentsList " + opponents);
	}

	@Override
	public void onPlayerDisconnected() {
		System.out.println(id + " onPlayerDisconnected");

	}
}
