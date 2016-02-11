package Asteroids_sockets;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
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

	public Thread getThread() {return this.t;}
	public Mapa getMapa() {return this;}
	public int getMax_Asteroides(){return this.max_Asteroides;}
	//public int getContadorDisparos(){return this.contDisparo;}
	//public void suma1disparo(){this.contDisparo+=1;}
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

		this.nombreJugador = "";
		//inicializamos propiedades del Mapa
		this.juego = true;
		this.listo = false;	//para cambiar de pantalla
		this.contDisparo=0;
		this.max_Asteroides = 10;
		this.contAsteroidesMuertos=0;
		Mapa.sonidos = generator.getSonidos();
		
		
		this.logged=false;
		this.client2server = new ClienteMapa2Server(this);    //hilo de comunicacion con server
		client2server.start();
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

	@Override
	public void run() {
		this.createBufferStrategy(2);

		while(this.isJugando()){
			paint();

			if(getListaAsteroides().size() < getMax_Asteroides()) generator.generaAsteroide();

			if(contAsteroidesMuertos > 4) {
				contAsteroidesMuertos = 0;
				generator.generaEnemigo();
			}

			//Comprueba si TODAS las Naves del Mapa están muertas para terminar el Juego
			if(getListaNaves().size()<0) this.juego = false;	//termina el juego
			else{
				
				int cont=0;
				for(int i=0; i<getListaNaves().size();i++){
					if(getListaNaves().get(i).getVidas()<1){
						getListaNaves().get(i).setMuerto(true);
					}
					else{
						cont++;
						System.out.println("nave aun vivas: "+cont);
						if(cont<1) this.juego = false;	//termina el juego
					}
				}
			}

			try {t.sleep(16);} //60fps
			catch (InterruptedException e) {e.printStackTrace();}
		}

		if(!listo){
			navesKiller(getListaNaves());
			misilesKiller(getListaMisiles());
			enemigosKiller(getListaEnemigos());
			misilesKiller(getListaMisilesEnemigo());
		}

		while(!this.isJugando() && !listo){
			paint();			//borra del Mapa objetos muertos
		}

		if(listo){
			sonidos.stop(sonidos.getSonidoJuego());
			this.setVisible(false);
			killAll();

			Nave nave = null;
			for(int i=0; i<getListaNaves().size();i++){
				nave = getNaveByID(i);
				generator.guardaDatosCSV(nave.getID());
			}
		}
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
					nave.restaVidaNave();
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
					nave.restaVidaNave();
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
		g2d.setFont(Launcher.arcade);
		
/*
		String ID="";	//ID de la nave (y jugador) q se pinta en el Mapa

		for(int i=0; i < getListaJugadores().size(); i++){
			Jugador j = getListaJugadores().get(i);
			//coge ID de jugador para mover la nave(ID)
		}
*/
				
		if(this.isJugando()) {
/*
			Nave nave = null;
			//Nave nave = getNaveByID(ID);
			for(int i=0; i < getListaNaves().size(); i++){
				nave = getListaNaves().get(i);
				g2d.drawString(nave.getPuntos().getTotal()+"", 10*i+10, 21);			//puntos
				g2d.drawString(nave.getNombreJugador()+"", this.getWidth()-50 +50*i, 20);		//player name
			}


			int vidas = nave.getVidas();
			for(int i=1; i<=vidas; i++) {g2d.drawImage(nave.getImage(), -10+i*22, 30, null);} 	//cargar img de vidas

			g2d.drawString(nave.getVidas()+" vidas", this.getWidth()-130, 20);		//Numero de vidas
			g2d.setColor(Color.gray);
			g2d.drawRect(this.getWidth()-110, 25, 100, 11);							//borde de la barra vida
			g2d.setColor(Color.white);
			g2d.fillRect(this.getWidth()-110, 26, nave.getVida(), 10);				//barra vida
			g2d.drawString(nave.getNombreJugador()+"", this.getWidth()-50, 20);
*/
			pintaMisiles(g2d);
		    pintaAsteroides(g2d);
		    pintaNaves(g2d);
		    pintaEnemigos(g2d);
		    pintaMisilesEnemy(g2d);
		}
		else{
			this.addMouseListener(this);
			requestFocus();
			pintaAsteroides(g2d);

			/*
			g2d.setColor(Color.white);
			g2d.setFont(Launcher.arcade);

			//boton LISTO
			g2d.setColor(Color.white);
			g2d.setFont(Launcher.arcade);
			g2d.drawString("LISTO", this.getWidth()/2-10, this.getHeight()/2);
			//area de interacción(Rectangle) del boton LISTO
			btnListo = new Rectangle(this.getWidth()/2-80, this.getHeight()/2-20, 200, 30);
			*/
		}

		//muestra TODOS los gráficos en el Canvas
		g2d.dispose();
		bs.show();
	}

	/**
	 * Devuelve una Nave buscada por su ID 
	 * @param ID
	 * @return Nave
	 */
	public Nave getNaveByID(int ID) {
		Nave nave = null;
		for(int i=0; i<getListaNaves().size();i++){
			if(getListaNaves().get(i).getID() == ID)
				nave = getListaNaves().get(i);
			System.out.println("nave ID= "+nave.getID());
		}
		return nave;
	}
	
	/**
	 * Devuelve una Nave al azar de la lista de Naves para que el Enemigo le dispare
	 * @return Nave
	 */
	public Nave getRandomNave() {return getListaNaves().get(new Random().nextInt(getListaNaves().size()));}
	
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
		enemigosKiller(getListaEnemigos());
		asteroidesKiller(getListaAsteroides());

		//reset listas
		getListaEnemigos().removeAllElements();
		getListaAsteroides().removeAllElements();
		getListaMisilesEnemigo().removeAllElements();
		getListaMisiles().removeAllElements();
		getListaNaves().removeAllElements();

		//reset variables
		/*
		nave.setVida(100);
		nave.setVidas(3);
		nave.setPuntos();
		nave.setMuerto(false);
		*/
		
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
	
	public void trataRespuesta(String respuesta) {
	    //OUT:IDMando;IDMapa;BtnACT_reply;vidas;puntos
        //IN: BtnACT;IDMando;IDmapa
		System.out.println("Tratando la respuesta del server: "+ respuesta);
		String[] separador;
		//String BtnACT_reply="";
		//String ACT = "bye | mapa_init";
		
		
		//Lee IN q recibe de ClienteMapa2Server
		separador = respuesta.trim().toLowerCase().split(";");
		
		if(separador.length == 3){
			//MANDO
			/*
			ACT = separador[0]; //ACT sobre la Nave(ID) indicada
			IDMando = Integer.valueOf(separador[1]); //identifica la Nave(ID) indicada
			IDMapa = Integer.valueOf(separador[2]); //reconoce que es él mismo
			*/
			
			if(respuesta.equals("null;null;null")){
				//si es 1º msg ===> "null;null;null"
				//TODO: pide IDmando + IDmapa
				
				client2server.sendMsg(null+";"+null+";"+null+";"+null+";"+null);

			}
			else if(separador[0].equals(null) && !separador[1].equals(null) && separador[2].equals(null)){ 
				//"null;"IDmando+";"+IDMapa
				this.IDMando = Integer.valueOf(separador[1]);
				this.IDMapa = Integer.valueOf(separador[2]);
				setMapaID(IDMapa);
				//Crea nueva Nave para pintar en Mapa
				Nave n = new Nave(this, getGenerator());
				n.start();
				getListaNaves().addElement(n);
				//Crea nuevo jugador
				Jugador j = new Jugador();
				j.setIDMando(IDMando);
				j.setIDMapa(IDMapa);
				getListaJugadores().addElement(j);
				j.setVidas(n.getVidas());
				j.setPuntos(n.getTotalPuntos());
				
////////////////////////
				System.out.println("Nave creada con ID "+n.getID());
				System.out.println("Jugador creado con ID "+j.getIDMando());
////////////////////////
				
				
				//envia respuesta con datos
				ACT=null;
				client2server.sendMsg(j.getIDMando()+";"+getMapaID()+";"+ACT+";"+getNaveByID(j.getIDMando())+";"+j.getPuntos());
				
			}
			else{
				//client2server.sendMsg(IDMando+";"+this.IDMapa+";"+ACT+";"+getNaveByID(ID).getVidas()+";"+getNaveByID(ID).getPuntos());
				switch(ACT){
				}
			}
		
		}
		
		if(separador.length == 5){
			/*
			IDMando = Integer.valueOf(separador[0]);		//identifica la Nave(ID) indicada
			IDMapa = Integer.valueOf(separador[1]) ;		//reconoce que es él mismo
			ACT = separador[2];			//ACT a realizar sobre la Nave(ID) 
			vidas = Integer.valueOf(separador[3]);
			puntos = Integer.valueOf(separador[4]);
			*/
		}
		else
			System.out.println("No se reconoce el msg recibido, ignorando...");
		
		
		//Envia respuesta OUT a ClienteMapa2Server
		String s=null;
		client2server.sendMsg(s);
	}
	
}
