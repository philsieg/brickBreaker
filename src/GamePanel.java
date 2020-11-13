import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

//create a panel for the game visual (like game canvas)
public class GamePanel extends JPanel implements KeyListener, MouseMotionListener{
	//create ball
	private Ball ball = new Ball(300, 350, 15, 15, "ball.png");
	//create paddle
	private Paddle paddle = new Paddle(225, 500, 150, 25, "bottom-glow.png");
	//create powerups
	Powerup life, nextLev, die, invincible, back, grow, shrink; 
	double speedFactor = 1; 
	double xcur= 0, xprev = -1, xdprev = -2;
	double ycur= 0, yprev = -1, ydprev = -2;
	//how many lives does the player have
	private Set<Powerup> powerups = new TreeSet<Powerup>();
	private int lives = 3; 
	//what level is the player on
	private int curLevel = 1; 
	//has the game began?
	boolean started = false;
	//create reference thread
	Thread thread; 
	//matrix of current blocks
	private Block[][] blocks;

	private int score; 

	private Levels L = new Levels(600, 600);
	//frames for start/end screen
	JFrame mainFrame, endFrame, startFrame;
	JLabel livesLabel;
	private boolean end = false, thru = false; 
	GamePanel(JFrame mainFrame, JFrame endFrame, JFrame startFrame, JLabel lives) {
		//get levels
		loadLevel(curLevel-1);
		//make this class the key listen
		addKeyListener(this);
		addMouseMotionListener(this);
		//set focusable
		setFocusable(true);
		this.mainFrame = mainFrame;
		this.endFrame = endFrame; 
		this.startFrame = startFrame;
		this.livesLabel = lives;
	}

	// -------------------- UPDATE GAME STATE -----------------------------------

	public void update() {
		if(ball.x >= (getWidth() - ball.width) || ball.x <= 0) {
			xcur = ball.getvX();
			ycur = ball.getvY();
			if (Math.abs(ball.getvX()) < 1) {
				ball.setvX(Math.copySign(1, ball.getvX() * -1));
			}
			else {
				ball.setvX(ball.getvX() * -1);
			}
			if(Math.abs(xcur) == Math.abs(xprev) && Math.abs(xprev) == Math.abs(xdprev)) {
				if (ball.x < 20) { ball.x += 5;}
				else{ball.x -=5;}
			}
			xprev = xcur;
			xdprev = xprev;
			if(Math.abs(ycur) == Math.abs(yprev) && Math.abs(yprev) == Math.abs(ydprev)) {
				ball.y += 5; 
			}
			yprev = xcur;
			ydprev = xprev;

		}
		if(ball.y < 0) {
			ball.setvY(ball.getvY() * -1);
		}
		//lose a life if ball drops below the screen
		if (ball.y > getHeight()) {
			loseLife();
		}

		//handle paddle collisions with ball
		else if(paddle.intersects(ball)) {
			if(ball.getvY() < 3) {
				ball.setvY(3);
			}
			ball.setvY(ball.getvY() * -1);
			double xDif = (double) (paddle.x + (paddle.width/2) - (ball.x + (ball.width/2)));
			if (xDif > 0) {
				ball.setvX((Math.abs(xDif))/(paddle.width/2) * -2.0);	
			}
			else {
				ball.setvX((((paddle.width/2) -xDif))/(paddle.width/2) * 2.0);	
			}

		}	
		//Handle block collisions with ball
		for(int i = 0; i< blocks.length; i++) {
			for(int j = 0; j < blocks[i].length; j++) {
				Block curBlock = blocks[i][j];
				if (curBlock == null) {
					continue;
				}
				if(ball.intersects(curBlock)) {
					score += 10;
					//hit the block
					curBlock.hit();
					//unless under a powerup bouce the ball
					if (!thru) {
						ball.setvY(ball.getvY() * -1);
					}
					//if it has no lives left remove it
					if (curBlock.getHits() < 1) {
						blocks[i][j] = null; 
					}
					if(levelStatus()) {
						nextlevel();
					}
					else {
						powerup(ball.x, ball.y);
					}
				}
			}
		}
		//UPDATE BALL POSITION BASED ON VELOCITY
		ball.move();
		//update state label
		livesLabel.setText(lives + " Lives" +" | Score " + score);

		//Update falling powerups and handle collisions
		for(Powerup p: powerups) {
			if (paddle.intersects(p)) {
				score += 100;
				handle(p);
				powerups.remove(p);
			}
			else {
				if(p.isGone(getHeight())) {
					powerups.remove(p);
				}
				else {
					p.fall();
				}
			}
		}
		repaint();
	}

	void loseLife() {
		lives--; 
		//depending on how many lives left, keep playing or lose game
		if(lives > 0) {
			reset();
		}
		else {
			end(false);
		}

	}

	void handle(Powerup p) {
		if(p == life) {
			lives ++; 
			livesLabel.setText(lives + " Lives");
		}
		else if(p == nextLev) {
			nextlevel();
		}
		else if(p == die) {
			loseLife();
		}
		else if(p == invincible) {
			thru = true;
		}
		else if(p == back) {
			end(false);
		}
		else if(p == grow && !paddle.isBig()) {
			paddle.x-= paddle.getWidth()/2;
			paddle.grow();
		}
		else if(p == shrink && !paddle.isSmall()) {
			paddle.x+= paddle.getWidth()/2;
			paddle.shrink();
		}

	}

