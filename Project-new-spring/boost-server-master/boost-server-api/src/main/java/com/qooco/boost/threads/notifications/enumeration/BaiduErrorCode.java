package com.qooco.boost.threads.notifications.enumeration;

import lombok.Getter;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/30/2018 - 2:11 PM
 */
public enum BaiduErrorCode {
    CLIENT_EXCEPTION                    (	0	,	1	,"	Client exception	","	Client exception    "),
    SUCCESS                         	(	200	,	0	,"	Success	","	Success	"),
    INTERNAL_SERVER_ERROR	            (	500	,	30600	,"	Internal Server Error	","	Service internal error	"),
    METHOD_NOT_ALLOWED	                (	405	,	30601	,"	Method Not Allowed	","	Request method is not allowed	"),
    REQUEST_PARAMS_NOT_VALID	        (	400	,	30602	,"	Request Params Not Valid	","	Request parameter error	"),
    AUTHENTICATION_FAILED	            (	403	,	30603	,"	Authentication Failed	","	Permission verification failed	"),
    QUOTA_USE_UP_PAYMENT_REQUIRED	    (	402	,	30604	,"	Quota Use Up Payment Required	","	Renewal of quota is required	"),
    DATA_REQUIRED_NOT_FOUND         	(	404	,	30605	,"	Data Required Not Found	","	Request data does not exist	"),
    REQUEST_TIME_EXPIRES_TIMEOUT	    (	408	,	30606	,"	Request Time Expires Timeout	","	Request timestamp verification timeout	"),
    CHANNEL_TOKEN_TIMEOUT	            (	408	,	30607	,"	Channel Token Timeout	","	Service token validity period expired	"),
    BIND_RELATION_NOT_FOUND	            (	404	,	30608	,"	Bind Relation Not Found	","	The binding relationship was not found or does not exist	"),
    BIND_NUMBER_TOO_MANY	            (	404	,	30609	,"	Bind Number Too Many	","	Too many binding relationships	"),
    DUPLICATE_OPERATION	                (	409	,	30610	,"	Duplicate Operation	","	Repeat operation	"),
    TAG_NOT_FOUND	                    (	404	,	30611	,"	Tag not Found	","	Tag not found or does not exist	"),
    APPLICATION_FORBIDDEN	            (	401	,	30612	,"	Application Forbidden, Need Whitelist Authorization	","	Application is forbidden, whitelist authorization is required	"),
    APP_NEED_INITED_FIRST_IN_PUSH_CONSOLE	(	402	,	30613	,"	App Need Inited First In Push Console	","	Need to first push the push function in Push-Console	"),
    APPLICATION_IS_NOT_APPROVED	        (	401	,	30616	,"	Application Is Not approved, Can Not Use The Push Service	","	Unreviewed, app can't use push service	"),
    APPLICATION_DO_NOT_HAVE_BROADCAST_PUSH_CAPABILITY	(	401	,	30617	,"	Application Do Not Have Broadcast Push Capability	","	Application has no broadcast push capability	"),
    APPLICATION_DO_NOT_HAVE_UNICAST_OR_GRPCAST_PUSH_CAPABILITY	(	401	,	30618	,"	Application Do Not Have Unicast Or Grpcast Push Capability	","	The application has no unicast or multicast push capability	"),
    DEFAULT_TAG_IS_RESERVED	            (	403	,	30619	,"	Default Tag Is Reserved	","	Default tag not allowed	"),
    ONE_APP_COULD_ONLY_HAVE_ONE_KIND_OF_DEVICE_PLATFORM	(	403	,	30620	,"	One App Could Only Have One Kind Of Device Platform	","	An app only supports one device platform	"),
    PACKAGE_NAME_INVALID	            (	400	,	30621	,"	Package Name Invalid	","	Android package name is illegal	"),
    REQUESTS_ARE_TOO_FREQUENT       	(	402	,	30699	,"	Requests Are Too Frequent	","	Requests are too frequent and are temporarily rejected or require whitelist authorization	"),
    INVALID_IOS_DEVICE_TOKEN	        (	400	,	40001	,"	Invalid iOS Device Token	","	The Device Token format is invalid.	"),
    INVALID_IOS_MESSAGE	                (	400	,	40002	,"	Invalid iOS Message	","	Invalid message format	"),
    IOS_BAD_DEVICE_TOKEN	            (	400	,	40003	,"	iOS Bad Device Token	","	APNS believes that Device Token has expired.	"),
    IOS_CERTIFICATION_ERROR         	(	400	,	40004	,"	iOS Certification Error	","	Certificate error, connection refused by APNs	"),
    IOS_DUPLICATE_MESSAGE	            (	400	,	40005	,"	iOS Duplicate Message	","	Duplicate message	"),
    IOS_PRODUCTION_CERT_INVALID	        (	400	,	40006	,"	iOS Production Cert Invalid	","	Production certificate is invalid	"),
    IOS_DEVELOPMENT_CERT_INVALID    	(	400	,	40007	,"	iOS Development Cert Invalid	","	Development certificate is invalid	"),
    IOS_PRODUCTION_CERT_EXPIRE      	(	400	,	40008	,"	iOS Production Cert Expire	","	Production certificate expired	"),
    IOS_DEVELOPMENT_CERT_EXPIRE     	(	400	,	40009	,"	iOS Development Cert Expire	","	Development certificate expired	"),
    CERT_TYPE_ERROR_NEED_A_DEVELOPMENT_CERT	(	400	,	40010	,"	Cert Type Error, Need A Development Cert	","	Need to develop a certificate, wrongly uploaded the production certificate	"),
    CERT_TYPE_ERROR_NEED_A_PRODUCTION_CERT	(	400	,	40011	,"	Cert Type Error, Need A Production Cert	","	A production certificate is required, and the production certificate is incorrectly uploaded.	"),
    IOS_CERT_FILE_INVALID	(	400	,	40012	,"	iOS Cert File Invalid	","	iOS certificate is not legal	"),
    TIMER_TASK_NOT_EXIST	(	400	,	41001	,"	Timer Task Not Exist	","	Timed task does not exist	"),
    TIMER_TASK_DUPLICATED	(	400	,	41002	,"	Timer Task Duplicated	","	Duplicate timed tasks	"),
    TIMER_TASK_NUM_EXCEED	(	400	,	41003	,"	Timer Task Num Exceed	","	The number of scheduled tasks exceeds the limit	"),
    TIMER_TASK_WILL_BE_EXECUTED_CAN_NOT_BE_CANCELED	(	400	,	41004	,"	Timer Task Will Be Executed, Can Not Be Canceled	","	Scheduled tasks will be executed, no cancellation is allowed	"),
    TIMER_TASK_HAS_BEEN_EXECUTED	(	400	,	41005	,"	Timer Task Has Been Executed	","	Scheduled tasks have been executed	"),
    GENERATE_CSRF_TOKEN_FAILED	    (	500	,	50001	,"	Generate CSRF Token Failed	","	Failed to generate CSRF Token	"),
    INVALID_CSRF_TOKEN	            (	400	,	50002	,"	Invalid CSRF Token	","	Invalid CSRF Token	"),
    CSRF_TOKEN_EXPIRED	            (	400	,	50003	,"	CSRF Token Expired	","	CSRF Token has expired	"),
    PASSPORT_NOT_LOGIN	            (	400	,	50004	,"	Passport Not Login	","	Not logged in to Baidu account passport	"),
    INVALID_BDUSS	                (	400	,	50005	,"	Invalid BDUSS	","	Invalid baidu account session	"),
    REQUIRED_TO_REGISTER_AS_A_DEVELOPER	(	403	,	50006	,"	Required To Register AS A Developer	","	Not a developer	"),
    INVALID_DEVELOPER	            (	403	,	50007	,"	Invalid Developer	","	Invalid developer	"),
    INVALID_APPNAME	                (	400	,	50008	,"	Invalid Appname	","	Illegal application name	");


    @Getter
    private int httpCode;
    @Getter
    private int errorCode;
    @Getter
    private String errorMessage;
    @Getter
    private String description;

    BaiduErrorCode(int httpCode, int errorCode, String errorMessage, String description) {
        this.httpCode = httpCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
