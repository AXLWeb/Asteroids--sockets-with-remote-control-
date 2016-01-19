package v2;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

import javax.sql.rowset.spi.SyncResolver;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Mapa extends Canvas implements Runnable, MouseListener{

	private static final long serialVersionUID = 1L;
	private static final String imagePath="/img/space_background.jpg";
	private static Thread t;
	private static URL imgFondo = Mapa.class.getResource(imagePath); 
	private static ImageIcon icoFondo = new ImageIcon(imgFondo);
	private static Image fondo = icoFondo.getImage();
	public static Sonidos sonidos;
	private Nave nave;
	private Generador generator;
	private int contDisparo, contAsteroidesMuertos, max_Asteroides, nivel;
	private boolean juego, listo;
	private volatile Stack<Misil> listaMisiles = new Stack<>();
	private volatile Stack<Misil> listaMisilesEnemigos = new Stack<>();
	private volatile Stack<Asteroide> listaAsteroides = new Stack<>();
	private volatile Stack<Enemigo> listaEnemigos = new Stack<>();

	private Rectangle btnListo;

	///////////////	setters & getters	//////////////////////////////
	public void setNave(Nave n) {this.nave = n;}
	public Nave getNave() {return this.nave;}
	public Mapa getMapa() {return this;}
	public int getMax_Asteroides(){return this.max_Asteroides;}
	public int getContadorDisparos(){return this.contDisparo;}
	public void suma1disparo(){this.contDisparo+=1;}
	public boolean isJugando() {return this.juego;}
	public Generador getGenerator() {return this.generator;}
	public Stack<Misil> getListaMisiles() {return this.listaMisiles;}
	public Stack<Misil> getListaMisilesEnemigo() {return this.listaMisilesEnemigos;}
	public Stack<Asteroide> getListaAsteroides() {return listaAsteroides;}
	public Stack<Enemigo> getListaEnemigos() {return listaEnemigos;}
	@Override
	public Dimension getPreferredSize() {return new Dimension(getWidth(), getHeight());}

	//Constructor de Mapa
	public Mapa(Generador g){
		//Creamos el panel con sus propiedades
		setBounds(0,0,999,599);
		requestFocus();
		setFocusable(true);

		//inicializamos propiedades del Mapa
		this.juego = true;
		this.listo = false;	//para cambiar de pantalla
		this.contDisparo=0;
		this.max_Asteroides = 10;
		this.generator = g;
		this.contAsteroidesMuertos=0;
		Mapa.sonidos = generator.getSonidos();
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

		while(this.isJugando()){

			paint();

			//TODO: Niveles
			
			if(getListaAsteroides().size() < getMax_Asteroides()) generator.generaAsteroide();

			if(contAsteroidesMuertos > 4) {
				contAsteroidesMuertos = 0;
				generator.generaEnemigo();
			}
			
			if(nave.getVidas()<1) {
				nave.setMuerto(true);
				this.juego = false; //termina juego
			}

			try {t.sleep(16);} //60fps
			catch (InterruptedException e) {e.printStackTrace();}
		}

		while(!this.isJugando() && !listo){
			misilesKiller(getListaMisiles());
			misilesKiller(getListaMisilesEnemigo());
			paint();			//borra del Mapa objetos muertos
			//TODO: string: "GAME OVER" desaparece a los 2"
		}

		if(listo){
			generator.gameOver();
		}

	}

	/**
	 * Comprueba si el Enemigo choca con algun otro Objeto del Mapa. En caso afirmativo destruye ambos
	 */
	protected void chocaObjeto(Enemigo enemigo) {

		if(!getListaAsteroides().empty()){
			//Colisiones Enemigo VS Asteroide
			for(int i=0; i<getListaAsteroides().size(); i++){
				if(chocan2Objetos(getListaAsteroides().get(i).getPosicion(), enemigo.getPosicion()) && !getListaAsteroides().get(i).isMuerto()){
					enemigo.setMuerto(true);
					getListaAsteroides().get(i).setMuerto(true);
					sonidos.play(sonidos.getExploBig());
				}
			}
		}

		if(!getListaEnemigos().empty()){
			//Colisiones Enemigo VS Enemigo
			for(int i=0; i<getListaEnemigos().size(); i++){
				if(getListaEnemigos().get(i).getId() != enemigo.getId()){	//si no es él mismo 
					if(chocan2Objetos(getListaEnemigos().get(i).getPosicion(), enemigo.getPosicion()) && !getListaEnemigos().get(i).isMuerto()){
						enemigo.setMuerto(true);
						getListaEnemigos().get(i).setMuerto(true);
						sonidos.play(sonidos.getExploBig());
					}
				}
			}
		}

		//Colisiones Enemigo VS Nave
		if(chocan2Objetos(enemigo.getPosicion(), nave.getPosicion())){
			enemigo.setMuerto(true);
			nave.quitaVidas();	//quita 1 vida
			nave.restaVidaNave();
			sonidos.play(sonidos.getExploBig());
		}
	}

	/**
	 * Comprueba si la Nave choca con algun objeto del Mapa. En caso afirmativo resta 1 vida a la Nave y elimina el objeto
	 */
	protected void chocaObjeto(Nave nave) {

		if(!getListaAsteroides().empty()){
			//Colisiones Nave VS Asteroides
			for(int i=0; i<getListaAsteroides().size(); i++){
				Asteroide asteroide_actual = getListaAsteroides().get(i);
				if(chocan2Objetos(asteroide_actual.getPosicion(), nave.getPosicion()) && !asteroide_actual.isMuerto()){
					asteroide_actual.setMuerto(true);
					nave.restaVidaNave();
					nave.quitaVidas();	//quita 1 vida
					System.out.println("Nave choca con Asteroide "+asteroide_actual.getName()+" y ambos mueren");
					sonidos.play(sonidos.getExploBig());
				}
			}
		}

		if(!getListaEnemigos().empty()){
			//Colisiones Nave VS Enemigos
			for(int i=0; i<getListaEnemigos().size(); i++){
				if(chocan2Objetos(getListaEnemigos().get(i).getPosicion(), nave.getPosicion()) && !getListaEnemigos().get(i).isMuerto()){
					getListaEnemigos().get(i).setMuerto(true);
					nave.restaVidaNave();
					nave.quitaVidas();	//quita 1 vida
					System.out.println("Nave choca con enemigo y ambos mueren");
					sonidos.play(sonidos.getExploBig());
				}				
			}
		}
	}

	/**
	 * Comprueba si el misil choca con algún  Objeto o debe morir
	 */
	protected void sigueDisparo(Misil misil) {

		if(misil.getX() > (this.getWidth()) || (misil.getX() < 0) ) misil.setMuerto(true);
		else if(misil.getY() > (this.getHeight()) || (misil.getY() < 0) ) misil.setMuerto(true);

		else if(misilChocaEnemigo(misil)) misil.setMuerto(true);

		else if(misilChocaAsteroide(misil)) misil.setMuerto(true);

		else if(misilChocaNave(misil)) misil.setMuerto(true);

		else if(misilChocaMisil(misil)) {
			misil.setMuerto(true);
			sonidos.play(sonidos.getExploMed());
		}
	}

	/**
	 * Comprueba si un Misil (cualquiera) choca con un Enemigo
	 */
	private boolean misilChocaEnemigo(Misil misil_actual) {
		boolean misil_muere=false;		
		for(int i=0; i<getListaEnemigos().size(); i++){
			Enemigo enemigo = getListaEnemigos().get(i);
			if(chocan2Objetos(misil_actual.getPosicion(), enemigo.getPosicion())){
				if(enemigo != misil_actual.getEnemigo()){
					//si NO es ÉL MISMO
					getListaEnemigos().get(i).setMuerto(true);
					if(misil_actual.getNave() != null) {

						if(enemigo.getScale() == 2) {
							misil_actual.getNave().getPuntos().killEnemy();
							sonidos.play(sonidos.getExploBig());
							double dado = (int) new Random().nextInt(6 - 1) + 1;	//tira dado
							if(dado == 5) generator.generaEnemigoPequeño(enemigo);	//para generar (o no) otro enemigo pequeño
						}
						else if(enemigo.getScale() == 1) {
							misil_actual.getNave().getPuntos().killEnemySmall();
							sonidos.play(sonidos.getExploSmall());
						}
					}
					else if(misil_actual.getEnemigo() != null){
						System.out.println("ha sido misil del enemigo, nada q hacer...");
					}
					misil_muere = true;
				}
			}
		}
		return misil_muere;
	}

	/**
	 * Comprueba si un Misil enemigo choca con la Nave
	 */
	private boolean misilChocaNave(Misil misil){
		boolean misil_muere=false;
		//si es un Misil de Enemigo y NO es de Nave
		if(misil.getEnemigo() != null && misil.getNave()==null){
			if(chocan2Objetos(misil.getPosicion(), nave.getPosicion())){
				System.out.println("Misil enemigo choca con Nave");
				misil_muere = true;
				nave.restaVidaNave();
				nave.quitaVidas();	//quita 1 vida
				sonidos.play(sonidos.getExploSmall());
			}
		}
		return misil_muere;
	}
	
	/**
	 * Comprueba si 1 Misil choca con otro Misil distinto
	 */
	private boolean misilChocaMisil(Misil misil) {
		boolean misil_muere=false;

		//misil enemigo que choca con el de Nave
		if(misil.getEnemigo() != null && misil.getNave()==null){
			if(!getListaMisiles().empty()){
				for(int i=0; i < getListaMisiles().size(); i++){
					Misil misil_nave = getListaMisiles().get(i);
					if(chocan2Objetos(misil.getPosicion(), misil_nave.getPosicion())){
						misil_muere = true;
						misil_nave.setMuerto(true);
						System.out.println("misil de enemigo Choca con misil de Nave y ambos mueren");
					}
				}
			}
			//misil enemigo que choca con otro misil enemigo (pero no es EL MISMO)
			if(!getListaMisilesEnemigo().empty()){
				for(int i=0; i<getListaMisilesEnemigo().size();i++){
					Misil misil_enemy = getListaMisilesEnemigo().get(i);
					if(chocan2Objetos(misil.getPosicion(), misil_enemy.getPosicion())){
						//si NO es EL mismo que se dispara
						if(misil.getEnemigo() != misil_enemy.getEnemigo()){
							misil_muere = true;
							misil_enemy.setMuerto(true);
							System.out.println("2 misiles de enemigos que chocan entre sí");
						}
					}
				}
			}
		}
		//misil de nave que choca con el misil del Enemigo
		else if(misil.getNave() != null && misil.getEnemigo()==null){
			if(!getListaMisilesEnemigo().empty()){
				for(int i=0; i<getListaMisilesEnemigo().size(); i++){
					Misil misil_enemy = getListaMisilesEnemigo().get(i);
					if(chocan2Objetos(misil.getPosicion(), misil_enemy.getPosicion())){
						misil_muere=true;
						misil_enemy.setMuerto(true);
						System.out.println("misil de nave Choca con misil enemigo y ambos mueren");
					}
				}
			}
		}

		return misil_muere;
	}

	/**
	 * Comprueba si un Misil (cualquiera) choca con un Asteroide. En caso de que choquen generará otros 2 asteroides + pequeños
	 */
	protected boolean misilChocaAsteroide(Misil misil_actual) {
		//Comprueba si Misil choca con algun Asteroide
		boolean misil_muere=false;

		for(int j=0; j < getListaAsteroides().size(); j++){
			Asteroide asteroide_actual = getListaAsteroides().get(j);

			if(chocan2Objetos(misil_actual.getPosicion(),asteroide_actual.getPosicion()) && !getListaAsteroides().get(j).isMuerto()){
				getListaAsteroides().get(j).setMuerto(true);
				misil_muere = true;
				double sk = getListaAsteroides().get(j).getScale();

				//Crea 2 new Asteroides + pequeños
				if(sk > 0.25 && getListaAsteroides().get(j).isMuerto()) {
					generator.generar2Asteroides(getListaAsteroides().get(j));
				}

				//Añade los puntos del Asteroide muerto, si es 1 misil disparado por la nave
				if(misil_actual.getNave() != null){
					contAsteroidesMuertos++;
					double escala = generator.devuelveEscala(sk);
					if(escala == 1.0)  {
						misil_actual.getNave().getPuntos().killAstBig();
						sonidos.play(sonidos.getExploBig());
					}
					else if(escala == 0.5) {
						misil_actual.getNave().getPuntos().killAstMed();
						sonidos.play(sonidos.getExploMed());
					}
					else if(escala == 0.25) {
						misil_actual.getNave().getPuntos().killAstMini();
						sonidos.play(sonidos.getExploSmall());
					}
				}
				else if(misil_actual.getEnemigo() != null && misil_actual.getNave() == null) {
					System.out.println("Misil enemigo choca con Asteroide");
					sonidos.play(sonidos.getExploSmall());
				}
			}
		}
		return misil_muere;
	}

	/**
	 * Comprueba si el objeto sale de los límites del mapa
	 */
	protected void calculaLimitesdelMapa(Nave nave, Asteroide asteroide, Enemigo e){
		//Control al salir del mapa de la >>> Nave <<<
		if(nave != null){
			if(nave.getPosX() > (this.getWidth())) nave.setPosX(-nave.getWidth());
			else if(nave.getPosX() + nave.getWidth() < 0) nave.setPosX(this.getWidth());

			if(nave.getPosY() > (this.getHeight())) nave.setPosY(-nave.getHeight()); 
			else if(nave.getPosY() + nave.getHeight() < 0) nave.setPosY(this.getHeight());
		}
		//Control al salir del mapa del >>> Asteroide <<<
		else if(asteroide != null){
			if(asteroide.getPosX() > (this.getWidth())) asteroide.setPosX(-asteroide.getWidth());
			else if(asteroide.getPosX() + asteroide.getWidth() < 0) asteroide.setPosX(this.getWidth());

			if(asteroide.getPosY() > (this.getHeight())) asteroide.setPosY(-asteroide.getHeight()); 
			else if(asteroide.getPosY() + asteroide.getHeight() < 0) asteroide.setPosY(this.getHeight());
		}
		//Control al salir del mapa del >>> Enemigo <<<
		else if( e != null){
			if(e.getPosX() > (this.getWidth())) e.setPosX(-e.getWidth());
			else if(e.getPosX() + e.getWidth() < 0) e.setPosX(this.getWidth());

			if(e.getPosY() > (this.getHeight())) e.setPosY(-e.getHeight()); 
			else if(e.getPosY() + e.getHeight() < 0) e.setPosY(this.getHeight());
		}
	}

	/**
	 * Comprueba si 2 Objetos chocan entre sí
	 */
	private boolean chocan2Objetos(Rectangle obj1, Rectangle obj2) {
		return (obj1.intersects(obj2));
	}
	
	protected synchronized void paint(){
		BufferStrategy bs = this.getBufferStrategy();
		Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
		g2d.drawImage(fondo, 0, 0, null); 		//pintar img fondo
		g2d.setColor(Color.white);
		g2d.setFont(Launcher.fuente);

		if(this.isJugando()) {
			g2d.drawString(nave.getPuntos().getTotal()+"", 10, 21);					//puntos
			g2d.drawString(nave.getNombreJugador()+"", this.getWidth()-50, 20);		//player name

			int vidas = nave.getVidas();
			for(int i=1; i<=vidas; i++) {g2d.drawImage(nave.getImage(), -10+i*22, 30, null);} 	//cargar img de vidas

			g2d.drawString(nave.getVidas()+" vidas", this.getWidth()-130, 20);		//Numero de vidas
			g2d.setColor(Color.gray);
			g2d.drawRect(this.getWidth()-110, 25, 100, 11);							//borde de la barra vida
			g2d.setColor(Color.white);
			g2d.fillRect(this.getWidth()-110, 26, nave.getVida(), 10);				//barra vida

			pintaMisiles(g2d);
		    pintaAsteroides(g2d);
		    pintaNave(g2d);
		    pintaEnemigos(g2d);
		    pintaMisilesEnemy(g2d);
		}
		else{
			this.addMouseListener(this);
			pintaAsteroides(g2d);
			//TODO: pedir ingresar nombre
			g2d.setColor(Color.white);
			g2d.drawString("Ingresa tu nombre ", this.getWidth()/2-80, this.getHeight()/2-50);
			g2d.setColor(Color.yellow);
			g2d.drawString("AXL", this.getWidth()/2-80, this.getHeight()/2-30);

			//boton LISTO
			g2d.setColor(Color.white);
			g2d.drawString("LISTO", this.getWidth()/2-10, this.getHeight()/2);
			g2d.setColor(Color.black);
			btnListo = new Rectangle(this.getWidth()/2-80, this.getHeight()/2-20, 200, 30); //Rectangle boton LISTO
			g2d.draw(btnListo.getBounds());
		}

		//muestra TODOS los gráficos en el Canvas
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
	 * Pinta los misiles disparados por la Nave en el mapa y elimina los muertos
	 */
	private void pintaMisiles(Graphics2D g2d){
		if(!getListaMisiles().empty()){
			for(int i=0; i < getListaMisiles().size(); i++){
				if(!getListaMisiles().get(i).isMuerto()) {
					getListaMisiles().get(i).pintaMisilNave(g2d);
				}
				else {
   					getListaMisiles().remove(i);
           			i=0;
				}
			}
		}
	}

	/**
	 * Pinta los misiles disparados por el Enemigo en el mapa y elimina los muertos
	 */
	protected void pintaMisilesEnemy(Graphics2D g2d){
		if(!getListaMisilesEnemigo().empty()){
			for(int i=0; i < getListaMisilesEnemigo().size(); i++){
				if(!getListaMisilesEnemigo().get(i).isMuerto()) {
					getListaMisilesEnemigo().get(i).pintaMisilEnemigo(g2d);
				}
				else {
   					getListaMisilesEnemigo().remove(i);
           			i=0;
				}
			}
		}
	}

	/**
	 * Pinta Asteroides en el Mapa, y los borra si están muertos
	 */
	protected void pintaAsteroides(Graphics2D g2d) {
		if(!getListaAsteroides().empty()){
			for(int i=0; i< getListaAsteroides().size(); i++){
				if(!getListaAsteroides().get(i).isMuerto()) getListaAsteroides().get(i).pintaAsteroide(g2d);
				else{
					getListaAsteroides().remove(i);
					i=0;
				}
			}
		}
	}

	/**
	 * Pinta Enemigos vivos en el Mapa, y los borra si están muertos 
	 */
	protected void pintaEnemigos(Graphics2D g2d) {

		if(!getListaEnemigos().empty()){
			for(int i=0; i< getListaEnemigos().size(); i++){
				if(!getListaEnemigos().get(i).isMuerto()) {getListaEnemigos().get(i).pintaEnemigo(g2d);}
				else{
					getListaEnemigos().remove(i);
					i=0;
				}
		    }
		}
	}
	
	/**
	 * Hace limpieza general para empezar el juego de nuevo: 
	 *  - Mata todos los hilos vivos
	 *  - Limpiar Listas
	 *  - reinicia variables 
	 */
	protected void killAll(){
		//kill todos los hilos q haya. Clear ArrayLists...vidas=3.....
		enemigosKiller(getListaEnemigos());
		asteroidesKiller(getListaAsteroides());


		//reset listas
		getListaEnemigos().removeAllElements();
		getListaAsteroides().removeAllElements();
		getListaMisilesEnemigo().removeAllElements();
		getListaMisiles().removeAllElements();

		System.out.println("getListaEnemigos "+getListaEnemigos().size());
		System.out.println("getListaAsteroides "+getListaAsteroides().size());
		System.out.println("getListaMisilesEnemigo "+getListaMisilesEnemigo().size());
		System.out.println("getListaMisiles "+getListaMisiles().size());

		System.out.println("Listas reiniciadas");
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Rectangle mouse = new Rectangle(e.getX(), e.getY(), 10, 10);
		if (mouse.intersects(btnListo)){
			System.out.println("boton listo");
			this.setVisible(false);
			killAll();
			listo = true;
			//TODO:
			//Pasa a pantalla estadisiticas
		}
	}

	private void enemigosKiller(Stack<Enemigo> lista) {
		for(int i=0; i<lista.size(); i++){
			lista.get(i).setMuerto(true);
		}
	}
	
	private void asteroidesKiller(Stack<Asteroide> lista) {
		for(int i=0; i<lista.size(); i++){
			lista.get(i).setMuerto(true);
		}
	}
	
	protected void misilesKiller(Stack<Misil> lista){
		for(int i=0; i<lista.size(); i++){
			lista.get(i).setMuerto(true);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}

}
