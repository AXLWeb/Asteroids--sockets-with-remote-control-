package Asteroids_sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

//pag 782 Libro Killer Game Programming in Java 

public class Server extends Thread {

	private ServerSocket serverSock;
	private Generador g;
	private Socket clientSock;
	private BufferedReader in; // i/o for the server
	private PrintWriter out;
	private DiscoveryThread dt;
	private boolean loggedUser;


	////////////////////////setters & getters	//////////////////////////////
	public boolean isLogged(){return this.loggedUser;}

	
	public Server(Generador g) {
		this.g = g;
		loggedUser = false;
		
		Thread discoveryThread = new Thread(DiscoveryThread.getInstance());
		discoveryThread.start();
	}

	public void conect() {
		try {
			serverSock = new ServerSocket(8888);

			while (true) {
				System.out.println("Waiting for a client...");
				clientSock = serverSock.accept();
				System.out.println("Client connection from " + clientSock.getInetAddress().getHostAddress());

				loggedUser = true;
				// Get I/O streams from the socket
				in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
				out = new PrintWriter(clientSock.getOutputStream(), true);

				//TODO new Cliente Thread ..... then:
				//processClient(in, out); // interact with a client

				// Close client connection
				clientSock.close();
				System.out.println("Client connection closed\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}

	}

	private void processClient(BufferedReader in, PrintWriter out) {
		String line, msg; String[] separador; int tipo;
		boolean done = false;

		try {
			while (!done) {
				if ((line = in.readLine()) == null){
					System.out.println("line = null. done = "+done);
					done = true;
				}
				else {

					System.out.println("Client msg: " + line);
					if (line.trim().equals("bye") || line.trim().equals("close") || line.trim().equals("exit")){
						done = true;
						System.out.println("byebye");
						out.println("byebye");
					}
					else{
						separador = line.split(";");
						tipo = Integer.valueOf(separador[0]);
						msg = separador[1];
						doRequest(out, tipo, msg);
					}
				}
				System.out.println("done = "+done);
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private void doRequest(PrintWriter out, int tipo, String msg) {

		if(loggedUser && g.getMapa().isVisible()){
			if(tipo==1){
				/////////  Pressed buttons ///////////
				switch(msg){
					case "up down":
						//out.println("sube");
						g.getNave().setPulsado(true);
						if(!g.getNave().isMuerto()){
							g.getNave().setImpulso(true);
							g.getNave().avanzar();
						}
						break;

					case "left down":
						//out.println("izq");
						if(!g.getNave().isMuerto()) g.getNave().bajaRotation();
						break;

					case "right down":
						//out.println("derecha");
						if(!g.getNave().isMuerto()) g.getNave().subeRotation();
						break;

					case "shoot down":
						//out.println("disparo");
						if(!g.getNave().isMuerto()){
							if(!g.getNave().getDisparo()) g.getNave().setDisparo(true);
							if(g.getNave().getDisparo()) g.getNave().disparar();
							g.getMapa().suma1disparo();
						}
						break;

					default: break;
				}
			}
			else if(tipo==2){
				/////////  released buttons ///////////
				switch(msg){
					case "up released":
						g.getNave().setImpulso(false);
						break;
						
					case "shoot released":
						g.getNave().setDisparo(false);
						break;
						
					case "left released":
						g.getNave().setIzquierda(false);
						break;
					
					case "right released":
						g.getNave().setDerecha(false);
						break;
					
					default: 
						System.out.println("Ignoring input line");
						out.println("Unknown line, ignoring line");
						break;
				}
			}

		}
	}

}




