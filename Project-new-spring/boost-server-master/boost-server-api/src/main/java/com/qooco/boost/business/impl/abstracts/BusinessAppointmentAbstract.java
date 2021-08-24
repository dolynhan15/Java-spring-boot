package com.qooco.boost.business.impl.abstracts;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BaseBusinessService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.constants.Const;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentSlotEmbedded;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.EventAction;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.request.appointment.AppointmentReq;
import com.qooco.boost.models.sdo.AppointmentSlotEmbeddedSDO;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.threads.models.CancelAppointmentDetailInMongo;
import com.qooco.boost.threads.models.SaveAppointmentInMongo;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BusinessAppointmentAbstract implements BaseBusinessService {
    protected Logger logger = LogManager.getLogger(BusinessAppointmentAbstract.class);
    @Autowired
    protected BoostActorManager boostActorManager;
    @Autowired
    protected AppointmentDetailService appointmentDetailService;
    @Autowired
    protected ConversationDocService conversationDocService;
    @Autowired
    protected MessageDocService messageDocService;
    @Autowired
    protected BusinessValidatorService businessValidatorService;
    @Autowired
    private VacancyDocService vacancyDocService;
    @Autowired
    protected StaffService staffService;
    @Autowired
    protected AppointmentDateRangeService appointmentDateRangeService;
    @Autowired
    protected AppointmentTimeRangeService appointmentTimeRangeService;
    @Autowired
    protected AppointmentService appointmentService;

    @Autowired
    protected MessageCenterDocService messageCenterDocService;
    @Autowired
    protected VacancyProcessingService vacancyProcessingService;

    protected static final List<String> ROLE_ACCESS_APPOINTMENT = CompanyRole.ADMIN.getRolesEqualOrLessNoAnalyst();



    protected boolean isLockedAppointment(Staff staff, String managerRole) {
        if (Objects.nonNull(staff) && Objects.nonNull(staff.getRole())) {
            if (staff.getRole().getName().equals(CompanyRole.ADMIN.getName())) {
                return false;
            } else if (staff.getRole().getName().equals(CompanyRole.HEAD_RECRUITER.getName())) {
                return managerRole.equals(CompanyRole.ADMIN.getName());
            } else if (staff.getRole().getName().equals(CompanyRole.RECRUITER.getName())) {
                return managerRole.equals(CompanyRole.ADMIN.getName())
                        || managerRole.equals(CompanyRole.HEAD_RECRUITER.getName());
            }
        }
        return true;
    }

    protected void doCancelAppointmentDetailOnMongo(List<AppointmentDetail> appointmentDetails) {
        Map<Appointment, List<AppointmentDetail>> mapAppointment = appointmentDetails.stream().collect(Collectors.groupingBy(AppointmentDetail::getAppointment));
        CancelAppointmentDetailInMongo appointmentDetailSDO = new CancelAppointmentDetailInMongo();
        appointmentDetailSDO.setCancelEventMap(mapAppointment);
        boostActorManager.cancelAppointmentDetailsToMongo(appointmentDetailSDO);
    }

    protected Appointment validateAppointmentBeforeSavingEvent(long appointmentId, AuthenticatedUser user) {
        List<String> roleNames = CompanyRole.ADMIN.getRolesEqualOrLessNoAnalyst();
        Appointment appointment = businessValidatorService.checkPermissionOnAppointment(appointmentId, user.getId(), roleNames);
        Optional<Date> maxOption = appointment.getAppointmentDateRange().stream().map(AppointmentDateRange::getAppointmentDate).max(Date::compareTo);
        if (!maxOption.isPresent()) {
            throw new EntityNotFoundException(ResponseStatus.APPOINTMENT_IS_NOT_AVAILABLE);
        }
        Optional<Date> minOption = appointment.getAppointmentTimeRange().stream().map(AppointmentTimeRange::getAppointmentTime).max(Date::compareTo);
        if (!minOption.isPresent()) {
            throw new EntityNotFoundException(ResponseStatus.APPOINTMENT_IS_NOT_AVAILABLE);
        }
        Date expiredDate = DateUtils.getUtcForOracle(appointment.getToDateOfAppointment());
        if (!expiredDate.after(new Date())) {
            throw new NoPermissionException(ResponseStatus.APPOINTMENT_IS_EXPIRED);
        }
        return appointment;
    }

    protected void saveAppointmentDetailsInMongoAndOracle(List<AppointmentDetail> events) {
        if (CollectionUtils.isNotEmpty(events)) {
            Appointment appointment = events.get(0).getAppointment();
            appointmentDetailService.save(events);
            List<AppointmentDetail> result = Lists.newArrayList(events);
            boostActorManager.saveAppointmentDetailToMongo(appointment, result);
            updateAppointmentCandidateInMongo(result, appointment.getVacancy().getId());
        }
    }

    protected Appointment hasPermissionToEditOrDelete(Long id, AuthenticatedUser user) {
        Appointment appointment = businessValidatorService.checkExistsAppointment(id);
        Staff staff = businessValidatorService.checkStaffPermissionOnAppointment(appointment, user.getId(), ROLE_ACCESS_APPOINTMENT);
        List<String> roleNames = CompanyRole.valueOf(staff.getRole().getName()).getRolesLarger();
        if (CollectionUtils.isNotEmpty(roleNames)) {
            List<Object[]> idGroup = appointmentDetailService.findIdByUpdaterWithStatusAndRoles(user.getId(), Lists.newArrayList(id), AppointmentStatus.getAvailableStatus(), roleNames);
            if (CollectionUtils.isNotEmpty(idGroup) && ArrayUtils.isNotEmpty(idGroup.get(0))) {
                throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS_APPOINTMENT);
            }
        }
        return appointment;
    }

    protected Appointment doCreateOrUpdateAppointment(Long id, AppointmentReq appointmentReq, Authentication authentication) {
        logger.info(StringUtil.append("Save Appointment Id = ", String.valueOf(id)));
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Vacancy vacancy = businessValidatorService.checkExistsValidVacancy(appointmentReq.getVacancyId());
        List<Appointment> appointments = vacancy.getVacancyAppointments();
        Staff creator = businessValidatorService.checkPermissionForSaveAppointmentOnVacancy(vacancy, user.getId());

        Appointment appointment = validateAppointmentRequestV2(appointmentReq, vacancy.getCompany(), creator, appointments, user.getTimezone());
        if (Objects.nonNull(appointment)) {
            appointment.setId(id);
            appointment.setVacancy(vacancy);
            appointment = appointmentService.save(appointment);

            appointments.remove(appointment);
            appointments.add(appointment);

            SaveAppointmentInMongo saveAppointmentInMongo = new SaveAppointmentInMongo(appointment, appointmentReq.getVacancyId());
            boostActorManager.saveAppointmentInMongo(saveAppointmentInMongo);
            logger.info(StringUtil.append("Finish save Appointment Id = ", appointment.getId().toString()));
        }
        return appointment;
    }

    protected List<Appointment> doCreateAppointment(List<Appointment> appointments) {
        if (CollectionUtils.isNotEmpty(appointments)) {
            appointmentService.save(appointments);
            List<SaveAppointmentInMongo> result = new ArrayList<>();
            appointments.forEach(appointment -> result.add(new SaveAppointmentInMongo(appointment, appointment.getVacancy().getId())));
            boostActorManager.saveAppointmentInMongo(result);
        }
        return appointments;
    }


    protected List<AppointmentDetail> doCancelAppointmentDetails(List<AppointmentDetail> appointmentDetails, boolean isDeleteAppointment) {
        if (CollectionUtils.isNotEmpty(appointmentDetails)) {
            appointmentDetails.forEach(ad -> {
                ad.setStatus(AppointmentStatus.mixAppointmentStatusByEventAction(ad.getStatus(), EventAction.DELETE.getAction()));
                if (isDeleteAppointment) {
                    ad.getAppointment().setIsDeleted(true);
                }
            });
            return appointmentDetailService.save(appointmentDetails);
        }
        return Lists.newArrayList();
    }

    protected List<AppointmentDateRange> createAppointmentDateRange(Appointment appointment, List<Date> appointmentDateRange) {
        List<AppointmentDateRange> saveDateRanges = new ArrayList<>();
        appointmentDateRange.forEach(date -> saveDateRanges.add(new AppointmentDateRange(DateUtils.toUtcForOracle(date), appointment)));
        return saveDateRanges;
    }

    protected List<AppointmentTimeRange> createAppointmentTimeRange(Appointment appointment, List<Date> appointmentTimeRange) {
        List<AppointmentTimeRange> saveTimeRanges = new ArrayList<>();
        appointmentTimeRange.forEach(date -> saveTimeRanges.add(new AppointmentTimeRange(DateUtils.toUtcForOracle(date), appointment)));
        return saveTimeRanges;
    }

    protected void doCancelAndAssignToTarget(List<AppointmentDetail> appointmentDetails, Appointment targetAppointment, Authentication authentication) {
        doCancelAndAssignToTarget(appointmentDetails, targetAppointment, Const.Vacancy.CancelAppointmentReason.UNKNOWN, authentication);
    }

    protected void doCancelAndAssignToTarget(List<AppointmentDetail> appointmentDetails, Appointment targetAppointment, Integer reasonToCancel, Authentication authentication) {
        if (CollectionUtils.isNotEmpty(appointmentDetails)) {
            Appointment cancelAppointment = appointmentDetails.get(0).getAppointment();
            List<AppointmentDetail> results = doCancelAppointmentDetails(appointmentDetails, false);
            CancelAppointmentDetailInMongo appointmentDetailSDO = new CancelAppointmentDetailInMongo();

            appointmentDetailSDO.getCancelEventMap().put(appointmentDetails.get(0).getAppointment(), results);
            appointmentDetailSDO.setTargetAppointment(targetAppointment);

            List<Long> userCVIds = appointmentDetails.stream()
                    .map(adt -> adt.getUserCurriculumVitae().getCurriculumVitaeId())
                    .distinct().collect(Collectors.toList());

            //Cancel all accepted appointment in target
            List<AppointmentDetail> acceptedEvents = appointmentDetailService.findByAppointmentIdAndUserCVIdAndStatus(
                    targetAppointment.getId(), userCVIds, AppointmentStatus.getAcceptedStatus());
            appointmentDetailSDO.getCancelEventMap().put(targetAppointment, doCancelAppointmentDetails(acceptedEvents, false));

            List<AppointmentDetail> newAppointmentDetails = initNewAppointmentDetailForCandidates(
                    targetAppointment.getId(), userCVIds, authentication);
            appointmentDetailService.save(newAppointmentDetails);
            appointmentDetailSDO.setNewAppointmentDetails(newAppointmentDetails);
            appointmentDetailSDO.setReason(reasonToCancel);
            updateAppointmentCandidateInMongo(newAppointmentDetails, cancelAppointment.getVacancy().getId());
            boostActorManager.cancelAppointmentDetailsToMongo(appointmentDetailSDO);
        }
    }

    private List<AppointmentDetail> initNewAppointmentDetailForCandidates(long id, List<Long> userCvIds, Authentication authentication) {
        if (CollectionUtils.isEmpty(userCvIds)) {
            throw new InvalidParamException(ResponseStatus.APPOINTMENTS_EVENT_EMPTY_USER_CV);
        }
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        List<UserCurriculumVitae> userCurriculumVitaes = businessValidatorService.checkExistsUserCurriculumVitae(userCvIds);
        Appointment appointment = validateAppointmentBeforeSavingEvent(id, user);

        final List<AppointmentDetail> events = new ArrayList<>();

        userCurriculumVitaes.forEach(cv -> events.add(new AppointmentDetail(appointment, cv, AppointmentStatus.PENDING.getValue(), user.getId())));
        return events;
    }

    private void updateAppointmentCandidateInMongo(List<AppointmentDetail> result, long vacancyId) {
        updateAppointmentCandidateInVacancyDoc(result, vacancyId);
        VacancyDoc vacancyDoc = vacancyDocService.findById(vacancyId);
        if (Objects.nonNull(vacancyDoc)) {
            vacancyDoc.setUpdatedDate(DateUtils.toServerTimeForMongo());
            boostActorManager.updateMessageCenterDocInMongoActor(vacancyDoc);
        }
    }

    private void updateAppointmentCandidateInVacancyDoc(List<AppointmentDetail> result, long vacancyId) {
        if (CollectionUtils.isNotEmpty(result)) {
            List<AppointmentSlotEmbeddedSDO> appointmentDetails = new ArrayList<>();
            result.forEach(appointmentDetail -> {
                if (appointmentDetail.getAppointment().getVacancy().getId() == vacancyId) {
                    AppointmentSlotEmbedded appointmentSlotEmbedded = MongoConverters.convertToAppointmentSlotEmbedded(appointmentDetail);
                    AppointmentSlotEmbeddedSDO slotEmbeddedSDO = new AppointmentSlotEmbeddedSDO();
                    slotEmbeddedSDO.setUserCvId(appointmentDetail.getUserCurriculumVitae().getCurriculumVitaeId());
                    slotEmbeddedSDO.setAppointmentSlotEmbedded(appointmentSlotEmbedded);
                    appointmentDetails.add(slotEmbeddedSDO);
                }
            });
            vacancyDocService.updateAppointmentCandidatesEmbedded(appointmentDetails, vacancyId);
        }
    }

    private Appointment validateAppointmentRequestV2(AppointmentReq appointmentReq, Company company, Staff creator, List<Appointment> appointments, String timezone) {
        if (Objects.nonNull(appointmentReq)) {
            if (Objects.isNull(appointmentReq.getLocationId()) || Objects.isNull(appointmentReq.getManagerId())) {
                throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
            }

            if (Objects.isNull(company.getCompanyId())) {
                throw new InvalidParamException(ResponseStatus.COMPANY_ID_IS_EMPTY);
            }
            Long companyId = company.getCompanyId();
            List<Location> locations = businessValidatorService.checkExistsLocations(companyId, Lists.newArrayList(appointmentReq.getLocationId()));

            try {
                List<String> roleNames = CompanyRole.valueOf(creator.getRole().getName()).getRolesEqualOrLessNoAnalyst();
                List<Staff> managers = staffService.findByIdsAndCompanyIdAndRoleNames(companyId, Lists.newArrayList(appointmentReq.getManagerId()), roleNames);

                if (CollectionUtils.isEmpty(managers)) {
                    throw new InvalidParamException(ResponseStatus.USER_IS_NOT_JOIN_COMPANY);
                }

                Long appointmentId = appointmentReq.getId();
                List<Date> dateRanges = appointmentReq.getAppointmentDateRange();
                List<Date> timeRanges = appointmentReq.getAppointmentTimeRange();
                if (CollectionUtils.isEmpty(dateRanges) || CollectionUtils.isEmpty(timeRanges)) {
                    throw new InvalidParamException(ResponseStatus.APPOINTMENT_DATE_TIME_REQUIRED);
                }

                Collections.sort(dateRanges);
                if (Objects.isNull(appointmentId)) {
                    Date localNowDate = DateUtils.convertUTCByTimeZone(DateUtils.nowUtc(), timezone);
                    Date startDate = DateUtils.atStartOfDate(localNowDate);
                    boolean isPast = appointmentReq.getAppointmentDateRange().stream().anyMatch(date -> date.before(startDate));
                    if (isPast) {
                        throw new InvalidParamException(ResponseStatus.START_DATE_RANGE_IS_PAST);
                    }
                } else {
                    int countSlot = appointmentDetailService.countByAppointmentId(appointmentId);
                    if (countSlot != 0) {
                        throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS_APPOINTMENT);
                    }
                    boolean validToUpdate = appointments.stream()
                            .anyMatch(ap -> appointmentId.equals(ap.getId()) && !ap.getIsDeleted());
                    if (!validToUpdate) {
                        throw new InvalidParamException(ResponseStatus.APPOINTMENTS_NOT_IN_VANCANCY);
                    }
                    appointmentDateRangeService.deleteByAppointmentId(appointmentId);
                    appointmentTimeRangeService.deleteByAppointmentId(appointmentId);
                }

                return initNewAppointment(appointmentReq, creator, locations, managers);
            } catch (IllegalArgumentException ex) {
                throw new InvalidParamException(ResponseStatus.NOT_FOUND_ROLE);
            }
        }
        return null;
    }

    private Appointment initNewAppointment(AppointmentReq appointmentReq, Staff creator, List<Location> locations, List<Staff> managers) {
        Appointment appointment = new Appointment(creator.getUserFit().getUserProfileId());
        appointment.setManager(managers.get(0));
        appointment.setLocation(locations.get(0));
        List<Date> dateRange = appointmentReq.getAppointmentDateRange();
        Collections.sort(dateRange);
        List<Date> timeRange = appointmentReq.getAppointmentTimeRange();
        Collections.sort(timeRange);
        appointment.setDateRangeTimeRange(createAppointmentDateRange(appointment, dateRange), createAppointmentTimeRange(appointment, timeRange));
        appointment.setType(appointmentReq.getType());

        if (Objects.nonNull(appointmentReq.getId())) {
            Appointment exists = businessValidatorService.checkExistsAppointment(appointmentReq.getId());
            appointment.setId(exists.getId());
            appointment.setCreatedBy(exists.getCreatedBy());
            appointment.setCreatedDate(exists.getCreatedDate());
        }
        return appointment;
    }


}
