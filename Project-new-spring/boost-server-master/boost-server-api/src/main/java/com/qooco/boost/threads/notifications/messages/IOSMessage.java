package com.qooco.boost.threads.notifications.messages;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/30/2018 - 2:29 PM
 */
@Setter @Getter @NoArgsConstructor
public class IOSMessage {
    private IOSAps aps;
    @SerializedName("custom_content")
    private Object customContent;

    public IOSMessage(IOSAps aps, Object customContent) {
        this.aps = aps;
        this.customContent = customContent;
    }
}
