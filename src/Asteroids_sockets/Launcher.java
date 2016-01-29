package Asteroids_sockets;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public class Launcher {

	public static Font arcade, titulo, courierNew;
	public static InputStream is;


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






