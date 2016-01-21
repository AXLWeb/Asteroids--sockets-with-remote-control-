package v2;

public class Jugador {

	private String nombre;
	private int totalPuntos;
	private Puntos puntos;
	
	
	///////// setters & getters  /////////////
	public int getPuntos() {return this.totalPuntos;}
	public String getNom() {return this.nombre;}
	
	
	public Jugador(String n, int p){
		this.nombre = n;
		this.totalPuntos = p;
	}

		
	
}
