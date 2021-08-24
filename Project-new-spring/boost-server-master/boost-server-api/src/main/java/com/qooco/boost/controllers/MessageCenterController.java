package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessConversationService;
import com.qooco.boost.business.BusinessSupportChannelService;
import com.qooco.boost.business.MessageCenterService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.data.enumeration.MessageCenterType;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.message.MessageCenterFullDTO;
import com.qooco.boost.models.request.ConversationReq;
import com.qooco.boost.models.request.TimestampReq;
import com.qooco.boost.models.response.HistoryMessageCenterFullResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Message Center")
@RestController
@RequestMapping(URLConstants.MESSAGE_CENTER_PATH)
public class MessageCenterController extends BaseController {

    @Autowired
    private MessageCenterService messageCenterService;
    @Autowired
    private BusinessConversationService businessConversationService;
    @Autowired
    private BusinessSupportChannelService businessSupportChannelService;

    //========================================== CAREER =====================================
    @ApiOperation(value = "Get message centers from career app with timestamp", httpMethod = "GET", response = MessageCenterFullInfoResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND + " : " + StatusConstants.NOT_FOUND_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.INVALID_PAGINATION + " : " + StatusConstants.INVALID_PAGINATION_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.CAREER_PATH)
    public Object getMessageFromCareerWithTimestamp(Authentication authentication,
                                                    @Valid TimestampReq req,
                                                    @RequestParam(name = "isGroup", required = false, defaultValue = "false") Boolean isGroup
    ) {
        BaseResp result = messageCenterService.findByMessageCenterTypeAndUserProfileFromCareerWithTimestamp(authentication, isGroup, req.getTimestamp(), req.getSize());
        return success(result);
    }

    @ApiOperation(value = "Get message from career app", httpMethod = "GET", response = MessageCenterInfoSingleResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND + " : " + StatusConstants.NOT_FOUND_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.ID_INVALID + " : " + StatusConstants.ID_INVALID_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.CAREER_PATH + URLConstants.ID)
    public Object getMessageFromCareerById(@PathVariable String id,
                                           @RequestParam(name = "isGroup", required = false, defaultValue = "false") Boolean isGroup,
                                           Authentication authentication) {
        BaseResp result = messageCenterService.findByMessageCenterTypeAndUserProfileFromCareerById(authentication, id, isGroup);
        return success(result);
    }

    //========================================== RECRUITER =====================================
    @ApiOperation(value = "Get message centers from hotel app with timestamp", httpMethod = "GET", response = MessageCenterFullInfoResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND + " : " + StatusConstants.NOT_FOUND_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.INVALID_PAGINATION + " : " + StatusConstants.INVALID_PAGINATION_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.RECRUITER_PATH)
    public Object getMessageFromHotelWithTimestamp(Authentication authentication, @Valid TimestampReq req) {
        BaseResp result = messageCenterService.findByUserProfileFromHotelWithTimestamp(authentication, req.getTimestamp(), req.getSize());
        return success(result);
    }

    @ApiOperation(value = "Get message from hotel app by message center Id", httpMethod = "GET", response = MessageCenterInfoSingleResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND + " : " + StatusConstants.NOT_FOUND_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.ID_INVALID + " : " + StatusConstants.ID_INVALID_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.RECRUITER_PATH + URLConstants.ID)
    public Object getMessageFromHotelById(@PathVariable String id, Authentication authentication) {
        BaseResp result = messageCenterService.findByUserProfileFromHotelById(authentication, id);
        return success(result);
    }


    //========================================== CONVERSATION =====================================
    @ApiOperation(value = "Get list conversation by message center Id And Timestamp", httpMethod = "GET", response = MessageController.ConversationsResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.ID_IS_NOT_EXISTED + " : " + StatusConstants.ID_IS_NOT_EXISTED_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND + " : " + StatusConstants.NOT_FOUND_MESSAGE
                    + "<br>" + StatusConstants.INVALID_PAGINATION + " : " + StatusConstants.INVALID_PAGINATION_MESSAGE
                    + "<br>" + StatusConstants.ID_INVALID + " : " + StatusConstants.ID_INVALID_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.ID + URLConstants.CONVERSATION)
    public Object getMessagesByMessageCenter(@PathVariable(value = "id") String id,
                                             @RequestParam(value = "type", required = false) Integer type,
                                             @Valid TimestampReq req,
                                             Authentication authentication) {

        BaseResp messages;
        if (MessageCenterType.BOOST_SUPPORT_CHANNEL.equals(MessageCenterType.fromValue(type))) {
            messages = businessSupportChannelService.getByMessageCenterIdAndTimestamp(authentication, id, req.getTimestamp(), req.getSize());
        } else {
            messages = businessConversationService.getByMessageCenterIdAndTimestamp(authentication, id, req.getTimestamp(), req.getSize());
        }
        return success(messages);
    }

    @ApiOperation(value = "Create new conversation between 2 users", httpMethod = "POST", response = MessageController.ConversationResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_MESSAGE_CENTER + " : " + StatusConstants.NOT_FOUND_MESSAGE_CENTER_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.CONVERSATION)
    public Object createConversation(@RequestBody ConversationReq request, Authentication authentication) {
        BaseResp result = businessConversationService.createConversation(authentication, request);
        return success(result);
    }

    @ApiOperation(value = "Delete message center", httpMethod = "DELETE", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.ID_INVALID + " : " + StatusConstants.ID_INVALID_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(URLConstants.ID)
    public Object deleteMessageCenter(@PathVariable(value = "id") String id,
                                      @RequestParam(value = "isHotelApp") boolean isHotelApp,
                                      Authentication authentication) {
        BaseResp messages = messageCenterService.delete(authentication, id, isHotelApp);
        return success(messages);
    }

    private class MessageCenterInfoSingleResp extends BaseResp<MessageCenterFullDTO> {
    }

    private class MessageCenterFullInfoResp extends BaseResp<HistoryMessageCenterFullResp> {
    }
}
