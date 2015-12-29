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

	private static final long serialVersionUID = 1L;
	private static Thread t;
	private Nave nave;
	private Enemigo enemigo;
	private Generador generator;
	private int max_Asteroides;
	private ArrayList<Misil> listaMisiles = new ArrayList<>();	//Stack?
	private Stack<Asteroide> listaAsteroides = new Stack<>();
	private Stack<Enemigo> listaEnemigos = new Stack<>();

	private static final String imagePath="/img/space_background.jpg";
	private URL imgFondo = Mapa.class.getResource(imagePath); 
	private ImageIcon icoFondo = new ImageIcon(imgFondo);
	private Image fondo = icoFondo.getImage();

	private int contAsteroidesMuertos;

	///////////////	setters & getters	//////////////////////////////
	public void setNave(Nave n) {this.nave = n;}
	public void setEnemy(Enemigo e){this.enemigo = e;}
	public Nave getNave() {return this.nave;}
	public Mapa getMapa() {return this;}
	public synchronized int getMax_Asteroides(){return this.max_Asteroides;}
	public synchronized ArrayList<Misil> getListaMisiles() {return listaMisiles;}
	public synchronized Stack<Asteroide> getListaAsteroides() {return listaAsteroides;}
	public synchronized Stack<Enemigo> getListaEnemigos() {return listaEnemigos;}
	
	@Override
	public Dimension getPreferredSize() {return new Dimension(getWidth(), getHeight());}


	//Constructor de Mapa
	public Mapa(Generador g){
		//Creamos el panel JPanel con sus propiedades
		setBounds(0,0,999,599);
		requestFocus();
		setFocusable(true);

		//inicializamos propiedades del Mapa
		this.max_Asteroides = 0;
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
			
			if(contAsteroidesMuertos > 3) {
				//generator.generaEnemigo();
				contAsteroidesMuertos=0;
				System.out.println("enemigos: "+ getListaEnemigos().size());
			}

			try {t.sleep(10);} //60fps
			catch (InterruptedException e) {e.printStackTrace();}
		}
	}

	public synchronized void sigueDisparo(Misil misil) {
		Rectangle misil_actual = misil.getPosicion();	//posicion del misil actual

		//if(misilChocaEnemigo(misil_actual)) misil.setMuerto(true);
		
		//if(misilChocaAsteroide(misil_actual)) misil.setMuerto(true);
		
		//misilChocaAsteroide(misil_actual);

		//if(misilChocaNave(misil_actual)) misil.setMuerto(true);

	}

	private synchronized boolean misilChocaNave(Rectangle misil_actual) {
		//Comprueba si Misil (enemigo) choca con la Nave
		boolean misil_muere=false;
		/*
		if(chocan2Objetos(misil_actual, nave.getPosicion())){
			System.out.println("Misil choca con Nave");
			misil_muere = true;
			nave.setMuerto(true);
			
		}
		*/
		return misil_muere;
	}
	
	private synchronized boolean misilChocaEnemigo(Rectangle misil_actual) {
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

	private synchronized boolean misilChocaAsteroide(Rectangle misil_actual) {
		//Comprueba si Misil choca con algun Asteroide
		boolean misil_muere=false;
		for(int j=0; j < getListaAsteroides().size(); j++){
			Rectangle asteroide_actual = getListaAsteroides().get(j).getPosicion();

			if(chocan2Objetos(misil_actual,asteroide_actual)){

				double sk = getListaAsteroides().get(j).getScale();
				getListaAsteroides().get(j).setMuerto(true);
				misil_muere = true;
				System.out.println("Misil choca con Asteroide");
				contAsteroidesMuertos++;

				//TODO: Crear 2 new Asteroides + pequeños
				if(sk > 0.25 && getListaAsteroides().get(j).isMuerto()) {
					generator.generar2Asteroides(getListaAsteroides().get(j));
					System.out.println("zzzzzzzzzzzzzzzzzzzz");
				}
			}
		}
		return misil_muere;
	}

	/**
	 * Comprueba si 2 Objetos chocan entre sí
	 * @return boolean
	 */
	private synchronized boolean chocan2Objetos(Rectangle obj1, Rectangle obj2) {
		return (obj1.intersects(obj2));
	}

	/**
	 * Comprueba si la Nave sale de los límites del mapa
	 * @param movil
	 */
	public void calculaLimitesdelMapa(Nave nave){
		//Control al salir del mapa
		if(nave.getPosX() > (this.getWidth())) nave.setPosX(-80);
		else if(nave.getPosX() < -80) nave.setPosX(this.getWidth());

		if(nave.getPosY() > (this.getHeight())) nave.setPosY(-40); 
		else if(nave.getPosY() < -40) nave.setPosY(this.getHeight());
	}

	/**
	 * Comprueba si el Asteroide sale de los límites del mapa
	 * @param ASTEROIDE
	 */
	public void calculaLimitesdelMapa(Asteroide asteroide){
		if(asteroide.getPosX() > (this.getWidth())) asteroide.setPosX(-80);
		else if(asteroide.getPosX() < -80) asteroide.setPosX(this.getWidth());

		if(asteroide.getPosY() > (this.getHeight())) asteroide.setPosY(-40); 
		else if(asteroide.getPosY() < -40) asteroide.setPosY(this.getHeight());
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
		g2d.setColor(Color.white);
		g2d.drawString(nave.getRotation() + " grados", 10, 20);		//pinta String info de la rotacion nave
		g2d.setColor(Color.gray);
		g2d.drawRect(this.getWidth()-161, 9, 101, 11);	//borde
		g2d.setColor(Color.white);
		g2d.fillRect(this.getWidth()-160, 10, nave.getVida(), 10);	//barra vida
		g2d.drawString(nave.getVida()+"%", this.getWidth()-50, 20);	//pinta % vida

		pintaMisiles(g2d);
		pintaNave(g2d);
	    pintaAsteroides(g2d);
	    pintaEnemigo(g2d);

		//muestra los gráficos en el Canvas
		g2d.dispose();
		bs.show();
	}

	/**
	 * Pinta la Nave si está viva 
	 */
	private void pintaNave(Graphics2D g2d) {
		if(!nave.isMuerto()) nave.pintaNave(g2d);
	}
	
	/**
	 * Pinta el Enemigo si está vivo 
	 */
	private void pintaEnemigo(Graphics2D g2d) {
		if(getListaEnemigos().size()>0){
			for(int i=0; i< getListaEnemigos().size(); i++){
				if(!getListaEnemigos().get(i).isMuerto()) {getListaEnemigos().get(i).pintaEnemigo(g2d);}
				else{
					getListaEnemigos().remove(i);
					i=0;	//repinta desde cero todos los Enemigos
				}
		    }
		}
	}
	
	/**
	 * Pinta Asteroides en el Mapa, y los borra si están muertos
	 */
	private void pintaAsteroides(Graphics2D g2d) {
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
	
	/**
	 * Pinta los misiles en el Mapa y los borra si estan muertos
	 */
	private void pintaMisiles(Graphics2D g2d) {
		g2d.setColor(Color.green);

		if(getListaMisiles().size()>0){
			for(int i=0; i< getListaMisiles().size(); i++){
				if(!getListaMisiles().get(i).isMuerto()) {getListaMisiles().get(i).pintaMisil(g2d);}
				else{
					getListaMisiles().remove(i);
					i=0;	//repinta desde cero todos los misiles
				}
		    }
		}
	}


}
