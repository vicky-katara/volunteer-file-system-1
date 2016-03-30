package requestReceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import lib.Peer;
import lib.Packet;
import lib.SenderReceiver;

public class Servant extends Thread{

	DatagramPacket udpDatagram;
	DatagramSocket socketOnWhichToSend;
	String pathToVideosDir;
	Servant(DatagramPacket fromRequestor, DatagramSocket onWhichToSend){
//		this.udpDatagram = fromRequestor;
//		this.socketOnWhichToSend = onWhichToSend;
//		pathToVideosDir = System.getProperty("user.home")+File.separatorChar+"Videos";
	}
	public void run() {
		Packet option100Packet = null;
		//while(true){
			try {
//				option100Packet = new Packet(new String(udpDatagram.getData()));
//				if(option100Packet.getOption()!=100)
//					throw new Exception("option100Packet has option "+option100Packet.getOption()+" : Servant.run()");
//				String[] splitted = option100Packet.getData().split(":");
//				Chunk chunkToBeReturned = getRequestedChunk(splitted[0], Integer.parseInt(splitted[1]));
//				//new SenderReceiver().sendMesssageOn(packetFromRequestor, new Packet(101, option100Packet.getData()+":"+chunkToBeReturned).getPayload());
//				new SenderReceiver().sendUDPReply(socketOnWhichToSend, new Peer(udpDatagram.getAddress().getHostAddress(), udpDatagram.getPort()), new Packet(101, option100Packet.getData()+":"+chunkToBeReturned).getPayload());
			}
			catch(NullPointerException npe){
				System.err.println("Problem with option100Packet: "+option100Packet);
				npe.printStackTrace();
				//currentThread().stop();
			}
			catch (Exception e) {e.printStackTrace();
			}
			
		//}
	}
	
	public static void main(String[] args) throws UnknownHostException {
		//System.out.println(System.getProperty("user.home")+File.separatorChar+"Videos");
		//System.out.println(InetAddress.getByName("192.168.1.1").getHostAddress());
		//byte[] size512 = "Vicky".getBytes();
//		byte[] size512 = new byte[512];
//		String s = new String(size512).trim();
//		System.out.println(s.length());
	}
}
