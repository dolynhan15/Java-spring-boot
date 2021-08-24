package com.qooco.boost.threads.models;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class DataFeedback {

    public static final String SCHEDULE_DATA_FEEDBACK_PROFILE = "SCHEDULE_DATA_FEEDBACK_PROFILE";
    public static final String SCHEDULE_DATA_FEEDBACK_SELECT = "SCHEDULE_DATA_FEEDBACK_SELECT";
    public static final String SCHEDULE_THREE_DAYS_DATA_FEEDBACK_SELECT = "SCHEDULE_THREE_DAYS_DATA_FEEDBACK_SELECT";

    // PROFILE feedback
    public static final int PROFILE_SALARY_TOO_HIGH = 1;
    public static final int PROFILE_NO_QUALIFICATION = 2;
    public static final int PROFILE_LOW_QUALIFICATION = 3;
    public static final int PROFILE_LOCATION_CHANGE = 4;
    public static final int PROFILE_NOT_ENOUGH_EXPERIENCE = 5;
    public static final int PROFILE_CODES_SHARED = 6;
    public static final int PROFILE_SEATS_CLOSED = 7;

    // FIT feedback
    public static final int SELECT_SALARY_TOO_LOW = 1000;
    public static final int SELECT_SALARY_TOO_HIGH = 1001;
    public static final int SELECT_SEARCH_RANGE_TOO_SMALL = 1002;
    public static final int SELECT_QUALIFICATION_TOO_HIGH = 1003;
    public static final int SELECT_QUALIFICATION_TOO_LOW = 1004;

    public static List<Integer> EVENT_BASE_ON_VACANCY = ImmutableList.of(
            SELECT_SEARCH_RANGE_TOO_SMALL,
            SELECT_QUALIFICATION_TOO_HIGH,
            SELECT_QUALIFICATION_TOO_LOW,
            SELECT_SALARY_TOO_HIGH,
            SELECT_SALARY_TOO_LOW,
            PROFILE_NO_QUALIFICATION,
            PROFILE_NOT_ENOUGH_EXPERIENCE,
            PROFILE_SALARY_TOO_HIGH);

    public static List<Integer>  EVENT_BASE_ON_USER_PROFILE = ImmutableList.of(
            PROFILE_LOW_QUALIFICATION,
            PROFILE_LOCATION_CHANGE,
            PROFILE_SEATS_CLOSED,
            PROFILE_CODES_SHARED);

    public static final int MIN_SIMILAR_PROFILE = 5;
    public static final float ONE_VALUE = 1.0f;
    public static final float MIN_SIMILAR_PROFILE_RATE = 0.7f;
    public static final float LOWER_RATE = 1.1f;
    public static final float HIGHER_RATE = 0.9f;

    public static final Integer MIN_VACANCY_NUMBER = 2;
    public static final Integer MIN_USER_HAS_QUALIFICATION = 2;
    public static final Integer MIN_CLOSED_SEAT_NUMBER = 2;
    public static final Integer FIVE_SHARE_TIMES = 5;
    public static final Integer MIN_SHARED_CODES = 5;
    public static final int USER_ID_INDEX = 0;
    public static final int PROVINCE_ID_INDEX = 1;
    public static final int REFERRAL_CODE_INDEX = 2;
    public static final int CREATED_DATE_INDEX = 3;
    public static final int STRONG_PROFILE = 4;

    private int feedbackType;
}
