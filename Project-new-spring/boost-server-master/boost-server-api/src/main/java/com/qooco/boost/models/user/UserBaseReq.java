package com.qooco.boost.models.user;


import com.qooco.boost.data.enumeration.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class UserBaseReq extends UserProfileStep {

    private String firstName;
    private String lastName;
    private Date birthday;
    private Gender gender;
    private String avatar;
    private long[] nativeLanguageIds;

    private String phoneNumber;
    private String address;
    private Long countryId;

    private String nationalId;
    private List<String> personalPhotos;
}
