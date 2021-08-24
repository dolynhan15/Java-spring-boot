package com.qooco.boost.data.enumeration;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum CompanyRole {
    ADMIN(1, "ADMIN"),
    HEAD_RECRUITER(2, "HEAD_RECRUITER"),
    RECRUITER(3, "RECRUITER"),
    ANALYST(4, "ANALYST"),
    NORMAL_USER(5, "NORMAL_USER");

    private final long code;
    private final String name;

    CompanyRole(long code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getCode() {
        return code;
    }

    // TODO: Relate to appointment
    public List<Long> getRolesSmallerOrAdminRole() {
        if (this.getName().equals(CompanyRole.HEAD_RECRUITER.getName())) {
            return Lists.newArrayList(CompanyRole.RECRUITER.getCode());
        } else if (this.getName().equals(CompanyRole.ADMIN.getName())) {
            return Lists.newArrayList(CompanyRole.RECRUITER.getCode(), CompanyRole.HEAD_RECRUITER.getCode(), CompanyRole.ADMIN.getCode());
        }
        return new ArrayList<>();
    }

    public List<String> getRolesForAppointmentManager() {
        if (this.getName().equals(CompanyRole.ADMIN.name())) {
            return Arrays.asList(CompanyRole.ADMIN.name(), CompanyRole.HEAD_RECRUITER.name(), CompanyRole.RECRUITER.name(), CompanyRole.ANALYST.name());
        } else if (this.getName().equals(CompanyRole.HEAD_RECRUITER.name())) {
            return Arrays.asList(CompanyRole.RECRUITER.name());
        }
        return new ArrayList<>();
    }

    //TODO: Relase to appointmnet
    public List<String> getRolesLarger() {
        if (this.getName().equals(CompanyRole.HEAD_RECRUITER.getName())) {
            return Collections.singletonList(CompanyRole.ADMIN.name());
        } else if (this.getName().equals(CompanyRole.RECRUITER.getName())) {
            return Arrays.asList(CompanyRole.ADMIN.name(), CompanyRole.HEAD_RECRUITER.name());
        } else if (this.getName().equals(CompanyRole.ANALYST.getName())) {
            return Arrays.asList(CompanyRole.ADMIN.name(), CompanyRole.HEAD_RECRUITER.name(), CompanyRole.RECRUITER.name());
        }
        return new ArrayList<>();
    }

    public List<String> getRolesEqualOrLessNoAnalyst() {
        if (this.getName().equals(CompanyRole.ADMIN.name())) {
            return Arrays.asList(CompanyRole.ADMIN.name(), CompanyRole.HEAD_RECRUITER.name(), CompanyRole.RECRUITER.name());
        } else if (this.getName().equals(CompanyRole.HEAD_RECRUITER.name())) {
            return Arrays.asList(CompanyRole.HEAD_RECRUITER.name(), CompanyRole.RECRUITER.name());
        } else if (this.getName().equals(CompanyRole.RECRUITER.name())) {
            return Collections.singletonList(CompanyRole.RECRUITER.name());
        }
        return new ArrayList<>();
    }

    public List<String> getRolesForStatictisDashboard() {
        return Arrays.asList(CompanyRole.ADMIN.name(), CompanyRole.HEAD_RECRUITER.name(), CompanyRole.ANALYST.name());
    }
}
