package com.example.demo.constant;

public enum ResponseStatus {
    SUCCESS(StatusConstant.SUCCESS_CODE, StatusConstant.SUCCESS_MESSAGE),
    REQUIRED_ID(StatusConstant.REQUIRED_ID_CODE, StatusConstant.REQUIRED_ID_MESSAGE),
    REQUIRED_WORK_NAME(StatusConstant.REQUIRED_WORK_NAME_CODE, StatusConstant.REQUIRED_WORK_NAME_MESSAGE),
    REQUIRED_STARTING_DATE(StatusConstant.REQUIRED_STARTING_DATE_CODE, StatusConstant.REQUIRED_STARTING_DATE_MESSAGE),
    REQUIRED_ENDING_DATE(StatusConstant.REQUIRED_ENDING_DATE_CODE, StatusConstant.REQUIRED_ENDING_DATE_MESSAGE),
    REQUIRED_START_DATE_BEFORE_END_DATE(StatusConstant.REQUIRED_START_DATE_BEFORE_END_DATE_CODE, StatusConstant.REQUIRED_START_DATE_BEFORE_END_DATE_MESSAGE),
    REQUIRED_STATUS(StatusConstant.REQUIRED_STATUS_CODE, StatusConstant.REQUIRED_STATUS_MESSAGE),
    NOT_FOUND_WORK(StatusConstant.NOT_FOUND_WORK_CODE, StatusConstant.NOT_FOUND_WORK_MESSAGE),
    STATUS_INVALID(StatusConstant.STATUS_INVALID_CODE, StatusConstant.STATUS_INVALID_MESSAGE),
    ;

    private final int code;
    private final String description;

    ResponseStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ResponseStatus{" +
                "code=" + code +
                ", description='" + description + '\'' +
                '}';
    }
}
