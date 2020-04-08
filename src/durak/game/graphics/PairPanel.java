package durak.game.graphics;

import durak.game.Card;
import durak.game.Pair;
import durak.game.graphics.utils.CardToImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class PairPanel extends JPanel {
	private Image botImage, topImage;

	PairPanel() { }

	PairPanel(Pair pair) {
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
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(botImage, 0, 0, null);
		g.drawImage(topImage, 0, 70, null);
	}
}
