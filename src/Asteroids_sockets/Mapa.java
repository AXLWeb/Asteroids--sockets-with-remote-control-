package Asteroids_sockets;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.net.URL;
import java.util.Random;
import java.util.Stack;

import javax.swing.ImageIcon;

public class Mapa extends Canvas implements Runnable, MouseListener{

	private static final long serialVersionUID = 1L;
	private static final String imagePath="/img/space_background.jpg";
	private static Thread t;
	private static URL imgFondo = Mapa.class.getResource(imagePath); 
	private static ImageIcon icoFondo = new ImageIcon(imgFondo);
	private static Image fondo = icoFondo.getImage();
	private static Sonidos sonidos;
	private Frame frame;
	private Generador generator;
	private ClienteMapa2Server client2server;
	private int contDisparo, contAsteroidesMuertos, max_Asteroides, nivel;
	private boolean juego, listo, logged;
	private String nombreJugador;
	private volatile Stack<Jugador> listaJugadores = new Stack<>();			//jugadores (mandos) activos en la partida
	private volatile Stack<Nave> listaNaves = new Stack<>();				//Las Naves q se pintan en el Mapa
	private volatile Stack<Mapa> listaMapas = new Stack<>();
	private volatile Stack<Misil> listaMisiles = new Stack<>();
	private volatile Stack<Misil> listaMisilesEnemigos = new Stack<>();
	private volatile Stack<Asteroide> listaAsteroides = new Stack<>();
	private volatile Stack<Enemigo> listaEnemigos = new Stack<>();
	private Rectangle btnListo;
	
	//variables del protoclo de comunicacion
    private String ACT;
    private int vidas, puntos, IDMapa, IDMando;

	///////////////	setters & getters	//////////////////////////////
    protected int getMapaID() {return this.IDMapa;}
    protected void setMapaID(int id) {this.IDMapa = id;}
    protected ClienteMapa2Server getClienteMapa2Server(){return this.client2server;}
	public Thread getThread() {return this.t;}
	public Mapa getMapa() {return this;}
	public int getMax_Asteroides(){return this.max_Asteroides;}
	public boolean isJugando() {return this.juego;}
	protected boolean isConnected(){return this.logged;}
	public Generador getGenerator() {return this.generator;}
	public Stack<Jugador> getListaJugadores() {return this.listaJugadores;}
	public Stack<Nave> getListaNaves() {return this.listaNaves;}
	public Stack<Mapa> getListaMapas() {return this.listaMapas;}
	public Stack<Misil> getListaMisiles() {return this.listaMisiles;}
	public Stack<Misil> getListaMisilesEnemigo() {return this.listaMisilesEnemigos;}
	public Stack<Asteroide> getListaAsteroides() {return listaAsteroides;}
	public Stack<Enemigo> getListaEnemigos() {return listaEnemigos;}
	@Override
	public Dimension getPreferredSize() {return new Dimension(getWidth(), getHeight());}

	//Constructor de Mapa
	public Mapa(Generador g, Frame frame){
		this.generator = g;
		this.frame = frame;
		//Creamos el panel con sus propiedades
		setBounds(0,0,999,699);
		requestFocus();
		setFocusable(true);

		//inicializamos propiedades del Mapa
		this.juego = true;
		this.listo = false;	//para cambiar de pantalla
		this.contDisparo=0;
		this.max_Asteroides = 4;
		this.contAsteroidesMuertos=0;
		Mapa.sonidos = generator.getSonidos();

		this.client2server = new ClienteMapa2Server(this);    //hilo de comunicacion con server
		client2server.start();
	}


	/**
	 * Crea / Coge hilo
	 */
	public synchronized void start() {
		if(t==null){
			t = new Thread(this);
			t.start();
		}
		else t.start();
	}

	@Override
	public void run() {
		this.createBufferStrategy(2);

		System.out.println("Run del MAPA isJugando() = "+isJugando());

		while(this.isJugando()){
			paint();

			if(getListaAsteroides().size() < getMax_Asteroides()) generator.generaAsteroide();

			if(contAsteroidesMuertos > 4) {
				contAsteroidesMuertos = 0;
				generator.generaEnemigo();
			}

			for(int i=0; i<getListaNaves().size();i++){
				if(getListaNaves().get(i).getVidas()<1)	
					getListaNaves().get(i).setMuerto(true);
			}

			try {t.sleep(16);} //60fps
			catch (InterruptedException e) {e.printStackTrace();}
		}
		
		killAll();
		paint();
		sonidos.stop(sonidos.getSonidoJuego());
	}

