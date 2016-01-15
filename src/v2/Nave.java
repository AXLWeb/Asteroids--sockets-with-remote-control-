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

	private static final double DECEL_FACTOR = 0.05;
	private static final double aceleracion = 0.25;
	private Image NaveImg,fuegoImg;
	private Mapa mapa;
	private Misil misil;
	private Generador generator;
	private Puntos puntos;
	private double x, y, velMax, velMin;
	private int rotation, width, height, vida, vidas, contaSleeps;
	private boolean pulsado, disparo, muerto, derecha, izquierda, arriba;
	private MyVector Vdir, Vf, Vact, Vimpulso;


	///////////////	setters & getters	//////////////////////////////
	public void setPulsado(boolean b) {this.pulsado = b;}
	public boolean getPulsado(){return this.pulsado;}
	public void setRotation(int rotation) {this.rotation = rotation;}
	public int getRotation() {return this.rotation;}
	public Nave getNave(){return this;}
	public Mapa getMapa(){return this.mapa;}
	public Misil getMisil(){return this.misil;}
	protected Puntos getPuntos(){return this.puntos;}
	protected void setIzquierda(boolean b){this.izquierda=b;}
	protected void setImpulso(boolean b){this.arriba = b;}
	protected void setDisparo(boolean b){this.disparo = b;}
	public boolean getIzquierda(){return this.izquierda;}
	public boolean getDerecha(){return this.derecha;}
	public boolean getImpulso(){return this.arriba;}
	public boolean getDisparo(){return this.disparo;}
	public MyVector getVdir() {return this.Vdir;}
	public Rectangle getPosicion() {return new Rectangle((int)getPosX(), (int)getPosY(), getWidth(), getHeight());}
	public boolean isMuerto(){return this.muerto;}
	public synchronized void setMuerto(boolean b){this.muerto = b;}
	public double getPosX() {return x;}
	protected synchronized void setPosX(int x){this.x = x;}
	public double getPosY() {return y;}
	protected synchronized void setPosY(int y){this.y = y;}
	protected int getWidth() {return this.width;}
	protected int getHeight() {return this.height;}
	protected synchronized void setDerecha(boolean b){this.derecha=b;}
	protected int getVida(){return this.vida;}
	protected int getVidas(){return this.vidas;}
	protected synchronized void restaVidasNave() {this.vida--;}
	


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
		this.vida = 100;		//empieza 100% de vida
		this.vidas = 3;			//empieza 3 vidas
		this.pulsado = false;	//des-activa inercia
		this.muerto = false;
		this.puntos = new Puntos();
		this.contaSleeps=0;
		this.rotation = new Random().nextInt(360-1)+2;	//random de ángulo inicial

		//inicializacion Vectores
		this.Vdir = new MyVector(Math.cos(rotation), Math.sin(rotation));	//vector director
		this.Vimpulso = this.Vdir.MultiplicaVectores(aceleracion);
		this.Vact = this.Vimpulso;
		this.Vf = this.Vact.SumaVectores(Vimpulso);	//calcula Vector final
	}
	
	public void run() {

		while(!muerto){
			mapa.chocaObjeto(this);
			if(getPulsado()) avanzar();	//activa inercia

			try {sleep(60);}
			catch (InterruptedException e) {e.printStackTrace();}
		}
	}



	/******************************************************************
	 ***************    Métodos propios de la Nave		***************
	 ******************************************************************/

	protected void quitaVidas() {if(this.vidas>0) this.vidas--;}
	
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
			//resta velActual hasta llegar velMin
			this.Vimpulso = new MyVector(0,0);	//resetea Vimpulso
			if(Vf.getCurrentModule() > velMin) Vf.decelerar(DECEL_FACTOR);
		}
		else if(getImpulso()){
			impulsaNave();
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
	}

	protected void pintaNave(Graphics2D g2d) {
		Graphics2D g = (Graphics2D) g2d.create();
		g.rotate(Math.toRadians(getRotation()), this.getPosX() + this.getWidth()/2, this.getPosY() + this.getHeight()/2 );

		if(getImpulso()) {
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
