package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.models.dto.user.ShortUserDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizationRequestDTO {
    private ShortUserDTO user;
    private long unreadMessage;

    public AuthorizationRequestDTO(ShortUserDTO user, long unreadMessage) {
        this.user = user;
        this.unreadMessage = unreadMessage;
    }
}
