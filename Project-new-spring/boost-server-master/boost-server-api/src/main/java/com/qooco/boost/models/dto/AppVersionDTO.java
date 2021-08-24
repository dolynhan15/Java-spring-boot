package com.qooco.boost.models.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.AppVersion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppVersionDTO {

    private Long id;

    private String appId;
    private Integer appVersion;
    private String appVersionName;
    private String os;
    private boolean isForceUpdate;
    private boolean hasNewVersion;
    private Date updatedDate;

    public AppVersionDTO(AppVersion appVersion) {
        if (Objects.nonNull(appVersion)) {
            this.id = appVersion.getId();
            this.appId = appVersion.getAppId();
            this.appVersion = appVersion.getAppVersion();
            this.appVersionName = appVersion.getAppVersionName();
            this.os = appVersion.getOs();
            this.isForceUpdate = appVersion.isForceUpdate();
            this.updatedDate = appVersion.getUpdatedDate();
        }
    }

    public AppVersionDTO(Integer appVersion, String appId, String os) {
            this.appId = appId;
            this.appVersion = appVersion;
            this.os = os;
    }
}
