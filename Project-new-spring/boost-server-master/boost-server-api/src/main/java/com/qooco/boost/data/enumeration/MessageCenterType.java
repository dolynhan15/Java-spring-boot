package com.qooco.boost.data.enumeration;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public enum MessageCenterType {
    AUTHORIZATION_CONVERSATION(0),
    VACANCY_CONVERSATION(1),
    BOOST_HELPER_CONVERSATION(2),
    BOOST_SUPPORT_CHANNEL(3);

    @Getter @Accessors(fluent = true)
    private final int value;

    public static List<Integer> forCareer(){
        return ImmutableList.of(VACANCY_CONVERSATION.value, BOOST_HELPER_CONVERSATION.value, BOOST_SUPPORT_CHANNEL.value);
    }

    public static MessageCenterType fromValue(Integer type) {
        return ofNullable(type)
                .map(value -> Arrays.stream(MessageCenterType.values())
                        .filter(it -> it.value == value)
                        .findFirst().orElse(null))
                .orElse(null);
    }

}
