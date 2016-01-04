package v2;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import javax.imageio.ImageIO;

public class Generador extends Thread {
	private Frame frame;
	private Mapa mapa;
	private Nave nave;

	/////////////////	Constructor 	//////////////////////////////
	public Generador() {
		this.mapa = new Mapa(this);
		this.frame = new Frame();
		this.frame.setMapa(this.mapa);
		this.frame.setBounds(100, 100, this.mapa.getWidth(), this.mapa.getHeight());
		generaNave();
		mapa.start();
	}

	public void run(){

		/*
		Image img = null;
		try {img = ImageIO.read(Launcher.class.getResource("/img/asteroides/1.png"));} 
		catch (IOException e) {e.printStackTrace();}
		MyVector Vf = new MyVector(0.0, 0.0);
		Asteroide asteroid = new Asteroide(mapa, 1, 1, 50, 50, Vf, img,75,75);		//muerto ficticio
		generar2Asteroides(asteroid);
		*/

		while(true){
				try {this.sleep(1000);} 
				catch (InterruptedException ex) {ex.printStackTrace();
			}
		}
	}

	protected void generaNave(){
		this.nave = new Nave(mapa, this);
		mapa.setNave(nave);
		nave.start();
	}

	protected synchronized void generaMisil(Nave nave){
		Misil misil = new Misil(nave);
		mapa.getListaMisiles().add(misil);	//lo añade al AL de misiles
		misil.start();
	}

}
