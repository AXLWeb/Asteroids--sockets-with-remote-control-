package v2;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public class Launcher {

	public static Font arcade, titulo, courierNew;
	public static InputStream is;

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

		try {
			is = Launcher.class.getResourceAsStream("/fonts/Adore64.ttf");
			titulo = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(25f);
			
			is = Launcher.class.getResourceAsStream("/fonts/ARCADECLASSIC.TTF");
			arcade = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(16f);
						
			is = Launcher.class.getResourceAsStream("/fonts/cour.ttf");
			courierNew = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(16f);
			
			
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		} 
	}
}






