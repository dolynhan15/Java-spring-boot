package com.qooco.boost.business.impl;

import com.google.common.collect.ImmutableMap;
import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.data.oracle.entities.UserAttribute;
import com.qooco.boost.data.oracle.entities.UserAttributeEvent;
import com.qooco.boost.data.oracle.services.ProfileAttributeEventService;
import com.qooco.boost.data.oracle.services.UserAttributeEventService;
import com.qooco.boost.data.oracle.services.UserAttributeService;
import com.qooco.boost.data.oracle.services.UserProfileService;
import com.qooco.boost.enumeration.ProfileStep;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResultV2;
import com.qooco.boost.models.dto.attribute.AttributeDTO;
import com.qooco.boost.models.request.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.qooco.boost.constants.AttributeEventType.*;
import static com.qooco.boost.enumeration.ProfileStep.JOB_PROFILE_STEP;
import static java.lang.Math.pow;
import static java.time.Clock.systemDefaultZone;
import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;
import static java.util.Date.from;
import static java.util.Optional.ofNullable;

@Service
public class BusinessProfileAttributeEventServiceImpl implements BusinessProfileAttributeEventService {
    private static final Map<Integer, Integer> EVT_ENTER_APP_MAP = ImmutableMap.of(
            EVT_ENTER_APP_EVERYDAY_FOR_3_DAYS, 3,
            EVT_ENTER_APP_EVERYDAY_FOR_7_DAYS, 7,
            EVT_ENTER_APP_EVERYDAY_FOR_14_DAYS, 14
    );

    private static final int MAX_PROFILE_STEP_COUNT = (int) pow(2, JOB_PROFILE_STEP.value) - 1;

    @Autowired(required = false)
    private Clock clock = systemDefaultZone();
    @Autowired
    private ProfileAttributeEventService profileAttributeEventService;
    @Autowired
    private UserAttributeEventService userAttributeEventService;
    @Autowired
    private UserAttributeService userAttributeService;
    @Autowired
    private UserProfileService userProfileService;

    public void onEnterAppEvent(Long userProfileId) {
        var time = now(clock);
        var uae = ofNullable(userAttributeEventService.findByUserAttributeAndEvent(userProfileId, null, EVT_ENTER_APP_EVERYDAY_FOR_3_DAYS))
                .orElseGet(() -> new UserAttributeEvent().toBuilder()
                        .userProfile(userProfileService.findById(userProfileId))
                        .eventCode(EVT_ENTER_APP_EVERYDAY_FOR_3_DAYS)
                        .count(1)
                        .build());
        var lastDate = ofInstant(uae.getUpdatedDate().toInstant(), systemDefault()).toLocalDate();
        uae.setUpdatedDate(from(time.atZone(systemDefault()).toInstant()));

        if (uae.getId() == null) {
            userAttributeEventService.insert(uae);
        } else if (lastDate.plusDays(1).equals(time.toLocalDate())) {
            Optional.of(userAttributeEventService.updateCountBeforeDate(uae.setCount(uae.getCount() + 1))).filter(it -> it > 0).ifPresent(e ->
                    EVT_ENTER_APP_MAP.entrySet().stream().filter(ent -> uae.getCount() % ent.getValue() == 0).forEach(ent -> onAttributeEvent(ent.getKey(), userProfileId)));
        } else if (!time.toLocalDate().equals(lastDate)) {
            userAttributeEventService.updateCountBeforeDate(uae.setCount(1));
        }
    }

    public void onUserProfileStep(Authentication authentication, ProfileStep profileStep) {
        onUserProfileStep(getUserId(authentication), profileStep);
    }

