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
		testFileName = "testFileName";
		metadata = new FileMetaData(testFileName,1);
	}

	@Test
	public void testStoreFileMetaData() {
		String directory = "";
		FileMetaData.StoreFileMetaData(directory, metadata);
		File storedData = new File(testFileName); 
		assertTrue(storedData.exists());
	}

	@Test
	public void testGetFileMetadata() {
		String directory = "";
		FileMetaData.StoreFileMetaData(directory, metadata);
		File storedData = new File(testFileName); 
		assertTrue(storedData.exists());
		FileMetaData retrievedFileMetaData;
		retrievedFileMetaData = FileMetaData.getFileMetadata(testFileName);
		assertTrue(retrievedFileMetaData.toString().contains(testFileName));
		}

}
