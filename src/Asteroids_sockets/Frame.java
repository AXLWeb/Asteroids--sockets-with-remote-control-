package Asteroids_sockets;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Frame extends JFrame implements KeyListener{
	
	private static final long serialVersionUID = 1L;
	private Generador g;

	/////////////// setters & getters //////////////////////////////
	public void setMapa(Mapa mapa) {this.getContentPane().add(mapa);}

	// Constructor inicial del frame.
	public Frame(Generador gen) {
		this.g= gen;
		setTitle("Asteroids");
		setFocusable(true);
		requestFocus();
		setVisible(true);
		pack();
		repaint();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(this);
	}

	
	@Override
	public void keyPressed(KeyEvent key) {
		/*
		int k = key.getKeyCode();
		switch(k){
			case KeyEvent.VK_RIGHT:
				if(!g.getMapa().getNaveByID(ID).isMuerto()) g.getMapa().getNaveByID(ID).subeRotation();
				break;
			case KeyEvent.VK_LEFT:
				if(!g.getMapa().getNaveByID(ID).isMuerto()) g.getMapa().getNaveByID(ID).bajaRotation();
				break;
			case KeyEvent.VK_UP:
				g.getMapa().getNaveByID(ID).setPulsado(true);
				if(!g.getMapa().getNaveByID(ID).isMuerto()){
					g.getMapa().getNaveByID(ID).setImpulso(true);
					g.getMapa().getNaveByID(ID).avanzar();
				}
				break;
			case KeyEvent.VK_SPACE:
				if(!g.getMapa().getNaveByID(ID).isMuerto()){
					if(!g.getMapa().getNaveByID(ID).getDisparo()) g.getMapa().getNaveByID(ID).setDisparo(true);
					if(g.getMapa().getNaveByID(ID).getDisparo()) g.getMapa().getNaveByID(ID).disparar();
					g.getMapa().suma1disparo();		//TODO: la Nave-ID suma 1 disparo
				}
				break;
			default: break;
		}
		*/
	}
		
	@Override
	public void keyReleased(KeyEvent key) {
		/*
		int k = key.getKeyCode();
		switch(k){
			case KeyEvent.VK_RIGHT:
				g.getMapa().getNaveByID(ID).setDerecha(false);
				break;
			case KeyEvent.VK_LEFT:
				g.getMapa().getNaveByID(ID).setIzquierda(false);
				break;
			case KeyEvent.VK_UP:
				g.getMapa().getNaveByID(ID).setImpulso(false);
				break;
			case KeyEvent.VK_SPACE:
				g.getMapa().getNaveByID(ID).setDisparo(false);
				break;
			default: break;
		}
		*/
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//Not used
	}


}
