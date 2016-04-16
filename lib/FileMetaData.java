package lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

class ChunkTriplePeer implements Serializable{
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

public class FileMetaData implements Serializable{
	private static final long serialVersionUID = 5470463765499328330L; //for serializing.
	String fileName;
	ArrayList<ChunkTriplePeer> chunkPeerList;
	int numChunks;

	public static void StoreFileMetaData(String directory, FileMetaData metadata){
		String path;
		if (directory.equals("")) path=metadata.fileName;
		else path = directory+File.separatorChar+metadata.fileName;
		
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(metadata);
			out.close();
			fileOut.close();
			System.out.println("metadata stored in "+path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<ChunkTriplePeer> returnChunkPeer(){
		return chunkPeerList;
	}
	

	public static FileMetaData getFileMetadata(String path){
		FileMetaData metadata = null;
		try
		{
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			metadata = (FileMetaData) in.readObject();
			in.close();
			fileIn.close();
			System.out.println("metadata retrieved from "+path);
		}catch(IOException i)
		{
			i.printStackTrace();
			return null;
		}catch(ClassNotFoundException c)
		{
			System.out.println("FileMetadata class not found");
			c.printStackTrace();
			return null;
		}
		return metadata;
	}


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
