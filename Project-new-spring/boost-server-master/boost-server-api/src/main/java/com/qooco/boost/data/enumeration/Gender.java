package com.qooco.boost.data.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public enum Gender {
    MALE(0),
    FEMALE(1),
    NON_BINARY(2),
    TRANS_GENDER(3),
    OTHER(4),
    DECLINE_TO_STATE(5);


    @Getter
    @Accessors(fluent = true)
    private final int value;

    public static Gender fromValue(Integer gender) {
        return ofNullable(gender)
                .map(value -> Arrays.stream(Gender.values())
                        .filter(it -> it.value == value)
                        .findFirst().orElse(DECLINE_TO_STATE))
                .orElse(null);
    }
}
