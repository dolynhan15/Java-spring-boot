package com.qooco.boost.data.enumeration;


import com.qooco.boost.data.constants.MessageConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum JoinCompanyStatus {
    PENDING(MessageConstants.JOIN_COMPANY_REQUEST_STATUS_PENDING, MessageConstants.JOIN_COMPANY_REQUEST_STATUS_PENDING_MESSAGE),
    AUTHORIZED(MessageConstants.JOIN_COMPANY_REQUEST_STATUS_AUTHORIZED, MessageConstants.JOIN_COMPANY_REQUEST_STATUS_AUTHORIZED_MESSAGE),
    DECLINED(MessageConstants.JOIN_COMPANY_REQUEST_STATUS_DECLINED, MessageConstants.JOIN_COMPANY_REQUEST_STATUS_DECLINED_MESSAGE);

    @Getter
    private final int code;
    @Getter
    private final String description;
}
