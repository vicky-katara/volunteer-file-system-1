package lib;

import java.util.ArrayList;

class ChunkTriplePeer{
	String chunkName;
	Peer one;
	Peer two;
	Peer three;
	
	ChunkTriplePeer(String chunkName, Peer one, Peer two, Peer three){
		this.chunkName = chunkName;
		this.one = one;
		this.two = two;
		this.three = three;
	}
}

public class FileMetadata {
	String fileName;
	ArrayList<ChunkTriplePeer> chunkPeerList;
	
	public FileMetadata(String fileName, int numChunks){
		this.fileName = fileName;
		this.chunkPeerList = new ArrayList<>();
	}
}
