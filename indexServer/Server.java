package indexServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lib.NetworkAddress;

public class Server implements Runnable{
	
	Server(Socket socketToClient){
		
	}
	
	@Override
	public void run() {
		// Exchange messages with the client to share data.
		
	}
	
	public static void main(String[] args) {
		int serverPort = 65423; // changed by Vicky
		ServerSocket ssock;
		
		try {
			ssock = new ServerSocket(serverPort);
			// changed by Vicky
			Socket connectionSocket;
			
			//Get IP Address of 'enp0s8'
			String ipAddress = NetworkAddress.getIPAddress("enp0s8");
			// Upload IPAddress:Port number at http://www4.ncsu.edu/~vpkatara/server.txt
			new UploadToServer().upload(ipAddress, serverPort);
			
			System.out.println("Server Started!! at "+ipAddress+":"+serverPort);
			
			while (true) {
				Socket sock = ssock.accept();
				new Thread(new Server(sock)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
