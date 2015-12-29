package v2;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Nave extends Thread  {

	private static final double DECEL_FACTOR = 0.015;
	private static final double aceleracion = 0.25;
	private Image NaveImg,fuegoImg;
	private Mapa mapa;
	private Misil misil;
	private Generador generator;

	private double x, y, velMax, velMin;
	private int rotation, vida, contaSleeps;
	private boolean pulsado, disparo, muerto, derecha, izquierda, arriba;
	private MyVector Vdir, Vf, Vact, Vimpulso, Vinercia;
	private int width, height;
	double bajaVel;

	///////////////	setters & getters	//////////////////////////////
	public void setPulsado(boolean b) {this.pulsado = b;}
	public boolean getPulsado(){return this.pulsado;}
	public void setRotation(int rotation) {this.rotation = rotation;}
	public int getRotation() {return this.rotation;}
	public Nave getNave(){return this;}
	public Mapa getMapa(){return this.mapa;}
	public Misil getMisil(){return this.misil;}
	protected void setIzquierda(boolean b){this.izquierda=b;}
	protected void setArriba(boolean b){this.arriba = b;}
	protected void setDisparo(boolean b){this.disparo = b;}
	public boolean getIzquierda(){return this.izquierda;}
	public boolean getDerecha(){return this.derecha;}
	public boolean getArriba(){return this.arriba;}
	public boolean getDisparo(){return this.disparo;}
	public synchronized MyVector getVdir() {return this.Vdir;}
	public synchronized Rectangle getPosicion() {return new Rectangle((int)getPosX(), (int)getPosY(), getWidth(), getHeight());} //devuelve posicion
	public synchronized boolean isMuerto(){return this.muerto;}
	public synchronized void setMuerto(boolean b){this.muerto = b;}
	public synchronized double getPosX() {return x;}
	protected synchronized void setPosX(int x){this.x = x;}
	public synchronized double getPosY() {return y;}
	protected synchronized void setPosY(int y){this.y = y;}
	protected synchronized int getWidth() {return this.width;}
	protected synchronized int getHeight() {return this.height;}
	protected synchronized void setDerecha(boolean b){this.derecha=b;}
	protected synchronized int getVida(){return this.vida;}
	protected synchronized void setVida(int i){this.vida=i;}
	
	
	/////////////// Constructor de Nave	///////////////
	public Nave(Mapa mapa, Generador g) {
		cargaImgs();
		this.mapa = mapa;
		this.generator = g;
		this.width = 100;
		this.height = 40;
		this.x = mapa.getWidth()/2;
		this.y = mapa.getHeight()/2;

		this.velMax = 10;
		this.velMin = 0.5;
		this.vida = 100;	//empieza 100% de vida
		this.pulsado = false;
		this.muerto = false;
		this.contaSleeps=0;
		this.rotation = new Random().nextInt(360-0);	//random de ángulo inicial
		
		//inicializacion Vectores
		this.Vact = new MyVector(0, 0);		//Vector actual
		this.Vdir = new MyVector((int)Math.cos(rotation), (int)Math.sin(rotation));	//vector director
		this.Vinercia = new MyVector(DECEL_FACTOR,DECEL_FACTOR);	//vector inercia
	}
	
	public void run() {

		while(!muerto){
			if(getArriba()) avanzar();

			try {
				sleep(60);
				contaSleeps++;	//contador sleeps para cambiar de img +1
				//System.out.println("sleeps: "+contaSleeps);
			}
			catch (InterruptedException e) {e.printStackTrace();}
		}
	}



	/******************************************************************
	 ***************    Métodos propios de la Nave		***************
	 ******************************************************************/

	public void avanzar() {
		mapa.calculaLimitesdelMapa(this);

		//control de movimiento Nave
		if(getIzquierda() && getDerecha() && getArriba()){
			//Avanza. No gira
			recalculaVelocidad();
		}
		else if(!getIzquierda() && getDerecha() && getArriba()){
			//avanza + drxa
			recalculaVelocidad();
			bajaRotation();
		}
		else if(getIzquierda() && !getDerecha() && getArriba()){
			//avanza + izqda
			recalculaVelocidad();
			subeRotation();
		}
		else if(!getIzquierda() && !getDerecha() && getArriba()){
			//avanza recto
			recalculaVelocidad();
		}
		else if(!getIzquierda() && !getDerecha() && !getArriba()){
			recalculaVelocidad();
		}
	}

	public void subeRotation() {
		setIzquierda(true);
		if(!getDerecha() && getIzquierda()){
			if(getRotation() > 350)	setRotation(0);
			else this.rotation+=5;

			this.Vdir = new MyVector(Math.cos(Math.toRadians(getRotation())), Math.sin(Math.toRadians(getRotation())));	//vector director
		}
	}

	public void bajaRotation() {
		setDerecha(true);
		if(!getIzquierda() && getDerecha()){
			if(getRotation() < 1) setRotation(355);
			else this.rotation-=5;

			this.Vdir = new MyVector(Math.cos(Math.toRadians(getRotation())), Math.sin(Math.toRadians(getRotation())));	//vector director
		}
	}
	
	public void recalculaVelocidad(){
		this.Vdir = new MyVector(Math.cos(Math.toRadians(getRotation())), Math.sin(Math.toRadians(getRotation())));	//vector director
		this.Vimpulso = this.Vdir.MultiplicaVectores(aceleracion);		//Calcula Vector Impulso
		this.Vf = this.Vact.SumaVectores(Vimpulso);						//calcula Vector final

		//double velActual = Vf.getCurrentModule();

		if(!getPulsado()){
			//resta velActual hasta llegar velMin
			if(Vf.getCurrentModule() > velMin) {
				//TODO: decelerar nave hasta velMin...
				Vf.decelerar(DECEL_FACTOR);
			}
		}
		else if(getPulsado()){
			if(Vf.getCurrentModule() > velMax) Vf.readjustModule(velMax);
		}
		this.x += this.Vf.getX();	//asigna posicion X a la Nave
		this.y += this.Vf.getY();	//asigna posicion Y a la Nave
		//System.out.println("Vector final => "+this.Vf.toString());
		this.Vact = this.Vf;		//Vector actual = Vector final
	}

	public synchronized void disparar() {
		//if(!getDisparo()) setDisparo(true);
		
		generator.generaMisil(this);
	}

	public void pintaNave(Graphics2D g2d) {
		Graphics2D g = (Graphics2D) g2d.create();
		g.rotate(Math.toRadians(getRotation()), this.getPosX() + this.getWidth()/2, this.getPosY() + this.getHeight()/2 );

		if(getPulsado()) {
			//g.fillRect((int)getPosX(), (int)getPosY(), this.getWidth(), this.getHeight());
			g.drawImage(fuegoImg, (int)this.getPosX(), (int)this.getPosY()+8, null);
			g.drawImage(NaveImg, (int)this.getPosX(), (int)this.getPosY(), null);
		}
		else g.drawImage(NaveImg, (int)this.getPosX(), (int)this.getPosY(), null);
	}
	
	/**
	 * Carga las img necesarias de la Nave
	 */
	private void cargaImgs() {
		try {
			fuegoImg = ImageIO.read(Launcher.class.getResource("/img/fuego.png"));
			NaveImg = ImageIO.read(Launcher.class.getResource("/img/nave1.png"));
		}
		catch (IOException e) {e.printStackTrace();}
	}

}
