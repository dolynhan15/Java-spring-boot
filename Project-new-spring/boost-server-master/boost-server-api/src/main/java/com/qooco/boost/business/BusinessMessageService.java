package com.qooco.boost.business;

import com.qooco.boost.models.dto.message.MessageDTO;
import com.qooco.boost.models.request.RoleAssignedReq;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BusinessMessageService extends BaseBusinessService {
    @Transactional
    List<MessageDTO> approvalRequestMessage(String messageId, Integer status, Authentication authentication);

    @Transactional
    List<MessageDTO> approvalRequestMessage(String messageId, Integer status, Long adminProfileId, String locale);

    void updateInterestForApplicantMessageStatus(String messageId, int status, Authentication authentication);

    void replyAppointmentMessage(String messageId, Integer appointmentStatus, Authentication authentication);

    void setRoleMessage(String messageId, RoleAssignedReq req, Authentication authentication);
}
