package ui;


import javax.swing.*;

import java.awt.*;

import level.*;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Action;

public class HomePanel extends JPanel {
	final int number_of_levels = 7;
	private JPanel container;
	List<JButton> startButtons;
	JButton startButton;
    //ImageIcon banner = new ImageIcon("src/data/spacerace.png");
    ImageIcon banner;
	Game game;
	private boolean ready;
	public HomePanel(Game game){
		banner = new ImageIcon(getClass().getClassLoader().getResource("data/spacerace.png"));
		container = new JPanel();
		//container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		this.setBackground(Color.BLACK);
		startButtons = new LinkedList<JButton>();
		ready = true;
		for(int i = 1; i <= number_of_levels; i++){
			startButton = new JButton("Click Here to Start");
			startButton.setAction(new StartGameAction(i));
			startButton.setText("Level " + i);
			container.add(startButton);
			startButtons.add(startButton);
		}
		add(container);
		this.setPreferredSize(new Dimension(800, 800));
		this.game = game;
		
	}
	@Override
	public void paint(Graphics g){
		super.paint(g);
		Image img = banner.getImage();
		g.drawImage(img, 0, 300, Color.BLACK, null);
		g.setFont(new Font("Serif", Font.PLAIN, 28));
		g.setColor(Color.red);
		g.drawString("Dan McKerricher made this game.", 200, 200);
		g.setFont(new Font("Serif", Font.PLAIN, 12));
		g.drawString("(and he did a really good job of it)", 200, 250);
	}
	private class StartGameAction implements Action{
		private int level_number;
		public void setLevel(int level_number){
			this.level_number = level_number;
		}
		public StartGameAction(int level_number){
			this.level_number = level_number;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			setEnabled(false);
			game.start(level_number);
			
		}

		@Override
		public void addPropertyChangeListener(PropertyChangeListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object getValue(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isEnabled() {
			// TODO Auto-generated method stub
			return ready;
		}

		@Override
		public void putValue(String arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removePropertyChangeListener(PropertyChangeListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setEnabled(boolean r) {
			ready = r;
		}
		
	};
}
