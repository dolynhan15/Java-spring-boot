package com.qooco.boost.constants;
/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 6/20/2018 - 3:17 PM
 */

public class StatusConstants {
    /*==================Code and Message Generall ==========================*/
    public static final int SUCCESS = 1;
    public static final String SUCCESS_MESSAGE = "Success";

    public static final int INTERNAL_SERVER_ERROR = 2;
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";
    public static final String QOOCO_SERVER_ERROR_MESSAGE = "External server error";

    public static final int NETWORK_ERROR = 3;
    public static final String NETWORK_ERROR_MESSAGE = "Network is error";

    public static final int LOGIN_UNAUTHORIZED = 4;
    public static final String LOGIN_UNAUTHORIZED_MESSAGE = "Unauthorized";

    public static final int NOT_FOUND = 5;
    public static final String NOT_FOUND_MESSAGE = "Not found";

    public static final int BAD_REQUEST = 6;
    public static final String BAD_REQUEST_MESSAGE = "Bad request";

    public static final int TOKEN_MISSING = 7;
    public static final String TOKEN_MISSING_MESSAGE = "Token is missing on header";

    public static final int NOT_FOUND_CURRENCY = 8;
    public static final String NOT_FOUND_CURRENCY_MESSAGE = "Not found currency";

    public static final int NOT_FOUND_EDUCATION = 9;
    public static final String NOT_FOUND_EDUCATION_MESSAGE = "Not found education";

    public static final int NOT_FOUND_USER_PROFILE = 10;
    public static final String NOT_FOUND_USER_PROFILE_MESSAGE = "Not found user";

    public static final int NOT_FOUND_JOB = 11;
    public static final String NOT_FOUND_JOB_MESSAGE = "Not found job";

    public static final int NOT_FOUND_WORKING_HOUR = 12;
    public static final String NOT_FOUND_WORKING_HOUR_MESSAGE = "Not found working hour";

    public static final int INVALID_PAGINATION = 13;
    public static final String INVALID_PAGINATION_MESSAGE = "Invalid pagination";

    public static final int NOT_FOUND_SOFT_SKILL = 14;
    public static final String NOT_FOUND_SOFT_SKILL_MESSAGE = "Not found soft skill";

    public static final int NOT_FOUND_BENEFIT = 15;
    public static final String NOT_FOUND_BENEFIT_MESSAGE = "Not found benefit";

    public static final int NOT_FOUND_USER_PREVIOUS_JOB = 16;
    public static final String NOT_FOUND_USER_PREVIOUS_JOB_MESSAGE = "Not found user previous position";

    public static final int HTTP_OR_HTTPS_WRONG_FORMAT = 17;
    public static final String HTTP_OR_HTTPS_WRONG_FORMAT_MESSAGE = "The link is wrong format (http or https)";

    public static final int NOT_FOUND_COUNTRY = 18;
    public static final String NOT_FOUND_COUNTRY_MESSAGE = "Not found country";

    public static final int NOT_FOUND_CITY = 19;
    public static final String NOT_FOUND_CITY_MESSAGE = "Not found city";

    public static final int NOT_FOUND_HOTEL_TYPE = 20;
    public static final String NOT_FOUND_HOTEL_TYPE_MESSAGE = "Not found hotel type";

    public static final int NOT_FOUND_COMPANY = 21;
    public static final String NOT_FOUND_COMPANY_MESSAGE = "Not found company";

    public static final int NOT_FOUND_STAFF = 22;
    public static final String NOT_FOUND_STAFF_MESSAGE = "Not found the staff";

    public static final int NOT_FOUND_PROVINCE = 23;
    public static final String NOT_FOUND_PROVINCE_MESSAGE = "Not found province id";

    public static final int NOT_FOUND_LANGUAGE = 24;
    public static final String NOT_FOUND_LANGUAGE_MESSAGE = "Not found languages";

    public static final int NOT_FOUND_VACANCY = 25;
    public static final String NOT_FOUND_VACANCY_MESSAGE = "Not found vacancy";

    public static final int NOT_FOUND_ASSESSMENT = 26;
    public static final String NOT_FOUND_ASSESSMENT_MESSAGE = "Not found assessment id";

    public static final int NO_PERMISSION_TO_ACCESS_VACANCY = 27;
    public static final String NO_PERMISSION_TO_ACCESS_VACANCY_MESSAGE = "You don't have permission to access to this vacancy";

    public static final int NOT_FOUND_USER_CURRICULUM_VITAE = 28;
    public static final String NOT_FOUND_USER_CURRICULUM_VITAE_MESSAGE = "Not found curriculum vitae id";

    public static final int STATUS_INVALID = 29;
    public static final String STATUS_INVALID_MESSAGE = "Status is invalid";

    public static final int ID_INVALID = 30;
    public static final String ID_INVALID_MESSAGE = "ID is not hex string";

    public static final int NOT_FOUND_ROLE = 31;
    public static final String NOT_FOUND_ROLE_MESSAGE = "The role is not found";

    public static final int NO_PERMISSION_TO_ACCESS = 32;
    public static final String NO_PERMISSION_TO_ACCESS_MESSAGE = "Access is denied";

    public static final int NO_PERMISSION_TO_DELETE = 33;
    public static final String NO_PERMISSION_TO_DELETE_MESSAGE = "No permission to delete";

    public static final int NOT_FOUND_CHAT_MESSAGE = 34;
    public static final String NOT_FOUND_CHAT_MESSAGE_MESSAGE = "Not found message";

    public static final int NOT_FOUND_JOIN_REQUEST = 35;
    public static final String NOT_FOUND_JOIN_REQUEST_MESSAGE = "Not found join request";

