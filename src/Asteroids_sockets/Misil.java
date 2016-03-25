package Asteroids_sockets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

public class Misil extends Thread {
	private Nave nave;
	private Mapa mapa;
	private Enemigo enemigo;
	private double x, y;
	private boolean muerto;
	private MyVector Vdir, Vimpulso, Vf;
	
	//Constantes
	private static final int width = 7;
	private static final int height = 7;
	private static final int widthEnemy = 5;
	private static final int heightEnemy = 5;
	private static final double aceleracion = 15;


	///////////////	setters & getters	//////////////////////////////
	public Nave getNave() {return nave;}
	public void setNave(Nave nave) {this.nave = nave;}
	public Enemigo getEnemigo() {return enemigo;}
	public void setEnemigo(Enemigo e) {this.enemigo = e;}
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	public double getX() {return x;}
	public synchronized void setX(double x) {this.x = x;}
	public double getY() {return y;}
	public synchronized void setY(double y) {this.y = y;}
	public boolean isMuerto() {return this.muerto;}
	public synchronized void setMuerto(boolean b) {this.muerto = b;}
	public Rectangle getPosicion() {return new Rectangle((int)getX(), (int)getY(), getWidth(), getHeight());} //devuelve posicion


	//Constructor Misil de Nave
	public Misil(Nave nave){
		this.nave = nave;
		this.enemigo=null;
		this.mapa = nave.getMapa();
		this.x = nave.getPosX() + nave.getWidth()/2;
		this.y = nave.getPosY() + nave.getHeight()/2 - 2;
		this.muerto = false;
		this.Vdir = new MyVector(Math.cos(Math.toRadians(nave.getRotation())), Math.sin(Math.toRadians(nave.getRotation())));	//vector director
		this.Vimpulso = this.Vdir.MultiplicaVectores(Misil.aceleracion);	//Calcula Vector Impulso
		this.Vf = this.Vimpulso;
	}

	//Constructor Misil del Enemigo
	public Misil(Enemigo e) {
		this.nave=null;
		this.enemigo = e;
		this.mapa = e.getMapa();
		this.x = e.getPosX() + e.getWidth()/2;
		this.y = e.getPosY() + e.getHeight()/2;
		this.muerto = false;

		double angulo = Math.atan2(mapa.getRandomNave().getPosY()-y, mapa.getRandomNave().getPosX()-x);	//Calcula angulo (en radianes) segun la posicion Nave
		if(angulo < 0) angulo = new Random().nextDouble();	//por si es nulo (cuando no hay naves)
		this.Vdir = new MyVector(Math.cos(angulo), Math.sin(angulo));		//Crea Vector director en base al angulo
		this.Vimpulso = this.Vdir.MultiplicaVectores(Misil.aceleracion);	//Calcula Vector Impulso
		this.Vf = this.Vimpulso;
	}

	public void run() {
		while(!isMuerto()){
 			calculaTrayectoria();
			try {sleep(40);} 
			catch (InterruptedException e) {e.printStackTrace();}
		}
	}

	protected void pintaMisilEnemigo (Graphics2D g2d){
		Graphics2D g = (Graphics2D) g2d.create();
		g.setColor(Color.white);
		g.fillOval((int)this.x, (int)this.y, Misil.widthEnemy, Misil.heightEnemy);
	}

	protected void pintaMisilNave (Graphics2D g2d){
		g2d.setColor(Color.green);
		g2d.fillOval((int)this.x, (int)this.y, Misil.width, Misil.height);
	}

	/**
	 * Calcula trayectoria de Misil vivo
	 */
	protected void calculaTrayectoria() {
		mapa.sigueDisparo(this);
		this.x += this.Vf.getX();	//asigna posicion X
		this.y += this.Vf.getY();	//asigna posicion Y
	}

}
