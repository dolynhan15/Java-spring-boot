package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessLanguageService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.LanguageDTO;
import com.qooco.boost.models.request.PageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 7/2/2018 - 5:22 PM
 */
@Api(tags = "Languages", value = URLConstants.LANGUAGE_PATH)
@RestController
@RequestMapping(URLConstants.LANGUAGE_PATH)
public class LanguageController extends BaseController {

    @Autowired
    private BusinessLanguageService businessLanguageService;

    @ApiOperation(value = "Get languages", httpMethod = "GET", response = LanguageResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = URLConstants.GET_METHOD, method = RequestMethod.GET)
    public Object getLanguages(@Valid PageRequest request, Authentication authentication) {
        BaseResp result = businessLanguageService.getLanguages(request.getPage(), request.getSize(), authentication);
        return success(result);
    }

    class LanguageResp extends BaseResp<List<LanguageDTO>> {}
}
