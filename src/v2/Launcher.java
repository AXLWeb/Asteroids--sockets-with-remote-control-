package v2;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public class Launcher {

	public static Font fuente;

/**
 http://www.classicgaming.cc/classics/asteroids/playguide.php
 //TODO:
  * Fuentes
  * Pantalla inicial & final
  * pedir ingresar nombre al morir
  * UML
  * Niveles del MAPA
  * Sockets		//no
**/

/** 
//TODO: cuando has muerto
 * jUEGO TERMINADO
 * Ingresa tu nombre
 * "AXL"
 * boton LISTO
 * 
 * Mapa vivo de fondo, sin enemigos, los Asteroides que había se mueven
 * 
 * Pasa a pantalla estadisiticas
 */	

/** 
//TODO: PANTALLA RANKING JUGADORES
 * 
 * 2 columnas: NOMBRE y PUNTOS
 * 
 * boton JUGAR
 * 
 * Fondo negro
 * 
 */	



	public static void main(String[] args) {
		cargaFuentes();
		Generador g = new Generador();
		g.start();
	}

	public static void cargaFuentes(){	
		InputStream is = Launcher.class.getResourceAsStream("/fonts/ARCADECLASSIC.TTF");
		try {
			fuente = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(20f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		} 
	}
}






