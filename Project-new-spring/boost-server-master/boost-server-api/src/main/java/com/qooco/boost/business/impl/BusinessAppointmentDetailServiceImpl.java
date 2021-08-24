package com.qooco.boost.business.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessAppointmentDetailService;
import com.qooco.boost.business.impl.abstracts.BusinessAppointmentAbstract;
import com.qooco.boost.constants.Const;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.enumeration.AppointmentLockedStatus;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.model.MessageGroupByAppointmentDetail;
import com.qooco.boost.data.model.count.CountByDate;
import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import com.qooco.boost.data.mongo.entities.AppointmentDetailNotifyDoc;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.services.AppointmentDetailDocService;
import com.qooco.boost.data.mongo.services.AppointmentDetailNotifyDocService;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.UserCurriculumVitaeService;
import com.qooco.boost.data.oracle.services.VacancyService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResultV2;
import com.qooco.boost.models.dto.appointment.*;
import com.qooco.boost.models.dto.message.ConversationDTO;
import com.qooco.boost.models.dto.user.UserCurriculumVitaeDTO;
import com.qooco.boost.models.dto.vacancy.VacancyShortInformationDTO;
import com.qooco.boost.models.request.appointment.AppointmentEventIdRemoveReq;
import com.qooco.boost.models.request.appointment.AppointmentEventRemoveReq;
import com.qooco.boost.models.request.appointment.AppointmentReq;
import com.qooco.boost.models.request.appointment.EventTimeReq;
import com.qooco.boost.models.response.CandidateFeedbackResp;
import com.qooco.boost.threads.models.CancelAppointmentDetailInMongo;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoConverters;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;
import static java.util.Optional.ofNullable;

@Service
public class BusinessAppointmentDetailServiceImpl extends BusinessAppointmentAbstract implements BusinessAppointmentDetailService {
    @Autowired
    private AppointmentDetailDocService appointmentDetailDocService;
    @Autowired
    private AppointmentDetailNotifyDocService appointmentDetailNotifyDocService;
    @Autowired
    private UserCurriculumVitaeService userCurriculumVitaeService;
    @Autowired
    private VacancyService vacancyService;
    @Autowired
    private ConversationDocService conversationDocService;

    private static final int USER_CV_INDEX = 0;
    private static final int VACANCY_INDEX = 1;
    private static final String BLANK_STRING = " ";

    @Override
    public List<AppointmentDetail> getAppointmentDetailOfCandidate(Authentication authentication, List<Integer> statuses, Date start, Date end) {
        List appointmentStatus = getAppointmentStatus(MessageConstants.APPOINTMENT_STATUS_ACCEPTED);
        if (Objects.isNull(start)) {
            throw new InvalidParamException(ResponseStatus.START_DATE_IS_REQUIRED);
        }
        if (Objects.nonNull(end) && end.before(start)) {
            throw new InvalidParamException(ResponseStatus.START_DATE_AFTER_END_DATE);
        }
        return appointmentDetailService.findByUserProfileIdAndStatus(getUserId(authentication), appointmentStatus, start, end);
    }

    @Override
    public BaseResp getAppointmentDetailOfCandidate(Authentication authentication, Long startDate, Long endDate, int page, int size) {
        List appointmentStatus = getAppointmentStatus(MessageConstants.APPOINTMENT_STATUS_ACCEPTED);
        if (Objects.isNull(startDate)) {
            Page<AppointmentDetail> events = appointmentDetailService.findByUserProfileIdAndStatus(getUserId(authentication), appointmentStatus, page, size);
            List<CandidateEventDTO> result = getListCandidateEventDTO(events.getContent(), getUserToken(authentication), getLocale(authentication));
            return new BaseResp<>(new PagedResultV2<>(result, page, events));
        } else {
            Date fromDate = DateUtils.toUtcForOracle(new Date(startDate));
            Date toDate = Objects.nonNull(endDate) ? DateUtils.toUtcForOracle(new Date(endDate)) : null;

            List<AppointmentDetail> events = getAppointmentDetailOfCandidate(authentication, appointmentStatus, fromDate, toDate);
            List<CandidateEventDTO> result = getListCandidateEventDTO(events, getUserToken(authentication), getLocale(authentication));
            return new BaseResp<>(new PagedResultV2<>(result));
        }
    }