    public static final int NOT_FOUND_SOCIAL_LINK = 37;
    public static final String NOT_FOUND_SOCIAL_LINK_MESSAGE = "Not found social link";

    public static final int NOT_FOUND_LOCATION = 38;
    public static final String NOT_FOUND_LOCATION_MESSAGE = "Not found location";

    public static final int NOT_FOUND_PERSONAL_ASSESSMENT = 39;
    public static final String NOT_FOUND_PERSONAL_ASSESSMENT_MESSAGE = "Not found personal assessment";

    public static final int NOT_FOUND_GIFT = 40;
    public static final String NOT_FOUND_GIFT_MESSAGE = "Not found gifts";

    public static final int NOT_FOUND_APPOINTMENT = 41;
    public static final String NOT_FOUND_APPOINTMENT_MESSAGE = "Not found appointment";

    public static final int INVALID_TIME_RANGE = 41;
    public static final String INVALID_TIME_RANGE_MESSAGE = "Invalid duration range";

    public static final int STAFF_OF_ANOTHER_COMPANY = 43;
    public static final String STAFF_OF_ANOTHER_COMPANY_MESSAGE = "The staff is belong to another company";

    public static final int NOT_FOUND_APPOINTMENT_DETAIL = 42;
    public static final String NOT_FOUND_APPOINTMENT_DETAIL_MESSAGE = "Not found appointment detail";

    public static final int NO_PERMISSION_TO_ACCESS_APPOINTMENT = 43;
    public static final String NO_PERMISSION_TO_ACCESS_APPOINTMENT_MESSAGE = "You don't have permission to access to this appointment";

    public static final int NONE_PUBLIC_KEY_IN_PARAM = 44;
    public static final String NONE_PUBLIC_KEY_IN_PARAM_MESSAGE = "There is none public key";

    public static final int INVALID_VACANCY_SEARCH_RANGE = 45;
    public static final String INVALID_VACANCY_SEARCH_RANGE_MESSAGE = "The search range is invalid";

    public static final int CURRENT_DATE_TIME_IS_OVER = 46;
    public static final String CURRENT_DATE_TIME_IS_OVER_MESSAGE = "It's not current time.";

    public static final int INVALID_SORT_TYPE = 47;
    public static final String INVALID_SORT_TYPE_MESSAGE = "Invalid sort type";

    public static final int ASSESSMENT_NOT_BELONG_TO_VACANCY = 48;
    public static final String ASSESSMENT_NOT_BELONG_TO_VACANCY_MESSAGE = "Assessment is not belong to vacancy";


    /*==================Code and Message Specification (For each API) ==========================*/
    public static final int LOGIN_FAIL = 100;
    public static final String LOGIN_FAIL_MESSAGE = "Invalid Username or Password";

    public static final int REGISTER_FAIL = 101;
    public static final String REGISTER_FAIL_MESSAGE = "Cannot create new account";

    public static final int ID_IS_EMPTY = 102;
    public static final String ID_IS_EMPTY_MESSAGE = "Id is empty";

    public static final int ID_IS_NOT_EXISTED = 103;
    public static final String ID_IS_NOT_EXISTED_MESSAGE = "Id is not existed";

    public static final int CONNECT_DEVICE_FAIL = 104;
    public static final String CONNECT_DEVICE_FAIL_MESSAGE = "An error occurred during execution of updating device status.";

    public static final int SYNC_FAIL = 105;
    public static final String SYNC_FAIL_MESSAGE = "Synchronization is failed.";

    public static final int EMAIL_IS_EMPTY = 106;
    public static final String EMAIL_IS_EMPTY_MESSAGE = "Email is empty. Please fill your correct email";

    public static final int EMAIL_WRONG_FORMAT = 107;
    public static final String EMAIL_WRONG_FORMAT_MESSAGE = "Email is wrong format. Please check it again!";

    public static final int PASSWORD_WRONG_FORMAT = 108;
    public static final String PASSWORD_WRONG_FORMAT_MESSAGE = "Password is wrong format. Please check it again!";

    public static final int PASSWORD_IS_EMPTY = 109;
    public static final String PASSWORD_IS_EMPTY_MESSAGE = "Password is empty. Please fill your correct Password!";

    public static final int VERIFICATION_CODE_IS_EMPTY = 120;
    public static final String VERIFICATION_CODE_IS_EMPTY_MESSAGE = "Verification code is empty. Please fill your correct code!";

    public static final int IMAGE_IS_WRONG_EXTENSION = 121;
    public static final String IMAGE_IS_WRONG_EXTENSION_MESSAGE = "The image's extension is not support";

    public static final int FILE_IS_BIG_SIZE = 122;
    public static final String FILE_IS_BIG_SIZE_MESSAGE = "The file's size is bigger requirement";

    public static final int FILE_PATH_IS_NULL = 123;
    public static final String FILE_PATH_IS_NULL_MESSAGE = "The file's path is null";

    public static final int MALFORMED_URL_NOT_FOUND = 124;
    public static final String MALFORMED_URL_NOT_FOUND_MESSAGE = " Either no legal protocol could be found in a specification string or the  string could not be parsed.";

    public static final int COMPANY_NAME_IS_REQUIRED = 125;
    public static final String COMPANY_NAME_IS_REQUIRED_MESSAGE = "The company's name is required";

    public static final int SAVE_FAIL = 126;
    public static final String SAVE_FAIL_MESSAGE = "Save failed";

    public static final int COMPANY_ID_IS_EMPTY = 127;
    public static final String COMPANY_ID_IS_EMPTY_MESSAGE = "CompanyId is empty";

