package v2;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public class Launcher {

/**
 http://www.classicgaming.cc/classics/asteroids/playguide.php
 //TODO:
  * Fuentes
  * Pantalla inicial & final
  * Sonidos 	//solo suena 1 disparo
  * UML
  * Niveles
  * Sockets		//no
**/
	public static Font fuente;
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






