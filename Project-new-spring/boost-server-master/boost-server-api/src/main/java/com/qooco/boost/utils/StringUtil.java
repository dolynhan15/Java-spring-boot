package com.qooco.boost.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class StringUtil {

    public static final String EMPTY_STRING = "";
    private static final String WHITE_SPACE = " ";
    private static final String SPLIP_PHONE_REGEX = "(?<=\\G...)";

    public static String safeToConcencate(String value) {
        return value == null ? EMPTY_STRING : value;
    }

    public static String generateAccessToken(String username, String userId) {
        StringBuilder builder = new StringBuilder(username);
        builder.append(userId).append(System.currentTimeMillis());
        return CryptUtils.sha256(builder.toString());
    }

    public static String append(String... params) {
        if (null != params) {
            StringBuilder result = new StringBuilder();
            for (String str : params) {
                result.append(str != null ? str : "");
            }
            return result.toString();
        }
        return "";
    }

    public static List<String> convertToList(String json) {
        if (StringUtils.isNotBlank(json)) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            Gson gson = new Gson();
            return gson.fromJson(json, type);
        }
        return null;
    }

    public static String[] convertToArray(String json) {
        if (StringUtils.isNotBlank(json)) {
            Type type = new TypeToken<String[]>() {}.getType();
            Gson gson = new Gson();
            return gson.fromJson(json, type);
        }
        return null;
    }

    public static String convertToJson(Object message) {
        return new GsonBuilder().create().toJson(message);
    }

    public static String convertToJsonByJackson(Object message) {
        DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_a_z");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(df);
        try {
            return mapper.writeValueAsString(message);//Plain JSON
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String convertToJson(List<String> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            return new Gson().toJson(list);
        }
        return null;
    }

    public static String format(String name){
        String _name = "";
        if(StringUtils.isNotBlank(name)){
            _name = name.trim().replaceAll(" +", " ");
        }
        return _name;
    }

    public static boolean isEmpty(String text) {
        return !Objects.nonNull(text) || text.trim().isEmpty();
    }

    public static boolean isContainBrace(String text) {
        return Objects.nonNull(text) && (text.contains("{") || text.contains("}"));
    }

    public static Long parseAsLong(final String s) {
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException numberFormatException) {
            return null;
        }
    }

    public static String getPhoneFormat(String phoneNumber) {
        if (Objects.nonNull(phoneNumber)) {
            String phoneReverse = StringUtils.reverse(phoneNumber.trim());
            int whiteSpaceIndex = phoneReverse.indexOf(WHITE_SPACE);
            if (whiteSpaceIndex < 0) {
                String[] phoneArrays = phoneNumber.split(SPLIP_PHONE_REGEX);
                StringBuilder builder = new StringBuilder();
                for (String phoneArray : phoneArrays) {
                    if (phoneArray.length() != 1) {
                        builder.append(WHITE_SPACE).append(phoneArray);
                    } else {
                        builder.append(phoneArray);
                    }
                }
                return builder.toString();

            }
            String phoneNumberNoCode = StringUtils.reverse(phoneReverse.trim().substring(0, whiteSpaceIndex)).replace(WHITE_SPACE, EMPTY_STRING);
            String[] phoneArrays = phoneNumberNoCode.split(SPLIP_PHONE_REGEX);
            String phoneCode = phoneReverse.substring(whiteSpaceIndex + 1);
            StringBuilder builder = new StringBuilder(StringUtils.reverse(phoneCode));
            for (String phoneArray : phoneArrays) {
                if (phoneArray.length() != 1) {
                    builder.append(WHITE_SPACE).append(phoneArray);
                } else {
                    builder.append(phoneArray);
                }
            }
            return builder.toString();
        }
        return EMPTY_STRING;
    }

}