    public static final int LOCATION_IS_EXISTS = 128;
    public static final String LOCATION_IS_EXISTS_MESSAGE = "Location is exist";

    public static final int EXCEL_IS_WRONG_EXTENSION = 129;
    public static final String EXCEL_IS_WRONG_EXTENSION_MESSAGE = "The excel's extension is not support";

    public static final int IN_USING = 130;
    public static final String IN_USING_MESSAGE = "It is using, can't edit/delete.";

    public static final int EXTENSION_IS_DENIED = 131;
    public static final String EXTENSION_IS_DENIED_MESSAGE = "The extension is denied";

    public static final int FILE_SIZE_IS_ZERO = 132;
    public static final String FILE_SIZE_IS_ZERO_MESSAGE = "The file size is zero";

    //	For get code
    public static final int FAILED_TO_SEND_CODE = 1001;
    public static final String FAILED_TO_SEND_CODE_MESSAGE = "Failed to send verification code";

    public static final int EMAIL_IS_EXISTED = 1002;
    public static final String EMAIL_IS_EXISTED_MESSAGE = "Email is already used in the system";

    public static final int USER_NOT_CREATE_SUCCESS = 1003;
    public static final String USER_NOT_CREATE_SUCCESS_MESSAGE = "The User Is Not Created Successfully Due To System Error, Please Contact Our System Support at tech@qooco.com Now!";

    public static final int EMAIL_IS_ALREADY_USED = 1004;
    public static final String EMAIL_IS_ALREADY_USED_MESSAGE = "This email is already used";

    public static final int CONTACT_WRONG_FORMAT = 1005;
    public static final String CONTACT_WRONG_FORMAT_MESSAGE = "The contact email or mobile number format is incorrect";

    //	Sign up
    public static final int USER_NAME_EXISTING = 1101;
    public static final String USER_NAME_EXISTING_MESSAGE = "The user name have been used, Please try another one!";
    public static final int USER_NAME_IS_EMPTY = 1102;
    public static final String USER_NAME_IS_EMPTY_MESSAGE = "The user name is empty!";
    public static final int USER_NAME_WRONG_FORMAT = 1103;
    public static final String USER_NAME_WRONG_FORMAT_MESSAGE = "The user name is wrong format!";

    //	Response code status for verification sign up code
    public static final int VERIFICATION_CODE_NOT_MATCH = 1202;
    public static final String VERIFICATION_CODE_NOT_MATCH_MESSAGE = "Validation code not match";

    public static final int VERIFICATION_CODE_EXPIRED = 1203;
    public static final String VERIFICATION_CODE_EXPIRED_MESSAGE = "Validation code is expired";

    public static final int VERIFICATION_CODE_NOT_FOUND = 1204;
    public static final String VERIFICATION_CODE_NOT_FOUND_MESSAGE = "Verification code not found";

    //	Response code status for login
    public static final int USERNAME_EMPTY = 1301;
    public static final String USERNAME_EMPTY_MESSAGE = "Username is empty";

    public static final int USER_WRONG_FORMAT = 1302;
    public static final String USERNAME_WRONG_FORMAT_MESSAGE = "Username is wrong format";

    public static final int USERNAME_NOT_EXIST = 1303;
    public static final String USERNAME_NOT_EXIST_MESSAGE = "Username is not exist";

    public static final int PASSWORD_WRONG = 1304;
    public static final String PASSWORD_WRONG_MESSAGE = "Password doesn't match with username";

    public static final int USERNAME_FOUND_ON_EXTERNAL_SERVER = 1305;
    public static final String USERNAME_FOUND_ON_EXTERNAL_SERVER_MESSAGE = "Username found on external linked server";

    // Response code status for forgot password
    public static final int USERNAME_OR_EMAIL_EMPTY_CODE = 1401;
    public static final String USERNAME_OR_EMAIL_EMPTY_MESSAGE = "Username or Email is empty";

    public static final int FORGOT_PASSWORD_UNKNOWN_CODE = 1402;
    public static final String FORGOT_PASSWORD_UNKNOWN_MESSAGE = "This email created many accounts. Please submit both username and email";

    public static final int FORGOT_PASSWORD_USER_NOT_EXIST_CODE = 1403;
    public static final String FORGOT_PASSWORD_USER_NOT_EXIST_MESSAGE = "User with such contact doesn't exist";

    public static final int FORGOT_PASSWORD_NOT_FOUND_CODE = 1404;
    public static final String FORGOT_PASSWORD_NOT_FOUND_MESSAGE = "Not found contacts by username";

    // Save user profile
    public static final int USER_PROFILE_ID_NOT_EXIST_CODE = 1501;
    public static final String USER_PROFILE_ID_NOT_EXIST_MESSAGE = "User profile ID is not exist";

    public static final int AVATAR_IS_REQUIRED = 1502;
    public static final String AVATAR_IS_REQUIRED_MESSAGE = "Avatar is required";

    public static final int GENDER_IS_REQUIRED = 1503;
    public static final String GENDER_IS_REQUIRED_MESSAGE = "Gender is required";

    public static final int FIRST_NAME_IS_REQUIRED = 1504;
    public static final String FIRST_NAME_IS_REQUIRED_MESSAGE = "First name is required";

    public static final int LAST_NAME_IS_REQUIRED = 1505;
    public static final String LAST_NAME_IS_REQUIRED_MESSAGE = "Last name is required";

    public static final int BIRTHDAY_IS_REQUIRED = 1506;
    public static final String BIRTHDAY_IS_REQUIRED_MESSAGE = "Birthday is required";

    public static final int LANGUAGES_ARE_REQUIRED = 1507;
    public static final String LANGUAGES_ARE_REQUIRED_MESSAGE = "Native languages are required";

