package lib;

import java.net.Socket;

public class Peer {
	private String ipAddress;
	public String getIpAddress() {
		return ipAddress;
	}
	public int getPortNumber() {
		return portNumber;
	}
	private int portNumber;
	public Peer(String ipAddress, int portNumber){
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
	}
	
	public Peer(String canonical){
		String[] arr = canonical.split(":");
		this.ipAddress = arr[0];
		this.portNumber = Integer.parseInt(arr[1]);
	}
	
	public boolean equals(Object o){
		if(o instanceof Peer == false )
			return false;
		Peer newPeer = (Peer)o;
		return newPeer.ipAddress.equals(this.ipAddress) && newPeer.portNumber==this.portNumber;
	}
	
	public String toString(){
		return this.ipAddress+":"+this.portNumber;
	}
	Socket returnOpenSocketToIt(){
		try{
			System.out.println("Attempting to create socket to "+toString());
			return new Socket(ipAddress, portNumber);
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(21);//some error
			return null;
		}
	}
	public static void main(String[] args) {
		System.out.println("Vicky was here!");
	}
}