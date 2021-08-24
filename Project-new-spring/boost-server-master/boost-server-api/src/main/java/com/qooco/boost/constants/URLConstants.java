package com.qooco.boost.constants;

public class URLConstants {

//    For authorization service
    public static final String AUTH_PATH = "/auth";
    public static final String PATA_PATH = "/pata";
    public static final String USER_PATH =  PATA_PATH + "/user";
    public static final String LANGUAGE_PATH =  PATA_PATH + "/language";
    public static final String JOB_PATH =  PATA_PATH + "/job";
    public static final String COMPANY_PATH =  PATA_PATH + "/company";
    public static final String STAFF =  "/staff";
    public static final String LOCATION_PATH =  PATA_PATH + "/location";
    public static final String ASSESSMENT_PATH =  PATA_PATH + "/assessment";
    public static final String PERSONAL_ASSESSMENT_PATH =  PATA_PATH + "/personal-assessment";
    public static final String VACANCY_PATH =  PATA_PATH + "/vacancy";
    public static final String MESSAGE_PATH =  PATA_PATH + "/message";
    public static final String MESSAGE_CENTER_PATH =  PATA_PATH + "/message-center";
    public static final String APP_VERSION_PATH =  "/app-version";
    public static final String REFERRAL = "/referral";
    public static final String REFERRAL_PATH = PATA_PATH + REFERRAL;
    public static final String STATISTIC_PATH = PATA_PATH + "/statistic";
    public static final String APPOINTMENT_PATH =  PATA_PATH + "/appointment";
    public static final String ATTRIBUTE_PATH =  PATA_PATH + "/attribute";
    public static final String ADMIN_PATH = PATA_PATH + "/admin";
    public static final String USER_ACCESS_TOKEN_PATH = PATA_PATH + USER_PATH + "/access-token";
    public static final String CUSTOMER_CARE_PATH = PATA_PATH + "/customer-care";

    public static final String UPDATE_PARTICIPANT_CONVERSATION =  "/update-participant-conversation";
    public static final String BOOST_HELPER = "/boost-helper";

    public static final String UPDATE_CLIENT_INFO = "/update-client-info";

    //Common API
    public static final String COUNTRY_PATH =  "/country";
    public static final String PROVINCE_PATH =  "/province";
    public static final String CITY_PATH =  "/city";
    public static final String BENEFIT_PATH =  "/benefit";
    public static final String CURRENCY_PATH =  "/currency";
    public static final String EDUCATION_PATH =  "/education";
    public static final String SOFT_SKILL_PATH =  "/soft-skills";
    public static final String WORKING_HOUR_PATH =  "/working-hour";
    public static final String HOTEL_TYPE_PATH =  "/hotel-type";

    //Admin
    public static final String ADMIN_SOCKET_CONNECTION_PATH = "/socket-connection";
    public static final String ADMIN_SYSTEM_LOGGER_PATH = "/system-logger";
    public static final String ADMIN_SYSTEM_PATCH_APPOINTMENT_DETAIL = "/patch-appointment-detail-doc";
    public static final String ADMIN_SYSTEM_PATCH_CLONE_USER = "/clone-user";
    public static final String ADMIN_SYSTEM_PATCH_VACANCY_SEAT = "/patch-vacancy-seat";
    public static final String ADMIN_SYSTEM_PATCH_LOCALE_DATA = "/patch-local-data";
    public static final String ADMIN_SYSTEM_GENERATE_DEMO_DATA = "/generate-demo-data";
    public static final String ADMIN_SYSTEM_SEND_FEEDBACK_MESSAGE = "/send-data-feedback-message";
    public static final String ADMIN_SYSTEM_PATCH_LOCALIZATION = "/patch-localization";


    // Sub path
    public static final String USER_PROFILE_PATH = "/user-profile";
    public static final String USER_CV_PATH = "/user-cv";
    public static final String EXPORT_PDF = "/export-pdf";
    public static final String ID_PATH = "/{id:\\d+}";
    public static final String ID = "/{id}";
    public static final String NAME = "/{name}";
    public static final String CODE = "/{code}";
    public static final String BOOST_API = "/api";

