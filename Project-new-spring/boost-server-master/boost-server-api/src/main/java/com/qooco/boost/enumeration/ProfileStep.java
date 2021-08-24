package com.qooco.boost.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum ProfileStep {
    EMPTY_PROFILE_STEP(0),
    BASIC_PROFILE_STEP(1),
    ADVANCED_PROFILE_STEP(2),
    JOB_PROFILE_STEP(3),
    PREVIOUS_EXPERIENCE_STEP(4),
    PERSONAL_INFORMATION_STEP(5),
    QUALIFICATION_STEP(6);

    @Getter @Accessors(fluent = true)
    public final int value;
}
