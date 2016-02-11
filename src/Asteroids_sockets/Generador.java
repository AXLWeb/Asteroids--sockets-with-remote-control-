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
	//public Nave getNave() {return this.nave;}
	public Sonidos getSonidos() {return this.sonidos;}

	//Constructor
	public Generador() {
		try{
			sonidos = new Sonidos();
		}catch(Exception e){ e.printStackTrace();}

		this.frame = new Frame(this);
		this.mapa = new Mapa(this, frame);
		mapa.setVisible(false);
	}

	public void run(){
		if (mapa.isVisible()){
			while(mapa.isJugando()){
				if(mapa.getListaAsteroides().size() < mapa.getMax_Asteroides() ){
					generaAsteroide();
				}
			}
		}

		try {Thread.sleep(1000);}
		catch (InterruptedException ex) {ex.printStackTrace();}
	}

	protected void guardaDatosCSV(int id) {
		Nave nave = mapa.getNaveByID(id);
		//TODO buscar Nave x ID y guardar sus datos

		String puntos = String.valueOf(nave.getPuntos().getTotal());
		//String nombre = nave.getNombreJugador();
		String nombre = "ABC";
		nombre = nombre.toUpperCase();

		if((nombre != null && nombre != "") && (puntos != null && puntos != ""))
			nave.getPuntos().writeStats(puntos, nombre);		//guarda en CSV
		else
			System.out.println("No se ha podido guarda los datos en el fichero CSV...");
	}


	protected void iniciarJuego() {
		this.mapa = new Mapa(this, frame);
		mapa.setVisible(false);

		//TODO: hacer copia de las naves/jugadores existentes
		Stack<Nave> copiaListaNaves = new Stack<>();

		generaMapa(); //El del juego de VERDAD. Al crear este Mapa se pierde el OTRO (donde se habian guardado las Naves del Server)

		//generaNave(ID);		//TODO: la nave se genera CON el mismo ID del MANDO
		//System.out.println("Generador: Nave creada con ID: "+nave.getID());
		System.out.println("copiaListaNaves.size()="+copiaListaNaves.size());
		System.out.println("Generador: mapa.getListaNaves().size() = "+mapa.getListaNaves().size());
		
		mapa.start();
	}


	protected void generaMapa(){
		this.frame.setMapa(this.mapa);
		this.frame.setBounds(100, 100, this.mapa.getWidth(), this.mapa.getHeight());
		mapa.setVisible(true);
		sonidos.loop(sonidos.getSonidoJuego());
	}

	protected void generaNave(int ID){
		Nave nave = new Nave(mapa, this);
		//mapa.setNave(nave);
		nave.setID(ID);
		mapa.getListaNaves().addElement(nave);
		nave.start();
	}

	protected synchronized void generaEnemigo(){
		Enemigo enemigo = new Enemigo(mapa, this);
		mapa.getMapa().getListaEnemigos().addElement(enemigo);
		enemigo.start();
		sonidos.play(sonidos.getEnemyBig());
	}
	
	public synchronized void generaEnemigoPequeño(Enemigo enemigo) {
		Enemigo enemySmall = new Enemigo(mapa, this, enemigo);
		mapa.getMapa().getListaEnemigos().addElement(enemySmall);
		enemySmall.start();
		sonidos.play(sonidos.getEnemySmall());
	}

	protected void generaMisil(Enemigo enemigo) {
		Misil misil = new Misil(enemigo);
		mapa.getListaMisilesEnemigo().addElement(misil);
		misil.start();
	}

	protected synchronized void generaMisil(Nave nave){
		Misil misil = new Misil(nave);
		mapa.getListaMisiles().addElement(misil);
		misil.start();
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

}
