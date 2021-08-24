package com.qooco.boost.threads.notifications.messages;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/30/2018 - 2:28 PM
 */

@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class AndroidMessage {
    private String title;
    private String description;
    @SerializedName("notification_builder_id")
    private int builderId;
    @SerializedName("notification_basic_style")
    private int basicStyle;
    @SerializedName("open_type")
    private int openType;
    @SerializedName("pkg_content")
    private String pkgContent = "";
    @SerializedName("custom_content")
    protected Object customContent;
    private String url = "";
}
