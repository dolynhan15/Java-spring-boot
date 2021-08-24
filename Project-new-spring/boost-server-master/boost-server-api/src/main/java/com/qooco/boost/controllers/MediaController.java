package com.qooco.boost.controllers;

import com.qooco.boost.business.MediaService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.PataFileDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "Media services")
@CrossOrigin
@RestController
@RequestMapping(URLConstants.MEDIA_PATH)
public class MediaController extends BaseController {
    @Autowired
    private MediaService mediaService;

    @ApiOperation(value = "Do upload image file: png, jpg;  max size 5MB for one image; ", httpMethod = "POST",
            response = UploadPhotoResp.class,
            notes = notes
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.UPLOAD_FILE_PATH)
    public Object singleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("type") String type, Authentication authentication) {

        BaseResp result = mediaService.store(file, type, authentication);
        return success(result);
    }

    @ApiOperation(value = "Do upload multiples images file: png, jpg ; max size 5MB for one image; total size 35MB", httpMethod = "POST",
            response = UploadPhotosResp.class,
            notes = notes
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.UPLOAD_MULTIPLE_FILE_PATH)
    public Object uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("type") String type, Authentication authentication) {
        BaseResp result = mediaService.stores(files, type, authentication);
        return success(result);
    }

    @ApiOperation(value = "Delete file base on full link", httpMethod = "DELETE",
            response = BaseResp.class,
            notes = notesDelete
    )

    @PreAuthorize("isAuthenticated() and @boostSecurity.isPataFileOwner(authentication, #path)")
    @DeleteMapping(URLConstants.DELETE_FILE_PATH)
    public Object deleteFile(@RequestParam(value = "path") String path) {
        BaseResp result = mediaService.delete(path);
        return success(result);
    }

    /**
     * ==================== Class to show response ===============
     **/
    final String notes = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.IMAGE_IS_WRONG_EXTENSION + " : " + StatusConstants.IMAGE_IS_WRONG_EXTENSION_MESSAGE
            + "<br> type: AVATAR, EXPERIENCE_JOB, COMPANY_LOGO, VACANCY_LOGO, GIFT_IMAGE, PERSONAL_PHOTO";

    final String notesDelete = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.FILE_PATH_IS_NULL + " : " + StatusConstants.FILE_PATH_IS_NULL_MESSAGE
            + "<br>" + StatusConstants.HTTP_OR_HTTPS_WRONG_FORMAT + " : " + StatusConstants.HTTP_OR_HTTPS_WRONG_FORMAT_MESSAGE;

    class UploadPhotoResp extends BaseResp<PataFileDTO> {

    }

    class UploadPhotosResp extends BaseResp<List<PataFileDTO>> {

    }

}
