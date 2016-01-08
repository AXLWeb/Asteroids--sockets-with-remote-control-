package v2;

public class Launcher {

	public static void main(String[] args) {
		Generador g = new Generador();
		g.start();
	}
}



/**
 //TODO:
  * Eliminar misiles muertos (pintados fuera límites del mapa cuando la nave dispara fuera)
  * Hay alguna funcion syncronized q bloquea el PINTADO del mapa, y asi bloquea el resto...
  *
  *
**/