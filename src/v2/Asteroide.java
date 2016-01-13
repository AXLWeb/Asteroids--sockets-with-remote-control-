package v2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Asteroide extends Thread{

	protected int width;
	protected int height;
	protected boolean muerto;
	private double x, y, aceleracion, rotation, factorRotacion, escala;
	private MyVector Vdir, Vimpulso, Vf, Vact;
	private Mapa mapa;
	private Image imgAsteroide, asteroide0, asteroide1, asteroide2, asteroide3;
	protected Stack<Image> listaImgAsteroides = new Stack<>();

	///////////////	setters & getters	//////////////////////////////	
	public double getPosX() {return this.x;}
	public void setPosX(int x) {this.x = x;}
	public double getPosY() {return this.y;}
	public void setPosY(int y) {this.y = y;}
	public int getWidth(){return this.width;}
	public int getHeight(){return this.height;}
	public boolean isMuerto() {return this.muerto;}
	protected void setMuerto(boolean b) {this.muerto = b;}
	public double getRotation() {return this.rotation;}
	public double getScale() {return this.escala;}
	public double getAceleracion() {return this.aceleracion;}
	public Rectangle getPosicion() {return new Rectangle((int)getPosX(), (int)getPosY(), getWidth(), getHeight());} //devuelve posicion
	public MyVector getVf() {return this.Vact;}
	public Image getImg() {return this.imgAsteroide;}


	//Constructor para cuando se divide el Asteroide
	public Asteroide(Mapa mapa, double escala, double a, double x, double y, MyVector Vant, Image img, int width, int height){
		cargaImagenes();
		this.escala = escala;
		this.aceleracion = a;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.muerto = false;
		this.factorRotacion = new Random().nextInt(7 - 2) + 1;
		this.mapa = mapa;		
		this.imgAsteroide = img;

		this.rotation = new Random().nextInt(360 - 0) + 0;
		this.Vact = Vant; //Vector anterior del Padre

		this.Vdir = new MyVector(Math.cos(Math.toRadians(this.rotation)),Math.sin(Math.toRadians(this.rotation)));
		this.Vimpulso = this.Vdir.MultiplicaVectores(aceleracion);
		this.Vf = this.Vimpulso;
	}

	//Constructor por defecto del Asteroide
	public Asteroide(Mapa mapa){
		//cargar las imagenes de los  tipos de asteroide
		cargaImagenes();
		this.width = 76;
		this.height = 76;
   		this.escala = 1;
		this.muerto = false;
		this.factorRotacion = new Random().nextInt(7 - 2) + 1;
		this.aceleracion = new Random().nextInt(5 - 1) + 1;
		this.mapa = mapa;
		this.y = new Random().nextInt(mapa.getHeight() - 10) + 10; //Random().nextInt(high-low) + low;
		this.x = new Random().nextInt(mapa.getWidth() - 10) + 10;	

		this.rotation = new Random().nextInt(360 - 0) + 0;
		this.Vdir = new MyVector(Math.cos(Math.toRadians(this.rotation)),Math.sin(Math.toRadians(this.rotation)));	//vector director
		this.Vimpulso = this.Vdir.MultiplicaVectores(aceleracion);	//Calcula Vector Impulso
		this.Vf = this.Vimpulso;
	}

	public void run() {
		while(!isMuerto()){
			mapa.calculaLimitesdelMapa(null, this, null);
			movimiento();
			try {sleep(40);} 
			catch (InterruptedException e) {e.printStackTrace();}
		}
	}

	protected synchronized void movimiento() {
		this.x += this.Vf.getX();	//asigna posicion X 
		this.y += this.Vf.getY();	//asigna posicion Y

		if(rotation > 359) this.rotation = 1;
		this.rotation += factorRotacion;		
	}

	protected synchronized void pintaAsteroide(Graphics2D g2d){
		Graphics2D g = (Graphics2D) g2d.create();	//crea el objeto a partir de otro graphics
		g.setColor(Color.red);
		//g.fillRect((int)x,(int)y,getWidth(), getHeight());
		g.rotate(Math.toRadians(getRotation()), this.getPosX() + this.width/2, this.getPosY() + this.height/2 );
		g.drawImage(imgAsteroide, (int)this.getPosX(), (int)this.getPosY(), getWidth(), getHeight(), null);
	}

	/**
	 * Carga las img de los Asteroides
	 */
	private void cargaImagenes() {
		try {
			asteroide0 = ImageIO.read(Launcher.class.getResource("/img/asteroide.png"));
			asteroide1 = ImageIO.read(Launcher.class.getResource("/img/asteroides/1.png"));
			asteroide2 = ImageIO.read(Launcher.class.getResource("/img/asteroides/2.png"));
			asteroide3 = ImageIO.read(Launcher.class.getResource("/img/asteroides/3.png"));
			//Las guarda en el Array
			listaImgAsteroides.push(asteroide0);
			listaImgAsteroides.push(asteroide1);
			listaImgAsteroides.push(asteroide2);
			listaImgAsteroides.push(asteroide3);

			imgAsteroide = listaImgAsteroides.get(new Random().nextInt(3 - 0) + 0);	//asignar img aleatoria al Asteroide
		} 
		catch (IOException e) {e.printStackTrace();}
	}


}