    public static final int COUNTRY_IS_REQUIRED = 1508;
    public static final String COUNTRY_IS_REQUIRED_MESSAGE = "Country is required";

    public static final int PHONE_NUMBER_IS_REQUIRED = 1509;
    public static final String PHONE_NUMBER_IS_REQUIRED_MESSAGE = "Phone Number is required";

    public static final int ADDRESS_IS_REQUIRED = 1510;
    public static final String ADDRESS_IS_REQUIRED_MESSAGE = "Address is required";

    public static final int NATIONAL_ID_IS_REQUIRED = 1511;
    public static final String NATIONAL_ID_IS_REQUIRED_MESSAGE = "National Id is required";

    public static final int CITY_IS_REQUIRED = 1512;
    public static final String CITY_IS_REQUIRED_MESSAGE = "City is required";

    public static final String FORGOT_PASSWORD_EMPTY_STRING = "";

    //User previous position Code: 16xx
    public static final int POSITION_NAME_IS_REQUIRED = 1601;
    public static final String POSITION_NAME_IS_REQUIRED_MESSAGE = "The position's name is required";

    public static final int START_DATE_IS_REQUIRED = 1602;
    public static final String START_DATE_IS_REQUIRED_MESSAGE = "The start date/from date is required";

    public static final int END_DATE_IS_NULL = 1603;
    public static final String END_DATE_IS_NULL_MESSAGE = "The end date/to date is null";

    public static final int START_DATE_AFTER_END_DATE = 1604;
    public static final String START_DATE_AFTER_END_DATE_MESSAGE = "The start date/from date is after end date/to date";

    public static final int START_DATE_AFTER_NOW = 1605;
    public static final String START_DATE_AFTER_NOW_MESSAGE = "The start date is after today";

    //Vacancy
    public static final int VACANCIES_ARE_EMPTY = 1701;
    public static final String VACANCIES_ARE_EMPTY_MESSAGE = "The vacancy list is empty";

    /*==================Code and Message for Job ==========================*/
    public static final int JOB_IS_EMPTY = 1801;
    public static final String JOB_IS_EMPTY_MESSAGE = "Job is empty";

    public static final int JOB_NAME_IS_EMPTY = 1802;
    public static final String JOB_NAME_IS_EMPTY_MESSAGE = "Job name is empty";

    public static final int JOB_IS_EXISTED = 1803;
    public static final String JOB_IS_EXISTED_MESSAGE = "This job is already created by your company";

    public static final int JOB_NAME_IS_TOO_SHORT = 1804;
    public static final String JOB_NAME_IS_TOO_SHORT_MESSAGE = "Job name is too short, it should be more than 1 character";

    public static final int JOB_NAME_IS_TOO_LONG = 1805;
    public static final String JOB_NAME_IS_TOO_LONG_MESSAGE = "Job name is too long, it should be less than 255 characters";

    public static final int JOB_DESCRIPTION_IS_TOO_SHORT = 1806;
    public static final String JOB_DESCRIPTION_IS_TOO_SHORT_MESSAGE = "Job description is too short, it should be more than 1 character";

    public static final int JOB_DESCRIPTION_IS_TOO_LONG = 1807;
    public static final String JOB_DESCRIPTION_IS_TOO_LONG_MESSAGE = "Job description is too long, it should be less than 1000 characters";

    //User CV Code: 20xx
    public static final int INVALID_SALARY_RANGE = 2001;
    public static final String INVALID_SALARY_RANGE_MESSAGE = "Invalid salary range";

    public static final int REQUIRED_JOB = 2002;
    public static final String REQUIRED_JOB_MESSAGE = "Jobs are required";

    public static final int REQUIRED_WORKING_HOUR = 2003;
    public static final String REQUIRED_WORKING_HOUR_MESSAGE = "Working hour is required";

    public static final int CONFLICT_WORKING_HOUR = 2004;
    public static final String CONFLICT_WORKING_HOUR_MESSAGE = "Conflict working hour";

    public static final int CURRENCY_CODE_IS_REQUIRED = 2005;
    public static final String CURRENCY_CODE_IS_REQUIRED_MESSAGE = "Currency is required";

    public static final int NOT_YOUR_PREVIOUS_JOB = 2006;
    public static final String NOT_YOUR_PREVIOUS_JOB_MESSAGE = "The previous job position is not yours";

    public static final int INVALID_SALARY = 2007;
    public static final String INVALID_SALARY_MESSAGE = "The salary have to greater than 0";

    public static final int CURRENCY_ID_IS_REQUIRED = 2008;
    public static final String CURRENCY_ID_IS_REQUIRED_MESSAGE = "Currency ID is required";

    public static final int INVALID_NUMBER_OF_SEAT = 2009;
    public static final String INVALID_NUMBER_OF_SEAT_MESSAGE = "The number of seat has to greater than 0";

    public static final int SALARY_RANGE_IS_REQUIRED = 2010;
    public static final String SALARY_RANGE_IS_REQUIRED_MESSAGE = "Salary range is required";

    public static final int EXPECTED_START_DATE_IS_REQUIRED = 2011;
    public static final String EXPECTED_START_DATE_IS_REQUIRED_MESSAGE = "Expected start date is required";

    public static final int SOFT_SKILLS_ARE_REQUIRED = 2012;
    public static final String SOFT_SKILLS_ARE_REQUIRED_MESSAGE = "Soft skills are required";

    public static final int SOCIAL_LINK_NOT_EXIST = 2013;
    public static final String SOCIAL_LINK_NOT_EXIST_MESSAGE = "Social link is not existed";

