package requestReceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;

import lib.Chunk;
import lib.Peer;
import lib.Packet;
import lib.SenderReceiver;

public class Servant extends Thread{

	DatagramPacket udpDatagram;
	DatagramSocket socketOnWhichToSend;
	String pathToDir;
	Servant(DatagramPacket fromRequestor, DatagramSocket onWhichToSend){
		this.udpDatagram = fromRequestor;
		this.socketOnWhichToSend = onWhichToSend;
		pathToDir = System.getProperty("user.home")+File.separatorChar+"Chunks";
		File pathDir = new File(pathToDir);
		if (pathDir.isDirectory()==false)
			pathDir.mkdir();
	}
	public void run() {
		Packet receivedPacket = null;
		//while(true){
			try {
				receivedPacket = new Packet(new String(udpDatagram.getData()));
				if(receivedPacket.getOption()==100) {
					new SenderReceiver().sendUDPReply(socketOnWhichToSend, new Peer(udpDatagram.getAddress().getHostAddress(), udpDatagram.getPort()), new Packet(101, "").getPayload());
				} else if(receivedPacket.getOption()==102) {
					String chunkName = receivedPacket.getData();
					Chunk chunkToBeReturned = getRequestedChunk(chunkName);
					new SenderReceiver().sendUDPReply(socketOnWhichToSend, new Peer(udpDatagram.getAddress().getHostAddress(), udpDatagram.getPort()), new Packet(103, receivedPacket.getData()+":"+chunkToBeReturned).getPayload());
				} else if(receivedPacket.getOption()==104) {
					String[] splitted = receivedPacket.getData().split(":");
					Chunk toBeStored = new Chunk(splitted[0], splitted[1]);
					storeChunk(toBeStored);
					new SenderReceiver().sendUDPReply(socketOnWhichToSend, new Peer(udpDatagram.getAddress().getHostAddress(), udpDatagram.getPort()), new Packet(105, splitted[0]).getPayload());
				} else {
					throw new Exception("receivedPacket has option "+receivedPacket.getOption()+" : Servant.run() --> "+receivedPacket);
				}
			}
			catch(NullPointerException npe){
				npe.printStackTrace();
			}
			catch (Exception e) {e.printStackTrace();
			}
	}
	
	Chunk getRequestedChunk(String fileName){
		try {
			byte[] b = new byte[256];
			RandomAccessFile raf = new RandomAccessFile(pathToDir+File.separatorChar+fileName, "r");// doesn't have to be done every time!
			raf.read(b);
			raf.close();
			return new Chunk(b);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	void storeChunk(Chunk toBeStored){
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(pathToDir+File.separatorChar+toBeStored.getChunkName());
			fos.write(toBeStored.returnBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws UnknownHostException {
		
	}
}
