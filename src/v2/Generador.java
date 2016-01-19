package v2;

import java.awt.Image;
import java.awt.Rectangle;

public class Generador extends Thread {

	private Frame frame;
	private Mapa mapa;
	private Nave nave;
	private PantInicial pantInicial;
	private Sonidos sonidos;

	////////////////////////	setters & getters	//////////////////////////////
	public Mapa getMapa() {return this.mapa;}
	public Frame getFrame(){return this.frame;}
	public Nave getNave() {return this.nave;}
	public Sonidos getSonidos() {return this.sonidos;}

	//Constructor
	public Generador() {
		//Instancia sonidos
		sonidos = new Sonidos();
		
		this.frame = new Frame(this);
		this.mapa = new Mapa(this);
		mapa.setVisible(false);

		this.pantInicial = new PantInicial(this, frame);
		new Thread(pantInicial).start();
		
	}

	public void run(){
		if (mapa.isVisible()){
			while(mapa.isJugando()){
				if(mapa.getListaAsteroides().size() < mapa.getMax_Asteroides() ){
					generaAsteroide();
					try {this.sleep(1000);}
					catch (InterruptedException ex) {ex.printStackTrace();}
				}
			}
		}
	}

	protected void generaMapa(){

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
		mapa.getMapa().getListaEnemigos().addElement(enemigo);
		enemigo.start();

		//Crea sonido del enemigo
		//this.sonidoEnemigo = new Sonidos("/sound/mp3");
		//sonidoEnemigo.play();
	}
	
	public void generaEnemigoPequeño(Enemigo enemigo) {
		Enemigo enemySmall = new Enemigo(mapa, this, enemigo);
		mapa.getMapa().getListaEnemigos().addElement(enemySmall);
		enemySmall.start();

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
		//sonidos.play(sonidos.getDisparoMisil());		//sonidoMisil.play();		//Esto = PLAY clip
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
		if(scale<0.3) escala = 0.25;
		else if(scale>0.3 && scale<0.6) escala = 0.5;
		else if(scale>0.6) escala = 1;

		return escala;
	}

	/**
	 * Genera 2 nuevos Asteroides a partir del Vector del Padre
	 * @param asteroide Padre
	 */
	protected synchronized void generar2Asteroides(Asteroide asteroide) {
		double angulo1 = asteroide.getRotation()+40;
		double angulo2 = asteroide.getRotation()-40;

		Asteroide asteroid1 = new Asteroide(asteroide, mapa, angulo1);
		Asteroide asteroid2 = new Asteroide(asteroide, mapa, angulo2);

		mapa.getMapa().getListaAsteroides().addElement(asteroid1);
		mapa.getMapa().getListaAsteroides().addElement(asteroid2);

		asteroid1.start();
		asteroid2.start();
	}
	
	protected void gameOver() {
		//getSonidoJuego().stop();

		String puntos = String.valueOf(nave.getPuntos().getTotal());
		String nombre = nave.getNombreJugador();
		nave.getPuntos().writeStats(puntos, nombre);
	}

	public void iniciarJuego() {
		pantInicial.setVisible(false);
		pantInicial.kill();	//kill todos los hilos q haya. Clear ArrayLists...vidas=3.....

		generaMapa();
		generaNave();
		mapa.start();
	}



}