    //User for Fit App
    public static final String FIT_USER_PATH = "/fit-user";

    // Sub path for user previous postion profile
    public static final String USER_PREVIOUS_POSITION_PATH = "/previous-position";

    // Login methods
    public static final String LOGIN_PATH = "/login";
    public static final String LOGOUT_PATH = "/logout";
    public static final String SOCIAL_LOGIN_PATH = "/social-login";
    public static final String FORGOT_PASSWORD_PATH = "/forgot-password";
    public static final String VERSION_2 = "/v2";

    // Channel Id method
    public static final String UPDATE_CHANNEL_ID_PATH = "/update-channel-id";

    // Signup methods
    public static final String GENERATE_CODE_PATH = "/generate-code";
    public static final String SIGN_UP_WITH_SYSTEM = "/sign-up-system";
    public static final String VERIFY_CODE_PATH = "/verify-code";

    //The methods for profile
    public static final String GET_METHOD = "/get";
    public static final String SAVE_METHOD = "/save";
    public static final String DELETE_METHOD = "/delete";

    public static final String GET_RECRUITER_INFO_METHOD = "/get-recruiter-info";
    public static final String GET_CAREER_INFO_METHOD = "/get-career-info";
    public static final String USER_WALLET_METHOD = "/user-wallet";
    public static final String USER_PUBLIC_KEY = "/user-public-key";
    public static final String USER_PROFILE_UPLOAD = "/user-profile_upload";
    public static final String SYNC_USER_CV = "/sync-user-cv";

    //Steps
    public static final String ADVANCED_PROFILE = "/advanced-profile";
    public static final String BASIC_PROFILE = "/basic-profile";
    public static final String PERSONAL_INFORMATION = "/personal-information";
    public static final String JOB_PROFILE = "/job-profile";
    public static final String STEP_PROFILE = "/step-profile";

    //Media API
    public static final String MEDIA_PATH = PATA_PATH +  "/media";
    public static final String UPLOAD_FILE_PATH = "/upload-file";
    public static final String UPLOAD_MULTIPLE_FILE_PATH = "/upload-multiple-file";
    public static final String DELETE_FILE_PATH = "/delete-file";

    //Download File
    public static final String UPLOAD_DIR_PATH = "/uploads";
    public static final String DOWNLOAD_IMAGE_PATH = UPLOAD_DIR_PATH;

    //Company methods
    public static final String APPROVAL_REQUEST = "/approval" + ID_PATH;
    public static final String GET_BY_STATUS = "/get-by-status";
    public static final String JOIN_REQUEST = "/join-company-request" + ID_PATH;
    public static final String ROLE_AUTHORIZED = "/role-authorize";
    public static final String ACTIVE_TIMES = "/active-times";

    @Deprecated
    public static final String GET_SHORT_COMPANY = "/get-short-company";
    public static final String SHORT_INFORMATION = "/short-information";
    public static final String UPDATE_JOIN_REQUEST = "/update-join-company-request";
    public static final String GET_JOIN_COMPANY_REQUEST = "/get-join-company-request";
    public static final String GET_BY_ROLE_COMPANY = "/get-by-role-company";

    public static final String SWITCH_COMPANY = "/switch-company";
    public static final String WORKING_COMPANY = "/working-company";

    // Job methods
    public static final String BY_COMPANY_PATH = "/get-by-company";

    //Get companies of user
    public static final String GET_COMPANY_OF_USER = "/get-companies-of-user";

    // Search company by name
    public static final String FIND_APPROVED_COMPANY_BY_COUNTRY = "/find-approved-company-by-country";
    public static final String SEARCH_COMPANY_BY_NAME = "/search-company-by-name";
    public static final String SEARCH_COMPANY_BY_NAME_FOR_JOIN_COMPANY = "/search-company-by-name-for-joining-company";

    // Matching candidate for vacancy
    public static final String MATCH_CANDIDATES = "/match-candidates";
    public static final String MATCH_CANDIDATES_COMPARE = "/matching";
    public static final String CERTIFICATE = "/certificate";

