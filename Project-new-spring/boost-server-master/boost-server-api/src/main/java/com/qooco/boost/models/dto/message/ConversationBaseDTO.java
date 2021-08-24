package com.qooco.boost.models.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.models.dto.LatestTimeDTO;
import com.qooco.boost.models.dto.user.UserProfileCvEmbeddedDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationBaseDTO extends LatestTimeDTO {
    private String id;
    private String messageCenterId;
    private List<UserProfileCvEmbeddedDTO> participants;
    private String encryptedKey;

}
