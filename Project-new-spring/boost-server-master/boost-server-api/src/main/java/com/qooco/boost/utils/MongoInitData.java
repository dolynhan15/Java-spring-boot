package com.qooco.boost.utils;

import com.qooco.boost.data.enumeration.doc.VacancyDocEnum;
import com.qooco.boost.data.enumeration.embedded.*;
import com.qooco.boost.data.mongo.embedded.*;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentDetailEmbedded;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;
import com.qooco.boost.data.mongo.embedded.message.AppliedMessage;
import com.qooco.boost.data.mongo.embedded.message.AppointmentDetailMessage;
import com.qooco.boost.data.mongo.embedded.message.AuthorizationMessage;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MongoInitData {
    public static Map<String, Object> initCompanyEmbedded(String prefix, CompanyEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(CompanyEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initJobEmbedded(String prefix, JobEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(JobEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initCityEmbedded(String prefix, CityEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(CityEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initProvinceEmbedded(String prefix, ProvinceEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(ProvinceEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initLocationEmbedded(String prefix, LocationEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(LocationEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initUserProfileEmbedded(String prefix, UserProfileEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(UserProfileEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initUserProfileCvEmbedded(UserProfileCvEmbedded embedded, String... prefixes) {
        Map<String, Object> result = new HashMap<>();
        if(ArrayUtils.isNotEmpty(prefixes)) {
            Arrays.stream(prefixes).forEach(prefix -> result.putAll(MongoInitData.initUserProfileCvEmbedded(prefix, embedded)));
        }
        return result;
    }

    public static Map<String, Object> initUserProfileCvEmbedded(String prefix, UserProfileCvEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(UserProfileCvEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initUserProfileBasicEmbedded(String prefix, UserProfileBasicEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(UserProfileEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initRoleCompanyEmbedded(String prefix, RoleCompanyEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(RoleCompanyEmbeddedEum.values()).forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initStaffEmbedded(String prefix, StaffEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(StaffEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initStaffShortEmbedded(String prefix, StaffShortEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(StaffShortEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initEducationEmbedded(String prefix, EducationEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(EducationEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initCurrencyEmbedded(String prefix, CurrencyEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(CurrencyEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initCountryEmbedded(String prefix, CountryEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(CountryEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initVacancyEmbedded(String prefix, VacancyEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(VacancyEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initAppliedMessage(String prefix, AppliedMessage embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(AppliedMessageEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initAuthorizationMessage(String prefix, AuthorizationMessage embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(AuthorizationMessageEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initAppointmentDetailMessage(String prefix, AppointmentDetailMessage embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(AppointmentDetailMessageEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initAppointmentEmbedded(String prefix, AppointmentEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(AppointmentEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    public static Map<String, Object> initVacancyDoc(VacancyDoc embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(VacancyDocEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum("", f.getValue(embedded), f.key())));
        }
        return result;
    }

    public static Map<String, Object> initAppointmentCandidateEmbedded(String prefix, AppointmentDetailEmbedded embedded) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(embedded)) {
            Arrays.stream(AppointmentCandidateEmbeddedEnum.values())
                    .forEach(f -> result.putAll(getValueFromEnum(prefix, f.getValue(embedded), f.getKey())));
        }
        return result;
    }

    private static Map<String, Object> getValueFromEnum(String prefix, Object value, String key) {
        Map<String, Object> result = new HashMap<>();
        if (value instanceof Map) {
            result.putAll(appendPrefix(prefix, (Map<String, Object>) value));
        } else {
            result.put(StringUtil.append(prefix, key), value);
        }
        return result;
    }

    private static Map<String, Object> appendPrefix(String prefix, Map<String, Object> resource) {
        Map<String, Object> result = new HashMap<>();
        resource.forEach((key, value) -> result.put(StringUtil.append(prefix, key), value));
        return result;
    }
}

