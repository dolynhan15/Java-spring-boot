package com.qooco.boost.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qooco.boost.data.oracle.entities.AppVersion;
import com.qooco.boost.utils.DateUtils;
import lombok.*;

import java.util.Objects;


@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppVersionReq {
    @Setter
    @Getter
    private String appId;
    @Setter
    @Getter
    private Integer appVersion;
    @Setter
    @Getter
    private String appVersionName;
    @Setter
    @Getter
    private String os;

    @Getter
    private boolean isForceUpdate;

    @JsonIgnore
    @Getter @Setter
    private Long id;

    @JsonProperty("isForceUpdate")
    public void setForceUpdate(boolean isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public AppVersionReq(Long id) {
        this.id = id;
    }

    public AppVersion updateEntity(AppVersion appVersion) {
        if (Objects.isNull(appVersion)) {
            appVersion = new AppVersion();
            appVersion.setCreatedDate(DateUtils.nowUtcForOracle());
        }
        appVersion.setAppId(this.appId);
        appVersion.setAppVersion(this.appVersion);
        appVersion.setAppVersionName(this.appVersionName);
        appVersion.setOs(this.os);
        appVersion.setForceUpdate(this.isForceUpdate);
        appVersion.setUpdatedDate(DateUtils.nowUtcForOracle());
        return appVersion;
    }

}
