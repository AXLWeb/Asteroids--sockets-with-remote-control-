package v2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import javax.imageio.ImageIO;

public class Generador extends Thread {

	private Frame frame;
	private Mapa mapa;
	private Nave nave;
	private PantInicial pantInicial;
	private Puntos puntos;
	private Sonidos sonidoEnemigo, sonidoMisil, sonidoMuerte, sonidoJuego;

	///////////////	setters & getters	//////////////////////////////
	public Sonidos getSonidoMisil() {return sonidoMisil;}
	public Sonidos getSonidoJuego() {return sonidoJuego;}
	public Sonidos getSonidoMuerte() {return sonidoMuerte;}
	public Sonidos getSonidoEnemigo() {return sonidoEnemigo;}
	public Mapa getMapa() {return this.mapa;}

	//Constructor
	public Generador() {
		//this.pantInicial = new PantInicial();
		generaMapa();
		generaNave();
		mapa.start();
	}

	public void run(){

		while(mapa.isJugando()){
			if(mapa.getListaAsteroides().size() < mapa.getMax_Asteroides() ){
				generaAsteroide();
				try {this.sleep(1000);}
				catch (InterruptedException ex) {ex.printStackTrace();}
			}
		}
	}

	protected void generaMapa(){
		this.mapa = new Mapa(this);
		this.frame = new Frame();
		this.frame.setMapa(this.mapa);
		this.frame.setBounds(100, 100, this.mapa.getWidth(), this.mapa.getHeight());
		mapa.setVisible(true);

		//Creamos sonido del juego
		//this.sonidoJuego = new Sonidos("/sound/mp3");
		//if(mapa.isJugando()) sonidoJuego.loop();
	}

	protected void generaNave(){
		this.nave = new Nave(mapa, this);
		mapa.setNave(nave);
		nave.start();
	}

	protected synchronized void generaEnemigo(){
		Enemigo enemigo = new Enemigo(mapa, this);
		//mapa.setEnemy(enemigo);
		mapa.getMapa().getListaEnemigos().addElement(enemigo);
		enemigo.start();

		//Crea sonido del enemigo
		//this.sonidoEnemigo = new Sonidos("/sound/mp3");
		//sonidoEnemigo.play();
	}

	protected void generaMisil(Enemigo enemigo) {
		Misil misil = new Misil(enemigo);
		mapa.getListaMisilesEnemigo().addElement(misil);
		misil.start();

		//Crea sonido del Misil lanzado
		//this.sonidoMisil = new Sonidos("/sound/mp3");
		//sonidoMisil.play();
	}

	protected synchronized void generaMisil(Nave nave){
		Misil misil = new Misil(nave);
		mapa.getListaMisiles().addElement(misil);
		misil.start();

		//Crea sonido del Misil lanzado
		this.sonidoMisil = new Sonidos("/sound/beep01.wav");
		sonidoMisil.play();
	}

	protected void generaAsteroide(){
		if(mapa.getListaAsteroides().size() < mapa.getMax_Asteroides()){
			Asteroide asteroid = new Asteroide(mapa);
			mapa.getMapa().getListaAsteroides().addElement(asteroid);
			asteroid.start();
		}
	}

	protected double devuelveEscala(double scale) {
		double escala=0;
		if(scale>0.001 && scale<0.3) escala = 0.25;
		else if(scale>0.3 && scale<0.6) escala = 0.5;
		else if(scale>0.6) escala = 1;

		return escala;
	}

	/**
	 * Genera 2 nuevos Asteroides a partir del Vector del Padre
	 * @param asteroide Padre
	 */
	protected synchronized void generar2Asteroides(Asteroide asteroide) {
		Rectangle posicion = asteroide.getPosicion();
		double escala = devuelveEscala(asteroide.getScale());
		double acel = asteroide.getAceleracion();
		int width = asteroide.getWidth();
		int height = asteroide.getHeight();
		Image img = asteroide.getImg();
		MyVector Vact = asteroide.getVf();
		escala /=2;	//divide escala del Padre /2
		double x = posicion.getX();
		double y = posicion.getY();
		
		//TODO: +40º y -40º, misma VDir (Vf?)

		Asteroide asteroid1 = new Asteroide(mapa, escala, acel, x, y, Vact, img, width/2, height/2);
		Asteroide asteroid2 = new Asteroide(mapa, escala, acel, x, y, Vact, img, width/2, height/2);
		mapa.getMapa().getListaAsteroides().addElement(asteroid1);
		mapa.getMapa().getListaAsteroides().addElement(asteroid2);

		asteroid1.start();
		asteroid2.start();
	}
	
	protected void gameOver() {
		//TODO: mostrar GAME OVER y PUNTOS

		this.puntos = new Puntos();

		String puntos = String.valueOf(nave.getPuntos().getTotal());
		String nombre = nave.getNombreJugador();

		nave.getPuntos().writeStats(puntos, nombre);
	}

}
