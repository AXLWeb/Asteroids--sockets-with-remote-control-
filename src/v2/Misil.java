package v2;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;

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
	private static final double aceleracion = 15;


	///////////////	setters & getters	//////////////////////////////
	public Nave getNave() {return nave;}
	public void setNave(Nave nave) {this.nave = nave;}
	public Enemigo getEnemigo() {return enemigo;}
	public void setEnemigo(Enemigo e) {this.enemigo = e;}
	public synchronized int getWidth() {return width;}
	public synchronized int getHeight() {return height;}
	public synchronized double getX() {return x;}
	public synchronized void setX(double x) {this.x = x;}
	public synchronized double getY() {return y;}
	public synchronized void setY(double y) {this.y = y;}
	public synchronized boolean isMuerto() {return this.muerto;}
	public synchronized void setMuerto(boolean b) {this.muerto = b;}
	public synchronized Rectangle getPosicion() {return new Rectangle((int)getX(), (int)getY(), width, height);} //devuelve posicion


	//Constructor Misil de Nave
	public Misil(Nave nave){
		this.nave = nave;
		this.mapa = nave.getMapa();
		this.x = nave.getPosX() + nave.getWidth()/2;
		this.y = nave.getPosY() + nave.getHeight()/2 - 2;
		this.muerto = false;
		this.Vdir = new MyVector(Math.cos(Math.toRadians(nave.getRotation())), Math.sin(Math.toRadians(nave.getRotation())));	//vector director
		this.Vimpulso = this.Vdir.MultiplicaVectores(Misil.aceleracion);	//Calcula Vector Impulso
		this.Vf = this.Vimpulso;
	}

	public void run() {

		try{
			while((!isMuerto()) && (this.getX() < mapa.getWidth() && this.getX() > 0) && (this.getY() > 0 && this.getY() < mapa.getHeight())){
			//while(!isMuerto()){
	 			calculaTrayectoria();
				try {sleep(40);} 
				catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		catch(Exception e){e.printStackTrace();}
	}

	public synchronized void pintaMisil (Graphics2D g2d){
		g2d.setColor(Color.green);
		g2d.fillOval((int)this.x, (int)this.y, Misil.width, Misil.height); 		//pintar Misil
	}

	/**
	 * Calcula trayectoria de Misil vivo
	 */
	public synchronized void calculaTrayectoria() {
		this.x += this.Vf.getX();	//asigna posicion X
		this.y += this.Vf.getY();	//asigna posicion Y
		mapa.sigueDisparo(this);
	}

}
