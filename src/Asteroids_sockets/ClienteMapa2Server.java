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
    protected boolean isLogged2Server(){return this.logged;}


	public ClienteMapa2Server(Mapa mapa) {
		logged = false;
		this.mapa = mapa;
	}


   public void run(){
        sendBroadCast();
        System.out.println( "ServerIP = " + ServerIP + ":" + ServerPORT);
        makeContact(ServerIP, ServerPORT);		//envia 1º linea de identificacion como MAPA y trata la respuesta
        boolean done=false;
        String respuesta = null;
		while(!done){
			System.out.println("ClienteMapa2Server lee otro mensaje del server (ClienteMapa)");
			if((respuesta = recibeMSg()) == null) done = true;
			else mapa.trataRespuesta(respuesta);
        }
		closeLink();
    }

   /**
    * El msg que reenvia al server (ClienteMapa)
    * @param s
    */
    protected void sendMsg(String s){
    	try{s.trim().toLowerCase();}
        catch (Exception e){e.printStackTrace();}

        System.out.println("Mapa envia msg a ClienteMapa: "+s);
        if(getSocket().isConnected() && !getSocket().isClosed()){
        	if(!s.equals(null))	out.println(s);
        }
    }
    
    /**
     * El msg que recibe del server (ClienteMapa)
     * @return
     */
    protected String recibeMSg(){
        String line = null;
        try{
        	if ((line=in.readLine())!=null) return line.trim().toLowerCase();
            else line=null;
        }catch(Exception e){e.printStackTrace();}
        return line;
    }

    private void makeContact(String HOST, int PORT) {
        try {
            sock = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);

            out.println("null;null;null;null;null");        //1º linea q envia para identificarse con el server como MAPA
            String str = in.readLine();
            System.out.println("makeContact de Mapa2Server Lee resp del server a la primera linea: "+str);
            mapa.PrimeraRespuesta(str);

        } catch (Exception e) {
        	System.out.println("Errrrrr.... "+ e.getMessage());
        	e.printStackTrace();
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
                System.out.println("sendBroadCast de MAPA");
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
            System.out.println( ">>> Broadcast response from server IP: " + receivePacket.getAddress().getHostAddress());

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
            out.println("all_mandos;"+mapa.getMapaID()+";bye;null;null");
            sock.close();
            in.close();
            out.close();
            c.close();
        } catch (Exception e) {System.out.println("Erro0or...."+ e.getMessage()); e.printStackTrace();}
    }

}
