package v2;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class Enemigo extends Thread{

	private static final double DECEL_FACTOR = 0.015;
	private static final double aceleracion = 0.25;
	private Image enemigoImg; 
	private Mapa mapa;
	private Misil misil;
	private Generador generator;
	private double x, y, velMax, velMin;
	private int rotation, vida, contaSleeps;
	private boolean pulsado, disparo, muerto, derecha, izquierda, arriba;
	private MyVector Vdir, Vf, Vact, Vimpulso;
	private int width, height;


	///////////////	setters & getters	//////////////////////////////
	public Enemigo getEnemigo(){return this;}
	public Mapa getMapa(){return this.mapa;}
	public Misil getMisil(){return this.misil;}
	public int getRotation() {return this.rotation;}
	protected int getWidth() {return this.width;}
	protected int getHeight() {return this.height;}
	public MyVector getVdir() {return this.Vdir;}
	public Rectangle getPosicion() {return new Rectangle((int)getPosX(), (int)getPosY(), getWidth(), getHeight());} //devuelve posicion
	public synchronized double getPosX() {return x;}
	protected synchronized void setPosX(int x){this.x = x;}
	public synchronized double getPosY() {return y;}
	protected synchronized void setPosY(int y){this.y = y;}
	public synchronized boolean isMuerto() {return this.muerto;}
	protected synchronized void setMuerto(boolean b) {this.muerto = b;}
	

	//Constructor Enemigo	
	public Enemigo(Mapa mapa, Generador g) {

		cargaImgs();
		this.generator = g;
		this.mapa = mapa;

		this.width = 41;
		this.height = 30;
		this.x = mapa.getWidth()/2  + 100;
		this.y = mapa.getHeight()/2 + 100;

		this.velMax = 10;
		this.velMin = 0.5;
		this.pulsado = false;
		this.muerto = false;
		this.contaSleeps=0;
		//this.rotation = new Random().nextInt(360-0);	//random de ángulo inicial
		this.rotation = 0;
		
		//inicializacion Vectores
		this.Vact = new MyVector(0, 0);		//Vector actual
		this.Vdir = new MyVector((int)Math.cos(rotation), (int)Math.sin(rotation));	//vector director
	}


	public void run() {
		
		while(!muerto){
			avanzar();

			try {
				sleep(60);
			}
			catch (InterruptedException e) {e.printStackTrace();}
		}
		//Cuando Enemigo muere, se elimina del AL
		this.muerto = true;
		//this.mapa.getListaEnemigos().remove(this);
	}
	
	
	


	/******************************************************************
	 ***************    Métodos propios del Enemigo		***************
	 ******************************************************************/

	public void avanzar() {
		//mapa.calculaLimitesdelMapa(this);
 		//recalculaVelocidad();
		//disparar();
	}

	public void recalculaVelocidad(){
		this.Vdir = new MyVector(Math.cos(Math.toRadians(getRotation())), Math.sin(Math.toRadians(getRotation())));	//vector director
		this.Vimpulso = this.Vdir.MultiplicaVectores(aceleracion);		//Calcula Vector Impulso
		this.Vf = this.Vact.SumaVectores(Vimpulso);						//calcula Vector final

		if(Vf.getCurrentModule() > velMax) Vf.readjustModule(velMax);
		
		this.x += this.Vf.getX();	//asigna posicion X a la Nave
		this.y += this.Vf.getY();	//asigna posicion Y a la Nave
		this.Vact = this.Vf;		//Vector actual = Vector final
	}

	public synchronized void disparar() {
		//TODO: Dispara a la posicion de la Nave + Su Vector Impulso
		//generator.generaMisil(this);
	}


	/**
	 * Carga las img necesarias del Enemigo
	 */
	private void cargaImgs() {
		try {
			enemigoImg = ImageIO.read(Launcher.class.getResource("/img/enemigo.png"));
		} 
		catch (IOException e) {e.printStackTrace();}
	}

	public void pintaEnemigo(Graphics2D g2d) {
		Graphics2D g = (Graphics2D) g2d.create();
		g.rotate(Math.toRadians(getRotation()), this.getPosX() + this.getWidth()/2, this.getPosY() + this.getHeight()/2 );
		//g.fillRect((int)getPosX(), (int)getPosY(), this.getWidth(), this.getHeight());
		g.drawImage(enemigoImg, (int)this.getPosX(), (int)this.getPosY(), getWidth(), getHeight(), null);
	}

}

