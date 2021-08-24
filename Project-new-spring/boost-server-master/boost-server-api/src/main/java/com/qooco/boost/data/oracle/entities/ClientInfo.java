package com.qooco.boost.data.oracle.entities;

import com.qooco.boost.models.request.authorization.ClientInfoReq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "CLIENT_INFO")
public class ClientInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(max = 255)
    @Column(name = "TOKEN", nullable = false, columnDefinition="NVARCHAR2")
    private String token;

    @Basic(optional = false)
    @NotNull
    @Size(max = 255)
    @Column(name = "APP_ID", nullable = false, columnDefinition = "NVARCHAR2")
    private String appId;

    @Basic(optional = false)
    @NotNull
    @Size(max = 255)
    @Column(name = "APP_VERSION", columnDefinition = "NVARCHAR2")
    private String appVersion;

    @Basic(optional = false)
    @Size(max = 255)
    @Column(name = "DEVICE_MODEL", columnDefinition = "NVARCHAR2")
    private String deviceModel;

    @Basic(optional = false)
    @NotNull
    @Size(max = 255)
    @Column(name = "PLATFORM", nullable = false, columnDefinition = "NVARCHAR2")
    private String platform;

    @Basic(optional = false)
    @Size(max = 255)
    @Column(name = "OS_VERSION", columnDefinition = "NVARCHAR2")
    private String osVersion;

    @Basic(optional = false)
    @Size(max = 255)
    @Column(name = "DEVICE_TOKEN", columnDefinition = "NVARCHAR2")
    private String deviceToken;

    @Basic(optional = false)
    @Size(max = 255)
    @Column(name = "CHANNEL_ID", columnDefinition = "VARCHAR")
    private String channelId;

    @NotNull
    @Column(name = "USER_PROFILE_ID", nullable = false)
    private Long userProfileId;

    @Basic(optional = false)
    @Column(name = "LOGIN_TIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date loginTime;

    @Basic(optional = false)
    @Column(name = "LOGOUT_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logoutTime;

    public ClientInfo(ClientInfoReq clientInfoReq) {
        if (Objects.nonNull(clientInfoReq)) {
            this.appId = clientInfoReq.getAppId();
            this.appVersion = clientInfoReq.getAppVersion();
            this.deviceModel = clientInfoReq.getDeviceModel();
            this.platform = clientInfoReq.getPlatform();
            this.osVersion = clientInfoReq.getOsVersion();
            this.deviceToken = clientInfoReq.getDeviceToken();
            this.channelId = clientInfoReq.getChannelId();
        }
    }

    public ClientInfo(String appId) {
        this.appId = appId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientInfo that = (ClientInfo) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {

        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return  appId + ":" + platform;
    }
}
