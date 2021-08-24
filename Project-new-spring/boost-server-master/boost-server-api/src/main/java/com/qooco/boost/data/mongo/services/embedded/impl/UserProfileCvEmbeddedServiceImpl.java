package com.qooco.boost.data.mongo.services.embedded.impl;

import com.google.common.collect.ImmutableList;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.enumeration.doc.AppointmentDetailDocEnum;
import com.qooco.boost.data.mongo.embedded.UserProfileBasicEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.*;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import com.qooco.boost.data.mongo.services.embedded.UserProfileCvEmbeddedService;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class UserProfileCvEmbeddedServiceImpl implements UserProfileCvEmbeddedService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void update(UserProfileCvEmbedded embedded) {
        updateInCompanyDoc(embedded);
        updateInVacancyDoc(embedded);
        updateInAppointmentDetailDoc(embedded);

        updateInMessageCenterDoc(embedded);
        updateInConversationDoc(embedded);
        updateInViewProfileDoc(embedded);
    }

    private void updateInCompanyDoc(UserProfileCvEmbedded user) {
        if (Objects.nonNull(user) && UserType.SELECT == user.getUserType()) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, CompanyDoc.class);
            ImmutableList.of(
                    CompanyDoc.Fields.admins,
                    CompanyDoc.Fields.staffs
            ).forEach(it -> genUpdaterForUserInList(it, user, bulkOps));
            bulkOps.execute();
        }
    }

    private void updateInMessageCenterDoc(UserProfileCvEmbedded user) {
        if (Objects.nonNull(user)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MessageCenterDoc.class);

            switch (user.getUserType()) {
                case UserType.SELECT:
                    genUpdaterForUser(MessageCenterDoc.Fields.createdBy, user, bulkOps);

                    ImmutableList.of(
                            MessageCenterDoc.Fields.adminOfCompany,
                            MessageCenterDoc.Fields.requestedJoinUsers,
                            MessageCenterDoc.Fields.contactPersons,
                            MessageCenterDoc.Fields.appointmentManagers,
                            MessageCenterDoc.Fields.freeChatRecruiter,
                            MessageCenterDoc.Fields.boostHelper
                    ).forEach(it -> genUpdaterForUserInList(it, user, bulkOps));
                    bulkOps.execute();
                    break;
                case UserType.PROFILE:
                    ImmutableList.of(
                            MessageCenterDoc.Fields.appliedUserProfiles,
                            MessageCenterDoc.Fields.appointmentCandidates,
                            MessageCenterDoc.Fields.freeChatCandidate,
                            MessageCenterDoc.Fields.boostHelperUser
                    ).forEach(it -> genUpdaterForUserInList(it, user, bulkOps));
                    bulkOps.execute();
                    break;
                default:
                    break;
            }
        }
    }

    private void updateInConversationDoc(UserProfileCvEmbedded user) {
        if (Objects.nonNull(user)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ConversationDoc.class);

            genUpdaterForUserInList(ConversationBase.Fields.participants, user, bulkOps);
            if (UserType.SELECT == user.getUserType()) {
                genUpdaterForUser(ConversationBase.Fields.createdBy, user, bulkOps);
            }
            bulkOps.execute();
        }
    }

    private void updateInAppointmentDetailDoc(UserProfileCvEmbedded user) {
        if (Objects.nonNull(user)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, AppointmentDetailDoc.class);

            Update updater;
            Criteria criteria;
            switch (user.getUserType()) {
                case UserType.SELECT:
                    criteria = Criteria.where(AppointmentDetailDocEnum.APPOINTMENT_MANAGER_PROFILE_ID.getKey()).is(user.getUserProfileId());
                    updater = new Update().set(AppointmentDetailDocEnum.APPOINTMENT_MANAGER_PROFILE.getKey(), user);
                    bulkOps.updateMulti(new Query(criteria), updater);


                    criteria = Criteria.where(AppointmentDetailDocEnum.VACANCY_STAFF_PROFILE_ID.getKey()).is(user.getUserProfileId());
                    updater = new Update().set(AppointmentDetailDocEnum.VACANCY_STAFF_PROFILE.getKey(), user);
                    bulkOps.updateMulti(new Query(criteria), updater);


                    criteria = Criteria.where(AppointmentDetailDocEnum.APPOINTMENT_CREATOR_PROFILE_ID.getKey()).is(user.getUserProfileId());
                    updater = new Update().set(AppointmentDetailDocEnum.APPOINTMENT_CREATOR_PROFILE.getKey(), user);
                    bulkOps.updateMulti(new Query(criteria), updater);

                    criteria = Criteria.where(AppointmentDetailDocEnum.APPOINTMENT_UPDATER_PROFILE_ID.getKey()).is(user.getUserProfileId());
                    updater = new Update().set(AppointmentDetailDocEnum.APPOINTMENT_UPDATER_PROFILE.getKey(), user);
                    bulkOps.updateMulti(new Query(criteria), updater);

                    bulkOps.execute();
                    break;
                case UserType.PROFILE:
                    criteria = Criteria.where(AppointmentDetailDocEnum.CANDIDATE_USER_PROFILE_CV_ID.getKey()).is(user.getUserProfileCvId());
                    updater = new Update().set(AppointmentDetailDocEnum.CANDIDATE.getKey(), user);
                    bulkOps.updateMulti(new Query(criteria), updater);

                    bulkOps.execute();
                    break;
                default:
                    break;
            }
        }
    }

    private void updateInVacancyDoc(UserProfileCvEmbedded user) {
        if (UserType.SELECT == user.getUserType()) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, VacancyDoc.class);

            ImmutableList.of(
                    VacancyDoc.Fields.contactPerson,
                    VacancyDoc.Fields.createdByStaff
            ).forEach(it -> genUpdaterForUser(it, user, bulkOps));

            genUpdaterForUserInList(VacancyDoc.Fields.candidateProfiles, user, bulkOps);

            bulkOps.execute();
        }
    }

    private void updateInViewProfileDoc(UserProfileCvEmbedded user) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ViewProfileDoc.class);
        switch (user.getUserType()) {
            case UserType.SELECT:
                genUpdaterForUser(ViewProfileDoc.Fields.viewer, user, bulkOps);
                bulkOps.execute();
                break;
            case UserType.PROFILE:
                genUpdaterForUser(ViewProfileDoc.Fields.candidate, user, bulkOps);
                bulkOps.execute();
                break;
            default:
                break;
        }
    }

    private void genUpdaterForUser(String property, UserProfileCvEmbedded user, BulkOperations bulkOps) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(UserProfileBasicEmbedded.Fields.userProfileId).is(user.getUserProfileId()),
                Criteria.where(UserProfileBasicEmbedded.Fields.userType).is(user.getUserType())
        );

        String prefix = StringUtil.append(property, Constants.DOT);
        Map<String, Object> map = MongoInitData.initUserProfileCvEmbedded(prefix, user);
        if (MapUtils.isNotEmpty(map)) {
            Update updater = new Update();
            map.forEach(updater::set);
            bulkOps.updateMulti(new Query(criteria), updater);
        }
    }

    private void genUpdaterForUserInList(String property, UserProfileCvEmbedded user, BulkOperations bulkOps) {
        Criteria criteriaUser = new Criteria().andOperator(
                Criteria.where(UserProfileBasicEmbedded.Fields.userProfileId).is(user.getUserProfileId()),
                Criteria.where(UserProfileBasicEmbedded.Fields.userType).is(user.getUserType())
        );

        Criteria criteria = Criteria.where(property).elemMatch(criteriaUser);

        String prefix = StringUtil.append(property, Constants.DOLLAR);
        Map<String, Object> map = MongoInitData.initUserProfileCvEmbedded(prefix, user);
        if (MapUtils.isNotEmpty(map)) {
            Update updater = new Update();
            map.forEach(updater::set);
            bulkOps.updateMulti(new Query(criteria), updater);
        }
    }
}
