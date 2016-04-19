package lib;

/*
 * Creates library functions to send and receive data over TCP / UDP.
 * receiveMessageOn: Receives a message on the given socket using TCP.
 * sendMesssageViaTCPOn: Sends a message containing 'payload' on TCP.
 * sendDatagramAndGetUDPReplyOn: Sends a UDP datagram and waits for a reply on the same datagram socket.
 * sendUDPReply: Sends a message on a created UDP Datagram Socket.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class SenderReceiver {
	Socket s;
	public SenderReceiver(){
	}
	
	public String receiveMessageViaTCPOn(Socket socket){
		try{
			if(socket.isClosed())
				throw new Exception("receiveMessageOn:"+socket.toString()+" is closed. Cannot continue");
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			String msg = dis.readUTF();
			//System.out.println("receiveMessageOn() Received "+msg.substring(0, 20));
			//System.out.println("receiveMessageOn() Received "+msg);
			System.out.println("Received "+msg+" from "+socket.getInetAddress().getHostAddress()+":"+socket.getPort());
			return msg;
		}
		catch(Exception e){
			e.printStackTrace();
			if(e.getMessage().contains("receiveMessageOn"))
				System.exit(12); // exit 12 --> client to video Server abnormally disconnected
			return "No reply received";
		}
	}
	
	public void sendMesssageViaTCPOn(Socket socket, String payload){
		try{
			System.out.println("Sending "+payload+" to "+socket.getInetAddress().getHostAddress()+":"+socket.getPort());
			if(socket.isClosed())
				throw new Exception("sendMesssageOn:"+socket.toString()+" is closed. Cannot continue");
			//System.out.println("Trying to send |"+payload.substring(0, Math.min(20, payload.length()))+"...| to "+socket.getInetAddress()+":"+socket.getPort());
			//System.out.println("Trying to send |"+payload+"...| to "+socket.getInetAddress()+":"+socket.getPort());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF(payload);
		}
		catch(Exception e){
			e.printStackTrace();
			if(e.getMessage().contains("sendMessageOn"))
				System.exit(11); // exit 11 --> client to video Server abnormally disconnected
		}
	}
	
	public String sendDatagramAndGetUDPReplyOn(Peer p, String payload){
		try{
			System.out.println("Sending UDP "+payload+" to "+p);
			DatagramSocket clientSocket = new DatagramSocket();
			byte[] sendData = payload.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(p.getIpAddress()), p.getPortNumber());
			System.out.println("Sending datagram:"+sendData);
			clientSocket.send(sendPacket);
			byte[] receiveData = new byte[10240];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			String reply = new String(receivePacket.getData());
			System.out.print("Received datagram:"+reply+"|");
			System.out.print("Of the form :"+Arrays.toString(receivePacket.getData()));
			System.out.println("from "+p);
			clientSocket.close();
			return reply;
		}catch(Exception e){e.printStackTrace();return "Error in receiving datagram packet";}
	}
	
	public String sendDatagramAndGetUDPReplyOnWithTimer(Peer p, String payload, int milliseconds){
		try{
			System.out.println("Sending UDP "+payload+" to "+p);
			DatagramSocket clientSocket = new DatagramSocket();
			// Set timeout in milliseconds
			clientSocket.setSoTimeout(milliseconds);
			
			byte[] sendData = payload.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(p.getIpAddress()), p.getPortNumber());
			System.out.println("Sending datagram:"+sendData);
			clientSocket.send(sendPacket);
			byte[] receiveData = new byte[10240];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			String reply = new String(receivePacket.getData());
			System.out.print("Received datagram:"+reply+"|");
			System.out.print("Of the form :"+Arrays.toString(receivePacket.getData()));
			System.out.println("from "+p);
			clientSocket.close();
			return reply;
		} catch(SocketTimeoutException ste){
			return "timeout";
		}
		catch(Exception e){e.printStackTrace();return "Error in receiving datagram packet";}
	}
	
	public void sendUDPReply(DatagramSocket serverSocket, Peer p, String payload){
		try{
			byte[] sendData = payload.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(p.getIpAddress()), p.getPortNumber());
			//System.out.println("Sending UDP Reply datagram:"+payload+"|");
			System.out.println("Replying UDP "+payload+" to "+p);
			serverSocket.send(sendPacket);
		}
		catch(UnknownHostException uhe){
			uhe.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Socket returnSocketTo(String serverIP, int serverPort){
		try{
		//System.out.println("Reached here");
		return new Socket(serverIP, serverPort);
		}
		catch(IOException e){
			System.err.println("Error:");
			e.printStackTrace();
			return null;
		}
	}
	
//	public static void main(String[] args) throws Exception {
//		System.out.println(InetAddress.getByName("123.123.123.123"));
//	}

}
