package requestReceiver;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import clientPeer.Client;
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
	
			
	//Refer types 106, 107
	public void delete(Peer p, Chunk c) throws Exception {
		String type106packet = new SenderReceiver().sendDatagramAndGetUDPReplyOn(p, new Packet(106,c.getChunkName()).getPayload());
		Packet receivedPacket = new Packet(type106packet);
		if(receivedPacket.getType()==107 && receivedPacket.getData().equals(c.getChunkName()) ) {
			if(Client.debugFlag)
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
		System.out.println("Pushed file.\n"+metadata);
		
	}
	
	//Refer types 104, 105
	public void push(Peer p, Chunk c){
		String payload = new SenderReceiver().sendDatagramAndGetUDPReplyOnWithTimer(p, new Packet(104,c.getChunkName()+":"+c.getByteString()).getPayload(), 7000);
		
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
		return availablePeers.get((lastPeerIndex++)%(availablePeers.size()));
	}
	
	//Fetch 
	public void fetchFile(P2pFile p2p){
		metadata = p2p.getMetadata();
		if(Client.debugFlag)
			System.out.println("MD of file to fetch: "+metadata);
		chunkList = p2p.getChunkList(); //empty
		ArrayList<ChunkTriplePeer> ctpList = metadata.returnChunkPeer();
		for (int chunkTripleIndex = 0; chunkTripleIndex< ctpList.size(); chunkTripleIndex++) {
			ChunkTriplePeer ctp = ctpList.get(chunkTripleIndex);
			for(int peerNumber=1; peerNumber <= 3; peerNumber++){
				if( fetchChunkFromPeerNumber(ctp, peerNumber) == true ){
					System.out.println("Fetched chunk number "+chunkTripleIndex+" from "+ctp.getPeer(peerNumber));
					break;
				}
				if( peerNumber == 3 ) {
					try {
						throw new Exception("Cannot fetch chunk number "+chunkTripleIndex+". No Peer is up.");
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				}
			}
		}
	}
	
	public boolean fetchChunkFromPeerNumber(ChunkTriplePeer ctp, int peerNumber) {
		Peer isUp = ctp.getPeer(peerNumber);
		if(checkIfPeerIsUp(isUp)){
			String chunkname = ctp.getChunkName();
			String replyOf102 = new SenderReceiver().sendDatagramAndGetUDPReplyOn(isUp, new Packet(102,chunkname).getPayload());
			Packet receivedPacket = new Packet(new String(replyOf102));
			if(receivedPacket.getType()==103) {
				String[] splitted = receivedPacket.getData().split(":");
				chunkname = splitted[0];
				Chunk toDistribute = new Chunk(chunkname, splitted[1]);
				byte[] chunksRetrieved = toDistribute.returnBytes();
				Chunk newChunk = new Chunk(chunkname, chunksRetrieved);
				chunkList.add(newChunk);
				return true;
			} else {
				try {
					throw new Exception("Reply not expected"+receivedPacket);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		} else {
			return false;
		}
	}
	
	//Delete
	public void deleteFile(P2pFile p2p){
		chunkList = p2p.getChunkList(); //chunk list to delete
		metadata = p2p.getMetadata(); //Old metadata
		
		for(int chunkIndex=0; chunkIndex<chunkList.size();chunkIndex++){
			
			for(int replicaNumber = 1; replicaNumber <=3 ; replicaNumber++){
				Peer whoHasChunk = metadata.getChunkPeerNumber(chunkIndex).getPeer(replicaNumber);
				//Peer toSend = returnNextPeer();
				if(checkIfPeerIsUp(whoHasChunk)){
					//metadata.getChunkPeerNumber(chunkIndex).setPeerNumber(replicaNumber, toSend);
					try {
						delete(whoHasChunk, chunkList.get(chunkIndex));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}
		
	}
}


