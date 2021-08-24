package com.qooco.boost.data.oracle.entities;

import com.qooco.boost.data.constants.AccountType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author mhvtrung
 */
@Entity
@Table(name = "USER_ACCESS_TOKEN")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "UserAccessToken.findAll", query = "SELECT u FROM UserAccessToken u")
        , @NamedQuery(name = "UserAccessToken.findByToken", query = "SELECT u FROM UserAccessToken u WHERE u.token = :token")
        , @NamedQuery(name = "UserAccessToken.findByRefreshToken", query = "SELECT u FROM UserAccessToken u WHERE u.refreshToken = :refreshToken")
})
@Getter
@Setter
public class UserAccessToken extends BaseEntity implements Serializable {

    public static final byte TOKEN_DELETED = 1;
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "TOKEN")
    private String token;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

    //notes = "1: System account, 2 Google Account, 3: Facebook Account"
    @Basic(optional = false)
    @NotNull
    @Column(name = "TYPE")
    private Integer type;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ACCESS_TOKEN_SEQUENCE")
    @SequenceGenerator(sequenceName = "USER_ACCESS_TOKEN_SEQ", allocationSize = 1, name = "USER_ACCESS_TOKEN_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "ACCESS_TOKEN_ID")
    private Long accessTokenId;

    @Basic(optional = false)
    @Size(max = 255)
    @Column(name = "CHANNEL_ID")
    private String channelId;

    @Column(name = "SIGN_IN_ID")
    private String signInId;

    @Column(name = "SESSION_ID")
    private String sessionId;

    //notes = "0 for default, 1 for web, 2 for pc, 3 for android, 4 for ios, 5 for wp"
    @Column(name = "DEVICE_TYPE")
    private int deviceType;

    @JoinColumn(name = "USER_PROFILE_ID", referencedColumnName = "USER_PROFILE_ID")
    @ManyToOne(optional = false)
    private UserProfile userProfile;

    @Column(name = "PUBLIC_KEY")
    private String publicKey;

    @Column(name = "COMPANY_ID")
    private Long companyId;

    public UserAccessToken() {
        super();
        this.type = AccountType.NORMAL;
    }

    public UserAccessToken(String token, String refreshToken, Long createdBy, UserProfile userProfile) {
        this();
        this.token = token;
        this.refreshToken = refreshToken;
        setCreatedBy(createdBy);
        setUpdatedBy(createdBy);
        this.userProfile = userProfile;
    }

    public UserAccessToken(Long accessTokenId) {
        super();
        this.accessTokenId = accessTokenId;
    }

    public UserAccessToken(Long accessTokenId, String token, String refreshToken, Integer type, String channelId,
                           Date createdDate, boolean isDeleted, Long createdBy, Date updatedDate, Long updatedBy) {
        super(createdDate, createdBy, updatedDate, updatedBy, isDeleted);
        this.accessTokenId = accessTokenId;
        this.token = token;
        this.refreshToken = refreshToken;
        this.type = type;
        this.channelId = channelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccessToken that = (UserAccessToken) o;
        return Objects.equals(accessTokenId, that.accessTokenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessTokenId);
    }

    @Override
    public String toString() {
        return "generate.UserAccessToken[ accessTokenId=" + accessTokenId + " ]";
    }

}
