package durak.game.graphics;

import javax.swing.*;

public class ClientView extends JFrame {
	private JPanel tablePanel;

	public ClientView(String windowName) {
		super(windowName);
		setBounds(150,150,1000,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		add(tablePanel);
		setVisible(true);
	}
}
