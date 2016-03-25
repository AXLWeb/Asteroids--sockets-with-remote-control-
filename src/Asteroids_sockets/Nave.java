package Asteroids_sockets;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class Nave extends Thread  {

	private static final double DECEL_FACTOR = 0.05;
	private static final double aceleracion = 0.25;
	private Image NaveImg,fuegoImg,NaveVidas;
	private Mapa mapa;
	private Misil misil;
	private Generador generator;
	private Sonidos sonidos;
	private Puntos puntos;
	private double x, y, velMax, velMin;
	private int rotation, width, height, vida, vidas;
	private boolean pulsado, disparo, muerto, derecha, izquierda, arriba;
	private MyVector Vdir, Vf, Vact, Vimpulso;
	private int IDmando;

	///////////////	setters & getters	//////////////////////////////
	public Image getImage(){return this.NaveVidas;}
	public void setPulsado(boolean b) {this.pulsado = b;}
	public boolean getPulsado(){return this.pulsado;}
	public void setRotation(int rotation) {this.rotation = rotation;}
	public int getRotation() {return this.rotation;}
	public Nave getNave(){return this;}
	protected void setID(int ID) {this.IDmando = ID;}
	protected int getID(){return this.IDmando;}
	public Mapa getMapa(){return this.mapa;}
	public Misil getMisil(){return this.misil;}
	protected Puntos getPuntos(){return this.puntos;}
	protected void setPuntos(){this.puntos = new Puntos();}
	protected int getTotalPuntos(){return this.puntos.getTotal();}
	protected void setImpulso(boolean b){this.arriba = b;}
	protected void setDisparo(boolean b){this.disparo = b;}
	public boolean getIzquierda(){return this.izquierda;}
	public boolean getDerecha(){return this.derecha;}
	protected synchronized void setIzquierda(boolean b){this.izquierda=b;}
	protected synchronized void setDerecha(boolean b){this.derecha=b;}
	public boolean getImpulso(){return this.arriba;}
	public boolean getDisparo(){return this.disparo;}
	public MyVector getVdir() {return this.Vdir;}
	public MyVector getVact() {return this.Vact;}
	public MyVector getVimp() {return this.Vimpulso;}
	public Rectangle getPosicion() {return new Rectangle((int)getPosX(), (int)getPosY(), getWidth(), getHeight());}
	public boolean isMuerto(){return this.muerto;}
	public synchronized void setMuerto(boolean b){this.muerto = b;}
	public double getPosX() {return x;}
	protected synchronized void setPosX(int x){this.x = x;}
	public double getPosY() {return y;}
	protected synchronized void setPosY(int y){this.y = y;}
	protected int getWidth() {return this.width;}
	protected int getHeight() {return this.height;}
	protected int getVida(){return this.vida;}
	public void setVida(int i) {this.vida = i;}
	protected int getVidas(){return this.vidas;}
	public void setVidas(int i) {this.vidas = i;}
	//protected String getNombreJugador() { return this.nombreJugador;}
	//protected void setNombreJugador(String name) { this.nombreJugador = name;}


	/////////////// Constructor de Nave	///////////////
	public Nave(Mapa mapa, Generador g) {
		this.mapa = mapa;
		this.generator = g;
		this.sonidos = this.mapa.getGenerator().getSonidos();
		//this.nombreJugador = "";
		this.width = 100;
		this.height = 40;
		this.x = mapa.getWidth()/2;
		this.y = mapa.getHeight()/2;
		this.velMax = 10;
		this.velMin = 0.5;
		this.vida = 100;		//empieza 100% de vida
		this.vidas = 3;			//empieza 3 vidas
		this.pulsado = false;	//des-activa inercia
		this.muerto = false;
		this.puntos = new Puntos();
		this.rotation = new Random().nextInt(360-1)+2;	//random de ángulo inicial

		
		//inicializacion Vectores
		this.Vdir = new MyVector(Math.cos(Math.toRadians(getRotation())), Math.sin(Math.toRadians(getRotation())));	//vector director
		this.Vimpulso = this.Vdir.MultiplicaVectores(aceleracion);
		this.Vact = this.Vimpulso;
		this.Vf = this.Vact.SumaVectores(Vimpulso);	//calcula Vector final
		System.out.println("ANGULO TEORICO: "+rotation+" VECTOR DIRECTOR: "+ Vdir.toString());

	}
	
	
	
	public Nave(Mapa mapa, Generador g, int idMando, int vidas, int puntos, boolean entraPorDerecha, double posY, int angulo, double vActX, double vActY, double vImpX, double vImpY){
		/*
		 * 		String comando = nave.getID()+";"+this.IDMapa+";"+"salemapa;"+nave.getVidas()+";"+nave.getTotalPuntos()+";izq;"+nave.getPosY()+";"+nave.getRotation()+";"+nave.getVact().getX()+";"+nave.getVact().getY()
				+";"+nave.getVimp().getX()+";"+nave.getVimp().getY()+";";
		 */
	
		//this.nombreJugador = "";
		this.width = 40;
		this.height = 40;
		this.mapa = mapa;
		this.generator = g;
		this.IDmando = idMando;
		this.vidas = vidas;
		this.puntos = new Puntos(puntos);
		this.pulsado = true;
		this.rotation  = angulo;
		this.Vdir = new MyVector(Math.cos(Math.toRadians(getRotation())), Math.sin(Math.toRadians(getRotation())));	//vector director
		this.Vact = new MyVector(vActX, vActY);
		this.Vimpulso = new MyVector(vImpX, vImpY);
		
		this.y = posY;
		
		System.out.println("CONSTRUCTOR: entraPorDerecha "+entraPorDerecha);
		if (entraPorDerecha){
			this.x = mapa.getWidth();
		}else this.x = 0-this.getWidth();	
		
		//inicializacion Vectores
		//this.Vdir = new MyVector(Math.cos(Math.toRadians(getRotation())), Math.sin(Math.toRadians(getRotation())));	//vector director
		System.out.println("ANGULO TEORICO: "+rotation+" VECTOR DIRECTOR: "+ Vdir.toString());
		//this.Vimpulso = this.Vdir.MultiplicaVectores(aceleracion);
		//this.Vact = this.Vimpulso;
		//this.Vf = this.Vact.SumaVectores(Vimpulso);	//calcula Vector final
		
		this.sonidos = this.mapa.getGenerator().getSonidos();
	}
	
	public void run() {
		cargaImgs();
		while(!muerto){
			//TODO: quitar comentario
			//mapa.chocaObjeto(this);

			if(getPulsado()) avanzar();	//activa inercia

			try {sleep(60);}
			catch (InterruptedException e) {e.printStackTrace();}
		}
		
		killAll();
	}


	/******************************************************************
	 ***************    Métodos propios de la Nave		***************
	 ******************************************************************/

	protected synchronized void restaVidaNave() {if(this.vida>1) this.vida-=33;}	//%de vida
	protected synchronized void quitaVidas() {if(this.vidas>0) this.vidas--;}		//num vidas

	protected void avanzar() {
		mapa.calculaLimitesdelMapa(this, null, null);

		if(getImpulso()) impulsaNave();
		if(getDerecha()) subeRotation();
		if(getIzquierda()) bajaRotation();

		recalculaVelocidad();
	}

	protected void subeRotation() {
		setDerecha(true);
		if(getDerecha() && !getIzquierda()){
			if(getRotation() > 350)	setRotation(0);
			else this.rotation+=5;

			this.Vdir = new MyVector(Math.cos(Math.toRadians(getRotation())), Math.sin(Math.toRadians(getRotation())));	//vector director
		}
	}

	protected void bajaRotation() {
		setIzquierda(true);
		if(getIzquierda() && !getDerecha()){
			if(getRotation() < 1) setRotation(355);
			else this.rotation-=5;

			this.Vdir = new MyVector(Math.cos(Math.toRadians(getRotation())), Math.sin(Math.toRadians(getRotation())));	//vector director
		}
	}

	protected void recalculaVelocidad(){
		this.Vf = this.Vact.SumaVectores(Vimpulso);						//calcula Vector final

		if(!getImpulso()){
			this.Vimpulso = new MyVector(0,0);	//resetea Vimpulso
			sonidos.stop(sonidos.getImpulso());
			if(Vf.getCurrentModule() > velMin) Vf.decelerar(DECEL_FACTOR);
		}
		else if(getImpulso()){
			impulsaNave();
			sonidos.loop(sonidos.getImpulso());	//sonido impulso
			if(Vf.getCurrentModule() > velMax) Vf.readjustModule(velMax);
		}

		this.x += this.Vf.getX();	//asigna posicion X a la Nave
		this.y += this.Vf.getY();	//asigna posicion Y a la Nave
		this.Vact = this.Vf;		//Vector actual = Vector final
	}

	private void impulsaNave() {
		this.Vimpulso = this.Vdir.MultiplicaVectores(aceleracion);		//Calcula Vector Impulso
	}

	protected synchronized void disparar() {
		generator.generaMisil(this);
		sonidos.play(sonidos.getDisparoMisil());
	}

	protected void pintaNave(Graphics2D g2d) {
		Graphics2D g = (Graphics2D) g2d.create();
		g.rotate(Math.toRadians(getRotation()), this.getPosX() + this.getWidth()/2, this.getPosY() + this.getHeight()/2 );

		if(getImpulso()) {
			g.drawImage(fuegoImg, (int)this.getPosX(), (int)this.getPosY()+12, null);
			g.drawImage(NaveImg, (int)this.getPosX(), (int)this.getPosY(), null);
		}
		else g.drawImage(NaveImg, (int)this.getPosX(), (int)this.getPosY(), null);
	}

	private void killAll() {
		if(sonidos.getImpulso().isActive() || sonidos.getImpulso().isRunning()) 
			sonidos.stop(sonidos.getImpulso());
	}

	/**
	 * Carga las img necesarias de la Nave
	 */
	private void cargaImgs() {
		try {
			//fuegoImg = ImageIO.read(Launcher.class.getResource("/img/fuego.png"));
			//System.out.println("Eligiendo img de la nave con ID "+this.IDmando);

			//NaveImg = ImageIO.read(Launcher.class.getResource("/img/nave1.png"));
			
			switch(this.IDmando){
				case 1: 
					NaveImg = ImageIO.read(Launcher.class.getResource("/img/nave1.png"));
					break;
				case 2:
					NaveImg = ImageIO.read(Launcher.class.getResource("/img/nave2.png"));
					break;
				case 3:
					NaveImg = ImageIO.read(Launcher.class.getResource("/img/nave3.png"));
					break;
				case 4:
					NaveImg = ImageIO.read(Launcher.class.getResource("/img/nave4.png"));
					break;
				case 5:
					NaveImg = ImageIO.read(Launcher.class.getResource("/img/nave5.png"));
					break;

				default: 
					NaveImg = ImageIO.read(Launcher.class.getResource("/img/nave0.png"));
					break;
			}
		}
		catch (IOException e) {e.printStackTrace();}
	}

}
