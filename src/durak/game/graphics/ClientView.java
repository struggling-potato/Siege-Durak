package durak.game.graphics;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientView extends JFrame {

	ClassLoader classLoader = getClass().getClassLoader();
	private JPanel tablePanel;
	private JPanel deckPanel;
	private Image tableImage;

	public ClientView(String windowName) {
		super(windowName);
		setBounds(150,150,1000,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			tableImage = ImageIO.read(classLoader.getResourceAsStream("assets/table.jpg"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		add(tablePanel);
		setVisible(true);
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

				DeckView dv = new DeckView("clovers__Q", classLoader);
				dv.draw(g);
			}
		};
	}
}
