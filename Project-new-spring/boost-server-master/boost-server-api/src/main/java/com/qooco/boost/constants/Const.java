package com.qooco.boost.constants;

import com.google.common.collect.ImmutableList;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/8/2018 - 10:17 AM
 */
public class Const {
    public static class Vacancy {
        public class Status {
            public static final int OPENING = 1;
            public static final int PERMANENT_SUSPEND = 2;
            public static final int INACTIVE = 3;
            public static final int SUSPEND = 4;
        }

        public class ClassifyAction {
            public static final int APPLIED = 1;
            public static final int REJECTED = 2;
        }

        public static class CandidateStatus {
            public static final int UNKNOWN = 0;
            public static final int RECRUITED = 1;
            public static final int NOT_RECRUITED = 2;

            public static final List<Integer> ALL_CANDIDATE_STATUSES = ImmutableList.of(UNKNOWN, RECRUITED, NOT_RECRUITED);
        }

        public class CancelAppointmentReason {
            public static final int UNKNOWN = 0;
            public static final int RECRUITED = 1;
            public static final int SUSPEND = 2;
            public static final int INACTIVE = 3;
            public static final int UNASSIGNED_ROLE = 4;
            public static final int NOT_RECRUITED = 5;
        }
    }

    public class VacancySeatStatus {
        public static final int OPENING = 1;
        public static final int CLOSED = 2;
        public static final int SUSPENDED = 3;
        public static final int TEMPORARY_SUSPENDDED = 4;
    }

    public class PushTarget {
        public static final int PUSH_TO_HOTEL = 1;
        public static final int PUSH_TO_CAREER = 2;
        public static final int PUSH_TO_WEB = 3;

        public static final int PUSH_TO_ALL = 999;
    }

    public class Platform {
        public static final String WEB = "WEB";
        public static final String PC = "PC";
        public static final String ANDROID = "ANDROID";
        public static final String IOS = "IOS";
        public static final String WP = "WP";
    }
}
