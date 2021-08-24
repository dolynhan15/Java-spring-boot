package com.qooco.boost.business.impl;

import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.PataFile;
import com.qooco.boost.data.oracle.services.PataFileService;
import com.qooco.boost.exception.FileNotFoundException;
import com.qooco.boost.models.dto.PataFileDTO;
import com.qooco.boost.utils.FileUtils;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
import com.qooco.boost.utils.Validation;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.*;

@PowerMockIgnore({"javax.management.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({FileUtils.class, StringUtil.class, ServletUriUtils.class, Validation.class})
public class FileStorageServiceImplTest extends BaseUserService {

    @InjectMocks
    private FileStorageServiceImpl fileStorageService = new FileStorageServiceImpl();

    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);

    @Mock
    private PataFileService pataFileService = Mockito.mock(PataFileService.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Value(ApplicationConstant.BOOST_FILE_UPLOAD_DIR)

    private String rootPath;
    private static AuthenticatedUser authenticatedUser = initAuthenticatedUser();

    public static PataFile createPataFileForUnitTest() {
        Long id = 1L;
        PataFile pataFile = PataFile.builder()
                .pataFileId(id)
                .url("abc").build();
        return pataFile;
    }

    @Test
    public void storeFile_whenInputRight_thenReturnSuccess() throws Exception {
        String rootPath = "/root";
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.png", "image/png", "Spring Framework".getBytes());
        MockMultipartFile[] mockMultipartFiles = {multipartFile};
        Long userId = authenticatedUser.getId();
        String subPath = "" + userId;

        List<PataFileDTO> fileDTOList = Collections.singletonList(new PataFileDTO(createPataFileForUnitTest()));
        PowerMockito.mockStatic(FileUtils.class);
        Mockito.when(FileUtils.createFolder(anyString(), anyString())).thenReturn(rootPath);
        Mockito.when(FileUtils.saveFile(anyString(), anyString(), eq(multipartFile))).thenReturn(true);
        Mockito.when(FileUtils.removeInvalidCharacter(anyObject())).thenReturn("file.png");
        Mockito.when(FileUtils.createFileName(anyObject())).thenReturn("file.png");

        PowerMockito.mockStatic(StringUtil.class);
        Mockito.when(StringUtil.append(anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(rootPath);


        List<PataFile> pataFileList = Collections.singletonList(createPataFileForUnitTest());
        Mockito.when(pataFileService.save(anyListOf(PataFile.class))).thenReturn(pataFileList);

        PowerMockito.mockStatic(ServletUriUtils.class);
        Mockito.when(ServletUriUtils.getDomain()).thenReturn(rootPath);


        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);

        Assert.assertEquals(fileDTOList.size(), fileStorageService.storeFile(userId, subPath, Arrays.asList(mockMultipartFiles)).size());
    }

    @Test
    public void loadResource_whenInputRightPath_thenReturnSuccess() throws Exception {

        Resource resourceMock = Mockito.mock(UrlResource.class);
        PowerMockito.whenNew(UrlResource.class).withAnyArguments().thenReturn((UrlResource) resourceMock);
        Mockito.when(resourceMock.exists()).thenReturn(true);
        //Don't have solution now, do later
        fileStorageService.loadResource("path");
    }

    @Test
    public void deleteFile_whenInputRightFile_thenReturnSuccess() throws Exception {
        PataFile pataFile = createPataFileForUnitTest();
        Long id = pataFile.getPataFileId();

        Mockito.doReturn(pataFile).when(pataFileService).findById(id);
        Mockito.doNothing().when(pataFileService).delete(id);

        String path = StringUtil.append(rootPath, pataFile.getUrl());
        PowerMockito.spy(FileUtils.class);
        FileUtils.deleteFile(path);

        fileStorageService.deleteFileById(id);
    }

    @Test
    public void deleteFile_whenInputRightFile_thenReturnFail() throws Exception {
        PataFile pataFile = createPataFileForUnitTest();
        Long id = pataFile.getPataFileId();

        Mockito.doReturn(null).when(pataFileService).findById(id);
        thrown.expect(FileNotFoundException.class);

        fileStorageService.deleteFileById(id);
    }

    @Test
    public void deleteFileByAbsolutePath_whenInputRightPath_thenReturnTrue() {
        String domain = "http://domain.com";
        String rootPath = StringUtil.append(domain, URLConstants.DOWNLOAD_IMAGE_PATH);
        String url = StringUtil.append(FileUtils.FILE_SEPARATOR, "lala.png");
        String path = StringUtil.append(rootPath, url);

        PowerMockito.mockStatic(ServletUriUtils.class);
        Mockito.when(ServletUriUtils.getRelativePath(path)).thenReturn(url);

        PowerMockito.spy(FileUtils.class);
        PowerMockito.doNothing().when(FileUtils.class);
        FileUtils.deleteFile(url);

        PowerMockito.spy(Validation.class);
        Mockito.when(Validation.validateHttpOrHttps(path)).thenReturn(true);

        Mockito.doNothing().when(pataFileService).delete(url);
        fileStorageService.deleteFileByAbsolutePath(StringUtil.append(rootPath, url));
    }

    @Test
    public void deleteFileByRelativePath_whenInputRightPath_thenReturnTrue() {
        Mockito.doNothing().when(pataFileService).delete("url");
        PowerMockito.spy(FileUtils.class);
        FileUtils.deleteFile("url");

        fileStorageService.deleteFileByRelativePath("absolutePath");
    }


}
