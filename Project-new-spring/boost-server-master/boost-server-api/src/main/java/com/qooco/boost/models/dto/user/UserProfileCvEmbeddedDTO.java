package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.enumeration.BoostHelperParticipant;
import com.qooco.boost.models.dto.CityDTO;
import com.qooco.boost.models.dto.CountryDTO;
import com.qooco.boost.models.dto.JobDTO;
import com.qooco.boost.models.dto.RoleCompanyDTO;
import com.qooco.boost.models.dto.company.CompanyBaseDTO;
import com.qooco.boost.models.dto.currency.CurrencyDTO;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.Date;
import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileCvEmbeddedDTO {
    private Long id;
    private Long userProfileCvId;
    private String firstName;
    private String lastName;
    private int gender;
    private String avatar;
    private String username;
    private String email;
    private Date birthday;
    private CountryDTO country;
    private CityDTO city;
    private boolean isHourSalary;
    private double minSalary;
    private double maxSalary;
    private boolean isFullTime;
    private String address;
    private String phone;
    private String nationalId;
    private List<String> personalPhotos;
    private CurrencyDTO currency;
    private List<JobDTO> jobs;
    private boolean hasPersonality;
    private int profileStrength;

    //For chatbot - boost helper
    private String description;
    //For recruiter user:
    CompanyBaseDTO company;
    RoleCompanyDTO role;

    public UserProfileCvEmbeddedDTO(UserProfileCvEmbedded embedded, String locale) {
        if (nonNull(embedded)) {
            this.id = embedded.getUserProfileId();
            this.userProfileCvId = (embedded.getUserProfileCvId());
            this.firstName = embedded.getFirstName();
            this.lastName = embedded.getLastName();
            this.gender = embedded.getGender();
            this.avatar = BoostHelperParticipant.getIds().contains(embedded.getUserProfileId())?
                    ServletUriUtils.getAbsoluteResourcePath(embedded.getAvatar())
                    : ServletUriUtils.getAbsolutePath(embedded.getAvatar());
            this.username = embedded.getUsername();
            this.email = embedded.getEmail();
            this.birthday = embedded.getBirthday();
            this.isHourSalary = embedded.isHourSalary();
            this.maxSalary = embedded.getMaxSalary();
            this.minSalary = embedded.getMinSalary();
            this.isFullTime = embedded.isFullTime();

            this.address = embedded.getAddress();
            this.phone = embedded.getPhone();
            this.nationalId = embedded.getNationalId();
            this.personalPhotos = embedded.getPersonalPhotos();
            this.hasPersonality = embedded.isHasPersonality();
            this.profileStrength = embedded.getProfileStrength();

            ofNullable(embedded.getCurrency()).ifPresent(it -> this.currency = new CurrencyDTO(it, locale));
            ofNullable(embedded.getCountry()).filter(it -> nonNull(it.getId())).ifPresent(it -> this.country = new CountryDTO(it, locale));
            ofNullable(embedded.getCity()).ifPresent(it -> this.city = new CityDTO(it, locale));
            ofNullable(embedded.getJobs()).filter(CollectionUtils::isNotEmpty).ifPresent(it -> this.jobs = it.stream().map(item -> new JobDTO(item, locale)).collect(toImmutableList()));

            if(embedded instanceof UserProfileCvMessageEmbedded){
                this.description = ((UserProfileCvMessageEmbedded)embedded).getDescription();
            }
        }
    }
}