    //The methods for vacancy
    public static final String GET_VACANCIES_OF_COMPANY = "/get-vacancies-of-company";
    public static final String CLASSIFY_CANDIDATE_FOR_VACANCY = "/classify-candidate";
    public static final String CANDIDATE_OF_VACANCY = "/candidate";
    public static final String SUSPEND_VACANCY = "/suspend";
    public static final String CLOSE = "/close";
    public static final String DECLINE = "/decline";
    public static final String CANDIDATE_ID = "/{candidateId:\\d+}";
    public static final String RESTORE_VACANCY = "/restore";

    // Conversation
    public static final String CAREER_PATH = "/career";
    public static final String RECRUITER_PATH = "/recruiter";
    public static final String PROFILE_PATH = "/profile";

    //The methods for message
    public static final String CONVERSATION = "/conversation";
    public static final String APPLICANT_RESPONSE = "/applicant-response";
    public static final String APPOINTMENT_RESPONSE = "/appointment-response";
    public static final String JOIN_COMPANY_RESPONSE = "/join-company-response";
    public static final String ASSIGN_ROLE = "/assign-role";
    public static final String SEND_FILE = "/send-file";
    public static final String CHAT_TEXT = "/chat-text";
    public static final String CHAT_BOOST_HELPER = "/chat-boost-helper";
    public static final String TEMPLATE_MESSAGE = "/template";

    //The methods for referral
    public static final String COUNT_REDEEM = "/count-redeem";
    public static final String REDEEM_CODE = "/redeem-code";
    public static final String GENERATE_CODE = "/generate-code";
    public static final String CLAIM_FREE_ASSESSMENT = "/claim-free-assessment";
    public static final String SYNC_DATA_GIFT = "/sync-data-gift";
    public static final String GIFTS = "/gifts";
    public static final String OWNED_GIFTS = "/owned-gifts";
    public static final String CLAIM_GIFT = "/claim-gift";
    public static final String USER_COINS = "/user-coins";
    public static final String SHARE_CODE = "/share-code";

    //The methods for assessment
    public static final String ASSESSMENT_QUALIFICATION = "/qualification";
    public static final String ASSESSMENT_QUALIFICATION_HOMEPAGE = "/qualification-homepage";
    public static final String ASSESSMENT_TEST_HISTORY = "/test-history";
    public static final String ASSESSMENT_SYNC_DATA_USER = "/sync-data-user";
    public static final String SYNC_DATA = "/sync-data";

    //The methods for statistic
    public static final String VIEW_PROFILE = "/view-profile";
    public static final String COMPANY_DASHBOARD = "/company-dashboard";
    public static final String EMPLOYEE_DASHBOARD = "/employee-dashboard";

    //The methods for statistic
    public static final String TEST_RESULT = "/test-result";

    //The methods for appointment
    public static final String APPOINTMENT = "/appointment";
    public static final String EVENTS = "/events";
    public static final String PROFILE_EVENTS = "/profile-events";
    public static final String TIME = "/time";
    public static final String COUNT = "/count";
    public static final String REMOVE = "/remove";
    public static final String MOVE_FROM_DURATION = "/move-from-duration";
    public static final String MOVE = "/move";
    public static final String CALENDAR = "/fit-calendar";
    public static final String DURATION = "/duration";
    public static final String REMIND = "/remind";
    public static final String MANAGE = "/manage";
    public static final String HAS_EXPIRED = "/has-expired";
    public static final String EXPIRED = "/expired";

    //The methods for attribute
    public static final String ATTRIBUTE_LEVEL_UP = "/level-up";
    public static final String ATTRIBUTE_OPENED_ALL_MENU = "/opened-all-menu";

    //The methods for Boost Helper
    public static final String RASA = "/rasa";

    //The methods for Customer Care
    public static final String SUPPORT_CHANNEL = "/support-channel";
    public static final String ARCHIVE = "/archive";
    public static final String RESTORE = "/restore";
}
