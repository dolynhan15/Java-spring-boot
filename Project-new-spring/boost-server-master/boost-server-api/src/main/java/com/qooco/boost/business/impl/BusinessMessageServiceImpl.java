package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessMessageService;
import com.qooco.boost.business.BusinessStaffService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.business.impl.abstracts.BusinessUserServiceAbstract;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.enumeration.JoinCompanyStatus;
import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.message.AppliedMessage;
import com.qooco.boost.data.mongo.embedded.message.AppointmentDetailMessage;
import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.services.AppointmentDetailDocService;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.dto.message.MessageDTO;
import com.qooco.boost.models.request.RoleAssignedReq;
import com.qooco.boost.socket.services.SendMessageToClientService;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.threads.services.AppointmentDetailActorService;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoConverters;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

import static java.util.Optional.ofNullable;

@Service
public class BusinessMessageServiceImpl extends BusinessUserServiceAbstract implements BusinessMessageService {
    @Autowired
    private UserFitService userFitService;
    @Autowired
    private MessageDocService messageDocService;
    @Autowired
    private ConversationDocService conversationDocService;
    @Autowired
    private BoostActorManager boostActorManager;
    @Autowired
    private StaffService staffService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private RoleCompanyService roleCompanyService;
    @Autowired
    private BusinessStaffService businessStaffService;
    @Autowired
    private CompanyJoinRequestService companyJoinRequestService;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private BusinessValidatorService businessValidatorService;
    @Autowired
    private AppointmentDetailService appointmentDetailService;
    @Autowired
    private AppointmentDetailDocService appointmentDetailDocService;
    @Autowired
    private AppointmentDetailActorService appointmentDetailActorService;
    @Autowired
    private SendMessageToClientService sendMessageToClientService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private UserAccessTokenService userAccessTokenService;

    @Value(ApplicationConstant.BOOST_MESSAGE_CONFIG_FILE_LIVE_TIME)
    private Integer numberLiveDayOfMessageFile;
    @Value(ApplicationConstant.BOOST_PATA_QOOCO_DOMAIN_PATH)
    private String qoocoDomainPath = "";

    @Override
    @Transactional
    public List<MessageDTO> approvalRequestMessage(String messageId, Integer status, Authentication authentication) {
        Long adminProfileId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        return approvalRequestMessage(messageId, status, adminProfileId, getLocale(authentication));
    }

    @Override
    @Transactional
    public List<MessageDTO> approvalRequestMessage(String messageId, Integer status, Long adminProfileId, String locale) {
        if (!ObjectId.isValid(messageId)) {
            throw new InvalidParamException(ResponseStatus.ID_INVALID);
        }

        if (JoinCompanyStatus.AUTHORIZED.getCode() != status
                && JoinCompanyStatus.DECLINED.getCode() != status) {
            throw new InvalidParamException(ResponseStatus.STATUS_INVALID);
        }

        MessageDoc messageDoc = businessValidatorService.checkExistsMessageDoc(messageId);

        if (messageDoc.getType() != MessageConstants.AUTHORIZATION_MESSAGE
                || Objects.isNull(messageDoc.getAuthorizationMessage())) {
            throw new EntityNotFoundException(ResponseStatus.UPDATE_JOIN_COMPANY_REQUEST_NOT_AUTHORIZATION_MESSAGE);
        }

        Long companyId = messageDoc.getAuthorizationMessage().getCompany().getId();
        Staff staff = getStaffAdminOfCompany(companyId, adminProfileId);
        if (Objects.isNull(staff)) {
            throw new NoPermissionException(ResponseStatus.UPDATE_JOIN_COMPANY_REQUEST_NOT_ADMIN);
        }

        CompanyJoinRequest companyJoinRequest = companyJoinRequestService.findById(messageDoc.getAuthorizationMessage().getId());
        if (Objects.isNull(companyJoinRequest)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_JOIN_REQUEST);
        }

