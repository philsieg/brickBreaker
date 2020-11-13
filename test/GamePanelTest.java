
import static org.junit.Assert.*;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.junit.Before;
import org.junit.Test;

public class GamePanelTest{

	public static void main(String[] args) {
	
	
	}

private static void showRules(JFrame parent) {
String info = "Welcome to 120 ball, here is how to play:\n"
		+ "1. The goal of the game is break the blocks and not let your ball drop below "
		+ "the screen\n"
		+ "2. You can move your paddle with the mouse or arrow keys\n"
		+ "3. Powerups and Handicaps can drop randomly, be ready!\n"
		+ "4. Most blocks break in one hit, but not all of them, be careful ;)\n"
		+ "*TIP* If blocks look the same, they behave the same across all levels";


JOptionPane.showMessageDialog(parent, info, "INSTRUCTIONS", 1); Block[][] blocks;
}

	private Levels L;
	private int curLevel = 1;
	private JFrame mainFrame, end, start;
	GamePanel panel;

	@Before
    public void setUp() {
		//create main game window
		mainFrame = new JFrame("120 Ball");
		
		//start screen with play button
		start = new JFrame();
		JButton play = new JButton("PLAY");
		JButton rules = new JButton("INSTRUCTIONS");
		JLabel intro = new JLabel("Welcome to 120 Ball Created By Phil", SwingConstants.CENTER); 
		intro.setFont(new Font("Verdana",1,20));
		
		end = new JFrame();
		
		play.addActionListener(lister -> {
			start.setVisible(false);
			mainFrame.setVisible(true);
		});
		
		
		rules.addActionListener(lister -> {
			showRules(start);
		});
		
		//----------------------MAIN PANEL-----------------
		//set up parameters for main game window but do not make it visible yet
		JLabel lives = new JLabel("3 Lives", SwingConstants.CENTER);
		mainFrame.getContentPane().add(lives, BorderLayout.PAGE_START);
		panel = new GamePanel(mainFrame, end, start, lives);
		mainFrame.getContentPane().add(panel);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(false);
		mainFrame.setSize(600, 600);
		mainFrame.setResizable(false);
		
		//----------------START PANEL----------------------
		//set layout
		GridLayout startLayout = new GridLayout(0,1);
		start.setLayout(startLayout);
		end.setLayout(startLayout);
		//add buttons
		start.getContentPane().add(intro);
		start.getContentPane().add(play);
		start.getContentPane().add(rules);
		start.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		start.setVisible(true);
		start.setSize(600, 600);
		start.setResizable(false);
		
		//-----------------END PANEL------------------------------------
		end.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		end.setVisible(false);
		end.setSize(600, 600);
		end.setResizable(false);
		
		//--------
		L = new Levels(600, 600);
		
    }
	
	@Test
	public void testLoadLevel() {
		//test load level
		panel.loadLevel(curLevel-1);
		assertArrayEquals(L.getLevel(0), panel.getBlocks());
		//test load level
		panel.loadLevel(curLevel);
		assertArrayEquals(L.getLevel(1), panel.getBlocks());
	}
	
	@Test
	public void testEnd() {
		//test end game
		panel.end(false);
		assertEquals(false, mainFrame.isVisible());
		assertEquals(true, end.isVisible());
		panel.end(true);
		assertEquals(false, mainFrame.isVisible());
		assertEquals(true, end.isVisible());
	}
	
	
	@Test
	public void testLoseLife() {
		//test lose life works properly
		assertEquals(3, panel.getLives());
		panel.loseLife();
		assertEquals(2, panel.getLives());
		panel.loseLife();
		assertEquals(1, panel.getLives());
		panel.loseLife();
		assertEquals(0, panel.getLives());
		assertEquals(false, mainFrame.isVisible());
		assertEquals(true, end.isVisible());
	}
	
	
	@Test
	public void testLevelStatus() {
		//test level status
		panel.loadLevel(curLevel);
		assertEquals(false, panel.levelStatus());
		panel.setBlocks(new Block[5][5]);
		assertEquals(true, panel.levelStatus());
	}
	
	@Test
	public void testReset() {
		//test game reset
		panel.reset();
		assertEquals(false, panel.getThru());
		assertEquals(true, panel.getPowerups().isEmpty());
		assertEquals(300, panel.getBall().x);
		assertEquals(350, panel.getBall().y);
		assertEquals(15, panel.getBall().getWidth(), 0.01);
		assertEquals(15, panel.getBall().getHeight(), 0.01);
		assertEquals(225, panel.getPaddle().x);
		assertEquals(500, panel.getPaddle().y);
		assertEquals(150, panel.getPaddle().getWidth(), 0.01);
		assertEquals(25, panel.getPaddle().getHeight(), 0.01);
	}
	
	//test going to the next level function
	@Test
	public void testNextLevel() {
		assertEquals(1, panel.getCurLevel());
		assertArrayEquals(L.getLevel(0), panel.getBlocks());
		panel.nextlevel();
		assertArrayEquals(L.getLevel(1), panel.getBlocks());
		assertEquals(false, panel.getThru());
		assertEquals(true, panel.getPowerups().isEmpty());
		assertEquals(300, panel.getBall().x);
		assertEquals(350, panel.getBall().y);
		assertEquals(15, panel.getBall().getWidth(), 0.01);
		assertEquals(15, panel.getBall().getHeight(), 0.01);
		assertEquals(225, panel.getPaddle().x);
		assertEquals(500, panel.getPaddle().y);
		assertEquals(150, panel.getPaddle().getWidth(), 0.01);
		assertEquals(25, panel.getPaddle().getHeight(), 0.01);
		assertEquals(2, panel.getCurLevel());
		for (int i = 0; i < 6; i++) {
			panel.nextlevel();
		}
		assertEquals(false, mainFrame.isVisible());		
	}
	
	//test handling life powerup
	@Test
	public void testHandleLife() {
		Powerup life = panel.getLife();
		assertEquals(3, panel.getLives());
		panel.handle(life);
		assertEquals(4, panel.getLives());
	}
	
	//test handling diying powerup
	@Test
	public void testHandleDie() {
		Powerup die = panel.getDie();
		assertEquals(3, panel.getLives());
		panel.handle(die);
		assertFalse(3 == panel.getLives());
	}


	

}
