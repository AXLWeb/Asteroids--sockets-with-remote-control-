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
		while(true){
			if(mapa.getListaAsteroides().size() < mapa.getMax_Asteroides() ){
				generaAsteroide();
				try {this.sleep(1000);} 
				catch (InterruptedException ex) {ex.printStackTrace();}
			}
		}
	}

	protected void generaNave(){
		this.nave = new Nave(mapa, this);
		mapa.setNave(nave);
		nave.start();
	}

	protected void generaMisil(Nave nave){
		//TODO: max 4 disparos seguidos
		if(mapa.getContadorDisparos()<4){
			Misil misil = new Misil(nave);
			mapa.getListaMisiles().add(misil);	//lo añade al AL de misiles
			misil.start();
		}
	}
	
	protected void generaAsteroide(){
		System.out.println("Hay "+mapa.getListaAsteroides().size()+" asteroides creados");
		if(mapa.getListaAsteroides().size() < mapa.getMax_Asteroides()){
			Asteroide asteroid = new Asteroide(mapa);
			//mapa.getMapa().getListaAsteroides().push(asteroid);
			mapa.getMapa().getListaAsteroides().addElement(asteroid);
			asteroid.start();
		}
	}

	private double devuelveEscala(double scale) {
		double escala=0;
		if(scale>0.001 && scale<0.3) escala = 0.25;
		else if(scale>0.3 && scale<0.6) escala = 0.5;
		else if(scale>0.6) escala = 1;
		System.out.println("escala: "+escala);
		return escala;
	}

	

	/**
	 * Genera 2 nuevos Asteroides a partir del Vector del Padre
	 * @param asteroide Padre
	 */
	public synchronized void generar2Asteroides(Asteroide asteroide) {
		Rectangle posicion = asteroide.getPosicion();
		double escala = devuelveEscala(asteroide.getScale());
		double acel = asteroide.getAceleracion();
		int width = asteroide.getWidth();
		int height = asteroide.getHeight();
		Image img = asteroide.getImg();
		MyVector Vact = asteroide.getVf();
		System.out.println("Padre murio en "+posicion+" con escala= "+escala+" y acel "+acel);
		//TODO: EN EL MISMO LUGAR DONDE MURIO SU PADRE y con la RESTA de VECTORES
		escala /=2;	//divide escala del Padre /2
		double x = posicion.getX();
		double y = posicion.getY();

		System.out.println("Nueva escala es: "+ escala+" aceleracion: "+acel);
		System.out.println("Nuevas posiciones "+ x +", "+ y);

		Asteroide asteroid1 = new Asteroide(mapa, escala, acel, x, y, Vact, img, width/2, height/2);
		Asteroide asteroid2 = new Asteroide(mapa, escala, acel, x, y, Vact, img, width/2, height/2);
		mapa.getMapa().getListaAsteroides().add(asteroid1);
		mapa.getMapa().getListaAsteroides().add(asteroid2);

		System.out.println("Hay "+mapa.getListaAsteroides().size()+" asteroides vivos");
		asteroid1.start();
		asteroid2.start();
	}


	protected void generaEnemigo(){
		Enemigo enemigo = new Enemigo(mapa, this);
		mapa.setEnemy(enemigo);
		mapa.getMapa().getListaEnemigos().add(enemigo);
		enemigo.start();
	}
	
	

}
