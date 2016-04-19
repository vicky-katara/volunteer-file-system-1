package requestReceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import lib.Chunk;
import lib.Peer;
import lib.Packet;
import lib.SenderReceiver;

public class Servant extends Thread{

	DatagramPacket udpDatagram;
	DatagramSocket socketOnWhichToSend;
	public static String pathToDir;
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
				System.out.println("Received "+new String(udpDatagram.getData()));
				receivedPacket = new Packet(new String(udpDatagram.getData()));
				System.out.println("Servant trying to handle "+receivedPacket+" ...");
				if(receivedPacket.getType()==100) {
					new SenderReceiver().sendUDPReply(socketOnWhichToSend, new Peer(udpDatagram.getAddress().getHostAddress(), udpDatagram.getPort()), new Packet(101, "").getPayload());
				} else if(receivedPacket.getType()==102) {
					String chunkName = receivedPacket.getData();
					Chunk chunkToBeReturned = getRequestedChunk(chunkName+udpDatagram.getAddress().getHostAddress());
					new SenderReceiver().sendUDPReply(socketOnWhichToSend, new Peer(udpDatagram.getAddress().getHostAddress(), udpDatagram.getPort()), new Packet(103, receivedPacket.getData()+":"+chunkToBeReturned).getPayload());
				} else if(receivedPacket.getType()==104) {
					String[] splitted = receivedPacket.getData().split(":");
					Chunk toBeStored = new Chunk(splitted[0]+udpDatagram.getAddress().getHostAddress(), splitted[1]);
					storeChunk(toBeStored);
					new SenderReceiver().sendUDPReply(socketOnWhichToSend, new Peer(udpDatagram.getAddress().getHostAddress(), udpDatagram.getPort()), new Packet(105, splitted[0]).getPayload());
				} else if(receivedPacket.getType()==106){
					deleteFile(receivedPacket.getData()+udpDatagram.getAddress().toString());
					new SenderReceiver().sendUDPReply(socketOnWhichToSend, new Peer(udpDatagram.getAddress().getHostAddress(), udpDatagram.getPort()), new Packet(107, receivedPacket.getData()).getPayload());
				} else {
					throw new Exception("receivedPacket has option "+receivedPacket.getType()+" : Servant.run() --> "+receivedPacket);
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
			byte[] b = new byte[Chunk.CHUNK_SIZE];
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
	
	void deleteFile(String fileName){
		Path deleteFilePath = FileSystems.getDefault().getPath(pathToDir+File.separatorChar+fileName);
		try {
			Files.delete(deleteFilePath);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", deleteFilePath);
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", deleteFilePath);
		} catch (IOException x) {
		    // File permission problems are caught here.
		    System.err.println(x);
		}
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
