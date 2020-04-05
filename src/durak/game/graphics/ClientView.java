package durak.game.graphics;

import durak.client.IView;
import durak.game.Card;

import durak.game.Hand;
import durak.game.Player;
import durak.game.Table;
import durak.game.graphics.utils.CardToImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import java.util.ArrayList;

public class ClientView extends JFrame implements IView {

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
	private Image       tableImage;
	private DeckView    deckView = new DeckView();
	private HandView    playerHandView = new HandView();
	private Hand        playerHand;
	private CardPanel[] cardPanels;
	private boolean     cardsState = false;

	public ClientView(String windowName) {
		super(windowName);
		setBounds(50,0,1600,1050);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			tableImage = ImageIO.read(classLoader.getResourceAsStream("assets/table.jpg"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		gameStatusBar.setBackground(new Color(255, 255, 255, 153));
		add(tablePanel);
		setVisible(true);
	}

	public void setTrump(Card card) {

//		System.out.println("clientView.setTrump called");
		deckView.setTrump(card, classLoader);
		repaint();
	}
//
//	public void drawPlayerHand(Hand hand, int pos) {
//
//	}

	@Override
	public void drawTable(Table table) {

	}

	@Override
	public void drawStringState(String state) {
		gameStateLabel.setText(state);
	}

	@Override
	public void drawHandOut(Hand hand) {
		playerHand = hand;
		repaint();
	}

	@Override
	public void drawPlayers(ArrayList<Player> players) {

	}

	@Override
	public void setButtonState(Buttons buttonId, boolean visible) {

	}

	@Override
	public void setCardsState(boolean clickable) {

	}

	private void createUIComponents() {
		tablePanel = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				g.drawImage(tableImage, 0, 0, tablePanel.getWidth(), tablePanel.getHeight(), null);
			}
		};

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

				if (playerHand == null) return;
				playerHandPanel.removeAll();
				ArrayList<Card> cards = playerHand.getCards();
//				cardPanels = new JPanel[cards.size()];
				String url;
				int offsetX = 0;

				for (int i = 0; i < cards.size(); i++) {
					url = CardToImage.getCartImageUrl(cards.get(i));

//					handPanels[i].setMinimumSize(new Dimension(172, 260));
//					handPanels[i].setPreferredSize(new Dimension(172, 260));
//					handPanels[i].setOpaque(false);
//					playerHandPanel.add(handPanels[i]);
//					g.drawImage(cardImage, offsetX, 10, null);
					offsetX += 175;
//					playerHandPanel.add(new JButton() {
//						@Override
//						protected void paintComponent(Graphics g) {
//							super.paintComponent(g);
//
//							this.setMinimumSize(new Dimension(172, 260));
//							g.drawImage(cardImage, 0, 10, null);
//						}
//					});
					playerHandPanel.revalidate();
					validate();
				}

			}
		};
	}
}
