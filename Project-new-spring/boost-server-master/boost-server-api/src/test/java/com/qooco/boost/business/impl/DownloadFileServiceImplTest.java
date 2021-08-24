package com.qooco.boost.business.impl;

import com.qooco.boost.business.DownloadFileService;
import com.qooco.boost.business.FileStorageService;
import com.qooco.boost.exception.InvalidParamException;
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
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;

@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
public class DownloadFileServiceImplTest {
    @InjectMocks
    private DownloadFileService downloadFileService = new DownloadFileServiceImpl();

    @Mock
    private FileStorageService fileStorageService = Mockito.mock(FileStorageService.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void load_whenInputRightPath_thenReturnSuccess() throws MalformedURLException, Exception {

        UrlResource resourceMock = Mockito.mock(UrlResource.class);
        PowerMockito.whenNew(UrlResource.class).withAnyArguments().thenReturn(resourceMock);
        Mockito.when(fileStorageService.loadResource("path")).thenReturn(resourceMock);

        Assert.assertEquals(resourceMock, downloadFileService.load("path"));
    }

    @Test
    public void load_whenInputWrongPath_thenReturnInvalidParamException() {
        thrown.expect(InvalidParamException.class);
        downloadFileService.load(null);
    }
}