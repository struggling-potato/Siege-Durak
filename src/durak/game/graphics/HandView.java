package durak.game.graphics;

import durak.game.Card;
import durak.game.Hand;
import durak.game.graphics.utils.CardToImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class HandView {
	private Hand hand;

	public HandView() {}

	public boolean isSet() {
		return hand != null;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public void draw(Graphics g, ClassLoader classLoader, int tableWidth, int tableHeight) {
		ArrayList<Card> cards     = hand.getCards();
		int             offsetX   = tableWidth/2 - 175*((cards.size() + cards.size()%2)/2), offsetY = tableHeight - 260;
		String          url;
		Image           cardImage = null;

		for (int i = 0; i < cards.size(); i++) {
			url = CardToImage.getCartImageUrl(cards.get(i));

			System.out.println("Coordinates - " + "X: " + offsetX + " Y: " + offsetY);

			try {
				cardImage = ImageIO.read(classLoader.getResourceAsStream(url));
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			g.drawImage(cardImage,offsetX,offsetY,null);

			offsetX += 175;
		}
	}
}
