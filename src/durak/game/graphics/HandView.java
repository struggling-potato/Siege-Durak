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
	private int position;

	public HandView() {}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void draw(Graphics g, ClassLoader classLoader, int tableHeight, int tableWidth) {
		int offsetX = 0, offsetY = 0;
		String url;
		Image cardImage = null;

		switch (position){
			case 1: {
				offsetX = tableWidth - 265;
				offsetY = tableHeight - 265;
				break;
			}
			case 2: {
				offsetX = tableWidth - 265*14;
				break;
			}
			case 3: {
				offsetX = 265;
				break;
			}
			default: {
				break;
			}
		}

		ArrayList<Card> cards = hand.getCards();
		for (int i = 0; i < cards.size(); i++) {
			url = CardToImage.getCartImageUrl(cards.get(i));

			try {
				cardImage = ImageIO.read(classLoader.getResourceAsStream(url));
				System.out.println("Trump in setTrump: " + cardImage);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			g.drawImage(cardImage,offsetX,offsetY,null);

			offsetX += 265;
		}
	}
}