    public static final int NUMBER_OF_SEAT_LESS_THAN_OR_EQUAL_NUMBER_OF_CLOSED_CANDIDATES = 2014;
    public static final String NUMBER_OF_SEAT_LESS_THAN_OR_EQUAL_NUMBER_OF_CLOSED_CANDIDATES_MESSAGE = "The number of seat has to greater than number of closed candidates";

    // Check app version
    public static final int GET_LATEST_APP_VERSION_INVALID_APP_VERSION = 2101;
    public static final String GET_LATEST_APP_VERSION_INVALID_APP_VERSION_MESSAGE = "App version is invalid";

    public static final int GET_LATEST_APP_VERSION_INVALID_APP_ID = 2102;
    public static final String GET_LATEST_APP_VERSION_INVALID_APP_ID_MESSAGE = "App id is invalid";

    public static final int GET_LATEST_APP_VERSION_INVALID_OS = 2103;
    public static final String GET_LATEST_APP_VERSION_INVALID_OS_MESSAGE = "OS platform is invalid";

    /*==================Code and Message for ClientInfo ==========================*/
    public static final int APP_ID_IS_EMPTY = 2201;
    public static final String APP_ID_IS_EMPTY_MESSAGE = "App Id is empty";

    public static final int APP_ID_INVALID = 2202;
    public static final String APP_ID_INVALID_MESSAGE = "App Id is invalid";

    public static final int APP_VERSION_IS_EMPTY = 2203;
    public static final String APP_VERSION_IS_EMPTY_MESSAGE = "App Version is empty";

    public static final int PLATFORM_IS_EMPTY = 2204;
    public static final String PLATFORM_IS_EMPTY_MESSAGE = "Platform is empty";

    public static final int DEVICE_TOKEN_EMPTY = 2205;
    public static final String DEVICE_TOKEN_EMPTY_MESSAGE = "Device token is empty";

    // Company code: 30xx
    public static final int SAVE_COMPANY_EMPTY_LOGO = 3001;
    public static final String SAVE_COMPANY_EMPTY_LOGO_MESSAGE = "Empty company logo";

    public static final int SAVE_COMPANY_EMPTY_NAME = 3002;
    public static final String SAVE_COMPANY_EMPTY_NAME_MESSAGE = "Empty company name";

    public static final int SAVE_COMPANY_EMPTY_ADDRESS = 3003;
    public static final String SAVE_COMPANY_EMPTY_ADDRESS_MESSAGE = "Empty address name";

    public static final int SAVE_COMPANY_EMPTY_PHONE_NUMBER = 3004;
    public static final String SAVE_COMPANY_EMPTY_PHONE_NUMBER_MESSAGE = "Empty phone number name";

    public static final int SAVE_COMPANY_EMPTY_EMAIL = 3005;
    public static final String SAVE_COMPANY_EMPTY_EMAIL_MESSAGE = "Empty email";

    public static final int SAVE_COMPANY_EMPTY_WEB = 3006;
    public static final String SAVE_COMPANY_EMPTY_WEB_MESSAGE = "Empty web";

    public static final int SAVE_COMPANY_EMPTY_AMADEUS = 3007;
    public static final String SAVE_COMPANY_EMPTY_AMADEUS_MESSAGE = "Empty Amadues code";

    public static final int SAVE_COMPANY_EMPTY_GALILEO = 3008;
    public static final String SAVE_COMPANY_EMPTY_GALILEO_MESSAGE = "Empty Galileo code";

    public static final int SAVE_COMPANY_EMPTY_WORLDSPAN = 3009;
    public static final String SAVE_COMPANY_EMPTY_WORLDSPAN_MESSAGE = "Empty Worldspan code";

    public static final int SAVE_COMPANY_EMPTY_SABRE = 3010;
    public static final String SAVE_COMPANY_EMPTY_SABRE_MESSAGE = "Empty Sabre code";

    public static final int SAVE_COMPANY_EMPTY_DESCRIPTION = 3011;
    public static final String SAVE_COMPANY_EMPTY_DESCRIPTION_MESSAGE = "Empty description";

    public static final int SAVE_COMPANY_EXCEED_MAX_LENGTH_DESCRIPTION = 3012;
    public static final String SAVE_COMPANY_EXCEED_MAX_LENGTH_DESCRIPTION_MESSAGE = "Description is exceeded " + ColumnLength.COMPANY_DESCRIPTION;

    public static final int SAVE_COMPANY_EXCEED_MAX_LENGTH_ADDRESS = 3013;
    public static final String SAVE_COMPANY_EXCEED_MAX_LENGTH_ADDRESS_MESSAGE = "Company address is exceeded " + ColumnLength.COMPANY_ADDRESS;

    public static final int SAVE_COMPANY_EXCEED_MAX_LENGTH_PHONE = 3014;
    public static final String SAVE_COMPANY_EXCEED_MAX_LENGTH_PHONE_MESSAGE = "Phone number is exceeded " + ColumnLength.COMPANY_PHONE;

    public static final int SAVE_COMPANY_EXCEED_MAX_LENGTH_AMADEUS = 3015;
    public static final String SAVE_COMPANY_EXCEED_MAX_LENGTH_AMADEUS_MESSAGE = "Phone number is exceeded " + ColumnLength.COMPANY_AMADEUS;

    public static final int SAVE_COMPANY_EXCEED_MAX_LENGTH_GALILEO = 3016;
    public static final String SAVE_COMPANY_EXCEED_MAX_LENGTH_GALILEO_MESSAGE = "Galileo is exceeded " + ColumnLength.COMPANY_GALILEO;

