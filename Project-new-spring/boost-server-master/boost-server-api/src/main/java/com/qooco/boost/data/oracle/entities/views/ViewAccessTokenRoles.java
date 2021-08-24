package com.qooco.boost.data.oracle.entities.views;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "VIEW_ACCESS_TOKEN_ROLES")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
public class ViewAccessTokenRoles implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "TOKEN")
    private String token;

    @Basic(optional = false)
    @NotNull
    @Column(name = "USER_PROFILE_ID")
    private Long userProfileId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "USERNAME")
    private String username;

    @Basic(optional = false)
    @NotNull
    @Column(name = "TYPE")
    private short type;

    @Column(name = "ROLE_NAME", columnDefinition = "NVARCHAR2")
    private String roleName;

    @Column(name = "SIGN_IN_ID")
    private String signInId;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @Column(name = "DEVICE_TYPE")
    private int deviceType;

    @Column(name = "CHANNEL_ID")
    private String channelId;

    @Column(name = "PLATFORM", columnDefinition = "NVARCHAR2")
    private String platform;

    @Column(name = "APP_ID", columnDefinition = "NVARCHAR2")
    private String appId;

    @Column(name = "PUBLIC_KEY")
    private String publicKey;

    @Column(name = "COMPANY_ID")
    private Long companyId;

    @Column(name = "IS_ADMIN")
    private boolean isSystemAdmin;

    public ViewAccessTokenRoles(Long userProfileId, String username, String token, short type, String roleName) {
        this.userProfileId = userProfileId;
        this.username = username;
        this.token = token;
        this.type = type;
        this.roleName = roleName;
    }

    public ViewAccessTokenRoles(Long userProfileId, String username, String token, String appId, short type, String roleName) {
        this(userProfileId, username, token, type, roleName);
        this.appId = appId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewAccessTokenRoles that = (ViewAccessTokenRoles) o;
        return token.equals(that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return username + " " + token;
    }
}
