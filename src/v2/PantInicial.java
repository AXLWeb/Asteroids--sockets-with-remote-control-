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

	private Frame frame;
	private Mapa mapa;
	private Nave nave;
	private Generador g;
	private boolean running;

	public PantInicial(Generador g, Frame frame){
		this.g = g;
		this.frame = frame;
		this.frame.setBounds(100, 100, 600, 400);
		this.frame.getContentPane().add(this);

		setBounds(0, 0, 600, 400);
		requestFocus();
		setFocusable(true);

		this.setVisible(true);
		this.addMouseListener(this);
	}

	
	@Override
	public void run() {
	
		this.createBufferStrategy(2);
		this.running = true;
		while (running){

			paint();
			
			try {
				Thread.sleep(1000/60);
			} catch (Exception e){
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	public synchronized void paint(){
		
		BufferStrategy bs = this.getBufferStrategy();
		Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
		//TODO: Jugar / Ver estadisticas

		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, 600, 400);
		
		g2d.setColor(Color.white);
		g2d.drawString("PLAY", 100, 200);
		g2d.setColor(Color.red);
		g2d.drawRect(100, 200, 100, 100);

		g2d.dispose();
		bs.show();
		
	}
	

	/**
	 * Boton que hace empezar el juego
	 */
	public void jugar(){
		/*
		BufferStrategy bs = this.getBufferStrategy();
		Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
		g2d.setColor(Color.white);
		g2d.drawString("String", 10, 20);
		*/

		System.out.println("Pasando a la pantalla de juego");
	}
	
	/**
	 * Muestra el ranking de puntuaciones de los jugadores
	 */
	public void verScores(){
		System.out.println("RANKING puntuaciones: ");
		System.out.println(nave.getPuntos().leerStats());
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// g2d.drawRect(100, 200, 100, 100);
		Rectangle juego = new Rectangle(100, 200, 100, 100);	//JUGAR
		Rectangle mouse = new Rectangle(e.getX(), e.getY(), 1, 1);

		Rectangle scores = new Rectangle(100, 200, 100, 100);	//SCORES

		if (mouse.intersects(juego)){
			this.g.iniciarJuego();
		}
		else if(mouse.intersects(scores)){
			verScores();
		}
			
		
		
	}

	
	public void kill() {
		this.running = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}


	@Override
	public void mouseExited(MouseEvent e) {
	}


	@Override
	public void mousePressed(MouseEvent e) {
	}


	@Override
	public void mouseReleased(MouseEvent e) {
	}








	
	
}
