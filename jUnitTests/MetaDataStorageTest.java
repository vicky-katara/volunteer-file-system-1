package jUnitTests;

import static org.junit.Assert.*;

import java.io.File;

import lib.FileMetaData;

import org.junit.Before;
import org.junit.Test;

public class MetaDataStorageTest {
	FileMetaData metadata;
	String testFileName;
	
	@Before
	public void setUp() throws Exception {
		testFileName = System.getProperty("user.home")+File.separatorChar+"testFileName.class";
		metadata = new FileMetaData(testFileName,1);
	}

	@Test
	public void testStoreFileMetaData() {
		String directory = "";
		FileMetaData.StoreFileMetaDataDirectory(directory, metadata);
		File storedData = new File(testFileName+FileMetaData.METADATA_FILE_ENDING); 
		assertTrue(storedData.exists());
	}

	@Test
	public void testGetFileMetadata() {
		String directory = "";
		FileMetaData.StoreFileMetaDataDirectory(directory, metadata);
		File storedData = new File(testFileName+FileMetaData.METADATA_FILE_ENDING); 
		assertTrue(storedData.exists());
		FileMetaData retrievedFileMetaData;
		retrievedFileMetaData = FileMetaData.getFileMetadata(testFileName);
		assertTrue(retrievedFileMetaData.toString().contains(testFileName));
		}

}
