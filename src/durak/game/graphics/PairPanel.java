package durak.game.graphics;

import durak.client.IController;
import durak.game.Card;
import durak.game.Pair;
import durak.game.graphics.utils.CardToImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

public class PairPanel extends JPanel {
	private Image botImage, topImage;
	private IController controller;
	private int pairIdx;
	private MouseAdapter onClicked = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			controller.onTableClicked(pairIdx);
		}
	};

	PairPanel() { }

	PairPanel(Pair pair, IController controller, int pairIdx) {
		ClassLoader classLoader = getClass().getClassLoader();
		ArrayList<Card> cards = pair.getCards();
		String botUrl = CardToImage.getCartImageUrl(cards.get(0));
		String topUrl = null;
		if (!pair.isOpen()) topUrl = CardToImage.getCartImageUrl(cards.get(1));

		try {
			botImage = ImageIO.read(classLoader.getResourceAsStream(botUrl));
			if (topUrl != null) topImage = ImageIO.read(classLoader.getResourceAsStream(topUrl));

		} catch (IOException ex) {
			ex.printStackTrace();
			System.err.println(botUrl);
		}

		this.controller = controller;
		this.pairIdx = pairIdx;
		this.addMouseListener(onClicked);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(botImage, 0, 0, null);
		g.drawImage(topImage, 0, 70, null);
	}
}
