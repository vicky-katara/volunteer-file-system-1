package clientPeer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import lib.NetworkAddress;
import lib.Packet;
import lib.Peer;
import lib.SenderReceiver;
import requestReceiver.RequestReceiver;

public class Client {
	
	ArrayList<Peer> peerList = new ArrayList<Peer>();
	
	static boolean debugFlag=true;
	static int requestReceiverPortNumber = 4576;
	static String selfIpAddress;
	static File currentDirectory;
	
	private Socket socketToServer;
	
	Client(String serverIP, int portNumber){
		// Set self IP Address
		//Client.selfIpAddress = "192.168.0.44"; //- for mac
		Client.selfIpAddress = NetworkAddress.getIPAddress("enp0s3");
		
		// Connect to the Server and ask for host names available to store files.
		System.out.println("Connecting to server at "+serverIP+":"+portNumber);
		
		// Create socket to server
		socketToServer = new SenderReceiver().returnSocketTo(serverIP, portNumber);
		
		// Create Payload to send to the server
		//String portNumberPayload = new Packet(0, Client.selfIpAddress+":"+Client.requestReceiverPortNumber).getPayload();//preparePayLoad(0, fileInfo); // FileNames is Option 0:
		String portNumberPayload = new Packet(1, Client.selfIpAddress+":"+Client.requestReceiverPortNumber).getPayload();//preparePayLoad(0, fileInfo); // FileNames is Option 0:
		// send payload via TCP to server
		new SenderReceiver().sendMesssageViaTCPOn(socketToServer, portNumberPayload);
		
		// receive list of all IP Addresses and Port Numbers from Server 
		String peerListString = new Packet(new SenderReceiver().receiveMessageViaTCPOn(socketToServer)).getData();
		
		System.out.println("Server said:"+peerListString);
		
		String[] peerArr = peerListString.split(";");
		
		Peer self = new Peer(Client.selfIpAddress, Client.requestReceiverPortNumber);
		
		for(int i=0; i<peerArr.length; i++){
			Peer newPeer = new Peer(peerArr[i]);
			if( newPeer.equals(self) == false){
				peerList.add(newPeer);
			}
		}
		
		// Available list
		System.out.println("Here is the list of all Peers online right now:"+peerList);
		
		// interact with user
		currentDirectory = new File(System.getProperty("user.dir"));
		try {
			main_menu();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// go to command line interface
	}

	private void main_menu() throws IOException {
		//assuming space seperators
		Scanner in = new Scanner(System.in);
		Pattern cd = Pattern.compile("^cd (.*)$"); 				//change directory
		Pattern mkdir = Pattern.compile("^mkdir (.*)$");		//make directory
		Pattern mv = Pattern.compile("^mv (.*) (.*)$");			//move (local_file remote_file)
		Pattern mvb = Pattern.compile("^mvb (.*) (.*)$");		//move back (remote_file local_file)
		Pattern rm = Pattern.compile("^rm (.*)$");				//remove
		Pattern rename = Pattern.compile("^rename (.*) (.*)$");	//rename (current_remote_file_name new_remote_file_name)
		Matcher m;
		String consoleString = "";
		//FileMetadata currentDirectory= new FileMetadata();

		while (!consoleString.equals("exit")){
			// contains the main menu options
			System.out.println("commands supported:cd,mkdir,mv,mvb,rm,rename,exit");
			consoleString = in.nextLine();

			m = cd.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("cd command called with parameter "+ fileName);
				cd(fileName);
				continue;
			}
			m = mkdir.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("mkdir command called with parameter "+ m.group(1));
				mkdir(fileName);
				continue;
			}
			m = mv.matcher(consoleString);
			if(m.find()) {
				String localFileName= m.group(1);
				String remoteFileName= m.group(2);
				if (debugFlag) System.out.println("mv command called with parameter "+ m.group(1));
				mv(localFileName,remoteFileName);
				continue;
			}
			m = mvb.matcher(consoleString);
			if(m.find()) {
				String remoteFileName= m.group(1);
				String localFileName= m.group(2);
				if (debugFlag) System.out.println("mvb command called with parameter "+ m.group(1));
				mvb(remoteFileName,localFileName);
				continue;
			}
			m = rm.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("rm command called with parameter "+ m.group(1));
				rm(fileName);
				continue;
			}		
			m = rename.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("rename command called with parameter "+ m.group(1));
				rename(fileName);
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


	private static void rename(String fileName) {
		// TODO Auto-generated method stub
		
	}

	private static void rm(String fileName) {
		// TODO Auto-generated method stub
		
	}

	private static void mvb(String remoteFileName, String localFileName) {
		// TODO Auto-generated method stub
		
	}

	private static void mv(String localFileName, String remoteFileName) {
		// TODO Auto-generated method stub
		
	}

	private static void mkdir(String fileName) {
		// TODO Auto-generated method stub
		String childPath = currentDirectory.getAbsolutePath()+File.separatorChar+fileName;
		File newDir=new File(childPath);
		boolean status = newDir.mkdir();
		if (status){
			currentDirectory = newDir;
		}
		else {
			System.out.println("directory creation failed:"+childPath);
		}
		System.out.println(currentDirectory.getAbsolutePath());
	}

	private static void cd(String fileName) throws IOException {
		// TODO Auto-generated method stub
		if (fileName.equals("..")||fileName.equals("../")){
			File parent = currentDirectory.getParentFile();
			currentDirectory = parent;
			System.out.println(currentDirectory.getAbsolutePath());
		}
		else if (fileName.contains(""+File.separatorChar)){ //absolute path
			System.out.println("absolute paths not supported yet.");
		}
		else { //relative path
			String childPath = currentDirectory.getAbsolutePath()+File.separatorChar+fileName;
			File child = new File(childPath);
			if (child.isDirectory()){
				currentDirectory = child;
			}
			else {
				System.out.println("not a directory:"+child.getAbsolutePath());
			}
			System.out.println(currentDirectory.getAbsolutePath());
		}
		
	}

	public static void main(String[] args) {
		try {
			String[] connectionInfo = new URLReader().getConnectionString().split(":");
			new RequestReceiver(requestReceiverPortNumber).start();
			Client c =new Client(connectionInfo[0],Integer.parseInt(connectionInfo[1]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
