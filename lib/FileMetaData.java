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
	
	public ChunkTriplePeer() {
		
	}
	
	public ChunkTriplePeer(String chunkName) {
		this.chunkName = chunkName;
	}
	
	void setChunkName(String chunkName) {
		this.chunkName = chunkName;
	}
	
	void setPrimaryPeer(Peer x){
		this.one = x;
	}
	void setSecondaryPeer(Peer x){
		this.two = x;
	}
	void setTertiaryPeer(Peer x){
		this.three = x;
	}
	
	public String toString(){
		return "["+chunkName+"-"+one+"-"+two+"-"+three+"]";
	}
}

public class FileMetaData {
	String fileName;
	ArrayList<ChunkTriplePeer> chunkPeerList;
	int numChunks;
	
	public FileMetaData(String fileName, int numChunks){
		this.fileName = fileName;
		this.numChunks = numChunks;
		this.chunkPeerList = new ArrayList<>(numChunks);
		for(int chunkNumber=0; chunkNumber<numChunks; chunkNumber++)
			chunkPeerList.add(new ChunkTriplePeer());
	}
	
	public void setChunkNameOf(int chunkNumber, String chunkName) {
		try {
			if(chunkNumber>numChunks)
				throw new Exception("Chunk number "+chunkNumber+" does not exist. NumChunks = "+numChunks);
			chunkPeerList.get(chunkNumber).setChunkName(chunkName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setChunkPeerOf(int chunkNumber, Peer primary, Peer secondary, Peer tertiary) {
		try {
			if(chunkNumber>numChunks)
				throw new Exception("Chunk number "+chunkNumber+" does not exist. NumChunks = "+numChunks);
			ChunkTriplePeer ctp = chunkPeerList.get(chunkNumber);
			ctp.setPrimaryPeer(primary);
			ctp.setSecondaryPeer(secondary);
			ctp.setTertiaryPeer(tertiary);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String toString(){
		return ""+fileName+">Chunks>"+chunkPeerList;
	}
	
}
