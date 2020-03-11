package durak.game.graphics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class DeckView {
	private Image trump;
	private Image cover;

	public DeckView(String trump, ClassLoader classLoader) {
		String url = "assets/" + trump +".png";

		try {
			System.out.println(url);
			this.trump = ImageIO.read(classLoader.getResourceAsStream(url));
			cover = ImageIO.read(classLoader.getResourceAsStream("assets/cover.png"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void draw(Graphics g) {
		System.out.println(cover.getWidth(null));
		g.drawImage(cover, 0, 0,cover.getWidth(null), cover.getHeight(null), null);
	}

}
