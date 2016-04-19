package requestReceiver;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Iterator;

import lib.SenderReceiver;
import lib.Chunk;
import lib.ChunkTriplePeer;
import lib.FileMetaData;
import lib.P2pFile;
import lib.Packet;
import lib.Peer;


public class Requester extends Thread{
	
	private DatagramPacket udpDatagram;
	private int option;
	private String chunkname;
	private byte[] storeBytes;
	
	private FileMetaData metadata;
	private ArrayList<Chunk> chunkList;
	private String[] splitted;
	private ArrayList<Peer> availablePeers;
	private int lastPeerIndex = 0;
	//private ArrayList<ChunkTriplePeer> chunkPeerList;
	
	//Refer types 100, 101
	public boolean checkIfPeerIsUp(Peer p){
		long nanotime = System.nanoTime();
		String payload = new SenderReceiver().sendDatagramAndGetUDPReplyOnWithTimer(p, new Packet(100, "").getPayload(), 7000);
		if(payload.contentEquals("timeout"))
			return false;
		Packet type101Packet = new Packet(payload);
//		Packet receivedPacket = new Packet(new String(udpDatagram.getData()));
		if(type101Packet.getType()==101) {
			return true;
		} else {
			System.out.println("Unexpected Type message received: "+type101Packet);
			return false;
		}
	}
	

	//Refer type 102, 103
//	public void getChunkList(String chunkname, Peer p, ArrayList<Peer> availablePeers) throws Exception{
//		new SenderReceiver().sendDatagramAndGetUDPReplyOn(p, new Packet(102,chunkname).getPayload());
//		Packet receivedPacket = new Packet(new String(udpDatagram.getData()));
//		if(receivedPacket.getType()==103) {
//			splitted = receivedPacket.getData().split(":");
//			chunkname = splitted[0];
//			Chunk toDistribute = new Chunk(chunkname, splitted[1]);
//			byte[] chunksRetrieved = toDistribute.returnBytes();
//			
////			RoundRobin<Peer> p4 = new RoundRobin<Peer>(availablePeers);
////			Iterator it = p4.iterator();
//			int chunkIndex = 0;
//			while(chunkIndex != chunksRetrieved.length-1) {
//				Peer toSend = (Peer) it.next();
//				if(checkIfPeerIsUp(toSend)){
//					Chunk c = new Chunk(chunkname,String.valueOf(chunksRetrieved[chunkIndex]));
//					chunkIndex++;
//					push(toSend, c);
//				} else {
//					System.out.println(toSend.getIpAddress()+"is down!");
//				}	
//			}	
//		} else {
//			throw new Exception("ACK not received");
//		}
//		
//	}
			
	//Refer types 106, 107
	public void delete(Peer p, Chunk c) throws Exception{
		new SenderReceiver().sendDatagramAndGetUDPReplyOn(p, new Packet(106,c.getChunkName()).getPayload());
		Packet receivedPacket = new Packet(new String(udpDatagram.getData()));
		if(receivedPacket.getType()==107 && receivedPacket.getData().split(":")[0].equals(c.getChunkName())) {
			System.out.println("Chunk deleted");
		} else {
			throw new Exception("Reply not expected");
		}
		
	}
	
	//Push 
	public void pushFile(P2pFile p2p, ArrayList<Peer> availablePeers){
		chunkList = p2p.getChunkList();
		metadata = p2p.getMetadata();
		this.availablePeers = availablePeers;
		
		for(int chunkIndex=0; chunkIndex<chunkList.size();chunkIndex++){
			
			for(int replicaNumber = 1; replicaNumber <=3 ; replicaNumber++){
				Peer toSend = returnNextPeer();
				if(checkIfPeerIsUp(toSend)){
					metadata.getChunkPeerNumber(chunkIndex).setPeerNumber(replicaNumber, toSend);
					System.out.println("Sending chunk number "+chunkIndex+" of "+metadata.getFileName()+" to "+toSend);
					push(toSend, chunkList.get(chunkIndex));
				} else {
					availablePeers.remove(toSend);
					if(availablePeers.size()<3){
						System.err.println("Not enough peers for triple replication");
						return;
					}
					chunkIndex--;
					continue;
				}
			}

		}		
		
	}
	
