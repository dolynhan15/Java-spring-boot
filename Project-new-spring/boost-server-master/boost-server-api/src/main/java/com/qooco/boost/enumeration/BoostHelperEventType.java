package com.qooco.boost.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static com.qooco.boost.enumeration.MessageActionButton.*;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum BoostHelperEventType {
    //Profile type
    FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME(1, "boost_message_finish_basic_profile", new MessageActionButton[]{}),
    FINISH_BASIC_PROFILE_NOT_RETURN_APP_FOR_24_HOURS(2, "boost_message_should_finish_advanced_profile", new MessageActionButton[]{ADVANCE_PROFILE}),
    FINISH_ADVANCED_PROFILE_NOT_RETURN_APP_FOR_24_HOURS(3, "boost_message_should_finish_job_profile", new MessageActionButton[]{ADD_JOB_DATA}),
    FINISH_JOB_PROFILE_NOT_RETURN_APP_FOR_24_HOURS(4, "boost_message_should_finish_job_experience", new MessageActionButton[]{ADD_EXPERIENCE}),
    ADD_JOB_EXPERIENCE_NOT_RETURN_APP_FOR_24_HOURS(5, "boost_message_should_finish_personal_info", new MessageActionButton[]{ADD_PERSONAL_DATA}),
    FILL_PERSONAL_INFORMATION_NOT_RETURN_APP_FOR_24_HOURS(6, "boost_message_should_take_assessment", new MessageActionButton[]{GET_QUALIFICATION}),
    USER_QUALIFICATION_EXPIRES_IN_3_DAYS(7, "boost_message_qualification_expires_in_3days", new MessageActionButton[]{RENEW_QUALIFICATION}),
    BOOST_INTRODUCTION(8, "boost_message_introduction", new MessageActionButton[]{}),
    BOOST_SHARE_REFERRAL_CODE(9, "boost_message_share_referral_code", new MessageActionButton[]{SHARE_REFERRAL_CODE}),
    BOOST_REDEEM_REFERRAL_CODE(10, "boost_message_redeem_referral_code", new MessageActionButton[]{REDEEM_REFERRAL_CODE}),
    BOT_NOT_UNDERSTAND(11, "boost_message_bot_not_understand", new MessageActionButton[]{}),
    BOOST_FEEDBACK_DATA_SALARY_HIGH(12, "boost_message_data_salary_high", new MessageActionButton[]{SET_SALARY}),
    BOOST_FEEDBACK_DATA_NO_QUALIFICATION(13, "boost_message_no_qualification", new MessageActionButton[]{GET_QUALIFICATION}),
    BOOST_FEEDBACK_DATA_LOW_QUALIFICATION(14, "boost_message_low_qualification", new MessageActionButton[]{RENEW_QUALIFICATION}),
    BOOST_FEEDBACK_DATA_LOCATION_SUGGESTION(15, "boost_message_location_suggestion", new MessageActionButton[]{CHANGE_LOCATION}),
    BOOST_FEEDBACK_DATA_NOT_ENOUGH_EXPERIENCE(16, "boost_message_not_enough_experience", new MessageActionButton[]{ADD_EXPERIENCE_MANAGEMENT}),
    BOOST_FEEDBACK_CODES_SHARED(17, "boost_message_codes_shared", new MessageActionButton[]{SHARE_REFERRAL_CODE}),
    BOOST_FEEDBACK_DATA_SEATS_CLOSED(18, "boost_message_seats_closed", new MessageActionButton[]{}),
    BOT_DETECT_SUPPORT_KEYWORDS(19, "boost_message_detect_support_keywords", new MessageActionButton[]{}),

    //Select type
    SELECT_FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME(2000, "select_boost_message_finish_basic_profile", new MessageActionButton[]{}),
    BOOST_FEEDBACK_DATA_SEARCH_RANGE_TOO_SMALL(2001, "select_boost_message_search_range_too_small", new MessageActionButton[]{EDIT_RANGE}),
    BOOST_FEEDBACK_DATA_QUALIFICATION_TOO_HIGH(2002, "select_boost_message_qualification_too_high", new MessageActionButton[]{EDIT_VACANCY}),
    BOOST_FEEDBACK_DATA_QUALIFICATION_TOO_LOW(2003, "select_boost_message_qualification_too_low", new MessageActionButton[]{EDIT_VACANCY}),
    BOOST_FEEDBACK_DATA_SALARY_VACANCY_TOO_HIGH(2004, "select_boost_message_vacancy_salary_too_high", new MessageActionButton[]{EDIT_SALARY}),
    BOOST_FEEDBACK_DATA_SALARY_VACANCY_TOO_LOW(2005, "select_boost_message_vacancy_salary_too_low", new MessageActionButton[]{EDIT_SALARY}),
    ;

    private final int type;
    private final String messageKey;
    private final MessageActionButton[] actionButtons;

}