        if (companyJoinRequest.getStatus() == JoinCompanyStatus.AUTHORIZED) {
            throw new NoPermissionException(ResponseStatus.UPDATE_JOIN_COMPANY_REQUEST_AUTHORIZED);
        } else if (companyJoinRequest.getStatus() == JoinCompanyStatus.DECLINED) {
            throw new NoPermissionException(ResponseStatus.UPDATE_JOIN_COMPANY_REQUEST_DECLINED);
        }

        JoinCompanyStatus joinCompanyStatus = JoinCompanyStatus.DECLINED.getCode() == status ? JoinCompanyStatus.DECLINED : JoinCompanyStatus.AUTHORIZED;
        messageDoc = approvalJoinCompanyRequest(companyJoinRequest, joinCompanyStatus, messageDoc, adminProfileId);

        MessageDoc assignmentRoleMsg = null;
        if (joinCompanyStatus == JoinCompanyStatus.AUTHORIZED) {
            assignmentRoleMsg = sendAssignmentMessage(
                    messageDoc.getConversationId(),
                    companyId,
                    adminProfileId,
                    messageDoc.getSender().getUserProfileId());
            pushNotificationService.notifyAssignRoleMessage(assignmentRoleMsg, true);
        }

        messageDocService.updateSeenForOlderMessage(messageDoc, true);
        List<MessageDTO> result = Lists.newArrayList(createUpdateMessage(messageDoc, locale));
        if (Objects.nonNull(assignmentRoleMsg)) {
            result.add(new MessageDTO(assignmentRoleMsg, MessageConstants.SUBMIT_MESSAGE_ACTION, qoocoDomainPath, locale));
        }
        var userAccessToken = userAccessTokenService.findByUserProfileIdAndNullCompany(companyJoinRequest.getUserFit().getUserProfileId());
        if (joinCompanyStatus == JoinCompanyStatus.AUTHORIZED && CollectionUtils.isNotEmpty(userAccessToken))
            userAccessToken.forEach(it -> saveDefaultCompany(ofNullable(it.getUserProfile()).map(UserProfile::getUserProfileId).orElse(-1L), it.getToken(), companyId));
        sendMessageToClientService.sendMessage(result);
        return result;
    }

    @Override
    public void replyAppointmentMessage(String messageId, Integer appointmentStatus, Authentication authentication) {
        if (!ObjectId.isValid(messageId)) {
            throw new InvalidParamException(ResponseStatus.ID_INVALID);
        }

        MessageDoc messageDoc = messageDocService.findById(new ObjectId(messageId));
        if (Objects.isNull(messageDoc)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND);
        }

        List<Integer> repliedStatus = Lists.newArrayList(
                AppointmentStatus.ACCEPTED.getValue(),
                AppointmentStatus.PENDING_ACCEPTED.getValue(),
                AppointmentStatus.DECLINED.getValue());

        if (MessageConstants.APPOINTMENT_MESSAGE != messageDoc.getType()) {
            throw new InvalidParamException(ResponseStatus.MESSAGE_IS_NOT_APPOINTMENT);
        }
        AppointmentDetailMessage appointmentDetailMessage = messageDoc.getAppointmentDetailMessage();
        if (Objects.isNull(appointmentDetailMessage)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_APPOINTMENT);
        }
        AppointmentDetail appointmentDetail = appointmentDetailService.findById(appointmentDetailMessage.getId());
        if (Objects.isNull(appointmentDetail) || appointmentDetail.getIsDeleted()) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_APPOINTMENT);
        } else if (appointmentDetail.getStatus() != AppointmentStatus.PENDING.getValue()) {
            throw new EntityNotFoundException(ResponseStatus.APPOINTMENT_IS_NOT_AVAILABLE_TO_REPLY);
        }
        Appointment appointment = appointmentService.findById(appointmentDetailMessage.getAppointment().getId());
        if (Objects.nonNull(appointment) && !DateUtils.getUtcForOracle(appointment.getToDateOfAppointment()).after(DateUtils.toServerTimeForMongo())) {
            throw new NoPermissionException(ResponseStatus.APPOINTMENT_IS_EXPIRED);
        }

        if (repliedStatus.contains(appointmentDetailMessage.getResponseStatus())) {
            throw new NoPermissionException(ResponseStatus.APPOINTMENT_IS_REPLIED_ALREADY);
        }

        if (AppointmentStatus.DECLINED.getValue() != appointmentStatus) {
            throw new InvalidParamException(ResponseStatus.STATUS_INVALID);
        }

        appointmentDetailMessage.setResponseStatus(appointmentStatus);
        appointmentDetailMessage.setStatus(MessageConstants.MESSAGE_STATUS_SENT);
        messageDoc.setAppointmentDetailMessage(appointmentDetailMessage);
        messageDoc.setStatus(MessageConstants.MESSAGE_STATUS_SEEN);
        messageDoc = messageDocService.save(messageDoc, true);
        messageDocService.updateSeenForOlderMessage(messageDoc, false);

        Integer formattedStatus = AppointmentStatus.mixAppointmentStatus(appointmentDetail.getStatus(), appointmentStatus);
        appointmentDetail.setStatus(formattedStatus);
        appointmentDetail.setUpdatedDate(DateUtils.nowUtcForOracle());
        appointmentDetail.setUpdatedBy(appointmentDetail.getUserCurriculumVitae().getUserProfile().getUserProfileId());
        appointmentDetailService.save(appointmentDetail);

        appointmentDetailDocService.updateStatusOfAppointmentDetailDoc(appointmentDetail.getId(), formattedStatus);
        AppointmentDetailDoc appointmentDetailDoc = appointmentDetailDocService.findById(appointmentDetail.getId());
        appointmentDetailActorService.updateAppointmentCandidatesInVacancyDoc(Lists.newArrayList(appointmentDetailDoc));

        pushNotificationService.notifyAppointmentResponseMessage(messageDoc, true);
        MessageDTO result = createUpdateMessage(messageDoc, getLocale(authentication));
        sendMessageToClientService.sendMessage(result);

        if (MessageConstants.APPOINTMENT_STATUS_ACCEPTED == appointmentStatus
                || MessageConstants.APPOINTMENT_STATUS_DECLINED == appointmentStatus) {
            sendMessageToClientService.sendMessage(new MessageDTO(
                    messageDoc.getConversationId().toHexString(),
                    messageDoc.getMessageCenterId().toHexString(),
                    MessageConstants.APPOINTMENT_MESSAGE_ACTION_ENABLE));
        }
    }

    @Override
    public void updateInterestForApplicantMessageStatus(String messageId, int status, Authentication authentication) {
        validateMessageId(messageId);
        MessageDoc messageDoc = messageDocService.findById(new ObjectId(messageId));
        ofNullable(messageDoc).orElseThrow(() -> new EntityNotFoundException(ResponseStatus.NOT_FOUND));

        if (messageDoc.getType() == MessageConstants.APPLICANT_MESSAGE) {
            AppliedMessage appliedMessage = messageDoc.getAppliedMessage();
            if (Objects.nonNull(appliedMessage) && (status == MessageConstants.APPLIED_STATUS_INTERESTED
                    || status == MessageConstants.APPLIED_STATUS_NOT_INTERESTED)) {
                appliedMessage.setResponseStatus(status);
                appliedMessage.setStatus(MessageConstants.MESSAGE_STATUS_SENT);
                messageDoc.setAppliedMessage(appliedMessage);
                messageDoc.setStatus(MessageConstants.MESSAGE_STATUS_SEEN);
                messageDoc = messageDocService.save(messageDoc, true);
                sendMessageToClientService.sendMessage(messageDoc, MessageConstants.UPDATE_MESSAGE_ACTION, getLocale(authentication));
                pushNotificationService.notifyApplicantResponseMessage(messageDoc, true);
            } else {
                throw new InvalidParamException(ResponseStatus.STATUS_INVALID);
            }
        } else {
            throw new InvalidParamException(ResponseStatus.MESSAGE_NOT_APPLIED);
        }
    }

    @Override
    public void setRoleMessage(String messageId, RoleAssignedReq req, Authentication authentication) {
        validateMessageId(messageId);
        MessageDoc messageDoc = messageDocService.findById(new ObjectId(messageId));
        if (Objects.nonNull(messageDoc) && Objects.nonNull(messageDoc.getStaff())) {
            req.setStaffId(messageDoc.getStaff().getId());
            businessStaffService.setRoleStaffOfCompany(req, authentication);
        }
    }

    private void validateMessageId(String id) {
        if (!ObjectId.isValid(id)) {
            throw new InvalidParamException(ResponseStatus.ID_INVALID);
        }
    }

    private Staff getStaffAdminOfCompany(Long companyId, Long userProfileId) {
        List<Staff> staffs = staffService.findByCompanyApprovalAndAdmin(companyId, userProfileId);
        if (CollectionUtils.isNotEmpty(staffs)) {
            return staffs.get(0);
        }
        return null;
    }

    private MessageDoc approvalJoinCompanyRequest(
            @NotNull CompanyJoinRequest companyJoinRequest,
            JoinCompanyStatus joinCompanyStatus,
            @NotNull MessageDoc messageDoc, long adminId) {

        companyJoinRequest.setStatus(joinCompanyStatus);
        companyJoinRequest.setUpdatedBy(adminId);
        companyJoinRequest.setUpdatedDate(DateUtils.nowUtcForOracle());
        companyJoinRequestService.save(companyJoinRequest);
        boostActorManager.updateJoinCompanyRequestOtherAdminInMongoActor(companyJoinRequest);

        messageDoc.getAuthorizationMessage().setResponseStatus(joinCompanyStatus.getCode());
        messageDoc.getAuthorizationMessage().setStatus(MessageConstants.MESSAGE_STATUS_SENT);
        messageDoc.setStatus(MessageConstants.MESSAGE_STATUS_SEEN);
        messageDoc.setUpdatedDate(DateUtils.toServerTimeForMongo(null));
        messageDoc = messageDocService.save(messageDoc, true);
        pushNotificationService.notifyJoinCompanyApproval(messageDoc, true);
        return messageDoc;

    }

    private MessageDoc sendAssignmentMessage(
            ObjectId conversationId,
            long companyId,
            long adminId,
            long requestUserId) {

        UserFit adminUser = userFitService.findById(adminId);
        UserFit requestUser = userFitService.findById(requestUserId);
        if (Objects.nonNull(adminUser) && Objects.nonNull(requestUser)) {
            // TODO Change to unassigned later
            Staff staff = new Staff();
            staff.setCompany(companyService.findById(companyId));
            staff.setUserFit(new UserFit(requestUser.getUserProfileId()));
            staff.setCreatedBy(adminId);
            staff.setUpdatedBy(adminId);
            staff.setRole(roleCompanyService.findByName(CompanyRole.RECRUITER.name()));
            staffService.save(staff);
            boostActorManager.addStaffToCompanyInMongo(new Staff(staff));
            UserProfileCvEmbedded adminProfileCvEmbedded = MongoConverters.convertToUserProfileCvEmbedded(adminUser);
            UserProfileCvEmbedded requestUserProfileCvEmbedded = MongoConverters.convertToUserProfileCvEmbedded(requestUser);
            ConversationDoc conversationDoc = conversationDocService.findById(conversationId);


            StaffEmbedded staffEmbedded = MongoConverters.convertToStaffEmbedded(staff);
            MessageDoc messageDoc = new MessageDoc.MessageDocBuilder(adminProfileCvEmbedded, requestUserProfileCvEmbedded, conversationDoc)
                    .withAssignRoleMessage(staffEmbedded).withStatus(MessageConstants.MESSAGE_STATUS_SEEN).build();

            messageDocService.save(messageDoc, true);
            return messageDoc;
        }
        return null;
    }

    private MessageDTO createUpdateMessage(MessageDoc doc, String locale) {
        return new MessageDTO(doc, MessageConstants.UPDATE_MESSAGE_ACTION, qoocoDomainPath, locale);
    }

}
