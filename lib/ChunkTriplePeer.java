package lib;

import java.io.Serializable;

public class ChunkTriplePeer implements Serializable{
	private static final long serialVersionUID = -2610681546566455003L;
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

	public void setPrimaryPeer(Peer x){
		this.one = x;
	}
	public void setSecondaryPeer(Peer x){
		this.two = x;
	}
	public void setTertiaryPeer(Peer x){
		this.three = x;
	}
	
	public Peer getPrimaryPeer(int number){
		if(number==1){
			return this.one;
		} else if(number == 2){
			return this.two;
		} else if(number ==3){
			return this.three;
		} 
		return null;
	}
	
	public String getChunk(){
		return chunkName;
	}
	
	public void setPeerNumber(int number, Peer x){
		if(number==1){
			this.one = x;
		} else if(number == 2){
			this.two = x;
		} else if(number ==3){
			this.three = x;
		} else {
			System.err.println("Wrong number for peer index");
		}
	}

	public String toString(){
		return "["+chunkName+"-"+one+"-"+two+"-"+three+"]";
	}
}