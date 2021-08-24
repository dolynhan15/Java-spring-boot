package com.qooco.boost.data.enumeration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UploadType {
    AVATAR("avatar"),
    EXPERIENCE_JOB("experience_job"),
    COMPANY_LOGO("company_logo"),
    VACANCY_LOGO("vacancy_logo"),
    GIFT_IMAGE("gift_image"),
    PERSONAL_PHOTO("personal_photo"),
    EXCEL_FILE("excel_file"),
    MEDIA_MESSAGE("media_message");
    private final String path;
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path;
    }
}
