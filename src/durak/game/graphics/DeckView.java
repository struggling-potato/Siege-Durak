package durak.game.graphics;

import durak.game.Card;
import durak.game.graphics.utils.CardToImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DeckView {
	private BufferedImage trump;
	private Image         cover;

	public DeckView() {}

	public void setTrump(Card trump, ClassLoader classLoader) {

		String url = CardToImage.getCartImageUrl(trump);

		try {
			this.trump = ImageIO.read(classLoader.getResourceAsStream(url));

			cover = ImageIO.read(classLoader.getResourceAsStream("assets/cover.png"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public boolean isTrumpSet() {
		return trump != null && cover != null;
	}

	public void draw(Graphics g) {
		double rotation = Math.toRadians(90);
		double location = (double) trump.getHeight() / 2;
		AffineTransform tx = AffineTransform.getRotateInstance(rotation, location, location);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

		g.drawImage(op.filter(trump, null), 0, cover.getWidth(null)/4, null);

		int offsetX = cover.getHeight(null) - cover.getWidth(null);
		g.drawImage(cover, offsetX, 0, null);
	}

}
