/*
 * File: Breakout.java
 * -------------------
 * This file will eventually implement the game of Breakout.
 *
 * TODO: Update this file with a description of what your program
 * actually does!
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;
		
	private GRect paddle = new GRect((WIDTH/2) - (PADDLE_WIDTH/2), APPLICATION_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
	
	private MouseMotionListener mml = new MouseMotionListener() {
		
		@Override
		public void mouseMoved(MouseEvent arg0) {
			// TODO Auto-generated method stub
			double x = arg0.getX();
			if(x > APPLICATION_WIDTH - PADDLE_WIDTH) {
				x = APPLICATION_WIDTH - PADDLE_WIDTH;
			}
			else if(x < 0) {
				x = 0;
			}
			paddle.setLocation(x, APPLICATION_HEIGHT - PADDLE_Y_OFFSET);
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	GOval ball = new GOval(APPLICATION_WIDTH / 2, APPLICATION_HEIGHT / 2, BALL_RADIUS, BALL_RADIUS);

	public void run() {
		// Setup the window
		setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		GCanvas gc = this.getGCanvas();
		gc.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		//setup the moving paddle
		gc.addMouseMotionListener(mml);
		this.paddle.setFilled(true);
		this.paddle.setFillColor(Color.GRAY);
		add(this.paddle);
		// flag variable
		boolean win = false;
		// velocity values
		double vx, vy;
		// add ball details
		this.ball.setFilled(true);
		this.ball.setFillColor(Color.GRAY);
		vy = 3.0;
		RandomGenerator rgen = new RandomGenerator();
		vx = rgen.nextDouble(1.0, 3.0);
		if(rgen.nextBoolean(0.5))vx = -vx;
		add(this.ball);
		// initialize the rows of bricks
		int bricksRemaining = NBRICK_ROWS * NBRICKS_PER_ROW;
		double brickOffset = BRICK_SEP / 2;
		double currentX = brickOffset;
		double currentY = BRICK_Y_OFFSET;
		for(int i = 0; i < NBRICKS_PER_ROW; i++) {
			for(int j = 0; j < NBRICK_ROWS; j++) {
				GRect brick = new GRect(currentX, currentY, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if(i < 2) {
					brick.setFillColor(Color.RED);
					brick.setColor(Color.RED);
				}
				else if(i >=2 && i < 4) {
					brick.setFillColor(Color.ORANGE);
					brick.setColor(Color.ORANGE);
				}
				else if(i >= 4 && i < 6) {
					brick.setFillColor(Color.YELLOW);
					brick.setColor(Color.YELLOW);
				}
				else if(i >= 6 && i < 8) {
					brick.setFillColor(Color.GREEN);
					brick.setColor(Color.GREEN);
				}
				else {
					brick.setFillColor(Color.CYAN);
					brick.setColor(Color.CYAN);
				}
				currentX += BRICK_WIDTH + BRICK_SEP;
				add(brick);
			}
			currentY += BRICK_HEIGHT + BRICK_SEP;
			currentX = brickOffset;
		}
		int lives = 3;
		// main loop
		while(!win) {
			//move ball
			this.ball.setLocation(this.ball.getX() + vx, this.ball.getY() + vy);
			//detect wall collisions
			if(this.ball.getX() + (BALL_RADIUS) >= APPLICATION_WIDTH) {
				vx = -vx;
				this.ball.setLocation(this.ball.getX() + vx, this.ball.getY() - vy);
				double random = rgen.nextDouble(0.5, 1.5);
				vy = vy * random;
				random = rgen.nextDouble(0.5, 1.5);
				vx = vx * random;
			}
			else if(this.ball.getX() <= 0) {
				vx = -vx;
				this.ball.setLocation(this.ball.getX() + vx, this.ball.getY() - vy);
				double random = rgen.nextDouble(0.5, 1.5);
				vy = vy * random;
				random = rgen.nextDouble(0.5, 1.5);
				vx = vx * random;
			}
			else if(this.ball.getY() - BALL_RADIUS <= 0) {
				vy = -vy;
				this.ball.setLocation(this.ball.getX() - vx, this.ball.getY() + vy);
				double random = rgen.nextDouble(0.5, 1.5);
				vy = vy * random;
				random = rgen.nextDouble(0.5, 1.5);
				vx = vx * random;
			}
			else if(this.ball.getY() + (BALL_RADIUS) >= APPLICATION_HEIGHT) {
				lives --;
				GLabel lose = new GLabel("You lost, try again. Lives Remaining: " + lives + ". Click to Start.", APPLICATION_WIDTH / 6, PADDLE_Y_OFFSET);
				add(lose);
				vy = 3;
				if(lives == 0) {
					break;
				}
				waitForClick();
				remove(lose);
				this.ball.setLocation(APPLICATION_WIDTH / 2, APPLICATION_HEIGHT / 2);
			}
			// detect paddle and brick collisions
			GObject collided = getCollision();
			if(collided == this.paddle) {
				vy = -vy;
			}
			else if(collided != null) {
				remove(collided);
				bricksRemaining--;
				vy = -vy;
				double random = rgen.nextDouble(0.5, 1.5);
				vy = vy * random;
				random = rgen.nextDouble(0.5, 1.5);
				vx = vx * random;
			}
			//check win condition
			if(bricksRemaining == 0) {
				win = true;
			}
			// set to 60 frames per second
			pause(1000/60);
			// change y velocity to make sure it doesn't get too slow
			if(vy < 2.0 && vy > 0) {
				vy = 2.0;
			}
			if(vy >= 6.0) {
				vy = 6.0;
			}
			if(vy < 0 && vy > -1.0) {
				vy = -1.0;
			}
			if(vx < 0.5 && vx > 0) {
				vx = 0.5;
			}
			if(vx < 0 && vx > -0.5) {
				vx = -0.5;
			}
		}
		removeAll();
		// win
		if(win == true) {
			GLabel wintext = new GLabel("You Won, Congratulations!", APPLICATION_WIDTH / 2, PADDLE_Y_OFFSET);
			add(wintext);
		}
		// lose
		else {
			GLabel lose = new GLabel("You lost. Lives Remaining: " + lives + ". Game Over.", APPLICATION_WIDTH / 6, PADDLE_Y_OFFSET);
			add(lose);
		}
	}
	
	private GObject getCollision() {
		double x = this.ball.getX();
		double y = this.ball.getY();
		// check for collisions
		if(this.getElementAt(x, y) != null) {
			return this.getElementAt(x, y);
		}
		else if(this.getElementAt(x + (BALL_RADIUS), y) != null) {
			return this.getElementAt(x + (BALL_RADIUS), y);
		}
		else if(this.getElementAt(x, y + (BALL_RADIUS)) != null) {
			return this.getElementAt(x, y + (BALL_RADIUS));
		}
		else if(this.getElementAt(x + (BALL_RADIUS), y + (BALL_RADIUS)) != null) {
			return this.getElementAt(x + (BALL_RADIUS), y + (BALL_RADIUS));
		}
		// if no collisions, return null
		else {
			return null;
		}
	}
}
