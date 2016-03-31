package clientPeer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.net.Socket;

import lib.NetworkAddress;
import lib.Packet;
import lib.Peer;
import lib.SenderReceiver;

public class Client {
	
	ArrayList<Peer> peerList = new ArrayList<Peer>();
	
	static boolean debugFlag=true;
	static int requestReceiverPortNumber = 4567;
	static String selfIpAddress;
	
	private Socket socketToServer;
	
	Client(String serverIP, int portNumber){
		// Set self IP Address
		//Client.selfIpAddress = "192.168.0.40"; - for mac
		Client.selfIpAddress = NetworkAddress.getIPAddress("enp0s8");
		
		// Connect to the Server and ask for host names available to store files.
		System.out.println("Connecting to server at "+serverIP+":"+portNumber);
		
		// Create socket to server
		socketToServer = new SenderReceiver().returnSocketTo(serverIP, portNumber);
		
		// Create Payload to send to the server
		String portNumberPayload = new Packet(0, Client.selfIpAddress+":"+Client.requestReceiverPortNumber).getPayload();//preparePayLoad(0, fileInfo); // FileNames is Option 0:
		System.out.println("For Debugging: ");
		System.out.println(Client.selfIpAddress);
		System.out.println("Check this: "+portNumberPayload);
		// send payload via TCP to server
		new SenderReceiver().sendMesssageViaTCPOn(socketToServer, portNumberPayload);
		
		// receive list of all IP Addresses and Port Numbers from Server 
		/*String peerListString = new Packet(new SenderReceiver().receiveMessageViaTCPOn(socketToServer)).getData();
		
		System.out.println("Server said:"+peerListString);
		
		String[] peerArr = peerListString.split(";");
		
		Peer self = new Peer(Client.selfIpAddress, Client.requestReceiverPortNumber);
		
		for(int i=0; i<peerArr.length; i++){
			Peer newPeer = new Peer(peerArr[i]);
			if( newPeer.equals(self) == false){
				peerList.add(newPeer);
			}
		}*/
		
		// Available list
		System.out.println("Here is the list of all Peers online right now:"+peerList);
		
		// interact with user
		main_menu();
		
		// go to command line interface
	}

	private static void main_menu() {
		Scanner in = new Scanner(System.in);
		String parenPattern = "[(](.*)[)]";
		Pattern open = Pattern.compile("^open"+parenPattern+"$");
		Pattern close = Pattern.compile("^close"+parenPattern+"$");
		Pattern read = Pattern.compile("^read"+parenPattern+"$");
		Pattern write = Pattern.compile("^write"+parenPattern+"$");
		Pattern mount = Pattern.compile("^mount"+parenPattern+"$");
		Matcher m;
		String consoleString = "";

		while (!consoleString.equals("exit")){
			// contains the main menu options
			System.out.println("commands supported: open(),close(),read(),write(),mount(),exit");
			consoleString = in.nextLine();

			m = open.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("open command called with parameter "+ fileName);
				open(fileName);
				continue;
			}
			m = close.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("close command called with parameter "+ m.group(1));
				close(fileName);
				continue;
			}
			m = read.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("read command called with parameter "+ m.group(1));
				read(fileName);
				continue;
			}
			m = write.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("write command called with parameter "+ m.group(1));
				write(fileName);
				continue;
			}
			m = mount.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("mount command called with parameter "+ m.group(1));
				mount(fileName);
				continue;
			}
			if (consoleString.equals("exit")){
				System.out.println("exiting now.");
				//System.exit(0);
				break;
			}
			//catchall
			System.out.println("command "+consoleString+"not recognized, please try again.");
		
		}

		in.close();
	}

	private static void mount(String fileName) {
		// TODO Auto-generated method stub
		
	}

	private static void write(String fileName) {
		// TODO Auto-generated method stub
		
	}

	private static void read(String fileName) {
		// TODO Auto-generated method stub
		
	}

	private static void close(String fileName) {
		// TODO Auto-generated method stub
		
	}

	private static void open(String fileName) {
		// TODO Auto-generated method stub
	}
	
	public static void main(String[] args) {
		try {
			//String[] connectionInfo = new URLReader().getConnectionString().split(":");
			//new RequestReceiver(requestReceiverPortNumber).start();
			Client c =new Client("192.168.0.36",7739);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