    public static final int SAVE_COMPANY_EXCEED_MAX_LENGTH_LOGO = 3017;
    public static final String SAVE_COMPANY_EXCEED_MAX_LENGTH_LOGO_MESSAGE = "Logo is exceeded " + ColumnLength.COMPANY_LOGO;

    public static final int SAVE_COMPANY_EXCEED_MAX_LENGTH_COMPANY_NAME = 3018;
    public static final String SAVE_COMPANY_EXCEED_MAX_LENGTH_COMPANY_NAME_MESSAGE = "Company name is exceeded " + ColumnLength.COMPANY_NAME;

    public static final int SAVE_COMPANY_EXCEED_MAX_LENGTH_SABRE = 3019;
    public static final String SAVE_COMPANY_EXCEED_MAX_LENGTH_SABRE_MESSAGE = "Sabre is exceeded " + ColumnLength.COMPANY_SABRE;

    public static final int SAVE_COMPANY_EXCEED_MAX_LENGTH_WEBPAGE = 3020;
    public static final String SAVE_COMPANY_EXCEED_MAX_LENGTH_WEBPAGE_MESSAGE = "Web page is exceeded " + ColumnLength.COMPANY_WEBPAGE;

    public static final int SAVE_COMPANY_EXCEED_MAX_LENGTH_WORLDSPAN = 3021;
    public static final String SAVE_COMPANY_EXCEED_MAX_LENGTH_WORLDSPAN_MESSAGE = "Worldspan is exceeded " + ColumnLength.COMPANY_WORLDSPAN;

    public static final int SAVE_COMPANY_HAS_PENDING_COMPANY = 3022;
    public static final String SAVE_COMPANY_HAS_PENDING_COMPANY_MESSAGE = "There is a pending company waiting for approval";

    public static final int SAVE_COMPANY_NOT_ADMIN_OF_COMPANY = 3023;
    public static final String SAVE_COMPANY_NOT_ADMIN_OF_COMPANY_MESSAGE = "Not admin of company";

    public static final int JOINED_COMPANY_REQUEST = 3024;
    public static final String JOINED_COMPANY_REQUEST_MESSAGE = "You joined this company";

    public static final int DECLINED_JOIN_COMPANY_REQUEST = 3025;
    public static final String DECLINED_JOIN_COMPANY_REQUEST_MESSAGE = "Your join request is declined by this company";

    public static final int PENDING_JOIN_COMPANY_REQUEST = 3026;
    public static final String PENDING_JOIN_COMPANY_REQUEST_MESSAGE = "Your join request is waiting approval";

    public static final int AUTHORIZED_JOIN_COMPANY_REQUEST = 3027;
    public static final String AUTHORIZED_JOIN_COMPANY_REQUEST_MESSAGE = "Your join request is authorized by this company";

    //Staff and company
    public static final int NOT_STAFF_OF_COMPANY = 3101;
    public static final String NOT_STAFF_OF_COMPANY_MESSAGE = "Not Staff of the company";

    /*==================Code and Message for Vacancy ==========================*/
    public static final int EXPECTED_START_DATE_IS_NULL = 3201;
    public static final String EXPECTED_START_DATE_IS_NULL_MESSAGE = "Expected start date is null or in past";

    public static final int SHORT_DESCRIPTION_IS_EMPTY = 3202;
    public static final String SHORT_DESCRIPTION_IS_EMPTY_MESSAGE = "Short description is empty";

    public static final int FULL_DESCRIPTION_IS_EMPTY = 3203;
    public static final String FULL_DESCRIPTION_IS_EMPTY_MESSAGE = "Full description is empty";

    public static final int ASSESSMENT_MORE_THAN_ONE_ASSESSMENT_LEVEL = 3204;
    public static final String ASSESSMENT_MORE_THAN_ONE_ASSESSMENT_LEVEL_MESSAGE = "The assessment have more than one assessment level";

    public static final int VACANCY_IS_SUSPENDED = 3205;
    public static final String VACANCY_IS_SUSPENDED_MESSAGE = "The vacancy is suspended";

    public static final int SUSPENDED_DAYS_IS_INVALID = 3206;
    public static final String SUSPENDED_DAYS_IS_INVALID_MESSAGE = "The suspended day is invalid";

    public static final int VACANCY_IS_CLOSED = 3207;
    public static final String VACANCY_IS_CLOSED_MESSAGE = "The vacancy is closed. You cannot close this candidate!";

    public static final int CANDIDATE_IS_ALREADY_CLOSED = 3208;
    public static final String CANDIDATE_IS_ALREADY_CLOSED_MESSAGE = "The candidate is already closed";

    public static final int VACANCY_IS_INACTIVE = 3209;
    public static final String VACANCY_IS_INACTIVE_MESSAGE = "The vacancy is inactive";

    /*==================Code and Message for Message ==========================*/
    public static final int MESSAGE_NOT_APPLIED = 4001;
    public static final String MESSAGE_NOT_APPLIED_MESSAGE = "This message is not applied yet";

    public static final int USER_IS_NOT_JOIN_COMPANY = 4002;
    public static final String USER_IS_NOT_JOIN_COMPANY_MESSAGE = "The user is not join company or company is pending/closed";

    public static final int CHANNEL_ID_INVALID = 4003;
    public static final String CHANNEL_ID_INVALID_MESSAGE = "ChannelId is not existed";

    public static final int NO_SENDER = 4004;
    public static final String NO_SENDER_MESSAGE = "Have not sender";

    public static final int CONVERSATION_IS_DISABLE = 4005;
    public static final String CONVERSATION_IS_DISABLE_MESSAGE = "This conversation is disable";

