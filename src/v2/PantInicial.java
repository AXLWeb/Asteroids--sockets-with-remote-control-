package v2;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;

public class PantInicial extends Canvas implements Runnable, MouseListener{

	private static final long serialVersionUID = -834793815010269880L;
	private Frame frame;
	private Generador g;
	private Mapa mapa;
	private boolean running;
	private Rectangle btnJugar, btnScores;

	///////////////	setters & getters	//////////////////////////////
	public boolean isVisible(){return this.running;}
	
	
	public PantInicial(Generador g, Frame frame, Mapa mapa){
		this.g = g;
		this.frame = frame;
		this.mapa = mapa;
		this.frame.setBounds(100, 100, 600, 400);
		this.frame.getContentPane().add(this);

		setBounds(0, 0, 500, 400);
		requestFocus();
		setFocusable(true);

		this.running = true;
		this.setVisible(true);
		this.addMouseListener(this);
	}

	@Override
	public void run() {	
		this.createBufferStrategy(2);

		while (running){
			paint();
			if(mapa.getListaAsteroides().size() < mapa.getMax_Asteroides()) g.generaAsteroide();

			try {Thread.sleep(1000/60);} 
			catch (Exception e){e.printStackTrace();}
		}
	}

	public synchronized void paint(){
		
		BufferStrategy bs = this.getBufferStrategy();
		Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
		
		/**
		 * Titulo + botones
		 * //TODO: Botones Jugar / Ver ranking
		 * Mapa vivo de fondo, sin enemigos, Asteroides de todos los tama�os
		 */


		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, 600, 600);


		mapa.pintaAsteroides(g2d);

		g2d.setColor(Color.white);
		g2d.drawString("PLAY", getWidth()/2-50, getHeight()/2);
		g2d.drawString("SCORES", getWidth()/2-50, getHeight()/2+40);
		
		btnJugar = new Rectangle(this.getWidth()/2-125, this.getHeight()/2-20, 200, 30); //Rectangle boton LISTO
		g2d.setColor(Color.red);
		g2d.draw(btnJugar.getBounds());
		
		btnScores = new Rectangle(this.getWidth()/2-125, this.getHeight()/2+20, 200, 30);
		g2d.setColor(Color.gray);
		g2d.draw(btnScores.getBounds());

		g2d.setColor(Color.white);
		g2d.setFont(Launcher.fuente);
		g2d.drawString("ASTEROIDS", getWidth()/2-80, getHeight()/2-80);
		
		g2d.dispose();
		bs.show();
	}

	/**
	 * Boton que hace empezar el juego
	 */
	public void jugar(){
		this.setVisible(false);
		this.g.iniciarJuego();
	}
	
	/**
	 * Muestra el ranking de puntuaciones de los jugadores
	 */
	public void verScores(){
		System.out.println("RANKING puntuaciones: ");
		this.setVisible(false);
		this.g.verStats();
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		Rectangle mouse = new Rectangle(e.getX(), e.getY(), 1, 1);

		if (mouse.intersects(btnJugar)){
			killAll();
			jugar();
		}
		else if(mouse.intersects(btnScores)){
			verScores();
		}
	}

	public void killAll() {
		//kill todos los hilos q haya. Clear ArrayLists...vidas=3.....
		this.running = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}

	
}