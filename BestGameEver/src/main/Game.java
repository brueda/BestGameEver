package main;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class Game extends JPanel implements Runnable {
	private Thread gameThread;
	private volatile boolean running;
	private volatile State currentState;
	private int gameWidth;
	private int gameHeight;
	private Image gameImage;

	private InputHandler inputHandler;

	public Game(int gameWidth, int gameHeight) {
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		setPreferredSize(new Dimension(gameWidth, gameHeight));
		setBackground(Color.BLACK);
		setFocusable(true);
		requestFocus();
	}
	      
	public void addNotify() {
		super.addNotify();
		initInput();
		setCurrentState(new MenuState(new Player()));
		initGame();
	}

	public void setCurrentState(State newState) {
		System.gc();
		newState.init();
		currentState = newState;
		inputHandler.setCurrentState(currentState);
	}
	
	private void prepareGameImage() {
		if (gameImage == null) {
			gameImage = createImage(gameWidth, gameHeight);
		}
		Graphics g = gameImage.getGraphics();
		g.clearRect(0, 0, gameWidth, gameHeight);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (gameImage == null) {
			return;
		}
		g.drawImage(gameImage, 0, 0, null);
	}

	private void initGame() {
		running = true;
		Resources.load();
		gameThread = new Thread(this, "Game Thread");
		gameThread.start();
	}

	@Override
	public void run() {
		while (running) {
			prepareGameImage();
			currentState.render(gameImage.getGraphics());
			repaint();
			try {
				Thread.sleep(34);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// End game immediately when running becomes false.
		System.exit(0);
	}

	public void exit() {
		running = false;
	}

	private void initInput() {
		inputHandler = new InputHandler();
		addKeyListener(inputHandler);
	}

}
