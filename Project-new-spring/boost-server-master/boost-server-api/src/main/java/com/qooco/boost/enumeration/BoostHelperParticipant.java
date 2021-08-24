package com.qooco.boost.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum BoostHelperParticipant {
    BOOST_GUIDE_PROFILE(-10L, "/images/chatbot/boost-guide.png", "boost_guide_name", "boost_guide_profile_description"),
    BOOST_GUIDE_SELECT(-11L, "/images/chatbot/boost-guide.png", "boost_guide_name", "boost_guide_select_description"),
    BOOST_ERROR(-12L, "/images/chatbot/boost-error.png", "boost_error_name", "boost_error_description"),
    BOOST_SUPPORTER(-13L, "/images/customercare/boost-support.png", "boost_support_name", "boost_support_description");

    private final long id;
    private final String avatar;
    private final String nameKey;
    private final String descriptionKey;

    public static List<Long> getIds() {
        return Arrays.stream(BoostHelperParticipant.values()).map(it -> it.id).collect(toImmutableList());
    }
}