	//Refer types 104, 105
	public void push(Peer p, Chunk c){
		String payload = new SenderReceiver().sendDatagramAndGetUDPReplyOnWithTimer(p, new Packet(104,c.getChunkName()+":"+c.returnBytes()).getPayload(), 7000);
		
		if(payload.contentEquals("timeout")){
			try {
				throw new Exception("timeout occured");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		Packet receivedPacket = new Packet(payload);
		if(receivedPacket.getType()==105 && receivedPacket.getData().split(":")[0].equals(c.getChunkName())) {
			System.out.println("Chunk inserted");
			//Update metadata here
		} else {
			try {
				throw new Exception("Reply not expected");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Peer returnNextPeer(){
		return availablePeers.get((lastPeerIndex+1)%availablePeers.size());
	}
	
	//Fetch 
	public void fetchFile(P2pFile p2p){
		metadata = p2p.getMetadata();
		chunkList = p2p.getChunkList(); //empty
		ArrayList<ChunkTriplePeer> ctp = metadata.returnChunkPeer();
		for (int chunkTripleIndex = 0; chunkTripleIndex< ctp.size(); chunkTripleIndex++){
			Peer isUp = ctp.get(chunkTripleIndex).getPrimaryPeer(chunkTripleIndex);
			if(checkIfPeerIsUp(isUp)){
				String chunkname = ctp.get(chunkTripleIndex).getChunk();
				Chunk c = new Chunk(chunkname);
				new SenderReceiver().sendDatagramAndGetUDPReplyOn(isUp, new Packet(102,chunkname).getPayload());
				Packet receivedPacket = new Packet(new String(udpDatagram.getData()));
				if(receivedPacket.getType()==103) {
					splitted = receivedPacket.getData().split(":");
					chunkname = splitted[0];
					Chunk toDistribute = new Chunk(chunkname, splitted[1]);
					byte[] chunksRetrieved = toDistribute.returnBytes();
					Chunk newChunk = new Chunk(chunkname, chunksRetrieved);
					chunkList.add(newChunk);
				} else {
					try {
						throw new Exception("Reply not expected");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				try {
					throw new Exception("Peer is not up");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		
		
		
	}
	
	//Delete
	public void deleteFile(P2pFile p2p){
		chunkList = p2p.getChunkList(); //chunk list to delete
		metadata = p2p.getMetadata(); //Old metadata
		
		for(int chunkIndex=0; chunkIndex<chunkList.size();chunkIndex++){
			
			for(int replicaNumber = 1; replicaNumber <=3 ; replicaNumber++){
				Peer toSend = returnNextPeer();
				if(checkIfPeerIsUp(toSend)){
					metadata.getChunkPeerNumber(chunkIndex).setPeerNumber(replicaNumber, toSend);
					try {
						delete(toSend, chunkList.get(chunkIndex));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					availablePeers.remove(toSend);
					chunkIndex--;
					continue;
				}
			}

		}
		
	}
	public static void main(String[] args){
		
	}
}
	
//	class RoundRobin<Peer> implements Iterable<Peer>{
//		private ArrayList<Peer> peers = new ArrayList<Peer>();
//		
//		public RoundRobin(ArrayList<Peer> peers){
//			this.peers = peers;
//		}
//		@Override
//		public Iterator<Peer> iterator() {
//			return new Iterator<Peer>(){
//				private int index = 0;
//				
//				public Peer next(){
//					Peer curr = peers.get(index);
//					//System.out.println(index);
//					index = (index+1)%peers.size();
//					return curr;
//				}
//				@Override
//				public boolean hasNext() {
//					// TODO Auto-generated method stub
//					return true;
//				}
//			};
//		}


