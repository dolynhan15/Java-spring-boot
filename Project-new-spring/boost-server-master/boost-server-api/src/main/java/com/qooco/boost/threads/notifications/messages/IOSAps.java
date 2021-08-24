package com.qooco.boost.threads.notifications.messages;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/30/2018 - 2:47 PM
 */

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class IOSAps {
    private String alert;
    private String sound;
    private int badge;
    @SerializedName("content-available")
    private int contentAvailable;
    @SerializedName("mutable_content")
    private int mutableContent;
}
