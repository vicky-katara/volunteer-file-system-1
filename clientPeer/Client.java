package clientPeer;

public class Client {
	
	Client(String serverIP, int portNumber){
		// Connect to the Server and ask for host names available to store files.
	}
	
	public static void main(String[] args) {
		String[] connectionInfo = new URLReader().getConnectionString().split(":");
//		new RequestReceiver(requestReceiverPortNumber).start();
		Client c =new Client(connectionInfo[0],Integer.parseInt(connectionInfo[1]));
	}
}
