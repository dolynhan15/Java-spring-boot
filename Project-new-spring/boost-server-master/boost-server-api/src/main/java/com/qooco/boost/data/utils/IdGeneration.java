package com.qooco.boost.data.utils;

import com.qooco.boost.data.constants.Constants;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/15/2018 - 3:38 PM
*/
public class IdGeneration {

    public static final String generateTestHistory(long userId, long testId, long submissionTime) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(userId).append(Constants.UNDER_SCORE)
                .append(testId).append(Constants.UNDER_SCORE).append(submissionTime);
        return stringBuilder.toString();
    }

    public static final String generateOwnedPackage(long userId, String packageId, String lessonId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(userId).append(Constants.UNDER_SCORE)
                .append(packageId).append(Constants.UNDER_SCORE).append(lessonId);
        return stringBuilder.toString();
    }
}
