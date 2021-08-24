package com.qooco.boost.business.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.qooco.boost.business.*;
import com.qooco.boost.constants.Const;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.core.thread.SpringExtension;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.enumeration.UploadType;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.entities.*;
import com.qooco.boost.data.mongo.services.*;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.PataFileDTO;
import com.qooco.boost.models.poi.StaticData;
import com.qooco.boost.models.request.AppVersionReq;
import com.qooco.boost.models.request.admin.CloneStressTestUserReq;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.threads.actors.CloneUserMongoActor;
import com.qooco.boost.threads.actors.SaveLocaleInMongoActor;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoConverters;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.qooco.boost.data.enumeration.MessageCenterType.AUTHORIZATION_CONVERSATION;
import static com.qooco.boost.data.enumeration.MessageCenterType.VACANCY_CONVERSATION;
import static java.util.Optional.ofNullable;

@Service
public class BusinessAdminServiceImpl implements BusinessAdminService {

    protected Logger logger = LogManager.getLogger(BusinessAdminServiceImpl.class);
    @Autowired
    private BusinessAppVersionService businessAppVersionService;
    @Autowired
    private BoostActorManager boostActorManager;
    @Autowired
    private MessageCenterDocService messageCenterDocService;
    @Autowired
    private ConversationDocService conversationDocService;
    @Autowired
    private MessageDocService messageDocService;
    @Autowired
    private BusinessValidatorService businessValidatorService;
    @Autowired
    private UserCvDocService userCvDocService;
    @Autowired
    private UserFitService userFitService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserPreviousPositionService userPreviousPositionService;
    @Autowired
    private UserQualificationService userQualificationService;
    @Autowired
    private UserCurriculumVitaeService userCurriculumVitaeService;
    @Autowired
    private SystemLoggerDocService systemLoggerDocService;
    @Autowired
    private SocketConnectionDocService socketConnectionDocService;
    @Autowired
    private AppointmentDetailDocService appointmentDetailDocService;
    @Autowired
    private AppointmentDetailService appointmentDetailService;
    @Autowired
    private VacancyService vacancyService;
    @Autowired
    private VacancySeatService vacancySeatService;
    @Autowired
    private ActorSystem system;
    @Autowired
    private POIService poiService;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private BusinessLocalizationService businessLocalizationService;

    @Override
    public BaseResp saveAppVersion(AppVersionReq request, Authentication authentication) {
        return businessAppVersionService.saveAppVersion(request, authentication);
    }

    @Override
    public BaseResp getAllAppVersion(Authentication authentication) {
        return businessAppVersionService.getAll(authentication);
    }

    @Override
    public BaseResp getSystemLogger(Authentication authentication, Long fromDate, Long toDate) {
        List<SystemLoggerDoc> result = systemLoggerDocService.findByCreatedDate(getFromDate(fromDate), getToDate(toDate));
        return new BaseResp<>(result);
    }

    @Override
    public BaseResp getSocketConnection(Authentication authentication, Long fromDate, Long toDate) {
        List<SocketConnectionDoc> result = socketConnectionDocService.findByUpdatedDate(getFromDate(fromDate), getToDate(toDate));
        return new BaseResp<>(result);
    }

    private Date getFromDate(Long fromDate) {
        return new Date(ofNullable(fromDate).orElse(Long.MIN_VALUE));
    }

    private Date getToDate(Long toDate) {
        return new Date(ofNullable(toDate).orElse(DateUtils.MAX_DATE_ORACLE));
    }

    //TODO: Only using when participant it wrong
    @Deprecated
    @Override
    public BaseResp updateParticipantAndCreatedBy(Authentication authentication, int total) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        businessValidatorService.checkUserProfileIsRootAdmin(user.getId());

        int count = 0;
        int limit = total > 10 ? 10 : total;
        List<ConversationDoc> conversations;
        List<ObjectId> conversationStored = new ArrayList<>();
        List<UserProfileCvEmbedded> userProfileCvEmbeddedStore = new ArrayList<>();
        List<UserProfileCvEmbedded> userFitCvEmbeddedStore = new ArrayList<>();

