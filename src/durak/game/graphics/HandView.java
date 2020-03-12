package durak.game.graphics;

import durak.game.Hand;

import java.awt.*;

public class HandView {
	private Hand hand;
	private int position;

	public HandView(Hand playerHand, int pos) {
		hand = playerHand;
		position = pos;
	}

	public void draw(Graphics g, ClassLoader classLoader, int tableHeight, int tableWidth) {
		int offsetX = 0, offsetY = 0;
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


	}
}
