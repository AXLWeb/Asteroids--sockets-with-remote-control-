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
	private static final double aceleracion = 1.5;


	///////////////	setters & getters	//////////////////////////////
	public Nave getNave() {return nave;}
	public void setNave(Nave nave) {this.nave = nave;}
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	public double getX() {return x;}
	public void setX(double x) {this.x = x;}
	public double getY() {return y;}
	public void setY(double y) {this.y = y;}
	public boolean isMuerto() {return this.muerto;}
	public void setMuerto(boolean b) {this.muerto = b;}
	
	public Rectangle getPosicion() {return new Rectangle((int)getX(), (int)getY(), width, height);} //devuelve posicion
	

	//Constructor Misil de Enemigo
	public Misil(Enemigo enemigo){
		this.enemigo = enemigo;
		this.x = enemigo.getPosX() + enemigo.getWidth()/2;
		this.y = enemigo.getPosY() + enemigo.getHeight()/2;
		this.muerto = false;
		this.Vdir = new MyVector(Math.cos(Math.toRadians(enemigo.getRotation())), Math.sin(Math.toRadians(enemigo.getRotation())));	//vector director
		this.Vimpulso = this.Vdir.MultiplicaVectores(Misil.aceleracion);	//Calcula Vector Impulso
		this.Vf = this.Vimpulso;
	}

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

		while((!isMuerto()) && (this.x < mapa.getWidth() && this.x > 0) && (this.y > 0 && this.y < mapa.getHeight())){
 			calculaTrayectoria();
 			System.out.println("run del misil "+this.getName()+", estado: "+this.getState());

			try {sleep(10);} 
			catch (InterruptedException e) {e.printStackTrace();}
		}

		eliminaMisil(this);
		System.out.println("quedan " +mapa.getListaMisiles().size()+" misiles vivos");
		System.out.println("Misil "+this.getName()+", estado: "+this.getState());
	}

	public synchronized void eliminaMisil(Misil misil) {
		System.out.println("Eliminar el misil "+this.getName()+", estado: "+this.getState());
		if(!isMuerto()) misil.setMuerto(true);
		mapa.getListaAsteroides().remove(misil);
	}

	public synchronized void pintaMisil (Graphics2D g2d){
		g2d.setColor(Color.green);
		g2d.fillOval((int)this.x, (int)this.y, Misil.width, Misil.height); 		//pintar Misil
	}

	public synchronized void calculaTrayectoria() {
		this.x += this.Vf.getX();	//asigna posicion X 
		this.y += this.Vf.getY();	//asigna posicion Y
		//mapa.sigueDisparo(this);
	}



}