        do {
            conversations = conversationDocService.findByLimit(conversationStored, limit);
            if (CollectionUtils.isNotEmpty(conversations)) {
                List<ObjectId> messageCenterIds = conversations.stream()
                        .map(ConversationDoc::getMessageCenterId)
                        .distinct().collect(Collectors.toList());

                List<ObjectId> conversationIds = conversations.stream()
                        .map(ConversationDoc::getId)
                        .distinct().collect(Collectors.toList());

                List<MessageCenterDoc> messageCenterDocs = messageCenterDocService.findAllById(messageCenterIds);
                List<MessageDoc> messages = messageDocService.findFirstMessage(conversationIds);

                List<Long> userFitIds = new ArrayList<>();
                List<Long> userProfileIds = new ArrayList<>();
                conversations.forEach(c -> {

                    Optional<MessageDoc> messageDocOptional = messages.stream().filter(mgs -> c.getId().equals(mgs.getConversationId())).findFirst();
                    if (messageDocOptional.isPresent()) {
                        MessageDoc message = messageDocOptional.get();
                        Optional<MessageCenterDoc> messageCenterDocOptional = messageCenterDocs.stream().filter(mgs -> c.getMessageCenterId().equals(mgs.getId())).findFirst();
                        if (messageCenterDocOptional.isPresent()) {
                            List<UserProfileCvMessageEmbedded> participants = new ArrayList<>();
                            if (messageCenterDocOptional.get().getType() == VACANCY_CONVERSATION.value()) {
                                participants.add(new UserProfileCvMessageEmbedded(message.getSender(), UserType.SELECT));
                                participants.add(new UserProfileCvMessageEmbedded(message.getRecipient(), UserType.PROFILE));
                                userFitIds.add(message.getSender().getUserProfileId());
                                userProfileIds.add(message.getRecipient().getUserProfileId());
                            } else if (messageCenterDocOptional.get().getType() == AUTHORIZATION_CONVERSATION.value()) {
                                participants.add(new UserProfileCvMessageEmbedded(message.getSender(), UserType.SELECT));
                                participants.add(new UserProfileCvMessageEmbedded(message.getRecipient(), UserType.SELECT));
                                userFitIds.add(message.getSender().getUserProfileId());
                                userFitIds.add(message.getRecipient().getUserProfileId());
                            }
                            if (CollectionUtils.isNotEmpty(participants)) {
                                c.setParticipants(participants);
                                c.setCreatedBy(new UserProfileCvEmbedded(message.getSender().getUserProfileId(), UserType.SELECT));
                            }
                        }
                    }
                });

                List<Long> userFitIdStore = userFitCvEmbeddedStore.stream().map(UserProfileCvEmbedded::getUserProfileId).collect(Collectors.toList());
                List<Long> userProfileIdStore = userProfileCvEmbeddedStore.stream().map(UserProfileCvEmbedded::getUserProfileId).collect(Collectors.toList());
                List<Long> userFitIdNeed = userFitIds.stream().filter(id -> !userFitIdStore.contains(id)).collect(Collectors.toList());
                List<Long> userProfileIdNeed = userProfileIds.stream().filter(id -> !userProfileIdStore.contains(id)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(userProfileIdNeed)) {
                    List<UserCvDoc> userCvDocs = userCvDocService.findByUserProfileId(userProfileIds.stream().distinct().collect(Collectors.toList()));
                    if (CollectionUtils.isNotEmpty(userCvDocs)) {
                        userProfileCvEmbeddedStore.addAll(userCvDocs.stream().map(MongoConverters::convertToUserProfileCvEmbedded).collect(Collectors.toList()));
                    }
                }
                if (CollectionUtils.isNotEmpty(userFitIdNeed)) {
                    List<UserFit> userFits = userFitService.findByIds(userFitIds.stream().distinct().collect(Collectors.toList()));
                    if (CollectionUtils.isNotEmpty(userFits)) {
                        userFitCvEmbeddedStore.addAll(userFits.stream().map(MongoConverters::convertToUserProfileCvEmbedded).collect(Collectors.toList()));
                    }
                }

                Map<ObjectId, List<UserProfileCvEmbedded>> participantMap = new HashMap<>();
                Map<ObjectId, UserProfileCvEmbedded> createdByMap = new HashMap<>();
                conversations.forEach(c -> {
                    List<UserProfileCvEmbedded> participants = new ArrayList<>();
                    c.getParticipants().forEach(p -> {
                        Optional<UserProfileCvEmbedded> cvEmbeddedOptional;
                        if (p.getUserType() == UserType.SELECT) {
                            cvEmbeddedOptional = userFitCvEmbeddedStore.stream().filter(f -> f.getUserProfileId().equals(p.getUserProfileId())).findFirst();
                            cvEmbeddedOptional.ifPresent(participants::add);
                        } else if (p.getUserType() == UserType.PROFILE) {
                            cvEmbeddedOptional = userProfileCvEmbeddedStore.stream().filter(f -> f.getUserProfileId().equals(p.getUserProfileId())).findFirst();
                            cvEmbeddedOptional.ifPresent(participants::add);
                        }
                    });

                    Optional<UserProfileCvEmbedded> createdByOptional = userFitCvEmbeddedStore.stream().filter(f -> f.getUserProfileId().equals(c.getCreatedBy().getUserProfileId())).findFirst();
                    createdByOptional.ifPresent(userProfileCvEmbedded -> createdByMap.put(c.getId(), userProfileCvEmbedded));

                    if (CollectionUtils.isNotEmpty(participants) && participants.size() >= 2) {
                        participantMap.put(c.getId(), participants);
                    }
                });
                conversationDocService.updateParticipant(participantMap);
                if (MapUtils.isNotEmpty(createdByMap)) {
                    conversationDocService.updateCreatedBy(createdByMap);
                }
                conversationStored.addAll(conversationIds);
                count += limit;
            }
        } while (CollectionUtils.isNotEmpty(conversations) && conversations.size() == limit && (total == 0 || count < total));
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Transactional
    public BaseResp patchAppointmentDetailDoc() {
        List<Long> appointmentDetailIds = new ArrayList<>();
        List<AppointmentDetailDoc> appointmentDetailDocs = appointmentDetailDocService
                .findNoneVacancy(Constants.DEFAULT_LIMITED_ITEM, appointmentDetailIds);
        do {
            appointmentDetailIds.addAll(appointmentDetailDocs.stream().map(AppointmentDetailDoc::getId).collect(Collectors.toList()));
            List<Appointment> appointments = appointmentDetailService
                    .findByIds(appointmentDetailDocs.stream().map(AppointmentDetailDoc::getId).collect(Collectors.toList()))
                    .stream().map(AppointmentDetail::getAppointment).collect(Collectors.toList());
            appointmentDetailDocs.forEach(ap -> appointments.stream().filter(
                    v -> v.getId().equals(ap.getAppointment().getId())).findFirst()
                    .ifPresent(appointment -> ap.setVacancy(new VacancyEmbedded(appointment.getVacancy()))));
            appointmentDetailDocService.updateBatchVacancies(appointmentDetailDocs);
            appointmentDetailDocService.deleteAll(appointmentDetailDocs.stream().filter(ap -> Objects.isNull(ap.getVacancy())).collect(Collectors.toList()));
            appointmentDetailDocs = appointmentDetailDocService
                    .findNoneVacancy(Constants.DEFAULT_LIMITED_ITEM, appointmentDetailIds);
        } while (!appointmentDetailDocs.isEmpty());
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp patchVacancySeat() {
        long vacancyId = 0;
        List<Vacancy> vacancies = vacancyService.findNoneSeatVacancies(vacancyId, Constants.DEFAULT_LIMITED_ITEM);
        while (CollectionUtils.isNotEmpty(vacancies)) {
            List<VacancySeat> vacancySeats = new ArrayList<>();
            vacancies.forEach(vacancy -> {
                int closedCandidates = CollectionUtils.size(vacancy.getClosedCandidates());
                List<VacancySeat> seats = new ArrayList<>();
                for (int i = 0; i < vacancy.getNumberOfSeat(); i++) {
                    VacancySeat vacancySeat = new VacancySeat(vacancy.getId(), vacancy.getCreatedDate(), vacancy.getContactPerson().getStaffId());
                    if (i < closedCandidates) {
                        vacancySeat.setStatus(Const.VacancySeatStatus.CLOSED);
                        vacancySeat.setClosedDate(vacancy.getClosedCandidates().get(i).getCreatedDate());
                        vacancySeat.setUserCvId(vacancy.getClosedCandidates().get(i).getCandidate().getCurriculumVitaeId());
                        vacancySeat.setClosedStaffId(vacancy.getClosedCandidates().get(i).getArchivist().getStaffId());
                    } else if (vacancy.getStatus() == Const.Vacancy.Status.PERMANENT_SUSPEND) {
                        vacancySeat.setStatus(Const.VacancySeatStatus.SUSPENDED);
                        vacancySeat.setSuspendFromDate(vacancy.getUpdatedDate());
                    } else if (vacancy.getStatus() == Const.Vacancy.Status.OPENING && vacancy.isInSuspendedTime(new Date())) {
                        vacancySeat.setStatus(Const.VacancySeatStatus.TEMPORARY_SUSPENDDED);
                        vacancySeat.setSuspendFromDate(vacancy.getStartSuspendDate());
                        vacancySeat.setSuspendedDays(vacancy.getSuspendDays());
                        vacancySeat.setEndSuspendDate(DateUtils.addDays(vacancy.getStartSuspendDate(), vacancy.getSuspendDays()));
                        VacancySeat newOpenSeat = new VacancySeat(vacancy.getId(), vacancy.getStartSuspendDate(), vacancy.getContactPerson().getStaffId());
                        seats.add(newOpenSeat);
                    } else if (vacancy.getStatus() == Const.Vacancy.Status.OPENING && Objects.nonNull(vacancy.getStartSuspendDate())) {
                        vacancySeat.setStatus(Const.VacancySeatStatus.TEMPORARY_SUSPENDDED);
                        vacancySeat.setSuspendFromDate(vacancy.getStartSuspendDate());
                        vacancySeat.setSuspendedDays(vacancy.getSuspendDays());
                        vacancySeat.setEndSuspendDate(DateUtils.addDays(vacancy.getStartSuspendDate(), vacancy.getSuspendDays()));
                        VacancySeat newOpenSeat = new VacancySeat(vacancy.getId(), DateUtils.addDays(vacancy.getStartSuspendDate(), vacancy.getSuspendDays()), vacancy.getContactPerson().getStaffId());
                        seats.add(newOpenSeat);
                    }
                    seats.add(vacancySeat);
                }
                vacancySeats.addAll(seats);
            });
            vacancySeatService.save(vacancySeats);
            vacancyId = vacancies.stream().max(Comparator.comparing(Vacancy::getId)).get().getId();
            vacancies = vacancyService.findVacancies(vacancyId, Constants.DEFAULT_LIMITED_ITEM);
        }
        return new BaseResp(ResponseStatus.SUCCESS);
    }


    @Override
    public BaseResp cloneUserProfile(Authentication authentication, CloneStressTestUserReq req) {
        UserProfile userProfile = businessValidatorService.checkExistsUserProfile(req.getUserProfileId());
        UserCurriculumVitae userCurriculumVitae = userCurriculumVitaeService.findByUserProfile(userProfile);
        List<UserPreviousPosition> userPreviousPositions = userPreviousPositionService.findByUserProfileId(userProfile.getUserProfileId());
        List<UserQualification> userQualifications = userQualificationService.findByUserProfileId(userProfile.getUserProfileId());
        if (Objects.isNull(userCurriculumVitae)) {
            userCurriculumVitae = new UserCurriculumVitae(userProfile);
        }

        long createdBy = Objects.nonNull(req.getCreatedBy()) ? req.getCreatedBy() : getUserId(authentication);
        long startUserProfileIdForStressTest = 10000000000L;
        long maxUserProfileId = userProfileService.getMaxUserProfileId();
        long startId = maxUserProfileId >= startUserProfileIdForStressTest ? maxUserProfileId + 1 : startUserProfileIdForStressTest;
        int step = 10;
        for (int i = 0; i < req.getRepeatNumber(); i += step) {
            startId = cloneUser(userProfile, userCurriculumVitae, userPreviousPositions, userQualifications, startId, step, createdBy) + 1;
        }
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp cloneUserCVDoc(Authentication authentication, CloneStressTestUserReq req) {

        ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system).props(CloneUserMongoActor.ACTOR_NAME));
        updater.tell(req, ActorRef.noSender());
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Transactional
    protected long cloneUser(UserProfile originalUserProfile, UserCurriculumVitae originalUserCv, List<UserPreviousPosition> userPreviousPositions, List<UserQualification> userQualifications, long startId, long repeatNumber, long createdBy) {
        long maxUserProfileId = startId;
        for (int i = 0; i < repeatNumber; i++) {
            maxUserProfileId = startId + i;

            List<UserPreviousPosition> userPreviousPositionsClone = new ArrayList<>();
            List<UserQualification> userQualificationsClone = new ArrayList<>();
            ofNullable(userPreviousPositions)
                    .ifPresent(it -> userPreviousPositionsClone.addAll(it.stream().map(UserPreviousPosition::new).collect(Collectors.toList())));
            ofNullable(userQualifications)
                    .ifPresent(it -> userQualificationsClone.addAll(it.stream().map(UserQualification::new).collect(Collectors.toList())));

            UserProfile userProfileClone = new UserProfile(originalUserProfile);
            userProfileClone.setUserProfileId(maxUserProfileId);
            setUserProfileAndCreatedBy(userProfileClone, userPreviousPositionsClone,  userQualificationsClone, createdBy);
            userProfileClone = userProfileService.save(userProfileClone);
            if (CollectionUtils.isNotEmpty(userPreviousPositionsClone)) {
                userPreviousPositionService.save(userPreviousPositionsClone);
            }

            if (CollectionUtils.isNotEmpty(userQualificationsClone)) {
                userQualificationService.save(userQualificationsClone);
            }

            UserCurriculumVitae userCurriculumVitaeClone = new UserCurriculumVitae(originalUserCv);
            userCurriculumVitaeClone.setUserProfile(userProfileClone);
            userCurriculumVitaeClone.setCurriculumVitaeId(null);
            setUserCuriumVitaeAndCreatedBy(userCurriculumVitaeClone, createdBy);
            userCurriculumVitaeService.save(userCurriculumVitaeClone);

            boostActorManager.saveUserCvInMongo(userCurriculumVitaeClone);
        }
        return maxUserProfileId;
    }

    @Override
    public BaseResp patchLocaleDataInMongo(String jsonFile) {
        ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system).props(SaveLocaleInMongoActor.ACTOR_NAME));
        updater.tell(jsonFile, ActorRef.noSender());
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp patchLocalization(MultipartFile file, Authentication authentication) {
        BaseResp result = mediaService.store(file, UploadType.EXCEL_FILE.toString(), authentication);
        PataFileDTO fileDTO = (PataFileDTO) result.getData();
        List<StaticData> data = null;
        if (Objects.nonNull(fileDTO)) {
            data = loadExcelSaveLocalization(fileDTO.getUrl(), getUserId(authentication));
        }
        return new BaseResp(data);
    }

