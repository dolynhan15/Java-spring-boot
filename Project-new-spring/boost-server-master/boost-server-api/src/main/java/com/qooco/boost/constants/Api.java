package com.qooco.boost.constants;
/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 6/18/2018 - 6:46 PM
 */

public class Api {

    public final static String SERVICE_GENERATE_SIGN_UP_CODE = "/verification/send";
    public final static String SERVICE_VERIFY_SIGN_UP_CODE = "/verification/verify";
    public final static String SERVICE_REGISTER_SYSTEM_ACCOUNT = "/register";
    public final static String SERVICE_LOGIN_SYSTEM_ACCOUNT = "/login";
    public final static String SERVICE_LOGIN_WITH_FACEBOOK_ACCOUNT = "/social-login";
    public final static String SERVICE_CHECK_USER_CONTACT = "/check-user-contact?contact={userContactData}";
    public final static String SERVICE_FORGOT_PASSWORD_EMAIL = "/retrievePassword?email={emailData}&platform=boost";
    public final static String SERVICE_FORGOT_PASSWORD_USERNAME = "/retrievePassword?username={usernameData}&platform=boost";
    public final static String SERVICE_ASSIGN_LESSON_TO_USER = "/user/assign-lesson?userId={UserId}&lessonId={lessonId}&sign={signId}";

    public final static String SERVICE_SYNC_DATA = "SPID={SPID}&UserId={UserId}&v=4&av=11";
}
