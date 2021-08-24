package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
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
@Table(name = "USER_CURRICULUM_VITAE")
@SuperBuilder(toBuilder = true)
@FieldNameConstants
public class UserCurriculumVitae extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_CURRICULUM_VITAE_SEQUENCE")
    @SequenceGenerator(sequenceName = "USER_CURRICULUM_VITAE_SEQ", allocationSize = 1, name = "USER_CURRICULUM_VITAE_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "CURRICULUM_VITAE_ID")
    @Getter
    @Setter
    private Long curriculumVitaeId;

    @Basic(optional = false)
    @Column(name = "IS_HOUR_SALARY")
    @Getter
    @Setter
    private boolean isHourSalary;

    @Basic(optional = false)
    @Column(name = "MIN_SALARY", columnDefinition = "NUMBER")
    @Getter
    @Setter
    private double minSalary;

    @Basic(optional = false)
    @Column(name = "MAX_SALARY", columnDefinition = "NUMBER")
    @Getter
    @Setter
    private double maxSalary;

    @Basic(optional = false)
    @Column(name = "IS_ASAP")
    @Getter
    @Setter
    private boolean isAsap;

    @Basic(optional = false)
    @Column(name = "EXPECTED_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date expectedStartDate;

    @Basic(optional = false)
    @Column(name = "IS_FULL_TIME")
    @Getter
    @Setter
    private boolean isFullTime = true;

    @Size(max = 4000)
    @Column(name = "SOCIAL_LINKS")
    @Getter
    @Setter
    private String socialLinks;

    @Getter
    @Setter
    @JoinColumn(name = "USER_PROFILE_ID", referencedColumnName = "USER_PROFILE_ID")
    @ManyToOne(optional = false)
    private UserProfile userProfile;

    @Getter
    @Setter
    @JoinColumn(name = "CURRENCY_ID", referencedColumnName = "CURRENCY_ID")
    @ManyToOne()
    private Currency currency;

    @Getter
    @Setter
    @JoinColumn(name = "EDUCATION_ID", referencedColumnName = "EDUCATION_ID")
    @ManyToOne()
    private Education education;

    @Getter
    @Setter
    @OneToMany(mappedBy = "userCurriculumVitae", cascade = CascadeType.ALL)
    private List<UserDesiredHour> userDesiredHours;

    @Getter
    @Setter
    @OneToMany(mappedBy = "userCurriculumVitae", cascade = CascadeType.ALL)
    private List<UserBenefit> userBenefits;

    @Getter
    @Setter
    @OneToMany(mappedBy = "userCurriculumVitae", cascade = CascadeType.ALL)
    private List<CurriculumVitaeJob> curriculumVitaeJobs;

    @Getter
    @Setter
    @OneToMany(mappedBy = "userCurriculumVitae", cascade = CascadeType.ALL)
    private List<UserSoftSkill> userSoftSkills;

    @Getter
    @Setter
    @OneToMany(mappedBy = "userCurriculumVitae", cascade = CascadeType.ALL)
    private List<UserPreferredHotel> preferredHotels;

    @Getter
    @Setter
    @Column(name = "HAS_PERSONALITY")
    private boolean hasPersonality;

    public UserCurriculumVitae() {
        super();
    }

    public UserCurriculumVitae(long curriculumVitaeId) {
        this();
        this.curriculumVitaeId = curriculumVitaeId;
    }

    public UserCurriculumVitae(UserProfile userProfile) {
        super(userProfile.getUserProfileId());
        this.userProfile = userProfile;
    }

    public UserCurriculumVitae(UserProfile userProfile, Education education) {
        super(userProfile.getUserProfileId());
        this.userProfile = userProfile;
        this.education = education;
    }

    public UserCurriculumVitae(Long curriculumVitaeId, boolean isHourSalary,
                               double minSalary, double maxSalary, boolean isAsap, Date expectedStartDate,
                               boolean isFullTime, boolean isDeleted,
                               Long createdBy, Date createdDate, Long updatedBy, Date updatedDate) {
        super(createdDate, createdBy, updatedDate, updatedBy, isDeleted);

        this.curriculumVitaeId = curriculumVitaeId;
        this.isHourSalary = isHourSalary;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.isAsap = isAsap;
        this.expectedStartDate = expectedStartDate;
        this.isFullTime = isFullTime;
    }

    public UserCurriculumVitae(UserCurriculumVitae userCurriculumVitae) {
        if (Objects.nonNull(userCurriculumVitae)) {
            curriculumVitaeId = userCurriculumVitae.getCurriculumVitaeId();
            isHourSalary = userCurriculumVitae.isHourSalary();
            minSalary = userCurriculumVitae.getMinSalary();
            maxSalary = userCurriculumVitae.getMaxSalary();
            isAsap = userCurriculumVitae.isAsap();
            expectedStartDate = userCurriculumVitae.getExpectedStartDate();
            isFullTime = userCurriculumVitae.isFullTime();
            socialLinks = userCurriculumVitae.getSocialLinks();
            hasPersonality = userCurriculumVitae.isHasPersonality();
            ofNullable(userCurriculumVitae.getPreferredHotels()).filter(CollectionUtils::isNotEmpty)
                    .ifPresent(it -> preferredHotels = it.stream().map(UserPreferredHotel::new).collect(Collectors.toList()));
            ofNullable(userCurriculumVitae.getUserProfile()).ifPresent(it -> userProfile = new UserProfile(it));
            ofNullable(userCurriculumVitae.getCurrency()).ifPresent(it -> currency = new Currency(it));
            ofNullable(userCurriculumVitae.getEducation()).ifPresent(it -> education = new Education(it));
            ofNullable(userCurriculumVitae.getUserDesiredHours()).filter(CollectionUtils::isNotEmpty)
                    .ifPresent(it -> userDesiredHours = it.stream().map(UserDesiredHour::new).collect(Collectors.toList()));

            ofNullable(userCurriculumVitae.getUserBenefits()).filter(CollectionUtils::isNotEmpty)
                    .ifPresent(it -> userBenefits = it.stream().map(UserBenefit::new).collect(Collectors.toList()));

            ofNullable(userCurriculumVitae.getCurriculumVitaeJobs()).filter(CollectionUtils::isNotEmpty)
                    .ifPresent(it -> curriculumVitaeJobs = it.stream().map(CurriculumVitaeJob::new).collect(Collectors.toList()));

            ofNullable(userCurriculumVitae.getUserSoftSkills()).filter(CollectionUtils::isNotEmpty)
                    .ifPresent(it -> userSoftSkills = it.stream().map(UserSoftSkill::new).collect(Collectors.toList()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCurriculumVitae that = (UserCurriculumVitae) o;
        return Objects.equals(curriculumVitaeId, that.curriculumVitaeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(curriculumVitaeId);
    }
}
