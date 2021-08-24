package com.qooco.boost.models.request.authorization;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder = true)
public class ClientInfoReq{
    private String appId;
    private String appVersion;
    private String deviceModel;
    private String platform;
    private String osVersion;
    private String deviceToken;
    private String channelId;
}
