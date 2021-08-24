package com.qooco.boost.data.mongo.entities;

import com.qooco.boost.data.mongo.embedded.*;
import com.qooco.boost.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.qooco.boost.data.enumeration.MessageCenterType.*;

@Document(collection = "MessageCenterDoc")
@Setter
@Getter
@Builder
@AllArgsConstructor
@FieldNameConstants
public class MessageCenterDoc {

    @Id
    private ObjectId id;
    private int type;
    private CompanyEmbedded company;
    private VacancyEmbedded vacancy;
    private BoostHelperEmbedded boostHelper;
    private long numberOfCandidate;
    private List<UserProfileCvMessageEmbedded> appliedUserProfiles;
    private List<UserProfileCvMessageEmbedded> contactPersons;

    private List<UserProfileCvMessageEmbedded> appointmentCandidates;
    private List<UserProfileCvMessageEmbedded> appointmentManagers;

    private List<UserProfileCvMessageEmbedded> requestedJoinUsers;
    private List<UserProfileCvMessageEmbedded> adminOfCompany;

    private List<UserProfileCvMessageEmbedded> freeChatRecruiter;
    private List<UserProfileCvMessageEmbedded> freeChatCandidate;

    private List<UserProfileCvMessageEmbedded> boostHelperUser;

    private Date createdDate;
    private Date updatedDate;
    private boolean isDeleted;
    private UserProfileCvEmbedded createdBy;

    public MessageCenterDoc(VacancyEmbedded vacancy) {
        super();
        this.type = VACANCY_CONVERSATION.value();
        this.vacancy = vacancy;
    }

    public MessageCenterDoc(CompanyEmbedded company) {
        super();
        this.type = AUTHORIZATION_CONVERSATION.value();
        this.company = company;
    }

    public MessageCenterDoc() {
        Date now = DateUtils.toServerTimeForMongo();
        createdDate = now;
        updatedDate = now;
    }

    public Long getCompanyId() {
        if ((AUTHORIZATION_CONVERSATION.value() == type
                || BOOST_SUPPORT_CHANNEL.value() == type)
                && Objects.nonNull(company)) {
            return company.getId();
        } else if (VACANCY_CONVERSATION.value() == type
                && Objects.nonNull(vacancy)
                && Objects.nonNull(vacancy.getCompany())) {
            return vacancy.getCompany().getId();
        }
        return null;
    }
}
