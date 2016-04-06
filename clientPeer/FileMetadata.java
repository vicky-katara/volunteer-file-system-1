package clientPeer;

import java.io.File;

public class FileMetadata {
	File CurrentFile;

	public FileMetadata(File currentFile) {
		super();
		CurrentFile = currentFile;
	}
	public FileMetadata() {
		super();
		CurrentFile = new File(System.getProperty("user.dir"));
	}
	public File getCurrentFile() {
		return CurrentFile;
	}
	public void setCurrentFile(File currentFile) {
		CurrentFile = currentFile;
	}
	
	
	
}
