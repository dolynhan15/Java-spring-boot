package com.qooco.boost.models.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.models.dto.LatestTimeDTO;
import com.qooco.boost.models.dto.company.CompanyDTO;
import com.qooco.boost.models.dto.user.UserCurriculumVitaeDTO;
import com.qooco.boost.models.dto.vacancy.VacancyShortInformationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.MessageCenterType.*;
import static java.util.Optional.ofNullable;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageCenterDTO extends LatestTimeDTO {
    @Setter @Getter
    private String id;
    @Setter @Getter
    private int type;
    @Setter @Getter
    private BoostHelperDTO boostHelper;
    @Setter @Getter
    private CompanyDTO company;
    @Setter @Getter
    private VacancyShortInformationDTO vacancy;
    @Setter @Getter
    private List<UserCurriculumVitaeDTO> userCurriculumVitaes;
    @Setter @Getter
    private long unreadMessage;
    @Deprecated
    @JsonProperty("isJoiner")
    @Setter
    private boolean isJoiner;
    @JsonProperty("isAdmin")
    @Setter
    private boolean isAdmin;

    public MessageCenterDTO(MessageCenterDoc messageCenterDoc, long unreadMessage, int fromApp, Long userProfileId, Date lastUpdateTime, List<UserProfileCvEmbedded> participants, String locale) {
        setLastUpdateTime(lastUpdateTime);
        if (Objects.nonNull(messageCenterDoc)) {
            this.type = messageCenterDoc.getType();
            this.unreadMessage = unreadMessage;

            ofNullable(messageCenterDoc.getId()).ifPresent(it -> this.id = it.toHexString());
            ofNullable(messageCenterDoc.getCompany()).filter(it -> AUTHORIZATION_CONVERSATION.value() == type).ifPresent(it -> this.company = new CompanyDTO(it));
            ofNullable(messageCenterDoc.getVacancy()).filter(it -> VACANCY_CONVERSATION.value() == type).ifPresent(it -> this.vacancy = new VacancyShortInformationDTO(it, locale));

            List<Integer> boostHelperType = ImmutableList.of(BOOST_HELPER_CONVERSATION.value(), BOOST_SUPPORT_CHANNEL.value());
            ofNullable(messageCenterDoc.getBoostHelper()).filter(it -> boostHelperType.contains(type)).ifPresent(it -> this.boostHelper = new BoostHelperDTO(it));


            if (PROFILE_APP.value() == fromApp) {
                if (Objects.nonNull(messageCenterDoc.getContactPersons())
                        || Objects.nonNull(messageCenterDoc.getAppointmentManagers())) {
                    if (CollectionUtils.isNotEmpty(participants)) {
                        userCurriculumVitaes = participants.stream().map(it -> new UserCurriculumVitaeDTO(it, locale))
                                .collect(Collectors.toList());
                    }
                }
            } else {
                if (VACANCY_CONVERSATION.value() == type) {
                    if (CollectionUtils.isNotEmpty(messageCenterDoc.getAppliedUserProfiles())
                            || CollectionUtils.isNotEmpty(messageCenterDoc.getAppointmentCandidates())) {
                        if (CollectionUtils.isNotEmpty(participants)) {
                            userCurriculumVitaes = participants.stream().map(it -> new UserCurriculumVitaeDTO(it, locale))
                                    .collect(Collectors.toList());
                        }
                    } else if (AUTHORIZATION_CONVERSATION.value() == type) {

                        List<UserCurriculumVitaeDTO> admins = new ArrayList<>();
                        List<UserCurriculumVitaeDTO> joiners = new ArrayList<>();

                        if (Objects.nonNull(messageCenterDoc.getAdminOfCompany())) {
                            admins = messageCenterDoc.getAdminOfCompany().stream()
                                    .map(it -> new UserCurriculumVitaeDTO(it, locale)).collect(Collectors.toList());
                        }

                        if (Objects.nonNull(messageCenterDoc.getRequestedJoinUsers())) {
                            joiners = messageCenterDoc.getRequestedJoinUsers().stream()
                                    .map(it -> new UserCurriculumVitaeDTO(it, locale)).collect(Collectors.toList());
                        }
                        userCurriculumVitaes = admins;
                        boolean isExist = admins.stream()
                                .anyMatch(ad -> Objects.nonNull(ad.getUserProfile()) && userProfileId.equals(ad.getUserProfile().getId()));
                        if (isExist) {
                            userCurriculumVitaes = joiners;
                        }
                    }
                }
            }
        }
    }

    boolean isJoiner() {
        return isJoiner;
    }

    boolean isAdmin() {
        return isAdmin;
    }
}
