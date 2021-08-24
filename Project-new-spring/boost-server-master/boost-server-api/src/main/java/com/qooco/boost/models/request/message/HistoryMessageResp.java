package com.qooco.boost.models.request.message;

import com.qooco.boost.models.dto.message.MessageDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryMessageResp {
    private List<MessageDTO> results;
    private boolean hasMoreMessage;
    private boolean isLockedAppointmentBtn;
    private boolean isLockedChatBtn;
}
