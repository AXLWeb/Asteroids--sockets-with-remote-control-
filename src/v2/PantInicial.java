package v2;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

public class PantInicial extends Canvas implements KeyListener{

	private Frame frame;
	private Mapa mapa;
	private Nave nave;
	private Generador g;

	public PantInicial(){

		this.frame = new Frame();
		this.frame.getContentPane().add(this);
		this.frame.setBounds(100, 100, 500, 500);

		this.addKeyListener(this);
		
		BufferStrategy bs = this.getBufferStrategy();
		Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
		g2d.setColor(Color.white);
		g2d.drawString("String", 10, 20);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
}
