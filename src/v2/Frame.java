package v2;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Frame extends JFrame implements KeyListener{
	
	private static final long serialVersionUID = 1L;
	private Generador g;

	/////////////// setters & getters //////////////////////////////
	public void setMapa(Mapa mapa) {this.getContentPane().add(mapa);}
	public void setPantInicial(PantInicial pantInicial) { this.getContentPane().add(pantInicial);}
	public void setStats(pantScores pantScores) { this.getContentPane().add(pantScores);}


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
		int k = key.getKeyCode();
		switch(k){
			case KeyEvent.VK_RIGHT:
				if(!g.getNave().isMuerto()) g.getNave().subeRotation();
				break;
			case KeyEvent.VK_LEFT:
				if(!g.getNave().isMuerto()) g.getNave().bajaRotation();
				break;
			case KeyEvent.VK_UP:
				g.getNave().setPulsado(true);
				if(!g.getNave().isMuerto()){
					g.getNave().setImpulso(true);
					g.getNave().avanzar();
				}
				break;
			case KeyEvent.VK_SPACE:
				if(!g.getNave().isMuerto()){
					if(!g.getNave().getDisparo()) g.getNave().setDisparo(true);
					if(g.getNave().getDisparo()) g.getNave().disparar();
					g.getMapa().suma1disparo();
				}
				break;
			default: break;
		}
	}
		
	@Override
	public void keyReleased(KeyEvent key) {
		int k = key.getKeyCode();
		switch(k){
			case KeyEvent.VK_RIGHT:
				g.getNave().setDerecha(false);
				break;
			case KeyEvent.VK_LEFT:
				g.getNave().setIzquierda(false);
				break;
			case KeyEvent.VK_UP:
				g.getNave().setImpulso(false);
				break;
			case KeyEvent.VK_SPACE:
				g.getNave().setDisparo(false);
				break;
			default: break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//Not used
	}


}
