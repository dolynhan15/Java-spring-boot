package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "USER_LANGUAGE")
@Setter @Getter
public class UserLanguage extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_LANGUAGE_SEQUENCE")
    @SequenceGenerator(sequenceName = "USER_LANGUAGE_SEQ", allocationSize = 1, name = "USER_LANGUAGE_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "USER_LANGUAGE_ID", nullable = false)
    private Long id;


    @Basic(optional = false)
    @Column(name = "IS_NATIVE", nullable = false)
    private boolean isNative;

    @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "LANGUAGE_ID")
    @ManyToOne(optional = false)
    private Language language;

    @JoinColumn(name = "USER_PROFILE_ID", referencedColumnName = "USER_PROFILE_ID")
    @ManyToOne(optional = false)
    private UserProfile userProfile;

    public UserLanguage() {
        super();
    }

    public UserLanguage(Long id) {
        this.id = id;
    }

    public UserLanguage(Boolean isNative, Language language, UserProfile userProfile) {
        super(userProfile.getUserProfileId());
        this.isNative = isNative;
        this.language = language;
        this.userProfile = userProfile;
    }

    public UserLanguage(Boolean isNative, Language language, UserProfile userProfile, Long ownerId) {
        super(ownerId);
        this.isNative = isNative;
        this.language = language;
        this.userProfile = userProfile;
    }

    public UserLanguage(UserLanguage userLanguage) {
        super(userLanguage.getCreatedBy());
        this.isNative = userLanguage.isNative();
        this.language = userLanguage.getLanguage();
        this.userProfile = userLanguage.getUserProfile();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLanguage that = (UserLanguage) o;
        return isNative == that.isNative &&
                Objects.equals(language, that.language) &&
                Objects.equals(userProfile, that.userProfile);
    }

    @Override
    public int hashCode() {

        return Objects.hash(isNative, language, userProfile);
    }

    @Override
    public String toString() {
        return userProfile.toString() + ": " + language.toString();
    }

}