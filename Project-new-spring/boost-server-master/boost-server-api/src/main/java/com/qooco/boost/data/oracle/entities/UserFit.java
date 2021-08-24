package com.qooco.boost.data.oracle.entities;

import com.google.common.collect.Lists;
import com.qooco.boost.data.enumeration.Gender;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "USER_PROFILE")
@Setter
@Getter
public class UserFit extends BaseEntity implements Serializable {

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "USER_PROFILE_ID")
    private Long userProfileId;

    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "FIT_FIRST_NAME", columnDefinition = "NVARCHAR2")
    private String firstName;

    @Size(max = 255)
    @Column(name = "FIT_LAST_NAME", columnDefinition = "NVARCHAR2")
    private String lastName;

    @Column(name = "FIT_GENDER")
    private Gender gender;

    @Size(max = 2000)
    @Column(name = "FIT_AVATAR", columnDefinition = "NVARCHAR2")
    private String avatar;

    @Column(name = "FIT_BIRTHDAY")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;

    @Size(max = 255)
    @Column(name = "FIT_PHONE_NUMBER")
    private String phoneNumber;

    @Size(max = 255)
    @Column(name = "FIT_ADDRESS", columnDefinition = "NVARCHAR2")
    private String address;

    @Size(max = 255)
    @Column(name = "FIT_NATIONAL_ID")
    private String nationalId;

    @Lob
    @Column(name = "FIT_PERSONAL_PHOTO")
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

    @JoinColumn(name = "DEFAULT_COMPANY", referencedColumnName = "COMPANY_ID", updatable = false, insertable = false)
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private Company defaultCompany;

    @JoinColumn(name = "COUNTRY_ID", referencedColumnName = "COUNTRY_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    private Country country;

    @Column(name = "FIT_PROFILE_STEP")
    private Integer profileStep;

    @Column(name = "DEFAULT_COMPANY")
    private Long defaultCompanyId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userFit", fetch = FetchType.EAGER)
    private List<UserFitLanguage> userFitLanguages;

    public UserFit() {
        super();
    }

    public UserFit(Long userProfileId) {
        super();
        this.userProfileId = userProfileId;
    }

    public UserFit(Long userProfileId, Long companyId) {
        this(userProfileId);
        this.defaultCompanyId = companyId;
    }

    public UserFit(Long userProfileId, String username) {
        super();
        this.userProfileId = userProfileId;
        this.username = username;
    }

    public UserFit(Long userProfileId, String username, String email) {
        super();
        this.userProfileId = userProfileId;
        this.username = username;
        this.email = email;
    }

    public UserFit(UserFit userFit) {
        if (Objects.nonNull(userFit)) {
            this.userProfileId = userFit.getUserProfileId();
            this.firstName = userFit.getFirstName();
            this.lastName = userFit.getLastName();
            this.gender = userFit.getGender();
            this.avatar = userFit.getAvatar();
            this.birthday = userFit.getBirthday();
            this.phoneNumber = userFit.getPhoneNumber();
            this.address = userFit.getAddress();
            this.nationalId = userFit.getNationalId();
            this.personalPhotos = userFit.getPersonalPhotos();
            this.username = userFit.getUsername();
            this.email = userFit.getEmail();
            this.isAdmin = userFit.isAdmin();
            if (Objects.nonNull(userFit.getDefaultCompany())) {
                this.defaultCompany = new Company(userFit.getDefaultCompany());
                this.defaultCompanyId = userFit.getDefaultCompany().getCompanyId();
            }
            if (Objects.nonNull(userFit.getCountry())) {
                this.country = new Country(userFit.getCountry());
            }
            this.profileStep = userFit.getProfileStep();
            if (CollectionUtils.isNotEmpty(userFit.getUserFitLanguages())) {
                this.userFitLanguages = Lists.newArrayList(userFit.getUserFitLanguages());
            }
            setUpdatedBy(userFit.getUserProfileId());
            setCreatedBy(userFit.getUserProfileId());
            setUpdatedDate(DateUtils.toUtcForOracle(userFit.getUpdatedDate()));
            setCreatedDate(DateUtils.toUtcForOracle(userFit.getCreatedDate()));
            setIsDeleted(false);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFit userFit = (UserFit) o;
        return Objects.equals(userProfileId, userFit.userProfileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userProfileId);
    }

    @Override
    public String toString() {
        return username;
    }
}
