package Asteroids_sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;

public class ClienteMapa2Server extends Thread{

    private Socket sock;
    private BufferedReader in; 		//i/o for the client
    private PrintWriter out;
    private DatagramSocket c;
    private Mapa mapa;
    private String ServerIP, respuesta;
    private int ServerPORT;
    private boolean logged;	

	//TODO: se comunica con Server.ClienteMapa
    //mando se comunica con ClienteMando, este reenvia msg a ClienteMapa y asi le llega al Mapa mapa

	//IN: BtnACT;IDMando;IDmapa					(de MANDO a MAPA)
    //OUT: IDMando;IDMapa;ACTreply;vidas;puntos	(de MAPA a MANDO)

    //////////////setters & getters  ///////////////////
    protected PrintWriter getOut(){return this.out;}
    protected BufferedReader getIn(){return this.in;}
    protected Socket getSocket() {return this.sock;}    
    protected int getMapaID() {return this.mapa.getMapaID();}
    protected void setMapaID(int id) {this.mapa.setMapaID(id);}



	public ClienteMapa2Server(Mapa mapa) {
		logged = false;
		this.mapa = mapa;
	}
	


   public void run(){
        sendBroadCast();
        System.out.println( "ServerIP = " + ServerIP + ":" + ServerPORT);
        makeContact(ServerIP, ServerPORT);
        boolean done=false;

        while(getSocket().isConnected() && !getSocket().isClosed()){
            if(!getSocket().isConnected())
                mapa.avisoUser("Problema de conexión con el Server");
            else{
                String line = null;
                try {
                    line = in.readLine();
                }
                catch (IOException e) {
                    if(line == null) mapa.avisoUser("No se ha recibido respuesta del Server");
                    e.printStackTrace();
                    System.out.println("Error == "+e.getMessage());
                }
                //mantiene hilo abierto entre Server(ClienteMapa) y Mapa
                respuesta = recibeMSg();       //mantiene escucha de la respuesta del server
                System.out.println("respuesta del server: "+ respuesta);
                mapa.trataRespuesta(respuesta);
            }
        }
    }

    protected void sendMsg(String s){
    	try{s.trim().toLowerCase();}
        catch (Exception e){e.printStackTrace();}

        System.out.println("Mapa envia msg a server: "+s);
        if (s.equals("bye") || s.equals("close") || s.equals("exit")) closeLink();
        else {
            out.println(s);
        }
    }
    
    protected String recibeMSg(){
        String line = null;
        try {
        	line = in.readLine().trim().toLowerCase();
        	System.out.println("msg del server: "+ line);
        }
        catch (IOException e) {e.printStackTrace();}
        return line;
    }

    private void makeContact(String HOST, int PORT) {
        try {
            sock = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);

            out.println("mando");
            String str = in.readLine();
            System.out.println(str);
            System.out.println( "makeContact lee linea: "+str);

        } catch (Exception e) {
        	System.out.println("Errrrrr.... "+ e.getMessage());
        }
    }

    private void sendBroadCast(){

        // Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = "DISCOVER_FUIFSERVER_REQUEST".getBytes();

            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                c.send(sendPacket);
                System.out.println( ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
            	System.out.println(">>>> "+e.getMessage());
            }

            // Broadcast the message over all the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        c.send(sendPacket);
                    } catch (Exception e) {
                    	System.out.println( ">>>> broadcast package failed: "+e.getMessage());
                    }
                    System.out.println(">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            System.out.println(">>> Done looping over all network interfaces. Now waiting for a reply!");

            //Wait for a response
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);

            //We have a response
            System.out.println( ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

            //Check if the message is correct
            String message = new String(receivePacket.getData()).trim();
            if (message.equals("DISCOVER_FUIFSERVER_RESPONSE")) {
            	System.out.println("Server's IP is: " + receivePacket.getAddress());
                ServerIP = receivePacket.getAddress().getHostAddress();
                ServerPORT = receivePacket.getPort();
                logged = true;
            }
            c.close(); //Close the port!
        } catch (Exception ex) {
            ex.printStackTrace();
           System.out.println(">>>"+ex.getMessage());
        }
    }

    protected  void closeLink() {
        try {
            out.println("bye"); // tell server
            sock.close();
            in.close();
            out.close();
            c.close();
        } catch (Exception e) {System.out.println("Erro0or...."+ e.getMessage());}
    }




}
