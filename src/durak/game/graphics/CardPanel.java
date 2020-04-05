package durak.game.graphics;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class CardPanel extends JPanel {
	private  ClassLoader classLoader = getClass().getClassLoader();
	private Image cardImage;
	private boolean selected;

	CardPanel(String url) {
		selected = false;
		try {
			cardImage = ImageIO.read(classLoader.getResourceAsStream(url));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int selectedOffset = 10;
		if (selected) selectedOffset = 0;
		g.drawImage(cardImage, 0, selectedOffset, null);
	}
}
