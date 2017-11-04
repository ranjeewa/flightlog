package com.ranjeewa.flightlog.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;

import static org.junit.Assert.*;

public class FileServiceTest {

    private static ServiceProperties testProps = new ServiceProperties();

    @Before
    public void setup() {
        testProps.setFilePath(System.getProperty("java.io.tmpdir") + "flightlog");
        new File(testProps.getFilePath()).mkdir();
    }

    @After
    public void cleanup() {
        File[] logFiles = new File(testProps.getFilePath()).listFiles();
        if (logFiles != null && logFiles.length > 0) {
            for (File logFile : logFiles) {
                logFile.delete();
            }
        }
        new File(testProps.getFilePath()).delete();
    }


    @Test
    public void saveFileWritesToFileSystem() throws Exception {

        FileService service = new FileService(testProps);

        File targetDir = new File(testProps.getFilePath());
        assertTrue(targetDir.exists());
        assertEquals(0, targetDir.listFiles().length);

        String testString = "This is a test string";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(testString.getBytes());
        service.saveFile(byteArrayInputStream);

        assertEquals(1, targetDir.listFiles().length);
    }

    @Test
    public void saveFileReturnsFileName() throws Exception {

        FileService service = new FileService(testProps);

        String testString = "This is another test string";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(testString.getBytes());
        String fileName = service.saveFile(byteArrayInputStream);

        assertNotNull(fileName);

        File targetDir = new File(testProps.getFilePath());
        assertTrue(new File(targetDir + File.separator + fileName).exists());
    }

    @Test
    public void canGetExistingFile() throws Exception {

        FileService service = new FileService(testProps);

        File newFile = new File(testProps.getFilePath() + File.separator + "newFile");
        if (!newFile.exists()) {
            newFile.createNewFile();
        }

        File fileFromService = service.findFile("newFile");
        assertNotNull(fileFromService);
        assertEquals(0L, fileFromService.length());
        assertEquals("newFile", fileFromService.getName());
    }

    @Test
    public void nonExistingFileReturnsNull() throws Exception {

        FileService service = new FileService(testProps);

        File fileFromService = service.findFile("noSuchFile");
        assertNull(fileFromService);
    }

}