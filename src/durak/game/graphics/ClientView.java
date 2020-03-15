package durak.game.graphics;

import durak.game.Card;
import durak.game.Hand;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientView extends JFrame {

	ClassLoader classLoader = getClass().getClassLoader();
	private JPanel tablePanel;
	private JPanel deckPanel;
	private Image tableImage;
	private DeckView deckView = new DeckView();
	private HandView handView = new HandView();

	public ClientView(String windowName) {
		super(windowName);
		setBounds(150,150,1100,800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			tableImage = ImageIO.read(classLoader.getResourceAsStream("assets/table.jpg"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		deckPanel.setOpaque(false);
		add(tablePanel);
		setVisible(true);
	}

	public void setTrump(Card card) {
		System.out.println("clientView.setTrump called");
		deckView.setTrump(card, classLoader);
		repaint();
	}

	public void drawPlayerHand(Hand hand, int pos) {
		handView.setHand(hand);
		handView.setPosition(pos);


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

				System.out.println("deckView.isTrumpSet: " + deckView.isTrumpSet());
				if (deckView.isTrumpSet()) {
					deckView.draw(g);
				}
			}
		};
	}
}