    public static final int NOT_FOUND_CONVERSATION = 4006;
    public static final String NOT_FOUND_CONVERSATION_MESSAGE = "This conversation is not existed";

    public static final int USER_IS_NOT_IN_CONVERSATION = 4007;
    public static final String USER_IS_NOT_IN_CONVERSATION_MESSAGE = "User is not participants in the conversation";

    public static final int NOT_FOUND_MESSAGE_CENTER = 4008;
    public static final String NOT_FOUND_MESSAGE_CENTER_MESSAGE = "The message center is not existed";

    /*==================Code and Message for Approval ==========================*/
    public static final int PENDING_STATUS = 0;
    public static final String PENDING_STATUS_MESSAGE = "This company is waiting approval";

    public static final int APPROVED_STATUS = 1;
    public static final String APPROVED_STATUS_MESSAGE = "This company is approved";

    public static final int DISAPPROVED_STATUS = 2;
    public static final String DISAPPROVED_STATUS_MESSAGE = "This company is disapproved";

    // Update join company message
    public static final int NOT_AUTHORIZATION_COMPANY_MESSAGE = 4101;
    public static final String NOT_AUTHORIZATION_COMPANY_MESSAGE_MESSAGE = "The message is not authorization company message";

    public static final int NOT_ADMIN_OF_COMPANY = 4102;
    public static final String NOT_ADMIN_OF_COMPANY_MESSAGE = "Not admin of company";

    public static final int UPDATE_JOIN_COMPANY_REQUEST_AUTHORIZED = 4103;
    public static final String UPDATE_JOIN_COMPANY_REQUEST_AUTHORIZED_MESSAGE = "The joined company request is authorized";

    public static final int UPDATE_JOIN_COMPANY_REQUEST_DECLINED = 4104;
    public static final String UPDATE_JOIN_COMPANY_REQUEST_DECLINED_MESSAGE = "The joined company request is declined";

    // Social login
    public static final int AUTH_ID_EMPTY = 5101;
    public static final String AUTH_ID_EMPTY_MESSAGE = "oauthId must be not empty";

    public static final int AUTH_PROVIDER_EMPTY = 5102;
    public static final String AUTH_PROVIDER_EMPTY_MESSAGE = "oauthProvider may not be empty";

    // Referral
    public static final int INVALID_REDEEM_CODE = 5201;
    public static final String INVALID_REDEEM_CODE_MESSAGE = "No valid code to redeem";

    public static final int CAN_NOT_REDEEM_YOUR_OWN_CODE = 5202;
    public static final String CAN_NOT_REDEEM_YOUR_OWN_CODE_MESSAGE = "Cannot redeem your own code";

    public static final int NOT_FOUND_CLAIM_ASSESSMENT = 5203;
    public static final String NOT_FOUND_CLAIM_ASSESSMENT_MESSAGE = "Not found claim assessment";

    public static final int NOT_FOUND_REDEEM_CODE = 5203;
    public static final String NOT_FOUND_REDEEM_CODE_MESSAGE = "The code is not valid";

    public static final int CODE_IS_EXPIRED = 5204;
    public static final String CODE_IS_EXPIRED_MESSAGE = "The code is expired";

    public static final int CAN_NOT_CLAIM = 5205;
    public static final String CAN_NOT_CLAIM_MESSAGE = "User is not enough coins to claim";

    public static final int ASSESSMENT_EMPTY = 5206;
    public static final String ASSESSMENT_EMPTY_MESSAGE = "Assessments empty";

    public static final int ASSESSMENT_ID_NOT_EXIST = 5207;
    public static final String ASSESSMENT_ID_NOT_EXIST_MESSAGE = "AssessmentId does not exist";

    public static final int CHECK_SUM_NOT_MATCH = 5208;
    public static final String CHECK_SUM_NOT_MATCH_MESSAGE = "Checksum does not match";

    public static final int CODE_IS_USED = 5209;
    public static final String CODE_IS_USED_MESSAGE = "The code is already used";

    public static final int GIFTS_EMPTY = 5210;
    public static final String GIFTS_EMPTY_MESSAGE = "Gifts are empty";

    public static final int NOT_FOUND_REFERRAL_COUNT = 5211;
    public static final String NOT_FOUND_REFERRAL_COUNT_MESSAGE = "Not found referral count";

    public static final int NO_GIFTS_AVAILABLE = 5212;
    public static final String NO_GIFTS_AVAILABLE_MESSAGE = "This gift is not available";

    // Statistic
    public static final int CAN_NOT_VIEW_YOURSELF = 5301;
    public static final String CAN_NOT_VIEW_YOURSELF_MESSAGE = "Cannot view yourself";

    public static final int CANDIDATE_IS_ALREADY_APPLIED = 5302;
    public static final String CANDIDATE_IS_ALREADY_APPLIED_MESSAGE = "Candidate is already applied in the vacancy";

    public static final int CANDIDATE_IS_ALREADY_REJECTED = 5303;
    public static final String CANDIDATE_IS_ALREADY_REJECTED_MESSAGE = "Candidate is already rejected in the vacancy";

    public static final int TIME_ZONE_INVALID = 5304;
    public static final String TIME_ZONE_INVALID_MESSAGE = "Time zone is invalid";

    // Channel Id
    public static final int NOTIFICATION_CHANNEL_ID_EMPTY = 5306;
    public static final String NOTIFICATION_CHANNEL_ID_EMPTY_MESSAGE = "Channel Id is empty";

    // Submit personal assessment
    public static final int SUBMIT_PERSONAL_ASSESSMENT_EMPTY_ANSWER = 5401;
    public static final String SUBMIT_PERSONAL_ASSESSMENT_EMPTY_ANSWER_MESSAGE = "Answer is empty";