	//Decide if a powerup should drop
	void powerup(int x, int y) {
		if(!powerups.isEmpty()) {
			return;
		}
		double drop = Math.random();
		//bad powerup
		if (drop <= 0.07) {
			double rand = Math.random();
			if (rand < 0.35) {
				shrink = new Powerup(x, y, 30, 30, 2, "shrink.png");
				powerups.add(shrink);
			}
			else if(rand <= 0.7) {
				die = new Powerup(x, y, 30, 30, 2, "fail.jpg");
				powerups.add(die);
			}
			else {
				back = new Powerup(x, y, 40, 40, 2.5, "erik.jpg");
				powerups.add(back);
			}

		}
		//good powerup
		else if (drop >= 0.93) {
			//decide which powerup
			double rand = Math.random();
			if(rand < 0.3) {
				grow = new Powerup(x, y, 30, 30, 2, "grow.png");
				powerups.add(grow); 
			}
			else if (rand <= 0.6) {
				//extra life
				life = new Powerup(x, y, 30, 30, 2, "steve.jpg");
				powerups.add(life);
			} 
			else if (rand <= 0.9) {
				//why?
				invincible = new Powerup(x, y, 30, 30 , 3, "yrvine.jpg");
				powerups.add(invincible);
			}
			else if (rand > 0.9) {
				//win level automatically
				nextLev = new Powerup(x, y, 30, 30, 4, "swap.jpg");
				powerups.add(nextLev);
			}

		}

	}

	//LOAD A LEVEL
	void loadLevel(int i) {
		blocks = L.getLevel(i);	
	}

	//check if player has completed level
	boolean levelStatus() {
		for(int i = 0; i< blocks.length; i++) {
			for(int j = 0; j < blocks[i].length; j++) {
				if(blocks[i][j] != null && blocks[i][j].getHits()> 0) {
					return false;
				}
			}
		}
		return true;
	}

	//reset the the game if the user loses a life
	void reset() {
		//reset the ball
		ball = new Ball(300, 350, 15, 15, "ball.png");
		//center paddle
		paddle = new Paddle(225, 500, 150, 25, "bottom-glow.png");
		powerups.removeAll(powerups);
		thru = false;
	}
	//advance player to next level
	void nextlevel() {
		curLevel++; 
		if (curLevel > L.howManyLevels()) {
			end(true);
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		loadLevel(curLevel-1);
		reset();
	}

	//END THE GAME IF THE USE HAS LOST	
	void end(boolean win) {
		//end the thread
		thread= null; 
		//make the End frame visable
		mainFrame.setVisible(false);
		endFrame.setVisible(true);
		//let the user know what level they got to
		JLabel message;
		if (win) {
			score += 100 * lives; 
			message = new JLabel("Congrats you beat all " + curLevel + " levels with a "
					+ "score of " + score,
					SwingConstants.CENTER); 
		}
		else {
			message = new JLabel("You lost on level " + curLevel + "... Your Score Was " 
					+ score, SwingConstants.CENTER); 
		}
		message.setFont(new Font("Verdana",1,20));
		//add an exit game button
		endFrame.getContentPane().add(message);
		JButton exit = new JButton("Exit"); 
		exit.addActionListener(lister -> {
			endFrame.setVisible(false);
		});
		endFrame.getContentPane().add(exit);
		end = true;

	}


	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paddle.draw(g, this);
		ball.draw(g, this);
		for(int i = 0; i< blocks.length; i++) {
			for(int j = 0; j< blocks[i].length; j++) {
				Block curBlock = blocks[i][j]; 
				if(curBlock != null) {
					curBlock.draw(g, this);
				}
			}
		}
		for(Powerup powerup: powerups) {
			powerup.draw(g, this);
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		//enter key
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER &&!started) {
			started = true; 
			thread= new Thread(()->  {
				while(!end) {
					//update thread
					update();
					try {
						Thread.sleep(8);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			thread.start();
		}
		//right arrow key movement
		if(arg0.getKeyCode() == KeyEvent.VK_RIGHT && paddle.x < getWidth() - paddle.width) {
			paddle.x+= 30;
		}
		//left arrow key
		if(arg0.getKeyCode() == KeyEvent.VK_LEFT && paddle.x > 0) {
			paddle.x-= 30;
		}
		//exit the game with escape
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) { 
			startFrame.setVisible(false);
			mainFrame.setVisible(false);
			endFrame.setVisible(false);
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	//how to move paddle w. mouse
	public void mouseMoved(MouseEvent e) {
		int xcoord = (int) (e.getX() + (paddle.getWidth()/2));
		if (!(xcoord < paddle.getWidth()) && !(xcoord > getWidth())) {
			paddle.x= (int) (xcoord-paddle.getWidth());
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	//For JUnit testing------------------------
	public int getLives() {
		return lives;
	}

	public Block[][] getBlocks() {
		return blocks;
	}

	public void setBlocks(Block[][] b) {
		blocks = b;
	}

	public boolean getThru() {
		return this.thru;
	}

	public Set<Powerup> getPowerups() {
		return this.powerups;
	}

	public Paddle getPaddle() {
		return this.paddle;
	}

	public Ball getBall() {
		return this.ball;
	}
	public int getCurLevel() {
		return this.curLevel;
	}
	
	public Powerup getLife() {
		return life;	
	}
	public Powerup getDie() {
		return die;
	}
	public Powerup getGrow() {
		return grow;
	}
	//-------------------------------------------
}
