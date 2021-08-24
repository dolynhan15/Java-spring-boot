package com.qooco.boost.controllers;

import com.qooco.boost.business.*;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.enumeration.MessageCenterType;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.MessageTemplateDTO;
import com.qooco.boost.models.dto.message.ConversationDTO;
import com.qooco.boost.models.request.LastRequest;
import com.qooco.boost.models.request.RoleAssignedReq;
import com.qooco.boost.models.request.StatusReq;
import com.qooco.boost.models.request.TimestampReq;
import com.qooco.boost.models.request.message.ChatMessageReq;
import com.qooco.boost.models.request.message.HistoryMessageResp;
import com.qooco.boost.models.request.message.MediaMessageReq;
import com.qooco.boost.models.user.UserMessageReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Message", value = URLConstants.MESSAGE_PATH, description = "Message Controller")
@RestController
@RequestMapping(URLConstants.MESSAGE_PATH)
@RequiredArgsConstructor
public class MessageController extends BaseController {
    private final BusinessConversationService businessConversationService;
    private final BusinessSupportChannelService businessSupportChannelService;
    private final BusinessChatService businessChatService;
    private final BusinessChatSupportService businessSupportChatService;
    private final BusinessMessageService businessMessageService;
    private final BusinessMessageTemplateService businessMessageTemplateService;
    private final BusinessBoostHelperService businessBoostHelperService;

