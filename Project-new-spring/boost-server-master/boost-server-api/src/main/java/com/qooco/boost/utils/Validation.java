package com.qooco.boost.utils;

import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import org.codehaus.plexus.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validation {
    private static final String ATOM = "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]";
    private static final String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)+";
    private static final String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";
    private static final String EMAIL_PATTERN = "^" + ATOM + "+(\\." + ATOM + "+)*@" + DOMAIN + "|" + IP_DOMAIN + ")$";
    private static final String PASSWORD_PATTERN = "([A-Z]|[a-z]|[0-9]|\\s){6,}";
    private static final String USERNAME_PATTERN = "([A-Z]|[a-z]|[0-9]){6,16}";
    private final static String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpe?g|png))$)";
    private final static String IMAGE_CONTENT_TYPE_PATTERN = "(image(\\/(?i)(jpe?g|png|gif|bmp))$)";
    private final static String EXCEL_PATTERN = "([^\\s]+(\\.(?i)(xls|xlsx))$)";
    private static final String HTTP_PATTERN = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-zA-Z0-9]+([\\-\\.]{1}[a-zA-Z0-9]+)*\\.[a-zA-Z]{2,}(:[0-9]{1,5})?(\\/.*)?$";
    private static final String PHONE_NUMBER_PATTERN = "^\\+(?:[0-9] ?){6,14}[0-9]$";
    private static final String GDS_PATTERN = "([A-Z]|[a-z]|[0-9]|\\s){4,10}";
    //Special case for developer
    private static final String LOCAL_HOST = "http://192.168.78.48:";

    public static ResponseStatus validateEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new InvalidParamException(ResponseStatus.EMAIL_IS_EMPTY);
        }

        if (!isMatchRegexPattern(email.toLowerCase(), EMAIL_PATTERN)) {
            throw new InvalidParamException(ResponseStatus.EMAIL_WRONG_FORMAT);
        }

        return ResponseStatus.SUCCESS;
    }

    public static ResponseStatus validateVerificationCode(String code) {
        if (StringUtils.isBlank(code)) {
            throw new InvalidParamException(ResponseStatus.VERIFICATION_CODE_EMPTY);
        }

        return ResponseStatus.SUCCESS;
    }

    /**
     * Validate password
     *
     * @param passwordStr passwordStr
     * @return ResponseStatus.SUCCESS or ResponseStatus.PASSWORD_IS_EMPTY or ResponseStatus.PASSWORD_WRONG_FORMAT
     */
    public static ResponseStatus validatePassword(String passwordStr) {
        if (StringUtils.isBlank(passwordStr)) {
            throw new InvalidParamException(ResponseStatus.PASSWORD_IS_EMPTY);
        }

        if (!isMatchRegexPattern(passwordStr, PASSWORD_PATTERN)) {
            throw new InvalidParamException(ResponseStatus.PASSWORD_WRONG_FORMAT);
        }
        return ResponseStatus.SUCCESS;
    }

    /**
     * Validate username
     *
     * @param username username
     * @return ResponseStatus.SUCCESS or ResponseStatus.USERNAME_WRONG_FORMAT or ResponseStatus.USERNAME_EMPTY
     */
    public static ResponseStatus validateUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new InvalidParamException(ResponseStatus.USERNAME_EMPTY);
        }

        if (!isMatchRegexPattern(username, USERNAME_PATTERN)) {
            throw new InvalidParamException(ResponseStatus.USERNAME_WRONG_FORMAT);
        }

        return ResponseStatus.SUCCESS;
    }

    public static boolean validateHttpOrHttps(String domain) {
        if (StringUtils.isBlank(domain)) {
            return false;
        }

        //Only for developer environment
        if (domain.startsWith(LOCAL_HOST)) {
            return true;
        }

        return isMatchRegexPattern(domain, HTTP_PATTERN);
    }

    public static boolean validateImageContentType(String contentType) {
        return isMatchRegexPattern(contentType, IMAGE_CONTENT_TYPE_PATTERN);
    }

    public static boolean validateExcelExtension(String extension) {
        return isMatchRegexPattern("matcher." + extension, EXCEL_PATTERN);
    }

    private static Boolean isMatchRegexPattern(String string, String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matchResult = pattern.matcher(string);

        return matchResult.matches();
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            return false;
        }
        return isMatchRegexPattern(phoneNumber, PHONE_NUMBER_PATTERN);
    }
}
