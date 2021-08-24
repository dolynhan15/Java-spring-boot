package com.qooco.boost.business.impl;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/10/2018 - 1:52 PM
 */

import com.qooco.boost.business.FileStorageService;
import com.qooco.boost.business.MediaService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.PataFile;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.dto.PataFileDTO;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.utils.FileUtils;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@PowerMockIgnore({"javax.management.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ServletUriUtils.class})
public class MediaServiceImplTest extends BaseUserService {

    @InjectMocks
    private MediaService mediaService = new MediaServiceImpl();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private FileStorageService fileStorageService = Mockito.mock(FileStorageServiceImpl.class);

    @Mock
    private BoostActorManager boostActorManager = Mockito.mock(BoostActorManager.class);

    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);
    private static AuthenticatedUser authenticatedUser = initAuthenticatedUser();

    @Value(ApplicationConstant.BOOST_FILE_UPLOAD_DIR)
    private String rootPath;

    public static PataFile createPataFile() {
        Long id = 1L;
        PataFile pataFile = PataFile.builder()
                .pataFileId(id)
                .url("abc").build();
        return pataFile;
    }

    @Test
    public void store_whenInputRightFile_thenReturnSuccess() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.png",
                "image/png", "Spring Framework".getBytes());
        Long id = authenticatedUser.getId();
        List<PataFile> fileDTOList = Collections.singletonList(createPataFile());

        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(fileStorageService.storeFile(id, StringUtil.append(id.toString(), FileUtils.FILE_SEPARATOR, "avatar"), Collections.singletonList(multipartFile))).thenReturn(fileDTOList);

        Assert.assertEquals(fileDTOList.get(0).getPataFileId(), ((PataFileDTO) mediaService.store(multipartFile, "AVATAR", authentication).getData()).getId());

    }


    @Test
    public void store_whenInputWrongFile_thenReturnInvalidParamException() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.png",
                "image/png", "Spring Framework".getBytes());
        Long id = authenticatedUser.getId();

        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(fileStorageService.storeFile(id, StringUtil.append(id.toString(), FileUtils.FILE_SEPARATOR, "avatar"), Collections.singletonList(multipartFile))).thenReturn(null);
        thrown.expect(InvalidParamException.class);

        mediaService.store(multipartFile, "AVATAR", authentication);
    }

    @Test
    public void store_whenInputWrongType_thenReturnInvalidParamException() {
        thrown.expect(InvalidParamException.class);
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.png",
                "image/png", "Spring Framework".getBytes());
        Long id = authenticatedUser.getId();

        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(fileStorageService.storeFile(id, StringUtil.append(id.toString(), FileUtils.FILE_SEPARATOR, "avatar"), Collections.singletonList(multipartFile))).thenReturn(null);

        mediaService.store(multipartFile, "AVATAR01", authentication);
    }

    @Test
    public void stores_whenInputRightFile_thenReturnSuccess() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.png",
                "image/png", "Spring Framework".getBytes());
        MockMultipartFile[] mockMultipartFiles = {multipartFile};

        Long id = authenticatedUser.getId();
        List<PataFile> fileDTOList = Collections.singletonList(createPataFile());
        List<PataFileDTO> result = fileDTOList.stream().map(PataFileDTO::new).collect(Collectors.toList());

        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(fileStorageService.storeFile(id, StringUtil.append(id.toString(), FileUtils.FILE_SEPARATOR, "avatar"), Arrays.asList(mockMultipartFiles))).thenReturn(fileDTOList);

        Assert.assertEquals(result, mediaService.stores(mockMultipartFiles, "AVATAR", authentication).getData());

    }

    @Test
    public void store_whenInputWrongContentType_thenReturnSuccess() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.abc",
                "application/png", "Spring Framework".getBytes());

        try {
            mediaService.store(multipartFile, "avatar", authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.IMAGE_IS_WRONG_EXTENSION, ex.getStatus());
        }
    }

    @Test
    public void stores_whenInputWrongContentType_thenReturnSuccess() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.abc",
                "application/png", "Spring Framework".getBytes());
        MockMultipartFile[] mockMultipartFiles = {multipartFile};

        try {
            mediaService.stores(mockMultipartFiles, "avatar", authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.IMAGE_IS_WRONG_EXTENSION, ex.getStatus());
        }
    }

    @Test
    public void delete_whenInputEmptyPath_thenReturnInvalidParamException() {
        thrown.expect(InvalidParamException.class);
        mediaService.delete("");
    }

    @Test
    public void delete_whenInputWrongPath_thenReturnInvalidParamException() {
        thrown.expect(InvalidParamException.class);
        mediaService.delete("ttp://google.com/image.png");
    }

    @Test
    public void delete_whenInputRightPath_thenReturnSuccess() {
        String path = "http://google.com/image.png";
        Mockito.doNothing().when(fileStorageService).deleteFileByAbsolutePath(path);
        PowerMockito.mockStatic(ServletUriUtils.class);
        Mockito.when(ServletUriUtils.getRelativePath(Mockito.any())).thenReturn("/image.png");
        Mockito.doNothing().when(boostActorManager).deleteFile(Mockito.anyList());

        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), mediaService.delete(path).getCode());
    }

}