    @ApiOperation(value = "Get conversation by Id", httpMethod = "GET", response = ConversationResp.class, notes = notesGetConversation)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.CONVERSATION + URLConstants.ID)
    public Object getConversation(@PathVariable String id,
                                  @RequestParam(value = "type", required = false) Integer type,
                                  Authentication authentication) {

        BaseResp messages;
        if (MessageCenterType.BOOST_SUPPORT_CHANNEL.equals(MessageCenterType.fromValue(type))) {
            messages = businessSupportChannelService.getById(authentication, id);
        } else {
            messages = businessConversationService.getById(authentication, id);
        }
        return success(messages);
    }

    @ApiOperation(value = "Get messages details of the conversation", httpMethod = "GET", response = HistoryMessageResp.class, notes = notesDetailMgs)
    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public Object getMessagesInPersonalConversation(@Valid TimestampReq req,
                                                    @RequestParam(value = "conversationId", required = false) String conversationId,
                                                    @RequestParam(value = "type", required = false) Integer type,
                                                    @RequestParam(value = "eventId", required = false) Long eventId,
                                                    Authentication authentication) {
        HistoryMessageResp result;
        if (MessageCenterType.BOOST_SUPPORT_CHANNEL.equals(MessageCenterType.fromValue(type))) {
            result = businessSupportChatService.getAllMessagesInPersonalConversation(authentication, conversationId, req.getTimestamp(), req.getSize());
        } else {
            result = businessChatService.getAllMessagesInPersonalConversation(authentication, conversationId, eventId, req.getTimestamp(), req.getSize());
        }
        return success(new BaseResp<>(result));
    }

    @ApiOperation(value = "Update message status: Sent, Delivery (Received), Seen", httpMethod = "PATCH", response = BaseResp.class, notes = notesUpdateStatus)
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.ID)
    public Object updateMessageStatus(@PathVariable(value = "id") String messageId,
                                      @RequestParam(value = "type", required = false) Integer type,
                                      @RequestBody StatusReq status, Authentication authentication) {
        saveRequestBodyToSystemLogger(status);
        if (MessageCenterType.BOOST_SUPPORT_CHANNEL.equals(MessageCenterType.fromValue(type))) {
            businessSupportChatService.updateMessageStatus(authentication, messageId, status.getStatus());
        } else {
            businessChatService.updateMessageStatus(authentication, messageId, status.getStatus());
        }
        return success(new BaseResp<>(ResponseStatus.SUCCESS));
    }

    @ApiOperation(value = "Send file via chat message", httpMethod = "POST", response = BaseResp.class, notes = notesUpdateStatus)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = URLConstants.CONVERSATION + URLConstants.ID + URLConstants.SEND_FILE)
    public Object sendFileMessage(@PathVariable(value = "id") String conversationId,
                                  @RequestParam("file") MultipartFile file,
                                  @RequestParam(value = "type", required = false) Integer type,
                                  @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
                                  @RequestParam(value = "clientId", required = false) String clientId,
                                  @RequestParam(value = "duration", required = false) Long duration,
                                  Authentication authentication) {
        MediaMessageReq mediaMessageReq = MediaMessageReq.builder().file(file).thumbnail(thumbnail).clientId(clientId).duration(duration).build();
        if (MessageCenterType.BOOST_SUPPORT_CHANNEL.equals(MessageCenterType.fromValue(type))) {
            businessSupportChatService.sendFileMessage(authentication, conversationId, mediaMessageReq);
        } else {
            businessChatService.sendFileMessage(authentication, conversationId, mediaMessageReq);
        }
        return success(new BaseResp<>(ResponseStatus.SUCCESS));
    }

    //============================MESSAGE TEMPLATE==========================

    @ApiOperation(value = "Get message template default from server", httpMethod = "GET", response = MessageTemplateResp.class)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = URLConstants.TEMPLATE_MESSAGE)
    public Object getMessageTemplate(@Valid LastRequest req) {
        BaseResp result = businessMessageTemplateService.getMessageTemplate(req);
        return success(result);
    }

    //============================MESSAGE CHAT==========================
    @ApiOperation(value = "Send text via chat message", httpMethod = "POST", response = BaseResp.class)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = URLConstants.CONVERSATION + URLConstants.ID + URLConstants.CHAT_TEXT)
    public Object sendTextMessage(@PathVariable(value = "id") String conversationId,
                                  @RequestParam(value = "type", required = false) Integer type,
                                  @RequestBody ChatMessageReq message,
                                  Authentication authentication) {

        if (MessageCenterType.BOOST_SUPPORT_CHANNEL.equals(MessageCenterType.fromValue(type))) {
            businessSupportChatService.submitMessage(authentication, conversationId, message);
        } else {
            businessChatService.submitMessage(authentication, conversationId, message);
        }
        return success(new BaseResp<>(ResponseStatus.SUCCESS));
    }

    @ApiOperation(value = "Send message to boost helper", httpMethod = "POST", response = BaseResp.class, notes = rasaNotes)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = URLConstants.CONVERSATION + URLConstants.ID + URLConstants.CHAT_BOOST_HELPER)
    public Object sendMessageRequestFromUserToRASA(@PathVariable(value = "id") String conversationId, @RequestBody UserMessageReq req, Authentication authentication) {
        BaseResp result = businessBoostHelperService.sendMessageRequestFromUserToRASA(req.setConversationId(conversationId), authentication);
        return success(result);
    }
    //============================MESSAGE ACTION==========================

    @ApiOperation(value = "Update interested/uninterested applicant message", httpMethod = "PATCH", response = MessageResp.class, notes = notesApplication)
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.ID + URLConstants.APPLICANT_RESPONSE)
    public Object responseApplicantMessage(@PathVariable(value = "id") String messageId, @RequestBody StatusReq status,
                                           Authentication authentication) {
        saveRequestBodyToSystemLogger(status);
        businessMessageService.updateInterestForApplicantMessageStatus(messageId, status.getStatus(), authentication);
        return success(new BaseResp<>(ResponseStatus.SUCCESS));
    }

    @ApiOperation(value = "Update accepted/decline appointment message", httpMethod = "PATCH", response = BaseResp.class, notes = notesAppointment)
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.ID + URLConstants.APPOINTMENT_RESPONSE)
    public Object responseAppointmentMessage(@PathVariable(value = "id") String messageId,
                                             @RequestBody StatusReq status,
                                             Authentication authentication) {
        saveRequestBodyToSystemLogger(status);
        businessMessageService.replyAppointmentMessage(messageId, status.getStatus(), authentication);
        return success(new BaseResp<>(ResponseStatus.SUCCESS));
    }

    @ApiOperation(value = "Update accepted/decline join company message", httpMethod = "PATCH", response = BaseResp.class, notes = notesJoinCompany)
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.ID + URLConstants.JOIN_COMPANY_RESPONSE)
    public Object responseJoinCompanyMessage(@PathVariable(value = "id") String messageId, @RequestBody StatusReq status, Authentication authentication) {
        saveRequestBodyToSystemLogger(status);
        businessMessageService.approvalRequestMessage(messageId, status.getStatus(), authentication);
        return success(new BaseResp<>(ResponseStatus.SUCCESS));
    }

    @ApiOperation(value = "Assign role via message and assign to another ", httpMethod = "PATCH", response = BaseResp.class, notes = notes)
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.ID + URLConstants.ASSIGN_ROLE)
    public Object assignRoleMessage(@PathVariable(value = "id") String messageId, @RequestBody RoleAssignedReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        request.setMessageId(messageId);
        businessMessageService.setRoleMessage(messageId, request, authentication);
        return success(new BaseResp<>(ResponseStatus.SUCCESS));
    }

    //============================NOTE==========================
    private static final String notesDetailMgs = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
            + "<br>" + StatusConstants.CHANNEL_ID_INVALID + " : " + StatusConstants.CHANNEL_ID_INVALID_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_CONVERSATION + " : " + StatusConstants.NOT_FOUND_CONVERSATION_MESSAGE
            + "<br>" + StatusConstants.USER_IS_NOT_IN_CONVERSATION + " : " + StatusConstants.USER_IS_NOT_IN_CONVERSATION_MESSAGE
            + "<br>" + StatusConstants.TIME_ZONE_INVALID + " : " + StatusConstants.TIME_ZONE_INVALID_MESSAGE
            + "<br>" + "Message Types:"
            + "<br>" + MessageConstants.APPLICANT_MESSAGE + " : APPLICANT_MESSAGE"
            + "<br>" + MessageConstants.APPOINTMENT_MESSAGE + " : APPOINTMENT_MESSAGE"
            + "<br>" + MessageConstants.AUTHORIZATION_MESSAGE + " : AUTHORIZATION_MESSAGE"
            + "<br>" + MessageConstants.ASSIGNMENT_ROLE_MESSAGE + " : ASSIGNMENT_ROLE_MESSAGE"
            + "<br>" + MessageConstants.TEXT_MESSAGE + " : TEXT_MESSAGE"
            + "<br>" + MessageConstants.APPOINTMENT_CANCEL_MESSAGE + " : APPOINTMENT_CANCEL_MESSAGE"
            + "<br>" + MessageConstants.APPOINTMENT_APPLICANT_MESSAGE + " : APPOINTMENT_APPLICANT_MESSAGE"
            + "<br>" + MessageConstants.CONGRATULATION_MESSAGE + " : CONGRATULATION_MESSAGE"
            + "<br>" + MessageConstants.SUSPENDED_VACANCY + " : SUSPENDED_VACANCY_MESSAGE"
            + "<br>" + MessageConstants.INACTIVE_VACANCY + " : INACTIVE_VACANCY_MESSAGE"
            + "<br>" + MessageConstants.MEDIA_MESSAGE + " : MEDIA_MESSAGE"
            + "<br>" + MessageConstants.BOOST_HELPER_MESSAGE + " : BOOST_HELPER_MESSAGE"
            + "<br>" + MessageConstants.SESSION_MESSAGE + " : SESSION_MESSAGE";

    private static final String notesGetConversation = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
            + "<br>" + StatusConstants.ID_IS_NOT_EXISTED + " : " + StatusConstants.ID_IS_NOT_EXISTED_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND + " : " + StatusConstants.NOT_FOUND_MESSAGE
            + "<br>" + StatusConstants.ID_INVALID + " : " + StatusConstants.ID_INVALID_MESSAGE;

    private static final String notes = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
            + "<br>" + StatusConstants.ID_INVALID + " : " + StatusConstants.ID_INVALID_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND + " : " + StatusConstants.NOT_FOUND_MESSAGE
            + "<br>" + StatusConstants.STATUS_INVALID + " : " + StatusConstants.STATUS_INVALID_MESSAGE
            + "<br>" + StatusConstants.SAVE_FAIL + " : " + StatusConstants.SAVE_FAIL_MESSAGE;

    private static final String notesApplication = notes
            + "<br><strong> Status:</strong>"
            + "<br>" + MessageConstants.APPLIED_STATUS_INTERESTED + " : " + "APPLIED_STATUS_INTERESTED"
            + "<br>" + MessageConstants.APPLIED_STATUS_NOT_INTERESTED + " : " + "APPLIED_STATUS_NOT_INTERESTED";

    private static final String notesAppointment = notes
            + "<br><strong> Status:</strong>"
            + "<br>" + MessageConstants.APPOINTMENT_STATUS_ACCEPTED + " : " + "APPOINTMENT_STATUS_ACCEPTED"
            + "<br>" + MessageConstants.APPOINTMENT_STATUS_DECLINED + " : " + "APPOINTMENT_STATUS_DECLINED";

    private static final String notesJoinCompany = notes
            + "<br><strong> Status:</strong>"
            + "<br>" + MessageConstants.JOIN_COMPANY_REQUEST_STATUS_AUTHORIZED + " : " + "JOIN_COMPANY_REQUEST_STATUS_AUTHORIZED"
            + "<br>" + MessageConstants.JOIN_COMPANY_REQUEST_STATUS_DECLINED + " : " + "JOIN_COMPANY_REQUEST_STATUS_DECLINED";

    private static final String notesUpdateStatus = notes
            + "<br><strong> Status:</strong>"
            + "<br>" + MessageConstants.MESSAGE_STATUS_SENT + " : " + "MESSAGE_STATUS_SENT"
            + "<br>" + MessageConstants.MESSAGE_STATUS_RECEIVED + " : " + "MESSAGE_STATUS_RECEIVED"
            + "<br>" + MessageConstants.MESSAGE_STATUS_SEEN + " : " + "MESSAGE_STATUS_SEEN";

    private static final String rasaNotes = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE;

    class ConversationsResp extends BaseResp<List<ConversationDTO>> {
    }

    class ConversationResp extends BaseResp<ConversationDTO> {
    }

    class MessageResp extends BaseResp {
    }

    class MessageTemplateResp extends BaseResp<List<MessageTemplateDTO>> {
    }
}
