package com.qooco.boost.constants;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 6/19/2018 - 5:27 PM
*/
public final class QoocoApiConstants {
    public static final String REPORT_DATA = "report=[REQUEST_BODY]";
    public static final String REPLACEMENT_REQUEST_BODY = "[REQUEST_BODY]";
    public static final String SPID_DATA = "{SPID}";
    public static final String USER_ID_DATA = "{UserId}";
    public static final String CONTACT_DATA = "{userContactData}";
    public static final String EMAIL_DATA = "{emailData}";
    public static final String USERNAME_DATA = "{usernameData}";
    public static final String LESSON_ID_DATA = "{lessonId}";
    public static final String SIGN_ID_DATA = "{signId}";
    public static final String LOCALE_US = "en_US";

    public static final String OWNED_PACKAGES_TIMESTAMP = "&ownedPackagesTimestamp=";
    public static final String LEVEL_TEST_SCALES_TIMESTAMP = "&levelTestScalesTimestamp=";
    public static final String REPORT = "&report=";

    public static final String DEFAULT_USER_ID = "1";

    public static final int OK_CODE = 0;
    public static final int NETWORK_ERROR_CODE = 9;

    public static final int VERIFICATION_CODE_SERVER_ERROR = 1; //Server Internal Error(we caught an exception)

    public static final int VERIFICATION_CODE_NOT_MATCH = 2; // 2 - Validation code not match

    public static final int VERIFICATION_CODE_EXPIRED = 3; // 3 - Validation code is expired

    public static final int VERIFICATION_CODE_NOT_FOUND = 6; // 6 - Verification code not found

    public static final int FORGOT_PASSWORD_UNKNOWN_CODE = 4;

    public static final int FORGOT_PASSWORD_USER_NOT_EXIST_CODE = 3;

    public static final int FORGOT_PASSWORD_NOT_FOUND_CODE = 7;

    public static final int FORGOT_PASSWORD_USER_NAME_NOT_FOUND = 9;

    public static final int GET_CODE_INTERNAL_SERVER_ERROR = 1;
    public static final int GET_CODE_FAILED_TO_SEND = 4;
    public static final int GET_CODE_NETWORK_ERROR = 9;
    public static final int GET_CODE_EMAIL_IS_ALREADY_USED = 5;
    public static final int GET_CODE_MOBILE_NUM_FORMAT_INCORRECT = 8;

    public static final int SIGN_UP_EXISTING_USERNAME = 1;
    public static final int SIGN_UP_USER_NOT_CREATE_SUCCESS = 2;

    public static final int LOGIN_SUCCESS = 0;
    public static final int LOGIN_USERNAME_ERROR = 1;
    public static final int LOGIN_PASSWORD_ERROR = 2;
    public static final int LOGIN_USERNAME_FOUND_ON_EXTERNAL_SERVER_ERROR = 3;
    public static final int LOGIN_SERVER_ERROR = 10;

    public static final int SIGN_UP_EXISTED_EMAIL = 4;

    //Referral Assessments
    public static final int CLAIM_SUCCESS = 0;
    public static final int CLAIM_SERVER_ERROR = 1;
    public static final int CLAIM_CHECK_SUM_NOT_MATCH = 2;
    public static final int CLAIM_USER_ID_NOT_EXIST = 3;
    public static final int CLAIM_LESSON_ID_NOT_EXIST = 4;

    public static final String LOCALE_EN_US = "en_US";
    public static final String LOCALE_ZH_CN = "zh_CN";
    public static final String LOCALE_ZH_TW = "zh_TW";
    public static final String LOCALE_DE_DE = "de_DE";
    public static final String LOCALE_ES_MX = "es_MX";
    public static final String LOCALE_ID_ID = "id_ID";
    public static final String LOCALE_JA_JP = "ja_JP";
    public static final String LOCALE_KM_KH = "km_KH";
    public static final String LOCALE_MS_MY = "ms_MY";
    public static final String LOCALE_RU_RU = "ru_RU";
    public static final String LOCALE_TH_TH = "th_TH";
    public static final String LOCALE_VI_VN = "vi_VN";
    public static final String LOCALE_KO_KR = "ko_KR";
    public static final String LOCALE_FR_FR = "fr_FR";
    public static final String LOCALE_PT_BR = "pt_BR";
    public static final String LOCALE_AR_SA = "ar_SA";
    public static final String LOCALE_EN_GB = "en_GB";
    public static final String LOCALE_MY_MM = "my_MM";

    public static final String SYNC_FORCE = "_syncForce";

    public static final String SYNC_LEVEL_TESTS = "levelTests";
    public static final String GET_LEVEL_TESTS = "getLevelTests";
    public static final String GET_LEVEL_TEST_WIZARDS = "getLevelTestWizards";
    public static final String GET_LEVEL_TEST_SCALES = "getLevelTestScales";
    public static final String GET_LEVEL_TEST_DATA = "getLevelTestData";
//    public static final String GET_OWNED_PACKAGES = "getOwnedPackages";

    public static final String LEVEL_TEST_NAME = "levelTestName";
    public static final String WIZARD_NAME = "wizardName";
    public static final String WIZARD_ANSWER_NAME = "wizardAnswerName";
    public static final String TIMESTAMP = "timestamp";
    public static final String PLATFORM_BOOST = "boost";
}