    public void onUserProfileStep(Long userProfileId, ProfileStep profileStep) {
        var time = now(clock);
        var uae = ofNullable(userAttributeEventService.findByUserAttributeAndEvent(userProfileId, null, EVT_FILL_THE_PROFILE_IN_ONE_SESSION))
                .orElseGet(() -> new UserAttributeEvent().toBuilder()
                        .userProfile(userProfileService.findById(userProfileId))
                        .eventCode(EVT_FILL_THE_PROFILE_IN_ONE_SESSION)
                        .count((int) pow(2, profileStep.value - 1))
                        .build());
        if (uae.getId() == null) {
            userAttributeEventService.insert(uae);
        } else if ((uae.getCount() & (1 << (profileStep.value - 1))) == 0) {
            var unfilled = (uae.getCount() & MAX_PROFILE_STEP_COUNT) != MAX_PROFILE_STEP_COUNT;
            uae.setCount(((1 << (profileStep.value - 1)) | uae.getCount()));
            uae.setUpdatedDate(from(time.atZone(systemDefault()).toInstant()));
            if ((userAttributeEventService.save(uae).getCount() & MAX_PROFILE_STEP_COUNT) == MAX_PROFILE_STEP_COUNT && unfilled && uae.getCreatedDate().toInstant().isAfter(time.minusDays(1).atZone(systemDefault()).toInstant())) {
                onAttributeEvent(EVT_FILL_THE_PROFILE_IN_ONE_SESSION, userProfileId);
            }
        }
    }

    public void onAttributeEvent(int eventCode, Authentication authentication) {
        onAttributeEvent(eventCode, getUserId(authentication));
    }

    public void onAttributeEvent(int eventCode, Long userProfileId) {
        switch (eventCode) {
            case EVT_ENTER_APPLICATION:
                onEnterAppEvent(userProfileId);
                return;
        }

        var userProfile = userProfileService.findById(userProfileId);
        profileAttributeEventService.findByEventCode(eventCode).forEach(it -> {
            var notexists = userAttributeEventService.adjustCount(userProfileId, it.getAttribute().getId(), eventCode, 1) == 0;
            if (notexists) {
                userAttributeEventService.insert(new UserAttributeEvent().toBuilder()
                        .userProfile(userProfile)
                        .attribute(it.getAttribute())
                        .eventCode(eventCode)
                        .build());
                userAttributeEventService.adjustCount(userProfileId, it.getAttribute().getId(), eventCode, 1);
            }
            if ((notexists || it.isRepeatable()) && userAttributeService.adjustScore(userProfileId, it.getAttribute(), it.getScore()) == 0) {
                userAttributeService.insert(new UserAttribute().toBuilder()
                        .userProfile(userProfile)
                        .attribute(it.getAttribute())
                        .build());
                userAttributeService.adjustScore(userProfileId, it.getAttribute(), it.getScore());
            }
        });
    }

    @Async
    public Future<Object> onAttributeEventAsync(int eventCode, Authentication authentication) {
        onAttributeEvent(eventCode, authentication);
        return new AsyncResult<>(null);
    }

    @Async
    public Future<Object> onAttributeEventAsync(int eventCode, Long userProfileId) {
        onAttributeEvent(eventCode, userProfileId);
        return new AsyncResult<>(null);
    }

    @Override
    public BaseResp getProfileAttributeEvent(Authentication auth, Long userProfileId, boolean hasLevel, PageRequest pageRequest) {
        var userAttributes = hasLevel && Objects.nonNull(userProfileId)
                ? userAttributeService.findActiveByUserProfile(userProfileId, pageRequest.getPage(), pageRequest.getSize())
                : userAttributeService.findByUserProfile(getUserId(auth), pageRequest.getPage(), pageRequest.getSize());
        var result = userAttributes.getContent().stream().map(AttributeDTO::new).collect(Collectors.toList());
        return new BaseResp<>(new PagedResultV2<>(result, pageRequest.getPage(), userAttributes));
    }

    @Override
    public BaseResp getAttributeLevelUp(Authentication auth) {
        var attributes = userAttributeService.findNewLevelByUserProfile(getUserId(auth));
        var result = attributes.stream().map(AttributeDTO::new).collect(toImmutableList());
        if (CollectionUtils.isNotEmpty(result)) {
            userAttributeService.updateNewLevel(attributes.stream().map(UserAttribute::getId).collect(toImmutableList()), false);
        }
        return new BaseResp<>(result);
    }
}
