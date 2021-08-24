package com.qooco.boost.threads;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.core.thread.SpringExtension;
import com.qooco.boost.data.mongo.embedded.StaffShortEmbedded;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.models.sdo.VacancyClonedSDO;
import com.qooco.boost.models.transfer.BoostHelperFitTransfer;
import com.qooco.boost.models.transfer.BoostHelperProfileTransfer;
import com.qooco.boost.models.transfer.ViewProfileTransfer;
import com.qooco.boost.threads.actors.*;
import com.qooco.boost.threads.actors.syncs.AssessmentActor;
import com.qooco.boost.threads.actors.syncs.LocalizationActor;
import com.qooco.boost.threads.actors.syncs.SyncDataOfEachUserInMongoActor;
import com.qooco.boost.threads.models.*;
import com.qooco.boost.threads.models.messages.AppliedVacancyMessage;
import com.qooco.boost.threads.models.messages.ChangeContactPersonApplicantMessage;
import com.qooco.boost.utils.MongoConverters;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.qooco.boost.constants.Const.Platform.ANDROID;
import static com.qooco.boost.constants.Const.Platform.IOS;
import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;

@Component
public class BoostActorManager {

    @Autowired
    private ActorSystem system;

    public void deleteFile(List<String> relativePaths) {
        if (CollectionUtils.isNotEmpty(relativePaths)) {
            ActorRef deleter = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system).props("deleteFileActor"));
            for (String path : relativePaths) {
                deleter.tell(path, ActorRef.noSender());
            }
        }
    }

    public void updateCompanyInMongo(Company company) {
        if (Objects.nonNull(company)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(UpdateCompanyInMongoActor.ACTOR_NAME));
            updater.tell(company, ActorRef.noSender());
        }
    }

    public void addStaffToCompanyInMongo(Staff staff) {
        if (Objects.nonNull(staff)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(UpdateCompanyInMongoActor.ACTOR_NAME));
            updater.tell(staff, ActorRef.noSender());
        }
    }

    public void updateStaffToCompanyInMongo(StaffShortEmbedded staff) {
        if (Objects.nonNull(staff)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(UpdateCompanyInMongoActor.ACTOR_NAME));
            updater.tell(staff, ActorRef.noSender());
        }
    }

    public void saveUserCvInMongo(UserCurriculumVitae userCurriculumVitae) {
        if (Objects.nonNull(userCurriculumVitae)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveUserCvInMongoActor.ACTOR_NAME));
            updater.tell(userCurriculumVitae, ActorRef.noSender());
        }
    }

    public void saveUserCvInMongo(UserFit userFit) {
        if (Objects.nonNull(userFit)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveUserCvInMongoActor.ACTOR_NAME));
            updater.tell(userFit, ActorRef.noSender());
        }
    }

    public void saveUserPersonalityInOracleAndMongo(UserProfile userProfile) {
        if (Objects.nonNull(userProfile)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveUserCvInMongoActor.ACTOR_NAME));
            updater.tell(userProfile, ActorRef.noSender());
        }
    }

    public void saveUserPersonalityInOracleAndMongo(Long userProfileId) {
        if (Objects.nonNull(userProfileId)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveUserCvInMongoActor.ACTOR_NAME));
            updater.tell(userProfileId, ActorRef.noSender());
        }
    }

    public void saveVacancyInMongo(VacancyClonedSDO vacancy) {
        if (Objects.nonNull(vacancy)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveVacancyInMongoActor.ACTOR_NAME));
            updater.tell(vacancy, ActorRef.noSender());
        }
    }

    public void saveVacancyInMongo(Vacancy vacancy) {
        if (Objects.nonNull(vacancy)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveVacancyInMongoActor.ACTOR_NAME));
            updater.tell(vacancy, ActorRef.noSender());
        }
    }

    public void editVacancyInMongo(EditVacancyInMongo editVacancyInMongo) {
        if (Objects.nonNull(editVacancyInMongo)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveVacancyInMongoActor.ACTOR_NAME));

            ActorRef appointmentActor = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveAppointmentDetailInMongoActor.ACTOR_NAME));

            updater.tell(editVacancyInMongo, appointmentActor);
        }
    }

    public void deleteAppointmentInMongo(DeleteAppointmentInMongo deleteAppointmentInMongo) {
        if (Objects.nonNull(deleteAppointmentInMongo)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveVacancyInMongoActor.ACTOR_NAME));
            updater.tell(deleteAppointmentInMongo, ActorRef.noSender());
        }
    }

    public void saveAppointmentInMongo(SaveAppointmentInMongo appointment) {
        if (Objects.nonNull(appointment)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveVacancyInMongoActor.ACTOR_NAME));
            updater.tell(appointment, ActorRef.noSender());
        }
    }

    public void saveAppointmentInMongo(List<SaveAppointmentInMongo> appointments) {
        if (CollectionUtils.isNotEmpty(appointments)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveVacancyInMongoActor.ACTOR_NAME));
            updater.tell(appointments, ActorRef.noSender());
        }
    }

    @Deprecated
    public void saveContactPersonInMongo(List<Vacancy> vacancies, Staff contactPerson) {
        if (CollectionUtils.isNotEmpty(vacancies) && Objects.nonNull(contactPerson)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveVacancyInMongoActor.ACTOR_NAME));

            List<Long> vacanciesId = vacancies.stream().map(Vacancy::getId).distinct().collect(Collectors.toList());
            SaveContactPersonInMongo saveContactPerson = new SaveContactPersonInMongo();
            saveContactPerson.setVacancyIds(vacanciesId);
            saveContactPerson.setContact(MongoConverters.convertToStaffEmbedded(contactPerson));
            updater.tell(contactPerson, ActorRef.noSender());
        }
    }

    public void saveVacancyInMongoByIds(List<Long> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveVacancyInMongoActor.ACTOR_NAME));
            updater.tell(ids, updater);
        }
    }

    public void sendAppliedMessageActor(AppliedVacancyMessage message) {
        if (Objects.nonNull(message)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SendAppliedMessageActor.ACTOR_NAME));
            updater.tell(message, ActorRef.noSender());
        }
    }

    public void sendApplicantMessageWithNewContactPersonActor(Staff oldStaff, Staff targetStaff, List<Vacancy> vacancies) {
        if (Objects.nonNull(oldStaff) && Objects.nonNull(targetStaff) && CollectionUtils.isNotEmpty(vacancies)) {
            ChangeContactPersonApplicantMessage changeContactPersonApplicantMessage = new ChangeContactPersonApplicantMessage();
            changeContactPersonApplicantMessage.setOldContactPerson(MongoConverters.convertToStaffEmbedded(oldStaff));
            changeContactPersonApplicantMessage.setTargetContactPerson(MongoConverters.convertToStaffEmbedded(targetStaff));
            List<Long> vacancyIds = vacancies.stream().map(Vacancy::getId).collect(Collectors.toList());
            changeContactPersonApplicantMessage.setVacancyIds(vacancyIds);
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system).props(SendAppliedMessageActor.ACTOR_NAME));
            updater.tell(changeContactPersonApplicantMessage, ActorRef.noSender());
        }
    }


    public void saveJoinCompanyRequestInMongoActor(CompanyJoinRequest companyJoinRequest) {
        if (Objects.nonNull(companyJoinRequest)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveJoinCompanyRequestInMongoActor.ACTOR_NAME));
            updater.tell(companyJoinRequest, ActorRef.noSender());
        }
    }

    public void updateJoinCompanyRequestOtherAdminInMongoActor(CompanyJoinRequest companyJoinRequest) {
        if (Objects.nonNull(companyJoinRequest)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(UpdateJoinCompanyRequestOtherAdminInMongoActor.ACTOR_NAME));
            updater.tell(companyJoinRequest, ActorRef.noSender());
        }
    }

    public void updateReceiveInAppMessageActor(MessageBase message) {
        if (Objects.nonNull(message)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(UpdateReceiveInAppMessageActor.ACTOR_NAME));
            updater.tell(message, ActorRef.noSender());
        }
    }

    public void setRoleInMessage(SetRoleInMongo req) {
        if (Objects.nonNull(req)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SetRoleInMessageInMongoActor.ACTOR_NAME));
            updater.tell(req, ActorRef.noSender());
        }
    }

    public void hideMessageWhenDeleteStaffActor(Staff staff) {
        if (Objects.nonNull(staff)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(HideMessageWhenDeleteStaffActor.ACTOR_NAME));
            updater.tell(staff, ActorRef.noSender());
        }
    }

    public void updateMessageCenterDocInMongoActor(VacancyDoc vacancyDoc) {
        if (Objects.nonNull(vacancyDoc)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(UpdateMessageCenterDocInMongoActor.ACTOR_NAME));
            updater.tell(vacancyDoc, ActorRef.noSender());
        }
    }

    public void syncDataOfUserInMongo(String userProfileId) {
        if (Objects.nonNull(userProfileId)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SyncDataOfEachUserInMongoActor.ACTOR_NAME));
            updater.tell(userProfileId, ActorRef.noSender());
        }
    }

    public void syncDataFromQooco(String syncKey) {
        if (StringUtils.isNotBlank(syncKey)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(LocalizationActor.ACTOR_NAME));
            ActorRef assessmentActor = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(AssessmentActor.ACTOR_NAME));
            updater.tell(syncKey, assessmentActor);
        }
    }

    public void saveViewProfileInMongo(ViewProfileTransfer profiles) {
        if (Objects.nonNull(profiles)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveViewProfileInMongoActor.ACTOR_NAME));
            updater.tell(profiles, ActorRef.noSender());
        }
    }

    public void saveAppointmentDetailToMongo(Appointment appointment, List<AppointmentDetail> appointmentDetails) {
        if (Objects.nonNull(appointment) && CollectionUtils.isNotEmpty(appointmentDetails)) {
            SaveAppointmentDetailInMongo saveAppointmentDetailInMongo = new SaveAppointmentDetailInMongo();
            saveAppointmentDetailInMongo.setAppointment(appointment);
            saveAppointmentDetailInMongo.setEvents(appointmentDetails);
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveAppointmentDetailInMongoActor.ACTOR_NAME));
            updater.tell(saveAppointmentDetailInMongo, ActorRef.noSender());
        }
    }

    public void cancelAppointmentDetailsToMongo(CancelAppointmentDetailInMongo appointmentSDO) {
        if (Objects.nonNull(appointmentSDO)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveAppointmentDetailInMongoActor.ACTOR_NAME));
            updater.tell(appointmentSDO, ActorRef.noSender());
        }
    }

    public void updateAppointmentDetailToMongo(AppointmentDetail appointmentDetail) {
        if (Objects.nonNull(appointmentDetail)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveAppointmentDetailInMongoActor.ACTOR_NAME));
            updater.tell(appointmentDetail, ActorRef.noSender());
        }
    }

    public void updateAppointmentDetailsToMongo(List<AppointmentDetail> appointmentDetails) {
        if (CollectionUtils.isNotEmpty(appointmentDetails)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveAppointmentDetailInMongoActor.ACTOR_NAME));
            updater.tell(appointmentDetails, ActorRef.noSender());
        }
    }

    public void saveBoostHelperMessageAfterFinishBasicProfileFirstTimeInMongo(Authentication authentication, UserProfile userProfile) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (isVersionSupportBoostHelperForProfile(user) && Objects.nonNull(userProfile)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveBoostHelperEventsForProfileInMongoActor.ACTOR_NAME));
            updater.tell(userProfile, ActorRef.noSender());
        }
    }

    public void saveBoostHelperMessageAfterFinishBasicProfileFirstTimeInMongo(Authentication authentication, BoostHelperFitTransfer fitTransfer) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (isVersionSupportBoostHelperForFit(user) && Objects.nonNull(fitTransfer.getUserFit())) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveBoostHelperEventsForFitInMongoActor.ACTOR_NAME));
            updater.tell(fitTransfer, ActorRef.noSender());
        }
    }

    public void saveBoostHelperMessageForOldUserNotHaveConversation(Authentication authentication, BoostHelperFitTransfer fitTransfer) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (isVersionSupportBoostHelperForFit(user) && Objects.nonNull(fitTransfer)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveBoostHelperEventsForFitInMongoActor.ACTOR_NAME));
            updater.tell(fitTransfer, ActorRef.noSender());
        }
    }

    public void saveBoostHelperMessageEventsInMongo(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (isVersionSupportBoostHelperForProfile(user)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveBoostHelperEventsForProfileInMongoActor.ACTOR_NAME));
            updater.tell(user.getId(), ActorRef.noSender());
        }
    }

    public void saveBoostHelperMessageEventsInMongoForProfile(BoostHelperProfileTransfer transfer) {
        if (Objects.nonNull(transfer)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveBoostHelperEventsForProfileInMongoActor.ACTOR_NAME));
            updater.tell(transfer, ActorRef.noSender());
        }
    }

    public void saveBoostHelperMessageEventsInMongoForFit(BoostHelperFitTransfer transfer) {
        if (Objects.nonNull(transfer)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(SaveBoostHelperEventsForFitInMongoActor.ACTOR_NAME));
            updater.tell(transfer, ActorRef.noSender());
        }
    }

    private boolean isVersionSupportBoostHelperForProfile(AuthenticatedUser user) {
        Map<String, Integer> careerVersionAccepted = new HashMap<>();
        careerVersionAccepted.put(IOS.toLowerCase(), 11);
        careerVersionAccepted.put(ANDROID.toLowerCase(), 35);

        return user.getAppVersion() >= careerVersionAccepted.get(user.getPlatform().toLowerCase())
                && PROFILE_APP.appId().equals(user.getAppId());
    }

    private boolean isVersionSupportBoostHelperForFit(AuthenticatedUser user) {
        Map<String, Integer> careerVersionAccepted = new HashMap<>();
        careerVersionAccepted.put(IOS.toLowerCase(), 11);
        careerVersionAccepted.put(ANDROID.toLowerCase(), 17);

        return user.getAppVersion() >= careerVersionAccepted.get(user.getPlatform().toLowerCase())
                && SELECT_APP.appId().equals(user.getAppId());
    }
}
