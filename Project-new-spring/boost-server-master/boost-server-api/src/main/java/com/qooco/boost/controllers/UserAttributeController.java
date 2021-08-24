package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.attribute.AttributeDTO;
import com.qooco.boost.models.request.PageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.qooco.boost.constants.AttributeEventType.EVT_OPEN_ALL_TABS_IN_MAIN_MENU;

@Api(tags = "User Attribute", value = URLConstants.ATTRIBUTE_PATH, description = "The attribute controller to management the all point and level of user")
@RestController
@RequestMapping(URLConstants.ATTRIBUTE_PATH)
public class UserAttributeController extends BaseController {

    @Autowired
    private BusinessProfileAttributeEventService businessProfileAttributeEventService;

    @ApiOperation(value = "hasLevel is FALSE: The attribute list for user profile. The information include: attribute name, attribute description, current level, current score, the score required to reach next level. hasLevel is TRUE: The attribute active (level > 0) of an user profile ",
            httpMethod = "GET", response = AttributeResp.class, notes = NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public Object getAttributes(Authentication authentication,
                                @RequestParam(value = "userProfileId", required = false) Long userProfileId,
                                @RequestParam(value = "hasLevel", required = false) boolean hasLevel,
                                @Valid PageRequest pageRequest) {
        BaseResp result = businessProfileAttributeEventService.getProfileAttributeEvent(authentication, userProfileId, hasLevel, pageRequest);
        return success(result);
    }

    @ApiOperation(value = "Get new level up at homepage screen when user finish an action like: Share code, Pass new qualification, Redeem Code ",
            httpMethod = "GET", response = AttributeResp.class, notes = NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.ATTRIBUTE_LEVEL_UP)
    public Object getAttributeLevelUp(Authentication authentication) {
        BaseResp result = businessProfileAttributeEventService.getAttributeLevelUp(authentication);
        return success(result);
    }

    @ApiOperation(value = "Tracking opened all menu, client just call api", httpMethod = "PATCH", response = BaseResp.class, notes = NOTE)
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.ATTRIBUTE_OPENED_ALL_MENU)
    public Object trackOpenedAllMenu(Authentication authentication) {
        businessProfileAttributeEventService.onAttributeEvent(EVT_OPEN_ALL_TABS_IN_MAIN_MENU, authentication);
        return success(new BaseResp<>(ResponseStatus.SUCCESS));
    }

    private static final String NOTE = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE;

    private class AttributeResp extends BaseResp<PagedResult<AttributeDTO>> {
    }
}
