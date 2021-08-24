package com.qooco.boost.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum MessageActionButton {
    //Profile
    ADVANCE_PROFILE(1, "btn_advance_profile", "Leads to an Advanced profile starting screen"),
    ADD_JOB_DATA(2, "btn_add_job_data", "Leads to Job profile starting screen"),
    ADD_EXPERIENCE(3, "btn_add_experience", "Leads to Previous experience starting screen"),
    ADD_PERSONAL_DATA(4, "btn_add_personal_data", "Leads to Personal information starting screen"),
    GET_QUALIFICATION(5, "btn_get_qualification", "Leads to an Boost Voice Assessments starting screen"),
    RENEW_QUALIFICATION(6, "btn_renew_qualification", "Leads to an Boost Voice Assessments starting screen"),
    SHARE_REFERRAL_CODE(7, "btn_share_referral_code", "Leads to a referral code sharing screen"),
    REDEEM_REFERRAL_CODE(8, "btn_redeem_referral_code", "Leads to a referral code redeem screen"),
    CHANGE_LOCATION(9, "btn_change_location", "Leads to Profile Management screen"),
    ADD_EXPERIENCE_MANAGEMENT(10, "btn_add_experience_management", "Leads to Profile Management screen"),
    SET_SALARY(11, "btn_set_salary", "Leads to Profile Management screen"),

    //Select
    EDIT_SALARY(1000, "btn_edit_salary", "Go to Edit vacancy screen"),
    EDIT_RANGE(1001, "btn_edit_range", "Go to Edit vacancy screen"),
    EDIT_VACANCY(1002, "btn_edit_vacancy", "Go to Edit vacancy screen"),
    ;

    @Getter @Accessors(fluent = true)
    private final int id;
    @Getter @Accessors(fluent = true)
    private final String btnName;
    private final String description;
}
