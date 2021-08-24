package com.qooco.boost.threads.notifications.dto;

import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.models.dto.RoleCompanyDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Optional.ofNullable;

@Setter
@Getter
@NoArgsConstructor
public class PushAssignRoleDTO extends PushMessageBaseDTO {
    private RoleCompanyDTO roleCompany;
    private int messageAction;

    public PushAssignRoleDTO(MessageBase messageDoc, String locale) {
        super(messageDoc);
        ofNullable(messageDoc.getStaff()).ifPresent(it -> this.roleCompany = new RoleCompanyDTO(it.getRoleCompany(), locale));
        this.messageAction = MessageConstants.UPDATE_MESSAGE_ACTION;
    }
}
