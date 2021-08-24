package com.qooco.boost.utils;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/10/2018 - 3:44 PM
 */

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.mock;

@PowerMockIgnore({"javax.management.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({FileUtils.class,RandomStringUtils.class})
public class FileUtilsTest {
    @InjectMocks
    private static FileUtils fileUtils = new FileUtils();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws Exception{

    }

    @Test
    public void createFileName_whenInputRighName_ReturnSuccess() throws IOException {
        PowerMockito.mockStatic(RandomStringUtils.class);
        Mockito.when(RandomStringUtils.randomAlphanumeric(8)).thenReturn("abcdefdgh");
        String fileName = FileUtils.createFileName("hello.com");

        Assert.assertTrue(fileName.contains("abcdefdgh") && fileName.contains(".com"));
    }

    @Test
    public void createRootFolder_whenInputRightPath_ReturnSuccess() throws Exception {
        String pathRoot = "boost";
        File directoryMock = mock(File.class);
        PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(directoryMock);
        Mockito.when(directoryMock.mkdirs()).thenReturn(true);
        Mockito.when(directoryMock.setExecutable(true, true)).thenReturn(true);
        Mockito.when(directoryMock.setReadable(true, true)).thenReturn(true);
        Mockito.when(directoryMock.setWritable(true, true)).thenReturn(true);
        Mockito.when(directoryMock.getPath()).thenReturn(pathRoot);

        Assert.assertEquals(pathRoot, fileUtils.createRootFolder(pathRoot));
    }

    @Test
    public void createFolder_whenInputRightPath_ReturnSuccess() throws Exception {
        String pathRoot = "boost";
        String pathSub = "file";
        File directoryMock = mock(File.class);
        PowerMockito.whenNew(File.class).withArguments(pathRoot, pathSub).thenReturn(directoryMock);
        Mockito.when(directoryMock.mkdirs()).thenReturn(true);
        Mockito.when(directoryMock.setExecutable(true, true)).thenReturn(true);
        Mockito.when(directoryMock.setReadable(true, true)).thenReturn(true);
        Mockito.when(directoryMock.setWritable(true, true)).thenReturn(true);
        Mockito.when(directoryMock.getPath()).thenReturn(pathRoot + FileUtils.FILE_SEPARATOR + pathSub);

        Assert.assertEquals(StringUtil.append(pathRoot, FileUtils.FILE_SEPARATOR, pathSub), fileUtils.createFolder(pathRoot, pathSub));
    }

    @Test
    public void checkExistedFolder_whenFolderIsNotExist_thenReturnTrue() throws Exception {
        String pathRoot = "boost";
        File directoryMock = mock(File.class);
        PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(directoryMock);
        Mockito.when(directoryMock.exists()).thenReturn(true);
        Assert.assertTrue(fileUtils.checkExistedFolder(pathRoot));
    }

    @Test
    public void removeInvalidCharacter_whenStringHaveInvalidate_thenReturnValidate() {
        Assert.assertEquals("pata", FileUtils.removeInvalidCharacter("\\\\/:*?\"<>|"));
        Assert.assertEquals("aaaaa", FileUtils.removeInvalidCharacter("aaaaa\\\\/:*?\"<>|"));
    }

    @Test
    public void saveFile_whenRightData_thenReturnOK() throws Exception{
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.png","image/png", "Spring Framework".getBytes());
        String pathRoot = "boost";
        String pathSub = "file";
        PowerMockito.mockStatic(Files.class);
        Path _path = Paths.get(pathRoot + pathSub);
        PowerMockito.when(Files.write(_path, multipartFile.getBytes())).thenReturn(_path);
        Assert.assertTrue(fileUtils.saveFile(pathRoot, pathSub, multipartFile));
    }

    @Test
    public void saveFile_whenWrongData_thenReturnFail() throws Exception, IOException{
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.png","image/png", "Spring Framework".getBytes());
        String pathRoot = "boost";
        String pathSub = "file";
        PowerMockito.mockStatic(Files.class);
        Path _path = Paths.get(pathRoot + pathSub);
        PowerMockito.when(Files.write(_path, multipartFile.getBytes())).thenThrow(IOException.class);

        fileUtils.saveFile(pathRoot, pathSub, multipartFile);
    }
}