	/**
	 * Comprueba si el Enemigo choca con algun otro Objeto del Mapa
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

		if(!getListaNaves().empty()){
			//Colisiones Enemigo VS Naves
			for(int i=0; i<getListaNaves().size(); i++){
				if(chocan2Objetos(getListaNaves().get(i).getPosicion(), enemigo.getPosicion()) && !getListaNaves().get(i).isMuerto()){
					enemigo.setMuerto(true);
					getListaNaves().get(i).quitaVidas();	//quita 1 vida
					getListaNaves().get(i).restaVidaNave();
					sonidos.play(sonidos.getExploBig());
				}
			}
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
					nave.restaVidaNave();	//% de vida
					nave.quitaVidas();	//quita 1 vida
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
					enemigo.setMuerto(true);	//getListaEnemigos().get(i).setMuerto(true);
					if(misil_actual.getNave() != null) {							//si es un Misil de Nave
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
						//System.out.println("ha sido misil del enemigo, nada q hacer...");
					}
					misil_muere = true;
				}
			}
		}
		return misil_muere;
	}

	/**
	 * Comprueba si un Misil (enemigo) choca con alguna de las Naves del Mapa
	 */
	private boolean misilChocaNave(Misil misil){
		boolean misil_muere=false;
		//si es un Misil de Enemigo y NO es de Nave
		if(misil.getEnemigo() != null && misil.getNave()==null){
			for(int i=0; i<getListaNaves().size();i++){
				Nave nave = getListaNaves().get(i);			//busca con qué Nave ha chocado el Misil
				if(chocan2Objetos(misil.getPosicion(), nave.getPosicion())){
					misil_muere = true;
					//nave.restaVidaNave();	// % de vida
					nave.quitaVidas();	//quita 1 vida a esa Nave
					sonidos.play(sonidos.getExploSmall());
					System.out.println("La Nave "+nave.getID()+" pierde 1 vida");
				}
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
			//x
			if(nave.getPosX() > (this.getWidth())){
				System.out.println("INTENTANDO SALIR POR LA DERECHA CON POSICION "+nave.getPosX());
				String comando = naveSalePorDerecha(nave);
				client2server.sendMsg(comando);
				nave.setMuerto(true);
			}
			//x
			else if(nave.getPosX() + nave.getWidth() < 0){
				System.out.println("INTENTANDO SALIR POR LA IZQUIERDA CON POSICION "+nave.getPosX());
				String comando = naveSalePorIzquierda(nave);
				client2server.sendMsg(comando);
				nave.setMuerto(true);
			}

			if(nave.getPosY() > (this.getHeight())) nave.setPosY(-nave.getHeight()); 
			else if(nave.getPosY() + nave.getHeight() < 0) nave.setPosY(this.getHeight());		//y
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

	

	private String naveSalePorIzquierda(Nave nave) {
		// id, idmapa, ACT, vidas, puntos, izq, y salida, angulo, vactx, vacty, vimpx, vimpy, teclas
		String comando = "salemapa;"+nave.getID()+";"+this.IDMapa+";"+nave.getVidas()+";"+nave.getTotalPuntos()+";izq;"+nave.getPosY()+";"+nave.getRotation()+";"+nave.getVact().getX()+";"+nave.getVact().getY()
				+";"+nave.getVimp().getX()+";"+nave.getVimp().getY()+";";
		String teclas = "";
		if (nave.getIzquierda())teclas += "1";
		else teclas += "0";
		
		if (nave.getImpulso())teclas += "1";
		else teclas += "0";
		
		if (nave.getDerecha())teclas += "1";
		else teclas += "0";
		
		comando += teclas;
		
		System.out.println("NAVE SALIENDO POR LA IZQUIERDA");
		return comando;
		
	}
	private String naveSalePorDerecha(Nave nave) {
		// id, idmapa, ACT, vidas, puntos, der, y salida, angulo, vactx, vacty, vimpx, vimpy, teclas
		String comando = "salemapa;"+nave.getID()+";"+this.IDMapa+";"+nave.getVidas()+";"+nave.getTotalPuntos()+";der;"+nave.getPosY()+";"+nave.getRotation()+";"+nave.getVact().getX()+";"+nave.getVact().getY()
				+";"+nave.getVimp().getX()+";"+nave.getVimp().getY()+";";
		String teclas = "";
		if (nave.getIzquierda())teclas += "1";
		else teclas += "0";
		
		if (nave.getImpulso())teclas += "1";
		else teclas += "0";
		
		if (nave.getDerecha())teclas += "1";
		else teclas += "0";
		
		comando += teclas;
		
		System.out.println("NAVE SALIENDO POR LA DERECHA");
		return comando;

	}
	
	
	/**
	 * Comprueba si 2 Objetos chocan entre sí
	 */
	private boolean chocan2Objetos(Rectangle obj1, Rectangle obj2) {
		return (obj1.intersects(obj2));
	}

	protected void paint(){
		
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) return;

		Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
		if(g2d == null)  return;

		g2d.drawImage(fondo, 0, 0, null); 		//pintar img fondo
		g2d.setColor(Color.white);
		g2d.setFont(Launcher.arcade);

		if(this.isJugando()) {
			pintaMisiles(g2d);
		    pintaAsteroides(g2d);
		    pintaNaves(g2d);
		    pintaEnemigos(g2d);
		    pintaMisilesEnemy(g2d);
		}

		//muestra TODOS los gráficos en el Canvas
		g2d.dispose();
		bs.show();
	}


	/**
	 * Devuelve un Jugador por su ID (IDMando)
	 * @param ID
	 * @return Jugador
	 */
	protected Jugador getJugadorByID(int ID){
		Jugador jugador = null;
		for(int i=0; i<getListaJugadores().size();i++){
			if(getListaJugadores().get(i).getIDMando() == ID){
				jugador = getListaJugadores().get(i);
				System.out.println("jugadoir ID= "+jugador.getIDMando());
			}
		}
		return jugador;
	}
	
	/**
	 * Devuelve una Nave buscada por su ID 
	 * @param ID
	 * @return Nave
	 */
	public Nave getNaveByID(int ID) {
		Nave nave = null;
		for(int i=0; i<getListaNaves().size();i++){
			if(getListaNaves().get(i).getID() == ID){
				nave = getListaNaves().get(i);
				System.out.println("nave ID= "+nave.getID());
			}
		}
		return nave;
	}
	
	/**
	 * Devuelve una Nave al azar de la lista de Naves para que el Enemigo le dispare
	 * @return Nave
	 */
	public Nave getRandomNave() {
		if(getListaNaves().size()>0) return getListaNaves().get(new Random().nextInt(getListaNaves().size()));
		else return null;
	}
	
	/**
	 * Pinta las Naves vivas del Mapa y elimina las muertas 
	 */
	private void pintaNaves(Graphics2D g2d) {
		if(!getListaNaves().empty()){
			for(int i=0; i < getListaNaves().size(); i++){
				if(!getListaNaves().get(i).isMuerto()) 
					getListaNaves().get(i).pintaNave(g2d);
				else 
					getListaNaves().remove(i);
			}
		}
		
	}

	/**
	 * Pinta los misiles disparados por la Nave en el mapa y elimina los muertos
	 */
	protected void pintaMisiles(Graphics2D g2d){
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
		asteroidesKiller(getListaAsteroides());
		navesKiller(getListaNaves());
		misilesKiller(getListaMisiles());
		enemigosKiller(getListaEnemigos());

		//reset listas
		getListaEnemigos().removeAllElements();
		getListaAsteroides().removeAllElements();
		getListaMisilesEnemigo().removeAllElements();
		getListaMisiles().removeAllElements();
		getListaNaves().removeAllElements();

		//mata hilo MAPA
		Mapa.t=null;
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
	
	private void misilesKiller(Stack<Misil> lista){
		for(int i=0; i<lista.size(); i++){
			lista.get(i).setMuerto(true);
		}
	}
	
	private void navesKiller(Stack<Nave> lista){
		for(int i=0; i<lista.size();i++){
			lista.get(i).setMuerto(true);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Rectangle mouse = new Rectangle(e.getX(), e.getY(), 10, 10);
		if (mouse.intersects(btnListo)) listo = true;
		else listo = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	public void avisoUser(String s) {
		System.out.println("AVISO: "+s);
	}

	public void PrimeraRespuesta(String str) {
		//resp ==> null;null,IDMapa
		//formato: ACT;IDMANDO;IDMAPA
		 if(str!=null){
            String[] separador = str.trim().toLowerCase().split(";");
            //ACT = separador[0];	        //null
            //IDMando = separador[1];	    //null 
            int IDMapa = Integer.valueOf(separador[2]);
            if(IDMapa > 0 ) setMapaID(IDMapa);
            System.out.println("MAPA creado en MAPA.java con ID "+getMapaID());
        }
        else {
            System.out.println( "respuesta nullaaaaaa");
            avisoUser("respuesta nullaaaaaa");
        }
	}
	
	public void trataRespuesta(String r) {
		//Lo q llegará siempre seguira este formato:
        //IN: BtnACT;IDMando;IDmapa
	    //OUT:IDMando;IDMapa;BtnACT_reply;vidas;puntos
		System.out.println(">>>>>En MAPA Tratando la respuesta de ClienteMapa2Server: "+ r+".\nFin del mensaje...");
		String[] separador;
		separador = r.trim().toLowerCase().split(";");	//Lee IN q recibe de ClienteMapa2Server

		//si es 1º msg ===> "null;null;null"
		if(r.equals("null;null;null") || r == null){
			System.out.println("respuesta erronea del servidor, no debe pasar");
			return;
		}

		//Comprobamos q la respuesta recibida es xa MAPA
		if(r!=null && separador.length == 3){
			System.out.println("respuesta del server recibida, tiene "+separador.length+" campos. Tratandolo...");

            //TRATA los mensajes q NO SON la 1º RESPUESTA del server. Son mensajes recibidos del MANDO
			//Lo q llegará siempre seguira este formato:  ////////////	IN: BtnACT;IDMando;IDmapa
			String ACT="null";  int IDMando = -1; int IDMapa = -1;
			ACT = separador[0];
			IDMando = Integer.valueOf(separador[1]);
			IDMapa = Integer.valueOf(separador[2]);

			if(getMapaID() == IDMapa){
				//si no existe la Nave ya en el MAPA, creamos
				if(IDMando > 0 && getNaveByID(IDMando)==null) {

					if(ACT.equals("mando_init")){
						System.out.println("iniciando nueva Nave con ID "+IDMando);
						//TODO: crea nueva Nave (si no existia ya antes) y new Jugador con ESE ID
						getGenerator().generaNave(IDMando);
						//getGenerator().generaJugador(IDMando);		//TODO: revisar si ya lo hace todo OK
						//Crea nuevo jugador---------- eliminar, innecesario
						//TODO: ELIMINAR JUGADOR, USAR SOLO NAVE
						//Jugador j = new Jugador();
						//j.setIDMando(IDMando);
						//j.setIDMapa(IDMapa);
						//getListaJugadores().add(j);
						//j.setVidas(getNaveByID(IDMando).getVidas());
						//j.setPuntos(getNaveByID(IDMando).getTotalPuntos());
						System.out.println("Nave creada con ID "+getNaveByID(IDMando).getID());
						//responde al jugador (Mando) cual es su ID en MAPA
						//IDMando;IDMapa;null;vidas;puntos
						ACT="null";		//ACTreply
						client2server.sendMsg(IDMando+";"+getMapaID()+";"+ACT+";"+getNaveByID(IDMando).getVidas()+";"+getNaveByID(IDMando).getTotalPuntos());
						//client2server.sendMsg(j.getIDMando()+";"+getMapaID()+";"+ACT+";"+j.getVidas()+";"+j.getPuntos());
					}
				}
				else if(IDMando > 0 && getNaveByID(IDMando) != null) {
					//si ya existia esa Nave......... tratamos su ACT
					if(!ACT.equals("null")) trataAccion(ACT, IDMando);
					
					//envia respuesta con datos. TODO: Android a veces se desconecta solo.
					//IDMando;IDMapa;null;vidas;puntos
					ACT="null"; //ACT reply
					client2server.sendMsg(getNaveByID(IDMando).getID()+";"+getMapaID()+";"+ACT+";"+getNaveByID(IDMando).getVidas()+";"+getNaveByID(IDMando).getTotalPuntos());
				}
			}
		}
		else if (separador.length == 13){
			System.out.println("ENTRA UNA NAVE A ESTE MAPA!!!!!!");
			 //FORMATO comando = "salemapa;"+nave.getID()+";"+this.IDMapa+";"+nave.getVidas()+";"
			 // 	+nave.getTotalPuntos()+";der;"+nave.getPosY()+";"+nave.getRotation()+";"+nave.getVact().getX()+";"
			 // 	+nave.getVact().getY()+";"+nave.getVimp().getX()+";"+nave.getVimp().getY()+";";

			String ACT="null";  int IDMando = -1; int IDMapa = -1; int vidas; int puntos; boolean entraDerecha = false; double posY; int rotacion; double vActX, vActY, vImpX, vImpY; String teclas;
			ACT = separador[0];
			IDMando = Integer.valueOf(separador[1]);
			IDMapa = Integer.valueOf(separador[2]); //mapa de donde viene. Lo usa solo servidor
			vidas = Integer.valueOf(separador[3]);
			puntos = Integer.valueOf(separador[4]);
			if (separador[5].equals("izq")) {
				entraDerecha = true; // entraDerecha es false por defecto (si string es der, entra por izquierda de esta pantalla)
				System.out.println("SALE POR IZQUIERDA, ENTRA POR LA DERECHA");
			}
			else if (separador[5].equals("der")){
				entraDerecha = false;
				System.out.println("SALE POR DERECHA, ENTRA POR LA IZQUIERDA");
			}
			posY = Double.parseDouble(separador[6]);
			rotacion = Integer.valueOf(separador[7]);
			vActX = Double.parseDouble(separador[8]);
			vActY = Double.parseDouble(separador[9]);
			vImpX = Double.parseDouble(separador[10]);
			vImpY = Double.parseDouble(separador[11]);
			teclas = separador[12];
			
			Nave nave = new Nave(this, this.generator, IDMando, vidas, puntos, entraDerecha, posY, rotacion, vActX, vActY, vImpX, vImpY); // nave que entra de otro mapa a este mapa
			
			char tecla = teclas.charAt(0);
			if (tecla == '1') nave.setIzquierda(true);
			
			tecla = teclas.charAt(1);
			if (tecla == '1') nave.setImpulso(true);
			
			tecla = teclas.charAt(2);
			if (tecla == '1') nave.setDerecha(true);
			
			// generamos nueva nave en nuevo mapa 
			getGenerator().generaNave(nave);
			// notificamos mando
			client2server.sendMsg(nave.getID()+";"+getMapaID()+";"+ACT+";"+nave.getVidas()+";"+nave.getPuntos().getTotal());
		}
		else System.out.println("No se reconoce el msg recibido, ignorando...");
	}

	private void trataAccion(String ACT, int IDMando) {
		switch(ACT){

			case "byebye":
				//msg despedida: desconecta mando
				Nave naveMuerta = getNaveByID(IDMando);
				naveMuerta.setMuerto(true);
				getListaNaves().remove(naveMuerta);
				//getListaJugadores().remove(getJugadorByID(IDMando));
				System.out.println("ELIMINANDO naveMuerta "+naveMuerta.getID()+" DEL MAPA ID "+ this.getMapaID());
				exit(IDMando);
				break;

			////////////////////////al apretar botones ////////////////////////////////////
			case "up down":
				getNaveByID(IDMando).setPulsado(true);
				if(!getNaveByID(IDMando).isMuerto()){
					getNaveByID(IDMando).setImpulso(true);
					getNaveByID(IDMando).avanzar();
				}
				break;

			case "left down":
				if(!getNaveByID(IDMando).isMuerto()) getNaveByID(IDMando).bajaRotation();
				break;

			case "right down":
				if(!getNaveByID(IDMando).isMuerto()) getNaveByID(IDMando).subeRotation();
				break;

			case "shoot down":
				if(!getNaveByID(IDMando).isMuerto()){
					if(!getNaveByID(IDMando).getDisparo()) getNaveByID(IDMando).setDisparo(true);
					if(getNaveByID(IDMando).getDisparo())  getNaveByID(IDMando).disparar();
				}
				break;
				
				
			//////////////////////// al soltar botones ////////////////////////////////////
			case "up released":
				getNaveByID(IDMando).setImpulso(false);
				break;

			case "shoot released":
				getNaveByID(IDMando).setDisparo(false);
				break;

			case "left released":
				getNaveByID(IDMando).setIzquierda(false);
				break;

			case "right released":
				getNaveByID(IDMando).setDerecha(false);
				break;

			default: 
				System.out.println("Ignoring input line. ACT = null");
				ACT = "null";
				break;
		}
	}

    public void exit(int IDMando){
    	System.out.println("Mensaje de cierre recibido del server");
    	//confirma despedida a MANDO
        client2server.sendMsg(IDMando+";"+getMapaID()+";closelink;"+getNaveByID(IDMando).getVidas()+";"+getNaveByID(IDMando).getTotalPuntos());
    }

	protected void cierraMapa() {
		//TODO: le dice al server que se va, y a todos sus mandos les dice bye y mando vidas+puntos
		getClienteMapa2Server().sendMsg("IDMando;IDMapa;ACT;vidas;puntos");
	}

}