    private void setUserCuriumVitaeAndCreatedBy(UserCurriculumVitae vitae, long createdBy) {
        vitae.setCreatedBy(createdBy);
        vitae.setUpdatedBy(createdBy);

        ofNullable(vitae.getCurriculumVitaeJobs()).ifPresent(jobs -> jobs.forEach(job -> {
            job.setCurriculumVitaeJobId(null);
            job.setUserCurriculumVitae(vitae);
            job.setCreatedBy(createdBy);
            job.setUpdatedBy(createdBy);
        }));

        ofNullable(vitae.getUserSoftSkills()).ifPresent(skills -> skills.forEach(skill -> {
            skill.setUserSoftSkillId(null);
            skill.setUserCurriculumVitae(vitae);
            skill.setCreatedBy(createdBy);
            skill.setUpdatedBy(createdBy);
        }));

        ofNullable(vitae.getUserBenefits()).ifPresent(benefits -> benefits.forEach(benefit -> {
            benefit.setUserBenefitId(null);
            benefit.setUserCurriculumVitae(vitae);
            benefit.setCreatedBy(createdBy);
            benefit.setUpdatedBy(createdBy);
        }));

        ofNullable(vitae.getUserDesiredHours()).ifPresent(desiredHours -> desiredHours.forEach(desiredHour -> {
            desiredHour.setUserDesiredHourId(null);
            desiredHour.setUserCurriculumVitae(vitae);
            desiredHour.setCreatedBy(createdBy);
            desiredHour.setUpdatedBy(createdBy);
        }));
    }

