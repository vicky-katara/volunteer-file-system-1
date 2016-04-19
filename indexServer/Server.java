package indexServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import lib.NetworkAddress;
import lib.Packet;
import lib.SenderReceiver;

public class Server extends Thread implements Runnable{
	
	protected Socket clientSocket = null;
	protected static int serverPort = 65423; // changed by Vicky
	static Map<String, Integer> globalSet = new HashMap<String, Integer>();
	private Thread runningThread = null;

	Server(Socket socketToClient){
		clientSocket = socketToClient;
	}
	
	@Override
	public void run() {
		// Exchange messages with the client to share data.
		while(true) {
			//Receive Payload sent from the client
			Packet payloadFromClient = new Packet(new SenderReceiver().receiveMessageViaTCPOn(clientSocket));
			
			//Get option from client
			int clientOption = payloadFromClient.getType();

			switch(clientOption){
			case 0:
				new SenderReceiver().sendMesssageViaTCPOn(clientSocket,new Packet(1,"").toString());
				//Get port, ip address from client
				String[] clientInformation = payloadFromClient.getData().split(":");
				String clientIPaddress = clientInformation[0];
				int clientPortNumber = Integer.parseInt(clientInformation[1]);
				addToHash(clientIPaddress, clientPortNumber);
				viewCurrentHash();
				break;
			case 2:
				//Send current list of active peers to requesting client
				Packet packetFromServer = new Packet(3, allActivePeersHash());
				String payloadFromServer = packetFromServer.getPayload();
				new SenderReceiver().sendMesssageViaTCPOn(clientSocket, payloadFromServer);
				break;
			}			
		}
		
		
	}
	
	public void addToHash(String clientIPaddress, int clientPortNumber){
		globalSet.put(clientIPaddress, clientPortNumber);
	}
	
	public void emptyHash(){
		
	}
	
	public void updateHash() {
		
	}
	
	public String allActivePeersHash(){
		
		StringBuilder listOfPeers = new StringBuilder();
		for (Map.Entry<String, Integer> entry : globalSet.entrySet()) {
		    listOfPeers.append(entry.getKey()+":"+entry.getValue()+";");
		}
		return listOfPeers.toString();
	}
	
	public void viewCurrentHash(){
		
		for (Map.Entry<String, Integer> entry : globalSet.entrySet()) {
		    System.out.println(entry.getKey()+" : "+entry.getValue());
		}
	}
	
	public static void main(String[] args) {
		
		
		try (ServerSocket ssock = new ServerSocket(serverPort)) { 
			
			Socket connectionSocket;
			//Get IP Address of 'enp0s8'
			String ipAddress = NetworkAddress.getIPAddress("enp0s8");
			//String ipAddress = "192.168.0.36"; //hardcoded for mac
			// Upload IPAddress:Port number at http://www4.ncsu.edu/~vpkatara/server.txt
			new UploadToServer().upload(ipAddress, serverPort);
			
			System.out.println("Server Started!! at "+ipAddress+":"+serverPort);
			
            while (true) {
            	Socket sock = ssock.accept();
				new Thread(new Server(sock)).start();
	        }
	    } catch (IOException e) {
            System.err.println("Could not listen on port " + serverPort);
            System.exit(-1);
        }
		
	}
}
