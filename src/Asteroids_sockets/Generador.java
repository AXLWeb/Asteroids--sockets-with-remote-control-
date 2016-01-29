package Asteroids_sockets;

public class Generador extends Thread {

	private Frame frame;
	private Mapa mapa;
	private Nave nave;
	private PantInicial pantInicial;
	private pantScores pantScores;
	private pantNombre pantNombre;
	private Sonidos sonidos;
	private Server server;

	////////////////////////	setters & getters	//////////////////////////////
	public Server getServer(){return this.server;}
	public Mapa getMapa() {return this.mapa;}
	public Frame getFrame() {return this.frame;}
	public Nave getNave() {return this.nave;}
	public Sonidos getSonidos() {return this.sonidos;}
	public pantNombre getPantNombre() {return this.pantNombre;}

	//Constructor
	public Generador() {
		try{
			sonidos = new Sonidos();
		}catch(Exception e){ e.printStackTrace();}

		this.frame = new Frame(this);
		this.mapa = new Mapa(this, frame);
		mapa.setVisible(false);

		this.pantInicial = new PantInicial(this, frame, mapa);
		pantInicial.setVisible(true);

		this.server = new Server(this);
		server.start();
	}

	public void run(){

		if(pantInicial.isVisible()){
			generaMapaPrevio();
			generaAsteroide();
			server.conect();
		}
		
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

	protected void guardaDatosCSV() {
		String puntos = String.valueOf(nave.getPuntos().getTotal());
		String nombre = nave.getNombreJugador();
		nombre = nombre.toUpperCase();

		if((nombre != null && nombre != "") && (puntos != null && puntos != ""))
			nave.getPuntos().writeStats(puntos, nombre);		//guarda en CSV
		else
			System.out.println("No se ha podido guarda los datos en el fichero CSV...");
	}
	
	protected void verStats() {
		this.pantScores = new pantScores(this, frame);
		new Thread(pantScores).start();
	}

	protected void cogeNombreJugador() {
		this.pantNombre = new pantNombre(this, frame); 
		new Thread(pantNombre).start();
	}

	protected void iniciarJuego() {
		this.mapa = new Mapa(this, frame);
		mapa.setVisible(false);

		generaMapa();
		generaNave();
		mapa.start();
	}
	
	protected void iniciarMapaPrevio(){
		this.mapa = new Mapa(this, frame);
		mapa.setVisible(true);
		this.pantInicial = new PantInicial(this, frame, mapa);
		pantInicial.setVisible(true);
		this.frame.pack();
		generaMapaPrevio();
		generaAsteroide();
	}
	
	protected void generaMapaPrevio(){
		this.frame.setPantInicial(this.pantInicial);
		this.frame.setBounds(100, 100, this.pantInicial.getWidth(), this.pantInicial.getHeight());
		this.frame.setTitle("Asteroids");
		pantInicial.setVisible(true);
		new Thread(pantInicial).start();
	}

	protected void generaMapa(){
		this.frame.setMapa(this.mapa);
		this.frame.setBounds(100, 100, this.mapa.getWidth(), this.mapa.getHeight());
		mapa.setVisible(true);
		sonidos.loop(sonidos.getSonidoJuego());
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
