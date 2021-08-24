package com.qooco.boost.threads.services.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.mongo.embedded.StaffShortEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;
import com.qooco.boost.data.mongo.embedded.message.AppointmentDetailMessage;
import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.AppointmentDetailDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.AppointmentDateRangeService;
import com.qooco.boost.data.oracle.services.AppointmentService;
import com.qooco.boost.data.oracle.services.AppointmentTimeRangeService;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.models.dto.message.MessageDTO;
import com.qooco.boost.socket.services.SendMessageToClientService;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.threads.services.AppointmentDetailActorService;
import com.qooco.boost.threads.services.MessageDocActorService;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoConverters;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class AppointmentDetailActorActorServiceImpl implements AppointmentDetailActorService {
    protected Logger logger = LogManager.getLogger(AppointmentDetailActorActorServiceImpl.class);

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private AppointmentDetailDocService appointmentDetailDocService;
    @Autowired
    private UserCvDocService userCvDocService;
    @Autowired
    private VacancyDocService vacancyDocService;
    @Autowired
    private MessageDocActorService messageDocActorService;
    @Autowired
    private SendMessageToClientService sendMessageToClientService;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private AppointmentDateRangeService appointmentDateRangeService;
    @Autowired
    private AppointmentTimeRangeService appointmentTimeRangeService;
    @Autowired
    private MessageDocService messageDocService;

    @Value(ApplicationConstant.BOOST_PATA_QOOCO_DOMAIN_PATH)
    private String qoocoDomainPath = "";

    @Override
    public void saveAppointmentDetail(Appointment appointment, List<AppointmentDetail> appointmentDetails, String locale) {
        saveAppointmentDetail(appointment, appointmentDetails, null, locale);
    }

    @Override
    public void saveAppointmentDetail(Appointment appointment, List<AppointmentDetail> appointmentDetails, Integer cancelReason, String locale) {
        List<AppointmentDetailDoc> result = saveAppointmentDetailDoc(appointment, appointmentDetails);
        saveAppointmentDetailMessage(result, locale);
    }

    private List<MessageDoc> saveAppointmentDetailMessage(List<AppointmentDetailDoc> appointmentDetails, String locale) {
        if (CollectionUtils.isNotEmpty(appointmentDetails)) {
            List<MessageDoc> docs = messageDocActorService.saveMessageForAppointmentDetails(appointmentDetails);
            sendMessageAppointment(docs, locale);
            docs.forEach(mgs -> logger.info("Save message appointment id = " + mgs.getId().toString()));
            return docs;
        }
        return ImmutableList.of();
    }

    private List<AppointmentDetailDoc> saveAppointmentDetailDoc(Appointment appointment, List<AppointmentDetail> appointmentDetails) {
        if (CollectionUtils.isNotEmpty(appointmentDetails)) {
            List<AppointmentDetailDoc> results = convertToAppointmentDetailDoc(appointment, appointmentDetails);
            if (CollectionUtils.isNotEmpty(results)) {
                appointmentDetailDocService.save(results);
                updateAppointmentCandidatesInVacancyDoc(results);
            }
            return results;
        }
        return  ImmutableList.of();
    }

    private List<AppointmentDetailDoc> convertToAppointmentDetailDoc(Appointment appointment, List<AppointmentDetail> appointmentDetails) {
        List<Long> cvIds = appointmentDetails.stream()
                .map(ap -> ap.getUserCurriculumVitae().getCurriculumVitaeId())
                .distinct().collect(toList());

        List<UserCvDoc> userCvDocs = userCvDocService.findAllById(cvIds);
        List<AppointmentDetailDoc> results = null;
        if (CollectionUtils.isNotEmpty(userCvDocs)) {
            results = new ArrayList<>();
            List<Long> appointmentActor = appointmentDetails.stream().map(BaseEntity::getCreatedBy).distinct().collect(toList());
            appointmentActor.addAll(appointmentDetails.stream().map(BaseEntity::getUpdatedBy).distinct().collect(toList()));
            appointmentActor = appointmentActor.stream().distinct().collect(toList());

            List<Staff> staffs = staffService.findByUserProfileAndCompany(appointment.getLocation().getCompany().getCompanyId(), appointmentActor);
            List<StaffShortEmbedded> staffShortEmbeds = staffs.stream().map(MongoConverters::convertToStaffShortEmbedded).collect(toList());

            List<Appointment> appointments = appointmentDetails.stream().map(AppointmentDetail::getAppointment).distinct().collect(toList());
            List<Long> appointmentIds = appointments.stream().map(Appointment::getId).collect(toList());
            List<AppointmentEmbedded> appointmentEmbeds = appointments.stream().map(MongoConverters::convertToAppointmentEmbedded).collect(toList());

            List<VacancyDoc> vacancyDocs = vacancyDocService.findByAppointmentIds(appointmentIds);


            AppointmentDetailDoc detailDoc;
            for (AppointmentDetail appointmentDetail : appointmentDetails) {
                detailDoc = new AppointmentDetailDoc();

                Optional<UserCvDoc> userCvDocOptional = userCvDocs.stream()
                        .filter(cv -> cv.getId().equals(appointmentDetail.getUserCurriculumVitae().getCurriculumVitaeId()))
                        .findFirst();

                if (userCvDocOptional.isPresent()) {
                    detailDoc.setId(appointmentDetail.getId());
                    detailDoc.setStatus(appointmentDetail.getStatus());
                    detailDoc.setCandidate(MongoConverters.convertToUserProfileCvEmbedded(userCvDocOptional.get()));
                    if (Objects.nonNull(appointmentDetail.getAppointmentTime())) {
                        detailDoc.setAppointmentTime(DateUtils.toServerTimeForMongo(appointmentDetail.getAppointmentTime()));
                    }
                    detailDoc.setCreatedDate(DateUtils.toServerTimeForMongo(appointmentDetail.getCreatedDate()));
                    detailDoc.setUpdatedDate(DateUtils.toServerTimeForMongo(appointmentDetail.getUpdatedDate()));
                    detailDoc.setDeleted(appointmentDetail.getIsDeleted());

                    Optional<AppointmentEmbedded> appointmentEmbeddedOptional = appointmentEmbeds.stream()
                            .filter(ap -> ap.getId().equals(appointmentDetail.getAppointment().getId())).findFirst();
                    if (appointmentEmbeddedOptional.isPresent()) {
                        appointmentEmbeddedOptional.get().setFromDateAndToDate();
                        detailDoc.setAppointment(appointmentEmbeddedOptional.get());
                    }

                    Optional<VacancyDoc> vacancyDocOptional = vacancyDocs.stream()
                            .filter(vd -> (vd.getAppointments().indexOf(new AppointmentEmbedded(appointmentDetail.getAppointment().getId())) >= 0)).findFirst();

                    if (vacancyDocOptional.isPresent()) {
                        detailDoc.setVacancy(MongoConverters.convertToVacancyEmbedded(vacancyDocOptional.get()));
                    }

                    Optional<StaffShortEmbedded> creatorOptional = staffShortEmbeds.stream().filter(
                            st -> st.getUserProfile().getUserProfileId().equals(appointmentDetail.getCreatedBy())).findFirst();
                    if (creatorOptional.isPresent()) {
                        detailDoc.setCreator(creatorOptional.get());
                    }

                    Optional<StaffShortEmbedded> updaterOptional = staffShortEmbeds.stream().filter(
                            st -> st.getUserProfile().getUserProfileId().equals(appointmentDetail.getUpdatedBy())).findFirst();
                    if (updaterOptional.isPresent()) {
                        detailDoc.setUpdater(updaterOptional.get());
                    }
                    results.add(detailDoc);
                }
            }
        }
        return results;
    }

    private void sendMessageAppointment(List<MessageDoc> docs, String locale) {
        //Send message to conversation via socket
        List<MessageDTO> messageDTOs = getAppointmentMessageDTO(docs, locale);
        sendMessageToClientService.sendMessage(messageDTOs);
        pushNotificationService.notifyAppointmentMessage(getAppointmentWithoutCanceled(docs), true);

        //Send push to enable/disable appointment button in chatting.
//        Map<ConversationId, Map<SenderId, List<MessageDoc>>>
        Map<ObjectId, Map<Long, List<MessageDoc>>> conversationMap = new HashMap<>();
        Map<ObjectId, List<MessageDoc>> conversationGroup = docs.stream().collect(groupingBy(MessageDoc::getConversationId));
        conversationGroup.forEach((conversationId, messageDocs) -> {
            Map<Long, List<MessageDoc>> senderMap = messageDocs.stream().collect(groupingBy(doc -> doc.getSender().getUserProfileId()));
            if (MapUtils.isNotEmpty(senderMap)) {
                conversationMap.put(conversationId, senderMap);
            }
        });

        if (MapUtils.isNotEmpty(conversationMap)) {
            conversationMap.forEach((conversationId, senderMap) -> senderMap.forEach((senderId, mgsDocs) -> {
                if (CollectionUtils.isNotEmpty(mgsDocs)) {
                    int action = MessageConstants.APPOINTMENT_MESSAGE_ACTION_ENABLE;

                    boolean isPending = mgsDocs.stream().anyMatch(doc -> Objects.nonNull(doc.getAppointmentDetailMessage())
                            && AppointmentStatus.PENDING.getValue() == doc.getAppointmentDetailMessage().getAppointmentDetailStatus());

                    if (isPending) {
                        action = MessageConstants.APPOINTMENT_MESSAGE_ACTION_DISABLE;
                    }
                    String messageCenterId = null;
                    if (!mgsDocs.isEmpty() && Objects.nonNull(mgsDocs.get(0).getMessageCenterId())) {
                        messageCenterId = mgsDocs.get(0).getMessageCenterId().toHexString();
                    }
                    sendMessageToClientService.sendMessage(new MessageDTO(conversationId.toHexString(), messageCenterId, action));
                }
            }));
        }
    }

    @Override
    public void updateAppointmentCandidatesInVacancyDoc(List<AppointmentDetailDoc> detailDocs) {
        List<AppointmentDetailDoc> appointmentDetailDocs = new ArrayList<>();
        Map<VacancyEmbedded, List<AppointmentDetailDoc>> vacancyEmbeddedMap = detailDocs.stream()
                .filter(ap -> Objects.nonNull(ap.getVacancy()))
                .collect(groupingBy(AppointmentDetailDoc::getVacancy, toList()));

        vacancyEmbeddedMap.forEach((K, V) -> {
            Map<UserProfileCvEmbedded, List<AppointmentDetailDoc>> userProfileCvEmbeddedMap = V.stream().collect(
                    groupingBy(AppointmentDetailDoc::getCandidate, toList())
            );
            userProfileCvEmbeddedMap.forEach((k, v) -> {
                v.sort(Comparator.comparing(AppointmentDetailDoc::getUpdatedDate));
                appointmentDetailDocs.add(Iterables.getLast(v));
            });
        });
        if (CollectionUtils.isNotEmpty(appointmentDetailDocs)) {
            vacancyDocService.updateAppointmentCandidates(appointmentDetailDocs);
        }
    }

    @Override
    public void updateAppointmentDetail(AppointmentDetail appointmentDetail, String locale) {
        Date appointmentTime = DateUtils.toServerTimeForMongo(appointmentDetail.getAppointmentTime());
        appointmentDetailDocService.updateAppointmentTimeAndStatus(appointmentDetail.getId(), appointmentTime, appointmentDetail.getStatus());

        AppointmentDetailDoc detailDoc = appointmentDetailDocService.findById(appointmentDetail.getId());
        if (Objects.nonNull(detailDoc)) {
            updateAppointmentCandidatesInVacancyDoc(Lists.newArrayList(detailDoc));
            List<MessageDoc> messages = messageDocActorService.saveMessageForAppointmentDetail(detailDoc);
            sendMessageAppointment(messages, locale);
        }
    }

    @Override
    public void updateAppointmentDetailDoc(AppointmentDetail appointmentDetail, String locale) {
        updateAppointmentDetail(appointmentDetail, locale);
        List<AppointmentDateRange> appointmentDateRanges = appointmentDateRangeService.findByAppointmentId(appointmentDetail.getAppointmentId());
        List<AppointmentTimeRange> appointmentTimeRanges = appointmentTimeRangeService.findByAppointmentId(appointmentDetail.getAppointmentId());
        Appointment appointment = appointmentService.findById(appointmentDetail.getAppointmentId());
        if (Objects.nonNull(appointment)) {
            Date maxDate = null;
            Date minDate = null;
            if (CollectionUtils.isNotEmpty(appointmentDateRanges)) {
                minDate = appointmentDateRanges.stream().map(d -> DateUtils.toServerTimeForMongo(d.getAppointmentDate())).min(Date::compareTo).orElse(null);
                maxDate = appointmentDateRanges.stream().map(d -> DateUtils.toServerTimeForMongo(d.getAppointmentDate())).max(Date::compareTo).orElse(null);
                if (Objects.nonNull(maxDate) && CollectionUtils.isNotEmpty(appointmentTimeRanges)) {
                    Date maxHourDate = appointmentTimeRanges.stream().map(d -> DateUtils.toServerTimeForMongo(d.getAppointmentTime())).max(Date::compareTo).orElse(null);
                    if (Objects.nonNull(maxHourDate)) {
                        maxDate = DateUtils.setHourByTimeStamp(maxDate, maxHourDate);
                        if (maxDate.before(maxHourDate)) {
                            maxDate = DateUtils.addDays(maxDate, 1);
                        }
                    }
                }
            }
            if (Objects.nonNull(maxDate)) {
                maxDate = DateUtils.atEndOfHour(maxDate);
            }
            appointmentDetailDocService.updateDateTimeRangeAndType(appointmentDetail.getId(),
                    appointmentDateRanges.stream().map(d -> DateUtils.toServerTimeForMongo(d.getAppointmentDate())).collect(toList()),
                    appointmentTimeRanges.stream().map(d -> DateUtils.toServerTimeForMongo(d.getAppointmentTime())).collect(toList()),
                    appointment.getType(),
                    minDate,
                    maxDate);
            vacancyDocService.updateDateTimeRangeAndType(appointmentDetail.getAppointmentId(),
                    appointmentDateRanges.stream().map(d -> DateUtils.toServerTimeForMongo(d.getAppointmentDate())).collect(toList()),
                    appointmentTimeRanges.stream().map(d -> DateUtils.toServerTimeForMongo(d.getAppointmentTime())).collect(toList()),
                    appointment.getType(),
                    minDate,
                    maxDate);
            messageDocService.updateDateTimeRangeAndType(appointmentDetail.getId(),
                    appointmentDateRanges.stream().map(d -> DateUtils.toServerTimeForMongo(d.getAppointmentDate())).collect(toList()),
                    appointmentTimeRanges.stream().map(d -> DateUtils.toServerTimeForMongo(d.getAppointmentTime())).collect(toList()),
                    appointment.getType(),
                    minDate,
                    maxDate);
        }
    }

    private List<MessageDTO> getAppointmentMessageDTO(List<MessageDoc> docs, String locale) {
        List<MessageDTO> messageDTOs = new ArrayList<>();
        docs.forEach(d -> {
            int status = MessageConstants.SUBMIT_MESSAGE_ACTION;
            switch (d.getType()) {
                case MessageConstants.APPOINTMENT_APPLICANT_MESSAGE:
                case MessageConstants.APPOINTMENT_CANCEL_MESSAGE:
                    status = MessageConstants.SUBMIT_MESSAGE_ACTION;
                    break;
                case MessageConstants.APPOINTMENT_MESSAGE:
                    if (d.isDeleted()) {
                        status = MessageConstants.DELETE_MESSAGE_ACTION;
                        break;
                    }
                    AppointmentDetailMessage aptEvent = d.getAppointmentDetailMessage();
                    if (Objects.nonNull(aptEvent) &&
                            (aptEvent.getAppointmentDetailStatus() == AppointmentStatus.CANCELED.getValue()
                                    || aptEvent.getResponseStatus() == AppointmentStatus.ACCEPTED.getValue()
                                    || aptEvent.getResponseStatus() == AppointmentStatus.DECLINED.getValue())) {
                        status = MessageConstants.UPDATE_MESSAGE_ACTION;
                    }
                    break;
            }
            messageDTOs.add(new MessageDTO(d, status, qoocoDomainPath, locale));
        });
        return messageDTOs;
    }


    private List<MessageDoc> getAppointmentWithoutCanceled(List<MessageDoc> resource) {
        List<MessageDoc> result = Lists.newArrayList(resource);
        if (CollectionUtils.isNotEmpty(resource)) {
            resource.forEach(ap -> {
                AppointmentDetailMessage aptDetail = ap.getAppointmentDetailMessage();
                if (Objects.nonNull(aptDetail)
                        && MessageConstants.APPOINTMENT_MESSAGE == ap.getType()
                        && AppointmentStatus.CANCELED.getValue() == aptDetail.getAppointmentDetailStatus()) {
                    result.remove(ap);
                }
            });
        }
        return result;
    }
}
