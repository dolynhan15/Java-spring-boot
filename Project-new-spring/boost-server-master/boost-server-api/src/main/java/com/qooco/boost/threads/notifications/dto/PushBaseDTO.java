package com.qooco.boost.threads.notifications.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter @Getter
public class PushBaseDTO {
    private Date serverTime;

    public PushBaseDTO() {
        this.serverTime = new Date();
    }
}
