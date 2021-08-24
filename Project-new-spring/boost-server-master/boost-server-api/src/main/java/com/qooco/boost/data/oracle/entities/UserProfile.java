package com.qooco.boost.data.oracle.entities;

import com.qooco.boost.data.enumeration.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Entity
@Table(name = "USER_PROFILE")
@Setter
@Getter
@FieldNameConstants
public class UserProfile extends BaseEntity implements Serializable {

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "USER_PROFILE_ID")
    private Long userProfileId;

    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "FIRST_NAME", columnDefinition = "NVARCHAR2")
    private String firstName;

    @Size(max = 255)
    @Column(name = "LAST_NAME", columnDefinition = "NVARCHAR2")
    private String lastName;

    @Column(name = "GENDER")
    private Gender gender;

    @Size(max = 2000)
    @Column(name = "AVATAR", columnDefinition = "NVARCHAR2")
    private String avatar;

    @Column(name = "BIRTHDAY")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;

    @Size(max = 255)
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Size(max = 255)
    @Column(name = "ADDRESS", columnDefinition = "NVARCHAR2")
    private String address;

    @Size(max = 255)
    @Column(name = "NATIONAL_ID")
    private String nationalId;

    @Lob
    @Column(name = "PERSONAL_PHOTO")
    private String personalPhotos;

    @Basic(optional = false)
    @NotNull
    @Column(name = "USERNAME")
    private String username;

    @Size(max = 255)
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "IS_ADMIN")
    private boolean isAdmin;

    @JoinColumn(name = "COUNTRY_ID", referencedColumnName = "COUNTRY_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    private Country country;

    @JoinColumn(name = "CITY_ID", referencedColumnName = "CITY_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    private City city;

    @Column(name = "PROFILE_STEP")
    private Integer profileStep;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userProfile")
    private List<UserLanguage> userLanguageList;

    public UserProfile() {
        super();
    }

    public UserProfile(Long userProfileId) {
        super();
        this.userProfileId = userProfileId;
    }

    public UserProfile(Long userProfileId, boolean isAdmin) {
        super();
        this.userProfileId = userProfileId;
        this.isAdmin = isAdmin;
    }

    public UserProfile(Long userProfileId, String username) {
        super();
        this.userProfileId = userProfileId;
        this.username = username;
    }

    public UserProfile(Long userProfileId, String username, String email) {
        super();
        this.userProfileId = userProfileId;
        this.username = username;
        this.email = email;
    }

    public UserProfile(UserProfile userProfile) {
        if (Objects.nonNull(userProfile)) {
            this.userProfileId = userProfile.getUserProfileId();
            this.firstName = userProfile.getFirstName();
            this.lastName = userProfile.getLastName();
            this.gender = userProfile.getGender();
            this.avatar = userProfile.getAvatar();
            this.birthday = userProfile.getBirthday();
            this.phoneNumber = userProfile.getPhoneNumber();
            this.address = userProfile.getAddress();
            this.nationalId = userProfile.getNationalId();
            this.personalPhotos = userProfile.getPersonalPhotos();
            this.username = userProfile.getUsername();
            this.email = userProfile.getEmail();
            this.isAdmin = userProfile.isAdmin();
            this.profileStep = userProfile.getProfileStep();
            ofNullable(userProfile.getCountry()).ifPresent(it -> this.country = new Country(it));
            ofNullable(userProfile.getCity()).ifPresent(it -> this.city = new City(it));
            if (CollectionUtils.isNotEmpty(userProfile.getUserLanguageList())) {
                this.userLanguageList = userProfile.getUserLanguageList().stream().map(UserLanguage::new).collect(Collectors.toList());
            }
        }
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userProfileId != null ? userProfileId.hashCode() : 0);
        return hash;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(userProfileId, that.userProfileId);
    }

    @Override
    public String toString() {
        return username;
    }
}
