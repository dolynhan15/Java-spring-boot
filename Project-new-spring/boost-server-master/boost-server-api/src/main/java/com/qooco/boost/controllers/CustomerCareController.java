package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessSupportChannelService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.message.ConversationDTO;
import com.qooco.boost.models.request.PageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.qooco.boost.constants.StatusConstants.*;
import static com.qooco.boost.constants.URLConstants.*;

@Api(tags = "Customer Care")
@RestController
@RequestMapping(URLConstants.CUSTOMER_CARE_PATH)
public class CustomerCareController extends BaseController {

    @Autowired
    private BusinessSupportChannelService businessSupportChannelService;

    @ApiOperation(value = "Get support channel base on user token", httpMethod = "GET", response = SupportConversationDTOResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + SUCCESS_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = URLConstants.SUPPORT_CHANNEL)
    public Object getSupportChannel(Authentication authentication) {
        var resp = businessSupportChannelService.getByUser(authentication);
        return success(resp);
    }

    @ApiOperation(value = "Create support channel base on user token", httpMethod = "POST", response = SupportConversationDTOResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + SUCCESS_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = URLConstants.SUPPORT_CHANNEL)
    public Object createSupportChannel(Authentication authentication) {
        var resp = businessSupportChannelService.createByUser(authentication);
        return success(resp);
    }

    @ApiOperation(value = "Get list of conversations", httpMethod = "GET", response = SupportConversationsResp.class, notes = NOTE)
    @GetMapping(CONVERSATION)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    public Object getConversations(@RequestParam(required = false) Integer fromApp,
                                   @RequestParam(required = false) Integer status,
                                   @Valid PageRequest request,
                                   @RequestParam(defaultValue = "") String[] sort,
                                   Authentication auth) {
        var result = businessSupportChannelService.getConversations(auth, fromApp, status, request, sort);
        return success(result);
    }

    @ApiOperation(value = "Delete the support conversation", httpMethod = "DELETE", response = BaseResp.class, notes = NOTE)
    @DeleteMapping(CONVERSATION + ID + RESTORE)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    public Object deleteSupportConversation(@PathVariable(value = "id") String conversationId, Authentication auth) {
        BaseResp result = businessSupportChannelService.deleteSupportConversation(auth, conversationId);
        return success(result);
    }

    @ApiOperation(value = "Archive the support conversation", httpMethod = "PATCH", response = BaseResp.class, notes = NOTE)
    @PatchMapping(CONVERSATION + ID + ARCHIVE)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    public Object archiveSupportConversation(@PathVariable(value = "id") String conversationId, Authentication auth) {
        BaseResp result = businessSupportChannelService.archiveSupportConversation(auth, conversationId);
        return success(result);
    }

    @ApiOperation(value = "Archive the support conversation", httpMethod = "PATCH", response = BaseResp.class, notes = NOTE)
    @PatchMapping(CONVERSATION + ID + RESTORE)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    public Object restoreSupportConversation(@PathVariable(value = "id") String conversationId, Authentication auth) {
        BaseResp result = businessSupportChannelService.restoreSupportConversation(auth, conversationId);
        return success(result);
    }

    private static final String NOTE = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + SUCCESS_MESSAGE
            + "<br>" + BAD_REQUEST + " : " + BAD_REQUEST_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + NO_PERMISSION_TO_ACCESS_MESSAGE;

    private static class SupportConversationDTOResp extends BaseResp<ConversationDTO> {
    }

    private static class SupportConversationsResp extends BaseResp<PagedResult<ConversationDTO>> {
    }
}
