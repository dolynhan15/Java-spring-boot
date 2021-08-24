package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileEmbedded;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.models.dto.CityDTO;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortUserDTO extends BaseUserDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private String nationalId;
    private List<String> personalPhotos;

    private String address;
    private CityDTO city;

    private void setShortUserDTO(String firstName, String lastName, String phone, String nationalId, List<String> personalPhotoRelatePath, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.nationalId = nationalId;
        this.personalPhotos = ServletUriUtils.getAbsolutePaths(personalPhotoRelatePath);
        this.address = address;
    }

    public ShortUserDTO(UserProfile userProfile, String locale) {
        super(userProfile);
        if (Objects.nonNull(userProfile)) {
            setShortUserDTO(userProfile.getFirstName(),
                    userProfile.getLastName(),
                    userProfile.getPhoneNumber(),
                    userProfile.getNationalId(),
                    StringUtil.convertToList(userProfile.getPersonalPhotos()),
                    userProfile.getAddress()
            );
            this.city =  Objects.nonNull(userProfile.getCity()) ? new CityDTO(userProfile.getCity(), locale) : null;
        }
    }

    public ShortUserDTO(UserFit userFit) {
        super(userFit);

        if (Objects.nonNull(userFit)) {
            setShortUserDTO(userFit.getFirstName(),
                    userFit.getLastName(),
                    userFit.getPhoneNumber(),
                    userFit.getNationalId(),
                    StringUtil.convertToList(userFit.getPersonalPhotos()),
                    userFit.getAddress()
            );
        }
    }

    public ShortUserDTO(UserProfileEmbedded userProfile, String locale) {
        super(userProfile);
        if (Objects.nonNull(userProfile)) {
            setShortUserDTO(userProfile.getFirstName(),
                    userProfile.getLastName(),
                    userProfile.getPhone(),
                    userProfile.getNationalId(),
                    userProfile.getPersonalPhotos(),
                    userProfile.getAddress()
            );
            this.city =  Objects.nonNull(userProfile.getCity()) ? new CityDTO(userProfile.getCity(), locale) : null;
        }
    }

    public ShortUserDTO(UserProfileCvEmbedded userProfile, String locale) {
        super(userProfile);
        if (Objects.nonNull(userProfile)) {
            setShortUserDTO(userProfile.getFirstName(),
                    userProfile.getLastName(),
                    userProfile.getPhone(),
                    userProfile.getNationalId(),
                    userProfile.getPersonalPhotos(),
                    userProfile.getAddress()
            );
            this.city =  Objects.nonNull(userProfile.getCity()) ? new CityDTO(userProfile.getCity(), locale) : null;
        }
    }

}