    @Override
    public BaseResp getAppointmentDetailForReminder(Authentication authentication, Long companyId, long lastTime, int size) {
        List appointmentStatus = lastTime > 0 ? getAppointmentStatusAcceptOrCanceled() : AppointmentStatus.getAcceptedStatus();
        Date lastDate = DateUtils.toUtcForOracle(new Date(lastTime));

        List<AppointmentDetail> events = new ArrayList<>();
        if(SELECT_APP.appId().equals(getAppId(authentication))){
            events = appointmentDetailService.findByUserFitAndStatus(getUserId(authentication), companyId, appointmentStatus, lastDate, size);
        } else if(PROFILE_APP.appId().equals(getAppId(authentication))){
            events = appointmentDetailService.findByUserProfileAndStatus(getUserId(authentication), appointmentStatus, lastDate, size);
        }

        List<AppointmentDetailFullDTO> data = events.stream().map(it -> new AppointmentDetailFullDTO(it, getLocale(authentication))).collect(Collectors.toList());
        return new BaseResp<>(data);
    }

    @Override
    public BaseResp getAppointmentDetailOfCompanyInDuration(Authentication authentication, long companyId, long managerId, long startDate, long endDate) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (startDate >= endDate) {
            throw new InvalidParamException(ResponseStatus.INVALID_TIME_RANGE);
        }
        Staff staff = businessValidatorService.checkHasPermissionToCreateVacancy(companyId, user.getId());
        Staff manager = businessValidatorService.checkExistsStaff(managerId);
        if (!manager.getCompany().getCompanyId().equals(staff.getCompany().getCompanyId())) {
            throw new NoPermissionException(ResponseStatus.STAFF_OF_ANOTHER_COMPANY);
        }
        List<AppointmentDetail> appointmentDetails = appointmentDetailService.getAppointmentDetailByCompanyIdInDuration(
                companyId, managerId, DateUtils.toUtcForOracle(new Date(startDate)), DateUtils.toUtcForOracle(new Date(endDate)), AppointmentStatus.getAvailableStatus());
        return new BaseResp<>(initAppointmentVacancyShort(appointmentDetails, staff, getLocale(authentication)));
    }

    @Override
    public BaseResp getAllAppointmentDetailInDurationForProfile(Authentication authentication, long appointmentId, long startDate, long endDate) {
        if (startDate >= endDate) {
            throw new InvalidParamException(ResponseStatus.INVALID_TIME_RANGE);
        }

        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Appointment appointment = businessValidatorService.checkExistsAppointment(appointmentId);
        Long managerId = appointment.getManager().getStaffId();

        if (appointment.getVacancy().getStatus() == Const.Vacancy.Status.PERMANENT_SUSPEND) {
            throw new NoPermissionException(ResponseStatus.VACANCY_IS_SUSPENDED);
        } else if (appointment.getVacancy().getStatus() == Const.Vacancy.Status.INACTIVE) {
            throw new NoPermissionException(ResponseStatus.VACANCY_IS_INACTIVE);
        }


        List<AppointmentDetail> appointmentDetails = appointmentDetailService.getAllAppointmentDetailByManagerAppointmentOrUserProfileInDuration(
                managerId,
                user.getId(),
                DateUtils.toUtcForOracle(new Date(startDate)),
                DateUtils.toUtcForOracle(new Date(endDate)),
                AppointmentStatus.getAcceptedStatus());


        Stream<Date> suspendedTimeSlot = appointment.getVacancy().getNotAvailableTimeSlotForTemporarySuspend()
                .stream().map(DateUtils::getUtcForOracle)
                .filter(date -> date.getTime() >= startDate && date.getTime() < endDate);

        Stream<Date> appointmentDates = appointmentDetails.stream()
                .map(AppointmentDetail::getAppointmentTime).map(DateUtils::getUtcForOracle);

        List<Date> result = Stream.concat(appointmentDates, suspendedTimeSlot).collect(Collectors.toList());
        return new BaseResp<>(result);
    }

    @Override
    public BaseResp getAppointmentEventCounting(long staffId, long companyId, long startDate, Long endDate, String timeZone, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Staff staff = businessValidatorService.checkExistsStaff(staffId);
        String offsetTime = businessValidatorService.checkValidTimeZone(timeZone);
        Staff userStaff = businessValidatorService.checkHasPermissionToCreateVacancy(companyId, user.getId());
        if (!staff.getCompany().getCompanyId().equals(userStaff.getCompany().getCompanyId())) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }

        List<CountByDate> countAppointmentByManager = appointmentDetailService.countAppointmentByManager(staffId, startDate, endDate, offsetTime);
        AppointmentEventCountDTO result = convertToAppointmentEventCountDTO(staff, timeZone, countAppointmentByManager);
        return new BaseResp<>(result);
    }

    @Override
    public BaseResp getAppointmentEventCountingByUserInCompany(long companyId, long startDate, Long endDate, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Staff staff = businessValidatorService.checkExistsStaffInApprovedCompany(companyId, user.getId());
        long staffId = staff.getStaffId();
        String timeZone = user.getTimezone();
        String offsetTime = businessValidatorService.checkValidTimeZone(timeZone);
        Staff userStaff = businessValidatorService.checkHasPermissionToCreateVacancy(companyId, user.getId());
        if (!staff.getCompany().getCompanyId().equals(userStaff.getCompany().getCompanyId())) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }

        List<String> roleNames = CompanyRole.valueOf(staff.getRole().getName()).getRolesForAppointmentManager();
        List<CountByDate> countAppointmentByManager = appointmentDetailService.countAppointmentByUserInCompany(
                staffId, userStaff.getCompany().getCompanyId(), startDate, endDate, offsetTime, roleNames);
        AppointmentEventCountDTO result = convertToAppointmentEventCountDTO(staff, timeZone, countAppointmentByManager);
        return new BaseResp<>(result);
    }

    @Override
    public BaseResp getAppointmentEventInDurationUserInCompany(long companyId, long startDate, long endDate, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Staff staff = businessValidatorService.checkExistsStaffInApprovedCompany(companyId, user.getId());
        long staffId = staff.getStaffId();
        Date startDateUtcOracle = DateUtils.toUtcForOracle(new Date(startDate));
        Date endDateUtcOracle = DateUtils.toUtcForOracle(new Date(endDate));
        List<Integer> acceptedStatus = AppointmentStatus.getAcceptedStatus();
        List<String> roleNames = CompanyRole.valueOf(staff.getRole().getName()).getRolesForAppointmentManager();
        List<AppointmentDetail> appointmentDetails = appointmentDetailService.getAppointmentDetailByCompanyIdInDurationWithRoles(
                companyId, staffId, startDateUtcOracle, endDateUtcOracle, acceptedStatus, roleNames);
        Map<Appointment, List<AppointmentDetail>> eventsMap = appointmentDetails.stream()
                .collect(Collectors.groupingBy(AppointmentDetail::getAppointment));
        List<AppointmentDTO> appointmentDTOS = new ArrayList<>();
        eventsMap.forEach((appointment, appointmentEvents) -> {
            AppointmentDTO appointmentDTO = new AppointmentDTO(appointmentEvents, getLocale(authentication));
            StringBuilder managerRole = new StringBuilder(appointment.getManager().getRole().getName());
            List<AppointmentDetailDTO> events = new ArrayList<>();
            AtomicInteger lockedStatus = new AtomicInteger(AppointmentLockedStatus.LOCKED_EDIT.getValue());
            appointmentEvents.forEach(ap -> {
                boolean isLocked = isLockedAppointment(staff, managerRole.toString());
                AppointmentDetailDTO appointmentShortVacancy = new AppointmentDetailDTO(ap, isLocked, getLocale(authentication));
                if (isLocked) {
                    lockedStatus.set(AppointmentLockedStatus.LOCKED_DELETE.getValue());
                }
                events.add(appointmentShortVacancy);
            });
            appointmentDTO.setLockedStatus(lockedStatus.get());
            appointmentDTO.setEvents(events);

            appointmentDTOS.add(appointmentDTO);
        });
        return new BaseResp<>(appointmentDTOS);
    }

    @Override
    @Transactional
    public BaseResp saveAppointmentEvent(Long eventId, EventTimeReq request, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        AppointmentDetailDoc detailDoc = appointmentDetailDocService.findById(eventId);
        if (Objects.isNull(detailDoc)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_APPOINTMENT_DETAIL);
        }
        Long userProfileId = detailDoc.getCandidate().getUserProfileId();
        if (!user.getId().equals(userProfileId)) {
            throw new InvalidParamException(ResponseStatus.APPOINTMENTS_EVENT_NO_PERMISSION);
        }
        if (detailDoc.getStatus() != AppointmentStatus.PENDING.getValue()) {
            throw new EntityNotFoundException(ResponseStatus.APPOINTMENT_IS_NOT_AVAILABLE);
        }
        if (!request.getEventTime().after(new Date())) {
            throw new NoPermissionException(ResponseStatus.APPOINTMENT_IS_EXPIRED);
        }

        if (!validateTimeRange(request.getEventTime(), detailDoc.getAppointment().getAppointmentTimeRange())) {
            throw new NoPermissionException(ResponseStatus.APPOINTMENTS_EVENT_DATE_OUT_OF_TIME_RANGE);
        }

        Long companyId = detailDoc.getVacancy().getCompany().getId();
        Long managerId = detailDoc.getAppointment().getManager().getUserProfile().getUserProfileId();

        boolean exists = appointmentDetailService.checkExistsAppointmentDetailByCompanyIdInDuration(companyId, managerId,
                DateUtils.toUtcForOracle(request.getEventTime()), AppointmentStatus.getAcceptedStatus());
        if (exists) {
            throw new InvalidParamException(ResponseStatus.APPOINTMENTS_EVENT_CONFLICT);
        }

        AppointmentDetail appointmentDetail = businessValidatorService.checkExistAppointmentDetail(eventId);

        if (appointmentDetail.getAppointment().getVacancy().getStatus() == Const.Vacancy.Status.INACTIVE) {
            throw new NoPermissionException(ResponseStatus.VACANCY_IS_INACTIVE);
        } else if (appointmentDetail.getAppointment().getVacancy().getStatus() == Const.Vacancy.Status.PERMANENT_SUSPEND) {
            throw new NoPermissionException(ResponseStatus.VACANCY_IS_SUSPENDED);
        } else if (appointmentDetail.getAppointment().getVacancy().getVacancyStatus() == Const.Vacancy.Status.SUSPEND) {
            if (appointmentDetail.getAppointment().getVacancy().isInSuspendedTime(request.getEventTime())) {
                throw new NoPermissionException(ResponseStatus.APPOINTMENTS_SELECTED_DATE_IN_SUSPENDED_TIME);
            }
        }

        appointmentDetail.setAppointmentTime(DateUtils.toUtcForOracle(request.getEventTime()));
        appointmentDetail.setStatus(AppointmentStatus.mixAppointmentStatus(detailDoc.getStatus(), AppointmentStatus.ACCEPTED.getValue()));
        appointmentDetailService.save(appointmentDetail);

        boostActorManager.updateAppointmentDetailToMongo(appointmentDetail);
        return new BaseResp<>(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp deleteAppointmentDetails(Long id, long fromDate, long toDate, Authentication authentication) {
        List<AppointmentDetail> appointmentDetails = getCancelOrAcceptedEvents(id, fromDate, toDate, authentication);
        Appointment appointment = appointmentDetails.get(0).getAppointment();
        List<AppointmentDetail> results = doCancelAppointmentDetails(appointmentDetails, false);
        CancelAppointmentDetailInMongo appointmentDetailSDO = new CancelAppointmentDetailInMongo();
        appointmentDetailSDO.getCancelEventMap().put(appointment, results);
        boostActorManager.cancelAppointmentDetailsToMongo(appointmentDetailSDO);
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp deleteAppointmentDetails(long[] ids, Authentication authentication) {
        List<AppointmentDetail> appointmentDetails = findAppointmentDetailUserHasEditOrDeletePermission(ids, authentication);
        List<AppointmentDetail> results = doCancelAppointmentDetails(appointmentDetails, false);
        doCancelAppointmentDetailOnMongo(results);
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp deleteAppointmentDetailsWithNewOrChangeOption(AppointmentEventRemoveReq request, Authentication authentication) {
        List<AppointmentDetail> appointmentDetails = getCancelOrAcceptedEvents(request.getDeleteId(), request.getFromDate(), request.getToDate(), authentication);
        cancelAppointmentDetailsAndMoveToAnotherAppointment(request.getTargetId(), request.getAppointment(), appointmentDetails, authentication);
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp deleteAppointmentDetailIdsWithNewOrChangeOption(AppointmentEventIdRemoveReq request, Authentication authentication) {
        List<AppointmentDetail> appointmentDetails = findAppointmentDetailUserHasEditOrDeletePermission(request.getIds(), authentication);
        cancelAppointmentDetailsAndMoveToAnotherAppointment(request.getTargetId(), request.getAppointment(), appointmentDetails, authentication);
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    public List<AppointmentDetail> cancelAppointmentDetails(List<AppointmentDetail> appointmentDetails) {
        return doCancelAppointmentDetails(appointmentDetails, false);
    }

    @Override
    public BaseResp hasExpiredEvent(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        boolean havExpired = false;
        if (Objects.nonNull(user.getCompanyId())) {
            Staff staff = businessValidatorService.checkExistsStaffInApprovedCompany(user.getCompanyId(), user.getId());
            List<AppointmentDetailNotifyDoc> eventNotified = appointmentDetailNotifyDocService.findByUserProfileAndCompany(staff.getStaffId());
            List<Long> expiredIds = eventNotified.stream().flatMap(notify -> notify.getAppointmentDetailIds().stream()).distinct().collect(toImmutableList());
            List<Long> appointmentDetailIds = appointmentDetailService.findExpiredEventByUserAndCompany(user.getId(), user.getCompanyId());

            havExpired = !expiredIds.containsAll(appointmentDetailIds);
            appointmentDetailIds.removeAll(expiredIds);
            if (havExpired && CollectionUtils.isNotEmpty(appointmentDetailIds)) {
                saveExpiredEventsNotified(staff, appointmentDetailIds);
            }
        }
        return new BaseResp<>(havExpired);
    }

    private void saveExpiredEventsNotified(Staff staff, List<Long> appointmentDetailIds) {
        AppointmentDetailNotifyDoc doc = AppointmentDetailNotifyDoc.builder()
                .staff(MongoConverters.convertToStaffEmbedded(staff))
                .appointmentDetailIds(appointmentDetailIds)
                .createdDate(DateUtils.toServerTimeForMongo())
                .build();
        appointmentDetailNotifyDocService.save(doc);
    }

    @Override
    public BaseResp getCandidatesOfExpiredEventsForFeedback(Long appointmentId, List<String> decideLaterCandidates, int size, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        decideLaterCandidates = ofNullable(decideLaterCandidates).filter(it -> !it.isEmpty()).orElseGet(() -> ImmutableList.of(BLANK_STRING));
        Page<Object[]> vacancyCandidatePage = appointmentDetailService.getVacancyCandidateExpiredEventsOfAppointmentForFeedBack(user.getId(), user.getCompanyId(), decideLaterCandidates, Objects.isNull(appointmentId), appointmentId, size);

        List<Long> candidateIds = new ArrayList<>();
        List<Long> vacancyIds = new ArrayList<>();
        List<CandidateFeedbackResp> candidateFeedbackResps = new ArrayList<>();
        ofNullable(vacancyCandidatePage.getContent()).ifPresent(item -> item.forEach(it -> {
            candidateIds.add(((BigDecimal) it[USER_CV_INDEX]).longValue());
            vacancyIds.add(((BigDecimal) it[VACANCY_INDEX]).longValue());
            candidateFeedbackResps.add(new CandidateFeedbackResp(((BigDecimal) it[USER_CV_INDEX]).longValue(), ((BigDecimal) it[VACANCY_INDEX]).longValue()));
        }));
        Map<Long, UserCurriculumVitae> candidates = userCurriculumVitaeService.findByIds(candidateIds).stream().collect(Collectors.toMap(UserCurriculumVitae::getCurriculumVitaeId, Function.identity()));
        Map<Long, Vacancy> vacancies = vacancyService.findValidByIds(vacancyIds).stream().collect(Collectors.toMap(Vacancy::getId, Function.identity()));
        candidateFeedbackResps.stream()
                .peek(it -> it.setVacancy(new VacancyShortInformationDTO(vacancies.get(it.getVacancy().getId()), getLocale(authentication))))
                .forEach(it -> it.setCandidate(new UserCurriculumVitaeDTO(candidates.get(it.getCandidate().getId()), getLocale(authentication))));
        return new BaseResp<>(new PagedResultV2<>(candidateFeedbackResps, 0, vacancyCandidatePage));
    }

    private void cancelAppointmentDetailsAndMoveToAnotherAppointment(Long targetId, AppointmentReq appointmentReq, List<AppointmentDetail> appointmentDetails, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (Objects.nonNull(targetId)) {
            Appointment targetAppointment = hasPermissionToEditOrDelete(targetId, user);
            doCancelAndAssignToTarget(appointmentDetails, targetAppointment, authentication);
        } else {
            Appointment targetAppointment = doCreateOrUpdateAppointment(null, appointmentReq, authentication);
            if (Objects.nonNull(targetAppointment)) {
                doCancelAndAssignToTarget(appointmentDetails, targetAppointment, authentication);
            }
        }
    }

    private List<AppointmentDetail> findAppointmentDetailUserHasEditOrDeletePermission(long[] ids, Authentication authentication) {
        List<Long> appointmentDetailId = Arrays.stream(ids).distinct().boxed().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(appointmentDetailId)) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        List<AppointmentDetail> appointmentDetails = appointmentDetailService.findByIds(appointmentDetailId);
        if (appointmentDetails.size() < appointmentDetailId.size()) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_APPOINTMENT_DETAIL);
        }
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        List<Staff> staffs = staffService.findByUserProfileAndStatus(user.getId(), ApprovalStatus.APPROVED);
        if (staffs.isEmpty()) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS_APPOINTMENT);
        }
        appointmentDetails.forEach(appointmentDetail -> {
            if (!AppointmentStatus.getAcceptedStatus().contains(appointmentDetail.getStatus())) {
                throw new NoPermissionException(ResponseStatus.INVALID_EVENT_ACTION);
            }
            Date now = new Date();
            if (Objects.isNull(appointmentDetail.getAppointmentTime())
                    || DateUtils.getUtcForOracle(appointmentDetail.getAppointmentTime()).before(now)) {
                throw new NoPermissionException(ResponseStatus.APPOINTMENT_IS_EXPIRED);
            }
            Staff staff = appointmentDetail.getAppointment().getManager();
            List<String> roleNames = CompanyRole.valueOf(staff.getRole().getName()).getRolesLarger();
            if (!appointmentDetail.getAppointment().getManager().getUserFit().getUserProfileId().equals(user.getId())) {
                if (staffs.stream().noneMatch(s -> s.getCompany().getCompanyId().equals(
                        appointmentDetail.getAppointment().getVacancy().getCompany().getCompanyId())
                        && roleNames.contains(s.getRole().getName()))) {
                    throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS_APPOINTMENT);
                }
            }
        });
        return appointmentDetails;
    }

    private List<AppointmentDetail> getCancelOrAcceptedEvents(Long id, long fromDate, long toDate, Authentication authentication) {
        Date fromDateServer = new Date(fromDate);
        Date toDateServer = new Date(toDate);
        businessValidatorService.isCurrentDateRange(fromDateServer, toDateServer);
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Appointment appointment = hasPermissionToEditOrDelete(id, user);
        Date fromDateUtc = DateUtils.nowUtc();
        if (fromDateServer.after(new Date())) {
            fromDateUtc = DateUtils.toUtcForOracle(fromDateServer);
        }
        List<AppointmentDetail> appointmentDetails = appointmentDetailService.findAcceptedAppointmentInDuration(
                appointment.getId(), fromDateUtc, DateUtils.toUtcForOracle(toDateServer));
        if (CollectionUtils.isEmpty(appointmentDetails)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_APPOINTMENT_DETAIL);
        }
        // Set appointment to avoid lazy load in list appointment detail
        appointmentDetails.get(0).setAppointment(appointment);
        return appointmentDetails;
    }

    private List<CandidateEventDTO> getListCandidateEventDTO(List<AppointmentDetail> events, String token, String locale) {
        List<CandidateEventDTO> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(events)) {
            List<Long> appointmentDetailIds = events.stream().map(AppointmentDetail::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(appointmentDetailIds)) {
                List<MessageGroupByAppointmentDetail> messageDocs = messageDocService.findDistinctAppointmentDetailMessage(appointmentDetailIds);
                Map<Long, ObjectId> eventConversationIdMap = new HashMap<>();
                messageDocs.forEach(
                        messageDoc -> eventConversationIdMap.put(messageDoc.getAppointmentDetailId(), messageDoc.getConversationId()));
                List<ObjectId> conversationIds = messageDocs.stream().map(MessageGroupByAppointmentDetail::getConversationId).collect(Collectors.toList());
                List<ConversationDoc> conversationDocs = conversationDocService.findAllById(conversationIds);

                events.forEach(appointmentDetail -> {
                    ObjectId conversationId = eventConversationIdMap.get(appointmentDetail.getId());
                    Optional<ConversationDoc> conversationDoc = conversationDocs.stream().filter(
                            c -> Objects.nonNull(c) && Objects.nonNull(c.getId()) && c.getId().equals(conversationId)).findAny();
                    CandidateEventDTO candidateEvent = new CandidateEventDTO(appointmentDetail, null, token, locale);
                    conversationDoc.ifPresent(c -> candidateEvent.setConversation(new ConversationDTO(c, token, locale)));
                    result.add(candidateEvent);

                });
            }
        }
        return result;
    }

    private List<Integer> getAppointmentStatusAcceptOrCanceled() {
        return Lists.newArrayList(
                AppointmentStatus.PENDING_ACCEPTED.getValue(),
                AppointmentStatus.ACCEPTED.getValue(),
                AppointmentStatus.PENDING_ACCEPTED_CANCELED.getValue(),
                AppointmentStatus.PENDING_ACCEPTED_CHANGED.getValue());
    }

    private boolean validateTimeRange(Date eventTime, List<Date> timeRanges) {
        for (Date timeRange : timeRanges) {
            Date endTimeRange = DateUtils.addSecond(timeRange, DateUtils.SECOND_OF_HOUR);
            if (eventTime.equals(timeRange) || (eventTime.after(timeRange) && eventTime.before(endTimeRange)) || eventTime.equals(endTimeRange)) {
                return true;
            }
        }
        return false;
    }

    private AppointmentEventCountDTO convertToAppointmentEventCountDTO(Staff staff, String timeZone, List<CountByDate> countAppointmentByManager) {
        Map<String, Integer> events = new TreeMap<>();
        if (CollectionUtils.isNotEmpty(countAppointmentByManager)) {
            countAppointmentByManager.forEach(countByDate -> {
                String formattedDate = DateUtils.formatShortLocalTimeByTimeZone(countByDate.getEventDate(), timeZone);
                events.put(formattedDate, countByDate.getTotal());
            });
        }
        AppointmentEventCountDTO appointmentEventCountDTO = new AppointmentEventCountDTO();
        appointmentEventCountDTO.setEvents(events);
        appointmentEventCountDTO.setCompanyId(staff.getCompany().getCompanyId());
        appointmentEventCountDTO.setUserProfileId(staff.getUserFit().getUserProfileId());
        appointmentEventCountDTO.setStaffId(staff.getStaffId());
        return appointmentEventCountDTO;
    }

    private List<Integer> getAppointmentStatus(Integer status) {
        switch (status) {
            case MessageConstants.APPOINTMENT_STATUS_PENDING:
                return Lists.newArrayList(AppointmentStatus.PENDING.getValue());
            case MessageConstants.APPOINTMENT_STATUS_ACCEPTED:
                return AppointmentStatus.getAcceptedStatus();
            case MessageConstants.APPOINTMENT_STATUS_DECLINED:
                return AppointmentStatus.getDeclineStatus();
            default:
                List<Integer> statuses = Lists.newArrayList(AppointmentStatus.PENDING.getValue());
                statuses.addAll(AppointmentStatus.getAcceptedStatus());
                statuses.addAll(AppointmentStatus.getDeclineStatus());
                return statuses;
        }
    }

    private List<AppointmentShortVacancyDTO> initAppointmentVacancyShort(List<AppointmentDetail> appointmentDetails, Staff staff, String locale) {
        List<AppointmentShortVacancyDTO> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(appointmentDetails)) {
            appointmentDetails.forEach(ap -> {
                String managerRole = ap.getAppointment().getManager().getRole().getName();
                boolean isLocked = isLockedAppointment(staff, managerRole);
                AppointmentShortVacancyDTO appointmentShortVacancy = new AppointmentShortVacancyDTO(ap, isLocked, locale);
                result.add(appointmentShortVacancy);
            });
        }
        return result;
    }
}