    private void setUserProfileAndCreatedBy(UserProfile profile, List<UserPreviousPosition> userPreviousPositions, List<UserQualification> userQualifications, long createdBy) {
        profile.setCreatedBy(createdBy);
        profile.setUpdatedBy(createdBy);
        profile.setFirstName("Stress User");
        profile.setLastName(profile.getUserProfileId().toString());

        ofNullable(userPreviousPositions).filter(CollectionUtils::isNotEmpty)
                .ifPresent(it -> it.forEach(previousPosition -> {
                    previousPosition.setId(null);
                    previousPosition.setCreatedBy(profile.getUserProfileId());
                    previousPosition.setUpdatedBy(createdBy);
                }));

        ofNullable(userQualifications).filter(CollectionUtils::isNotEmpty)
                .ifPresent(it -> it.forEach(qualification -> {
                    qualification.setId(null);
                    qualification.setUserProfileId(profile.getUserProfileId());
                    qualification.setCreatedBy(createdBy);
                    qualification.setUpdatedBy(createdBy);
                }));

        ofNullable(profile.getUserLanguageList()).ifPresent(languages -> languages.forEach(language -> {
            language.setId(null);
            language.setUserProfile(profile);
            language.setCreatedBy(createdBy);
            language.setUpdatedBy(createdBy);
        }));
    }

    private List<StaticData> loadExcelSaveLocalization(String filePath, long createdBy) {
        try {
            List<StaticData> result = poiService.readLocalizationStaticData(filePath);
            businessLocalizationService.saveLocalization(result, createdBy);

            return result;
        } catch (IOException | InvalidFormatException e) {
            logger.info(e.getMessage());
        }
        return List.of();
    }

}
