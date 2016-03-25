package Asteroids_sockets;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Frame extends JFrame {
	
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

		//setDefaultCloseOperation(exit());
	}

	
	protected int exit(){
		g.getMapa().cierraMapa(); 		//le dice al server que se va
		return JFrame.EXIT_ON_CLOSE;
	}
}
