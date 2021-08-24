package com.qooco.boost.models.user;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.user.UserCurriculumVitaeDTO;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/3/2018 - 10:03 AM
*/
public class UserCurriculumVitaeResp extends BaseResp {

    private UserCurriculumVitaeDTO userCurriculumVitae;

    public UserCurriculumVitaeDTO getUserCurriculumVitae() {
        return userCurriculumVitae;
    }

    public void setUserCurriculumVitae(UserCurriculumVitaeDTO userCurriculumVitae) {
        this.userCurriculumVitae = userCurriculumVitae;
    }
}
