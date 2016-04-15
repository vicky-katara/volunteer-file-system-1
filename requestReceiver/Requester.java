package requestReceiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import lib.SenderReceiver;
import lib.Packet;
import lib.Peer;


public class Requester extends Thread{
	
	private DatagramSocket requesterSocket;
	private int option;
	private String chunkname;
	private byte[] storeBytes;
	
	
	Requester(int port, int option) throws SocketException{
		this.requesterSocket = new DatagramSocket(port);
		this.option = option;
	}
	
	Requester(int port,int option, String chunkname)throws SocketException{
		this.requesterSocket = new DatagramSocket(port);
		this.option = option;
		this.chunkname = chunkname;
	}
	
	Requester(int port,int option, String chunkname, byte[] dataBytes) throws SocketException{
		this.requesterSocket = new DatagramSocket(port);
		this.option = option;
		this.chunkname = chunkname;
		this.storeBytes = dataBytes;
	}
	
	
	public void run()
	   {
		Packet sendPacket = null;
	    	try {
	    		if(option==100){
	    			sendPacket = new Packet(100, "");
	    			new SenderReceiver().sendDatagramAndGetUDPReplyOn(new Peer(requesterSocket.getInetAddress().getHostAddress(),requesterSocket.getPort()),sendPacket.getPayload());
	    			//recieved msg processing
	    			
	    		} else if(option == 102) {
	    			sendPacket = new Packet(102, chunkname);
	    			new SenderReceiver().sendDatagramAndGetUDPReplyOn(new Peer(requesterSocket.getInetAddress().getHostAddress(),requesterSocket.getPort()),sendPacket.getPayload());
	    			//recieved msg processing
	    			
	    		} else if(option == 104){
	    			StringBuilder chunks = new StringBuilder(chunkname+":[");
	    			for(byte b: storeBytes){
	    				chunks.append(b+",");
	    			}
	    			chunks.append("]");
	    			sendPacket = new Packet(104, chunks.toString());
	    			new SenderReceiver().sendDatagramAndGetUDPReplyOn(new Peer(requesterSocket.getInetAddress().getHostAddress(),requesterSocket.getPort()),sendPacket.getPayload());
	    			//recieved msg processing
	    			
	    		} else if(option == 106){
	    			sendPacket = new Packet(106, chunkname);
	    			new SenderReceiver().sendDatagramAndGetUDPReplyOn(new Peer(requesterSocket.getInetAddress().getHostAddress(),requesterSocket.getPort()),sendPacket.getPayload());
	    			//recieved msg processing
	    			
	    		} else {
	    			throw new Exception("Wrong option added");
	    		}
	    	} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
	    		
	    	}
		
	   }
	
	public static void main(String[] args){
		
		
	}

}
