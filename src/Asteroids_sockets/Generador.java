package Asteroids_sockets;

import java.util.Stack;

public class Generador extends Thread {

	private Frame frame;
	private Mapa mapa;
	private Nave nave;
	private Sonidos sonidos;

	////////////////////////	setters & getters	//////////////////////////////
	public Mapa getMapa() {return this.mapa;}
	public Frame getFrame() {return this.frame;}
	public Sonidos getSonidos() {return this.sonidos;}

	//Constructor
	public Generador() {
		try{
			sonidos = new Sonidos();
		}catch(Exception e){e.printStackTrace();}

		this.frame = new Frame(this);
		this.mapa = new Mapa(this, frame);		//Esta MAPA es el unico MAPA de ESTE juego. Cada juego tendrá su propio MAPA
	}

	public void run(){
		if (mapa.isVisible()){
			muestraMapa();

			while(mapa.isJugando()){
				if(mapa.getListaAsteroides().size() < mapa.getMax_Asteroides() ){
					System.out.println("Generando nuevo asteroide en MAPA ID "+mapa.getMapaID());
					generaAsteroide();
				}

				try {Thread.sleep(10);}
				catch (InterruptedException ex) {ex.printStackTrace();}
			}
		}
	}

	protected void muestraMapa(){
		this.frame.setMapa(this.mapa);
		this.frame.setBounds(100, 100, this.mapa.getWidth(), this.mapa.getHeight());
		mapa.setVisible(true);
		mapa.start();
		System.out.println("GENERADOR inicia mapa");
		//sonidos.loop(sonidos.getSonidoJuego());
	}

	protected void generaNave(Nave nave){
		mapa.getListaNaves().add(0, nave);
		nave.start();
	}
	
	protected void generaNave(int ID){
		Nave nave = new Nave(mapa, this);
		nave.setID(ID);
		mapa.getListaNaves().add(nave);
		nave.start();
	}
	
	protected void generaJugador(int ID){
		Jugador jugador = new Jugador();
		jugador.setIDMando(ID);
		mapa.getListaJugadores().add(jugador);
	}

	protected synchronized void generaEnemigo(){
		Enemigo enemigo = new Enemigo(mapa, this);
		mapa.getMapa().getListaEnemigos().add(enemigo);
		enemigo.start();
		sonidos.play(sonidos.getEnemyBig());
	}
	
	public synchronized void generaEnemigoPequeño(Enemigo enemigo) {
		Enemigo enemySmall = new Enemigo(mapa, this, enemigo);
		mapa.getMapa().getListaEnemigos().add(enemySmall);
		enemySmall.start();
		sonidos.play(sonidos.getEnemySmall());
	}

	protected void generaMisil(Enemigo enemigo) {
		Misil misil = new Misil(enemigo);
		mapa.getListaMisilesEnemigo().add(misil);
		misil.start();
	}

	protected synchronized void generaMisil(Nave nave){
		Misil misil = new Misil(nave);
		mapa.getListaMisiles().add(misil);
		misil.start();
	}

	protected void generaAsteroide(){		
		if(mapa.getListaAsteroides().size() < mapa.getMax_Asteroides()){
			Asteroide asteroid = new Asteroide(mapa);
			mapa.getMapa().getListaAsteroides().add(asteroid);
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

	protected synchronized void generar2Asteroides(Asteroide asteroide) {
		double angulo1 = asteroide.getRotation()+40;
		double angulo2 = asteroide.getRotation()-40;

		Asteroide asteroid1 = new Asteroide(asteroide, mapa, angulo1);
		Asteroide asteroid2 = new Asteroide(asteroide, mapa, angulo2);

		mapa.getMapa().getListaAsteroides().add(asteroid1);
		mapa.getMapa().getListaAsteroides().add(asteroid2);

		asteroid1.start();
		asteroid2.start();
	}

}
