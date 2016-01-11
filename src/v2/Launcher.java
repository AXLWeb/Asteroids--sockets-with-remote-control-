package v2;

public class Launcher {

	public static void main(String[] args) {
		Generador g = new Generador();
		g.start();
	}
}



/**
 //TODO:
  * 
  *
  * Hay alguna funcion syncronized q bloquea el PINTADO del mapa, y asi bloquea el resto...
  * Si 1 misil choca 1 Asteroide, eliminarlo, e impedir q el resto chocque con el mismo
  *
**/