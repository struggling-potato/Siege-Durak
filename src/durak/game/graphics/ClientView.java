package durak.game.graphics;

import durak.client.IController;
import durak.client.IView;
import durak.game.*;
import durak.game.graphics.utils.CardToImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientView extends JFrame implements IView {

	private final static HashMap<Buttons, String> BUTTON_MAP = new HashMap<>();
	static {
		BUTTON_MAP.put(Buttons.BUTTON_START, "Начать игру");
		BUTTON_MAP.put(Buttons.BUTTON_PASS, "Закончить ход");
		BUTTON_MAP.put(Buttons.BUTTON_GIVEUP, "Взять карты");
		BUTTON_MAP.put(Buttons.BUTTONS_EXITGAME, "Выйти из игры");
	}

	private final IController controller;
	private ClassLoader classLoader = getClass().getClassLoader();
	private JPanel      tablePanel;
	private JPanel      deckPanel;
	private JPanel      playerHandPanel;
	private JButton     actionButton;
	private JLabel      enemyPlayer1Label;
	private JLabel      enemyPlayer2Label;
	private JLabel      playerNameLabel;
	private JPanel      enemyPlayer1Panel;
	private JPanel      enemyPlayer2Panel;
	private JPanel      gameStatusBar;
	private JLabel      gameStateLabel;
	private JPanel pairsPanel;
	private Image       tableImage;
	private DeckView    deckView = new DeckView();
	private Hand        playerHand;
	private CardPanel[] cardPanels;
	private boolean     cardsState = false;

	public ClientView(IController controller) {
		super("Siege-Durak");
		setBounds(50, 0, 1600, 1050);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			tableImage = ImageIO.read(classLoader.getResourceAsStream("assets/table.jpg"));
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}

		gameStatusBar.setBackground(new Color(255, 255, 255, 153));
		add(tablePanel);
		setVisible(true);

		this.controller = controller;
	}

	public void setTrump(Card card) {
		deckView.setTrump(card, classLoader);
		repaint();
	}


	@Override
	public void drawTable(Table table) {
		Suit trumpSuit = table.getDeck().getTrump().getSuit();
		setTrump(new Card(trumpSuit, Rank.RANK_A));

		for(Pair pair: table.getThrownCard()) {
			PairPanel pairPanel = new PairPanel(pair);
			pairPanel.setMinimumSize(new Dimension(172, 330));
			pairPanel.setPreferredSize(new Dimension(172, 330));
			pairPanel.setOpaque(false);
			pairsPanel.add(pairPanel);
			pairPanel.revalidate();
			validate();
		}
		repaint();
	}

	@Override
	public void drawStringState(String state) {
		gameStateLabel.setText(state);
	}

	@Override
	public void drawHandOut(Hand hand) {
		playerHand = hand;
		if (playerHand == null) return;
		playerHandPanel.removeAll();
		ArrayList<Card> cards = playerHand.getCards();
		String url;

		for (int i = 0; i < cards.size(); i++) {
			url = CardToImage.getCartImageUrl(cards.get(i));

			CardPanel cardPanel = new CardPanel(url);
			cardPanel.setMinimumSize(new Dimension(172, 270));
			cardPanel.setPreferredSize(new Dimension(172, 270));
			cardPanel.setOpaque(false);
			playerHandPanel.add(cardPanel);
			playerHandPanel.revalidate();
			validate();
		}
		repaint();
	}

	@Override
	public void drawPlayers(ArrayList<Player> players) {
		if (players == null) return;
		Player player1 = null;
		Player player2 = null;
		try {
			player1 = players.get(0);
			player2 = players.get(1);
		} catch (IndexOutOfBoundsException ex) {
			System.out.println(ex.getMessage());
		}
		String url = "assets/cover.png";

		enemyPlayer1Panel.removeAll();
		enemyPlayer2Panel.removeAll();

		if (player1 != null) {
			for (int i = 0; i < player1.getHandSize(); i++) {
				CardPanel cardPanel = new CardPanel(url);
				cardPanel.setState(false);
				cardPanel.setSelected(true);
				cardPanel.setMinimumSize(new Dimension(172, 260));
				cardPanel.setPreferredSize(new Dimension(172, 260));
				cardPanel.setOpaque(false);
				enemyPlayer1Panel.add(cardPanel);
				enemyPlayer1Panel.revalidate();
				validate();
			}
		}
		if (player2 != null) {
			for (int i = 0; i < player2.getHandSize(); i++) {
				CardPanel cardPanel = new CardPanel(url);
				cardPanel.setState(false);
				cardPanel.setSelected(true);
				cardPanel.setMinimumSize(new Dimension(172, 260));
				cardPanel.setPreferredSize(new Dimension(172, 260));
				cardPanel.setOpaque(false);
				enemyPlayer2Panel.add(cardPanel);
				enemyPlayer2Panel.revalidate();
				validate();
			}
		}
		repaint();
	}

	@Override
	public void setButtonState(Buttons buttonId, boolean visible) {
		actionButton.setText(BUTTON_MAP.get(buttonId));
		actionButton.setVisible(visible);
	}

	@Override
	public void setCardsState(boolean clickable) {
		for (CardPanel cardPanel: cardPanels) {
			cardPanel.setState(clickable);
		}
	}

	private void createUIComponents() {
		deckPanel = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				if (deckView.isTrumpSet()) {
					deckView.draw(g);
				}
			}
		};

		playerHandPanel = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
		};

		tablePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				g.drawImage(tableImage, 0, 0, tablePanel.getWidth(), tablePanel.getHeight(), null);
			}
		};

		enemyPlayer1Panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
		};

		enemyPlayer2Panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
		};

		pairsPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
		};
	}
}
