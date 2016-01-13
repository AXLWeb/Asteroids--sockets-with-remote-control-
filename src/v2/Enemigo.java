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

	private Image enemigoImg; 
	private Mapa mapa;
	//private Misil misil;
	private Generador generator;
	private double x, y, aceleracion;
	private int rotation, rotation_inicial, width, height, contaSleeps, contaDisparos;
	private boolean muerto;
	private MyVector Vdir, Vf, Vimpulso;

	///////////////	setters & getters	//////////////////////////////
	public Enemigo getEnemigo(){return this;}
	public Mapa getMapa(){return this.mapa;}
	//public Misil getMisil(){return this.misil;}
	public int getRotation() {return this.rotation;}
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
		this.width = 41;
		this.height = 30;

		this.y = new Random().nextInt(mapa.getHeight() - 10) + 10; //Random().nextInt(high-low) + low;
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


	public void run() {

		while(!muerto){
			avanzar();

			if(contaSleeps > 60) {
				this.rotation = new Random().nextInt(300-10);	//ángulo movimiento
				disparar();
				contaSleeps=0;
			}
			try {sleep(60); contaSleeps++; contaDisparos++;}
			catch (InterruptedException e) {e.printStackTrace();}
		}
	}



	/******************************************************************
	 ***************    Métodos propios del Enemigo		***************
	 ******************************************************************/

	protected void avanzar() {
		mapa.calculaLimitesdelMapa(null,null,this);
 		recalculaVelocidad();
 		//mapa.chocaObjeto(this);
	}

	protected void recalculaVelocidad(){
		this.Vdir = new MyVector(Math.cos(Math.toRadians(getRotation())),Math.sin(Math.toRadians(getRotation())));	//vector director
		this.Vimpulso = this.Vdir.MultiplicaVectores(aceleracion);		//Calcula Vector Impulso
		this.Vf = this.Vimpulso;

   		this.x += this.Vf.getX();	//asigna posicion X
		this.y += this.Vf.getY();	//asigna posicion Y
	}

	protected synchronized void disparar() {
		generator.generaMisil(this);
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

