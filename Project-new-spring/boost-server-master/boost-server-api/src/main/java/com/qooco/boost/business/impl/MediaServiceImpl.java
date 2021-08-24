package com.qooco.boost.business.impl;

import com.qooco.boost.business.FileStorageService;
import com.qooco.boost.business.MediaService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.enumeration.UploadType;
import com.qooco.boost.data.oracle.entities.PataFile;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.PataFileDTO;
import com.qooco.boost.models.request.message.MediaMessageReq;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.utils.FileUtils;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
import com.qooco.boost.utils.Validation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static java.util.Optional.ofNullable;

@Service
public class MediaServiceImpl implements MediaService {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private BoostActorManager boostActorManager;

    @Value(ApplicationConstant.BOOST_FILE_BLACKLIST_EXTENSION)
    private String[] blacklistExtensions;

    @Override
    public BaseResp store(MultipartFile file, String uploadType, Authentication authentication) {
        List<PataFile> pataFileDTOList = storeFileToServer(Collections.singletonList(file), uploadType, authentication);
        if (CollectionUtils.isNotEmpty(pataFileDTOList)) {
            return new BaseResp<>(convertToPateFileDTO(pataFileDTOList).get(0));
        }
        throw new InvalidParamException(ResponseStatus.BAD_REQUEST);
    }

    @Override
    public BaseResp stores(MultipartFile[] files, String uploadType, Authentication authentication) {
        List<PataFile> pataFileDTOList = storeFileToServer(Arrays.asList(files), uploadType, authentication);
        return new BaseResp<>(convertToPateFileDTO(pataFileDTOList));
    }

    @Override
    public BaseResp delete(String absolutePath) {
        if (StringUtils.isBlank(absolutePath)) {
            throw new InvalidParamException(ResponseStatus.FILE_PATH_IS_NULL);
        }

        if (!Validation.validateHttpOrHttps(absolutePath)) {
            throw new InvalidParamException(ResponseStatus.HTTP_OR_HTTPS_WRONG_FORMAT);
        }

        String relativePath = ServletUriUtils.getRelativePath(absolutePath);
        boostActorManager.deleteFile(Collections.singletonList(relativePath));
        return new BaseResp<>(ResponseStatus.SUCCESS);
    }

    private List<PataFile> storeFileToServer(List<MultipartFile> files, String folderName, Authentication authentication) {
        if (UploadType.EXCEL_FILE.toString().equals(folderName)) {
            if (!validateExcelExtension(files)) {
                throw new InvalidParamException(ResponseStatus.EXCEL_IS_WRONG_EXTENSION);
            }
        } else if (!validateImageContentType(files)) {
            throw new InvalidParamException(ResponseStatus.IMAGE_IS_WRONG_EXTENSION);
        }

        Long userProfileID = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        String subPath = getSubPath(authentication, folderName);
        return fileStorageService.storeFile(userProfileID, subPath, files);
    }

    @Override
    public PataFile storeMediaFileToServer(MediaMessageReq mediaMessageReq, String folderName, Authentication authentication) {

        ofNullable(mediaMessageReq.getFile()).filter(it -> it.getSize() > 0).orElseThrow(() -> new InvalidParamException(ResponseStatus.FILE_SIZE_IS_ZERO));
        if(isBlacklistExtension(mediaMessageReq.getFile())){
            throw new InvalidParamException(ResponseStatus.EXTENSION_IS_DENIED);
        }

        ofNullable(mediaMessageReq.getThumbnail()).ifPresent(thumbnail -> {
            Optional.of(thumbnail).filter(it -> it.getSize() > 0).orElseThrow(() -> new InvalidParamException(ResponseStatus.FILE_SIZE_IS_ZERO));
            if(!validateImageContentType(thumbnail)){
                throw new InvalidParamException(ResponseStatus.IMAGE_IS_WRONG_EXTENSION);
            }

        });

        Long userProfileId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        String subPath = getSubPath(authentication, folderName);
        return fileStorageService.storeFile(userProfileId, subPath, mediaMessageReq);
    }

    private List<PataFileDTO> convertToPateFileDTO(List<PataFile> pataFiles) {
        List<PataFileDTO> pataFileDTOS = new ArrayList<>();
        pataFiles.forEach(pataFile -> {
            PataFileDTO pataFileDTO = new PataFileDTO(pataFile.getPataFileId(), pataFile.getFileName(), pataFile.getUrl());
            pataFileDTO.setDownloadUrl(StringUtil.append(ServletUriUtils.getDownloadMethodPath(), pataFile.getUrl()));
            pataFileDTOS.add(pataFileDTO);
        });
        return pataFileDTOS;
    }

    private String getSubPath(Authentication authentication, String type) {
        String subPath = ((AuthenticatedUser) authentication.getPrincipal()).getId().toString();

        try {
            return StringUtil.append(subPath, FileUtils.FILE_SEPARATOR, UploadType.valueOf(type.toUpperCase()).toString());
        } catch (IllegalArgumentException ex) {
            return subPath;
        }
    }

    private boolean isBlacklistExtension(MultipartFile file){
        return Objects.nonNull(file) && ArrayUtils.contains(blacklistExtensions, FilenameUtils.getExtension(file.getOriginalFilename()));
    }
    private boolean validateImageContentType(List<MultipartFile> files) {
        return ofNullable(files).filter(CollectionUtils::isNotEmpty).get().stream().allMatch(this::validateImageContentType);
    }

    private boolean validateImageContentType(MultipartFile file) {
        return Objects.nonNull(file) && Validation.validateImageContentType(file.getContentType());
    }

    private boolean validateExcelExtension(List<MultipartFile> files) {
        return ofNullable(files).filter(CollectionUtils::isNotEmpty).get().stream().allMatch(this::validateExcelExtension);
    }

    private boolean validateExcelExtension(MultipartFile file) {
        return Objects.nonNull(file) && Validation.validateExcelExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
    }
}