    public static final int SUBMIT_PERSONAL_ASSESSMENT_NOT_EXIST_ASSESSMENT = 5402;
    public static final String SUBMIT_PERSONAL_ASSESSMENT_NOT_EXIST_ASSESSMENT_MESSAGE = "Personal assessment is not existed";

    public static final int SUBMIT_PERSONAL_ASSESSMENT_NOT_ENOUGH_ANSWER = 5403;
    public static final String SUBMIT_PERSONAL_ASSESSMENT_NOT_ENOUGH_ANSWER_MESSAGE = "Not enough answer for the assessment";

    public static final int SUBMIT_PERSONAL_ASSESSMENT_ANSWER_NOT_RANGE_VALID_RANGE = 5404;
    public static final String SUBMIT_PERSONAL_ASSESSMENT_ANSWER_NOT_RANGE_VALID_RANGE_MESSAGE = "There is answer not in valid range";

    public static final int SUBMIT_PERSONAL_ASSESSMENT_ANSWER_TESTED_ALREADY = 5404;
    public static final String SUBMIT_PERSONAL_ASSESSMENT_ANSWER_TESTED_ALREADY_MESSAGE = "You tested this assessment already";

    public static final int SUBMIT_PERSONAL_ASSESSMENT_NO_ANSWER_IN_ASSESSMENT = 5405;
    public static final String SUBMIT_PERSONAL_ASSESSMENT_NO_ANSWER_IN_ASSESSMENT_MESSAGE = "There is no answer in the assessment";

    public static final int APPOINTMENTS_IS_DUPLICATE = 5501;
    public static final String APPOINTMENTS_IS_DUPLICATE_MESSAGE = "There are the same appointment in a vacancy";

    public static final int APPOINTMENTS_IS_NOT_THE_SAME_VACANCY = 5502;
    public static final String APPOINTMENTS_IS_NOT_THE_SAME_VACANCY_MESSAGE = "Difference vacancy is appointment list";

    public static final int APPOINTMENTS_VACANCY_NOT_IN_COMPANY = 5503;
    public static final String APPOINTMENTS_VACANCY_NOT_IN_COMPANY_MESSAGE = "The vacancy is not belong the company";

    public static final int APPOINTMENTS_SELECTED_DATE_IN_SUSPENDED_TIME = 5504;
    public static final String APPOINTMENTS_SELECTED_DATE_IN_SUSPENDED_TIME_MESSAGE = "Selected date is in suspended range";

    public static final int APPOINTMENTS_NOT_IN_VACANCY = 5505;
    public static final String APPOINTMENTS_NOT_IN_VACANCY_MESSAGE = "The appointment is not belong the vacancy or to be disable";

    public static final int APPOINTMENTS_EVENT_EMPTY_USER_CV = 5506;
    public static final String APPOINTMENTS_EVENT_EMPTY_USER_CV_MESSAGE = "The user CV is null";

    public static final int APPOINTMENTS_EVENT_CONFLICT = 5507;
    public static final String APPOINTMENTS_EVENT_CONFLICT_MESSAGE = "The same schedule in the appointment";

    public static final int APPOINTMENTS_EVENT_DATE_OUT_OF_TIME_RANGE = 5508;
    public static final String APPOINTMENTS_EVENT_DATE_OUT_OF_TIME_RANGE_MESSAGE = "The appointment time slot is not the same appointment date";

    public static final int APPOINTMENTS_EVENT_NO_PERMISSION = 5509;
    public static final String APPOINTMENTS_EVENT_NO_PERMISSION_MESSAGE = "No permission to modify the candidate appointment";

    public static final int INVALID_EVENT_ACTION = 5510;
    public static final String INVALID_EVENT_ACTION_MESSAGE = "Invalid event action";

    //Appointment
    public static final int APPOINTMENT_IS_EXPIRED = 5601;
    public static final String APPOINTMENT_IS_EXPIRED_MESSAGE = "Appointment is expired";

    public static final int APPOINTMENT_IS_REPLIED_ALREADY = 5602;
    public static final String APPOINTMENT_IS_REPLIED_ALREADY_MESSAGE = "Appointment is replied already";

    public static final int APPLICANT_IS_REPLIED_ALREADY = 5603;
    public static final String APPLICANT_IS_REPLIED_ALREADY_MESSAGE = "Applicant is replied already";

    public static final int MESSAGE_IS_NOT_APPOINTMENT = 5604;
    public static final String MESSAGE_IS_NOT_APPOINTMENT_MESSAGE = "This is not appointment message";

    public static final int APPOINTMENT_IS_NOT_AVAILABLE = 5605;
    public static final String APPOINTMENT_IS_NOT_AVAILABLE_MESSAGE = "Appointment is not available to reply";

    public static final int APPOINTMENT_IS_NOT_AVAILABLE_TO_REPLY = 5606;
    public static final String APPOINTMENT_IS_NOT_AVAILABLE_TO_REPLY_MESSAGE = "Appointment is not available to reply";

    public static final int APPOINTMENT_INVITATION_IS_SENT_ALREADY = 5607;
    public static final String APPOINTMENT_INVITATION_IS_SENT_ALREADY_MESSAGE = "Appointment invitation is sent already";

    public static final int APPOINTMENT_DATE_TIME_REQUIRED = 5608;
    public static final String APPOINTMENT_DATE_TIME_REQUIRED_MESSAGE = "Appointment date or time range is required";

    public static final int START_DATE_RANGE_IS_PAST = 5609;
    public static final String START_DATE_RANGE_IS_PAST_MESSAGE = "The start of appointment date range is the past";

}
