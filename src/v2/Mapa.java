package v2;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import javax.sql.rowset.spi.SyncResolver;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Mapa extends Canvas implements Runnable, KeyListener {

	private static final String imagePath="/img/space_background.jpg";
	private URL imgFondo = Mapa.class.getResource(imagePath); 
	private ImageIcon icoFondo = new ImageIcon(imgFondo);
	private Image fondo = icoFondo.getImage();
	private static final long serialVersionUID = 1L;
	private static Thread t;
	private Nave nave;
	private Enemigo enemigo;
	private Generador generator;
	private int max_Asteroides;
	private Stack<Misil> listaMisiles = new Stack<>();
	private Stack<Asteroide> listaAsteroides = new Stack<>();
	private Stack<Enemigo> listaEnemigos = new Stack<>();
	private int contAsteroidesMuertos;

	///////////////	setters & getters	//////////////////////////////
	public void setNave(Nave n) {this.nave = n;}
	public void setEnemy(Enemigo e){this.enemigo = e;}
	public Nave getNave() {return this.nave;}
	public Mapa getMapa() {return this;}
	public int getMax_Asteroides(){return this.max_Asteroides;}
	public Stack<Misil> getListaMisiles() {return this.listaMisiles;}
	public Stack<Asteroide> getListaAsteroides() {return listaAsteroides;}
	public Stack<Enemigo> getListaEnemigos() {return listaEnemigos;}

	@Override
	public Dimension getPreferredSize() {return new Dimension(getWidth(), getHeight());}


	//Constructor de Mapa
	public Mapa(Generador g){
		//Creamos el panel JPanel con sus propiedades
		setBounds(0,0,999,599);
		requestFocus();
		setFocusable(true);

		//inicializamos propiedades del Mapa
		this.max_Asteroides = 2;
		this.addKeyListener(this);
		this.generator = g;
		this.contAsteroidesMuertos=0;
	}


	/**
	 * Crea / Coge hilo
	 */
	public void start() {
		if(t==null){
			t = new Thread(this);
			t.start();
		}
		else t.start();
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		this.createBufferStrategy(2);

		while(true){
			paint();
			try {t.sleep(16);} //60fps
			catch (InterruptedException e) {e.printStackTrace();}
		}
	}

	/**
	 * Comprueba si el misil choca o debe morir
	 */
	public void sigueDisparo(Misil misil) {
		Rectangle misil_actual = misil.getPosicion();	//posicion del misil actual
		//System.out.println("Posicion actual del misil: "+ misil_actual);
		//System.out.println(" ");

		if(misil.getX() > (this.getWidth()) || (misil.getX() < 0) ) misil.setMuerto(true);
		else if(misil.getY() > (this.getHeight()) || (misil.getY() < 0) ) misil.setMuerto(true);
		
		else if(misilChocaAsteroide(misil_actual)) misil.setMuerto(true);

		else if(misilChocaEnemigo(misil_actual)) misil.setMuerto(true);

		//if(misilChocaNave(misil_actual)) misil.setMuerto(true);
	}


	private boolean misilChocaEnemigo(Rectangle misil_actual) {
		//Comprueba si Misil choca con algun Enemigo
		boolean misil_muere=false;
		for(int i=0; i<getListaEnemigos().size(); i++){
			Rectangle enemigo_actual = getListaEnemigos().get(i).getPosicion();
			if(chocan2Objetos(misil_actual, enemigo_actual)){
				//System.out.println("Misil choca con Enemigo");
				getListaEnemigos().get(i).setMuerto(true);
				misil_muere = true;
			}
		}
		return misil_muere;
	}


	protected boolean misilChocaAsteroide(Rectangle misil_actual) {
		//Comprueba si Misil choca con algun Asteroide
		boolean misil_muere=false;

		for(int j=0; j < getListaAsteroides().size(); j++){
			Rectangle asteroide_actual = getListaAsteroides().get(j).getPosicion();
			//System.out.println("xxxxxxxxxxxxx"+j);

			if(chocan2Objetos(misil_actual,asteroide_actual)){
				System.out.println("Misil choca con Asteroide");
				/*
				getListaAsteroides().get(j).setMuerto(true);
				misil_muere = true;
				double sk = getListaAsteroides().get(j).getScale();
				
				contAsteroidesMuertos++;

				//Crear 2 new Asteroides + pequeños
				if(sk > 0.25 && getListaAsteroides().get(j).isMuerto()) {
					//generator.generar2Asteroides(getListaAsteroides().get(j));
					System.out.println("zzzzzzzzzzzzzzzzzzzz");
				}
				*/
			}
		}
		return misil_muere;
	}

	/**
	 * Comprueba si el objeto sale de los límites del mapa
	 * @param movil
	 */
	public void calculaLimitesdelMapa(Nave nave, Asteroide asteroide, Enemigo e){
		//Control al salir del mapa de la >>> Nave <<<
		if(nave != null){
			if(nave.getPosX() > (this.getWidth())) nave.setPosX(-80);
			else if(nave.getPosX() < -80) nave.setPosX(this.getWidth());

			if(nave.getPosY() > (this.getHeight())) nave.setPosY(-40); 
			else if(nave.getPosY() < -40) nave.setPosY(this.getHeight());
		}
		//Control al salir del mapa de la >>> Asteroide <<<
		else if(asteroide != null){
			if(asteroide.getPosX() > (this.getWidth())) asteroide.setPosX(-80);
			else if(asteroide.getPosX() < -80) asteroide.setPosX(this.getWidth());

			if(asteroide.getPosY() > (this.getHeight())) asteroide.setPosY(-40); 
			else if(asteroide.getPosY() < -40) asteroide.setPosY(this.getHeight());
		}
		//Control al salir del mapa de la >>> Enemigo <<<		
		else if( e != null){
			if(e.getPosX() > (this.getWidth())) e.setPosX(-80);
			else if(e.getPosX() < -80) e.setPosX(this.getWidth());

			if(e.getPosY() > (this.getHeight())) e.setPosY(-40); 
			else if(e.getPosY() < -40) e.setPosY(this.getHeight());
		}
	}

	/**
	 * Comprueba si 2 Objetos chocan entre sí
	 * @return boolean
	 */
	private boolean chocan2Objetos(Rectangle obj1, Rectangle obj2) {
		return (obj1.intersects(obj2));
	}

	@Override
	public void keyPressed(KeyEvent key) {
		int k = key.getKeyCode();
		switch(k){
			case KeyEvent.VK_RIGHT:
				nave.bajaRotation();
				break;
			case KeyEvent.VK_LEFT:
				nave.subeRotation();
				break;
			case KeyEvent.VK_UP:
				nave.setArriba(true);
				nave.setPulsado(true);
				nave.avanzar();
				break;
			case KeyEvent.VK_SPACE:
				System.out.println("space");
				nave.setDisparo(true);
				nave.disparar();
				break;
			default: break;
		}
	}
		
	@Override
	public void keyReleased(KeyEvent key) {
		int k = key.getKeyCode();
		switch(k){
			case KeyEvent.VK_RIGHT:
				nave.setDerecha(false);
				break;
			case KeyEvent.VK_LEFT:
				nave.setIzquierda(false);
				break;
			case KeyEvent.VK_UP:
				nave.setPulsado(false);
				break;
			case KeyEvent.VK_SPACE:
				nave.setDisparo(false);
				break;
			default: break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {/*Not used*/}

	protected synchronized void paint(){
		BufferStrategy bs = this.getBufferStrategy();
		Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
		g2d.drawImage(fondo, 0, 0, null); 		//pintar img fondo
/*
		g2d.setColor(Color.white);
		g2d.drawString(nave.getRotation() + " grados", 10, 20);		//pinta String info de la rotacion nave
		g2d.setColor(Color.gray);
		g2d.drawRect(this.getWidth()-161, 9, 101, 11);	//borde
		g2d.setColor(Color.white);
		g2d.fillRect(this.getWidth()-160, 10, nave.getVida(), 10);	//barra vida
		g2d.drawString(nave.getVida()+"%", this.getWidth()-50, 20);	//pinta % vida
*/
		pintaMisiles(g2d);
		pintaNave(g2d);
	    pintaAsteroides(g2d);
	    //pintaEnemigos(g2d);

		//muestra TODOS los gráficos en el Canvas
		g2d.dispose();
		bs.show();
	}

	/**
	 * Pinta la Nave si está viva 
	 */
	private synchronized void pintaNave(Graphics2D g2d) {
		if(!nave.isMuerto()) nave.pintaNave(g2d);
	}

	/**
	 * Pinta los misiles vivos en el mapa y elimina los muertos
	 */
	private synchronized void pintaMisiles(Graphics2D g2d){
		if(!getListaMisiles().empty()){
			for(int i=0; i < getListaMisiles().size(); i++){
				Misil aux = getListaMisiles().get(i);
				if(!getListaMisiles().get(i).isMuerto()) getListaMisiles().get(i).pintaMisil(g2d);
				else {
   					getListaMisiles().remove(i);
           			i=0;
				}
			}
		}
	}

	/**
	 * Pinta Asteroides en el Mapa, y los borra si están muertos
	 */
	private synchronized void pintaAsteroides(Graphics2D g2d) {
		if(getListaAsteroides().size()>0){
			for(int i=0; i< getListaAsteroides().size(); i++){
				if(!getListaAsteroides().get(i).isMuerto()) {getListaAsteroides().get(i).pintaAsteroide(g2d);}
				else{
					getListaAsteroides().remove(i);
					i=0;	//repinta desde cero todos los Asteroides
				}
			}
		}
	}

}
