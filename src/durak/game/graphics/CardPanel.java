package durak.game.graphics;

import durak.client.IController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class CardPanel extends JPanel {
	private  ClassLoader classLoader = getClass().getClassLoader();
	private Image cardImage;
	private boolean selected;
	private boolean state;
	private CardPanelMouseAdapter adapter = new CardPanelMouseAdapter();
	private IController controller;
	private int cardIdx;

	CardPanel() {
		selected = false;
		state = true;
		addMouseListener(adapter);
	}

	CardPanel(String url) {
		selected = false;
		state = true;
		try {
			cardImage = ImageIO.read(classLoader.getResourceAsStream(url));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		addMouseListener(adapter);
	}

	CardPanel(String url, IController controller, int cardIdx) {
		this.controller=controller;
		this.cardIdx=cardIdx;
		selected = false;
		state = true;
		try {
			cardImage = ImageIO.read(classLoader.getResourceAsStream(url));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		addMouseListener(adapter);
	}

	private class CardPanelMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (!state) return;
			if (e.getButton()==MouseEvent.BUTTON1) {
				controller.onCardClicked(cardIdx);
			}
			//selected = !selected;
			repaint();
		}
	}

	public void setImage(String url) {
		try {
			cardImage = ImageIO.read(classLoader.getResourceAsStream(url));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public Image getImage() {
		return cardImage;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean getState() {
		return state;
	}

	public void setSelected(boolean selected) { this.selected = selected; }

	public boolean getSelected() { return selected; }

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int selectedOffset = 10;
		if (selected) selectedOffset = 0;
		g.drawImage(cardImage, 0, selectedOffset, null);
	}
}
