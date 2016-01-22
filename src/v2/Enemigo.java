package v2;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class Enemigo extends Thread{

	private Image enemigoImg; 
	private Mapa mapa;
	private Generador generator;
	public Sonidos sonidos;
	private double x, y, aceleracion;
	private int escala, rotation, rotation_inicial, width, height, contaSleeps;
	private boolean muerto;
	private MyVector Vdir, Vf, Vimpulso;

	///////////////	setters & getters	//////////////////////////////
	public Enemigo getEnemigo(){return this;}
	public Mapa getMapa(){return this.mapa;}
	public int getRotation() {return this.rotation;}
	public int getScale() {return this.escala;}
	protected int getWidth() {return this.width;}
	protected int getHeight() {return this.height;}
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
		this.sonidos = this.mapa.getGenerator().getSonidos();

		this.escala = 2;
		this.width = 55;
		this.height = 45;

		this.y = new Random().nextInt(mapa.getHeight() - 10) + 10;
		this.x = new Random().nextInt(mapa.getWidth() - 10) + 10;
		this.aceleracion = new Random().nextInt(5 - 1) + 1;
		this.contaSleeps=0;
		this.rotation_inicial = 0;
		this.rotation = new Random().nextInt(360-0);	//angulo de movimiento

		//inicializacion Vectores
		this.Vdir = new MyVector(Math.cos(Math.toRadians(this.rotation)),Math.sin(Math.toRadians(this.rotation)));	//vector director
		this.Vimpulso = this.Vdir.MultiplicaVectores(aceleracion);	//Calcula Vector Impulso
		this.Vf = this.Vimpulso;
	}


	//Constructor de Enemigo pequeño
	public Enemigo(Mapa mapa, Generador g, Enemigo enemigo) {
		cargaImgs();
		this.generator = g;
		this.mapa = mapa;
		this.sonidos = this.mapa.getGenerator().getSonidos();

		this.escala = 1;
		this.width = enemigo.getWidth() -25;	//30
		this.height = enemigo.getHeight() - 25;	//20

		this.y = new Random().nextInt(mapa.getHeight() - 10) + 10;
		this.x = new Random().nextInt(mapa.getWidth() - 10) + 10;
		this.aceleracion = new Random().nextInt(10 - 2) + 2;	//+ veloz
		this.contaSleeps=0;
		this.rotation_inicial = 0;
		this.rotation = new Random().nextInt(360-0);	//angulo de movimiento

		//inicializacion Vectores
		this.Vdir = new MyVector(Math.cos(Math.toRadians(this.rotation)),Math.sin(Math.toRadians(this.rotation)));	//vector director
		this.Vimpulso = this.Vdir.MultiplicaVectores(aceleracion);	//Calcula Vector Impulso
		this.Vf = this.Vimpulso;
	}

	public void run() {

		while(!muerto){
			avanzar();
			mapa.chocaObjeto(this);

			if(contaSleeps > 60) {
				this.rotation = new Random().nextInt(300-10);	//ángulo movimiento
				disparar();
				contaSleeps=0;
			}
			try {sleep(60); contaSleeps++;}
			catch (InterruptedException e) {e.printStackTrace();}
		}

	}



	/******************************************************************
	 ***************    Métodos propios del Enemigo		***************
	 ******************************************************************/

	protected void avanzar() {
		mapa.calculaLimitesdelMapa(null,null,this);
 		recalculaVelocidad();
	}

	protected void recalculaVelocidad(){
		this.Vdir = new MyVector(Math.cos(Math.toRadians(getRotation())),Math.sin(Math.toRadians(getRotation())));	//vector director
		this.Vimpulso = this.Vdir.MultiplicaVectores(aceleracion);		//Calcula Vector Impulso
		this.Vf = this.Vimpulso;

   		this.x += this.Vf.getX();	//asigna posicion X
		this.y += this.Vf.getY();	//asigna posicion Y
	}

	protected void disparar() {
		generator.generaMisil(this);
		sonidos.play(sonidos.getDisparoMisil());
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

	protected void pintaEnemigo(Graphics2D g2d) {
		Graphics2D g = (Graphics2D) g2d.create();
		g.rotate(Math.toRadians(rotation_inicial), this.getPosX() + this.getWidth()/2, this.getPosY() + this.getHeight()/2 );
		g.drawImage(enemigoImg, (int)this.getPosX(), (int)this.getPosY(), getWidth(), getHeight(), null);
	}

}

