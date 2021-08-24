package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessAppointmentService;
import com.qooco.boost.business.impl.abstracts.BusinessAppointmentAbstract;
import com.qooco.boost.constants.Const;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.enumeration.AppointmentLockedStatus;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResultV2;
import com.qooco.boost.models.dto.appointment.AppointmentDTO;
import com.qooco.boost.models.dto.appointment.AppointmentDetailDTO;
import com.qooco.boost.models.dto.appointment.AppointmentDetailFullDTO;
import com.qooco.boost.models.dto.appointment.AppointmentReminderDTO;
import com.qooco.boost.models.request.appointment.AppointmentBaseReq;
import com.qooco.boost.models.request.appointment.AppointmentRemoveReq;
import com.qooco.boost.models.request.appointment.AppointmentReq;
import com.qooco.boost.models.sdo.AppointmentRemoveSDO;
import com.qooco.boost.threads.models.DeleteAppointmentInMongo;
import com.qooco.boost.utils.DateUtils;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;

@Service
public class BusinessAppointmentServiceImpl extends BusinessAppointmentAbstract implements BusinessAppointmentService {
    protected Logger logger = LogManager.getLogger(BusinessAppointmentServiceImpl.class);
    private static final int APPOINTMENT_ID_INDEX = 0;
    private static final int APPOINTMENT_DETAIL_ID_INDEX = 1;

    @Override
    public BaseResp saveAppointmentV2(Long id, AppointmentReq appointmentReq, Authentication authentication) {
        Appointment appointment = doCreateOrUpdateAppointment(id, appointmentReq, authentication);
        return new BaseResp<>(Objects.nonNull(appointment) ? new AppointmentDTO(appointment, getLocale(authentication)) : null);
    }

    @Override
    public BaseResp getAppointment(Long id, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        Long userProfileId = authenticatedUser.getId();
        Appointment foundAppointment = businessValidatorService.checkExistsAppointment(id);
        Staff staff;
        if (SELECT_APP.appId().equals(authenticatedUser.getAppId())) {
            staff = businessValidatorService.checkStaffPermissionOnAppointment(foundAppointment, userProfileId, ROLE_ACCESS_APPOINTMENT);
        } else {
            boolean isExisted = appointmentDetailService.checkExistsByIdAndUserProfileId(id, userProfileId);
            if (!isExisted) {
                throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
            }
            staff = null;
        }

        List<AppointmentDTO> appointmentDTOS = convertAppointmentsWithEventsToDTO(Lists.newArrayList(foundAppointment), staff, null, getLocale(authentication));
        if (CollectionUtils.isNotEmpty(appointmentDTOS)) {
            return new BaseResp<>(appointmentDTOS.get(0));
        }
        return new BaseResp();
    }

    @Override
    public BaseResp getAppointmentsByVacancyId(Long vacancyId, boolean isOnlyActive, Authentication authentication, Long exceptedUserCVId) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        Long userProfileId = authenticatedUser.getId();
        Vacancy foundVacancy = businessValidatorService.checkOpeningVacancy(vacancyId);
        Long companyId = foundVacancy.getCompany().getCompanyId();
        Staff staff = businessValidatorService.checkHasPermissionToCreateVacancy(companyId, userProfileId);
        List<Appointment> foundAppointments = findAppointmentsByVacancyIdWithRole(foundVacancy, staff, isOnlyActive);
        List<AppointmentDTO> appointmentDTOS = convertAppointmentsWithEventsToDTO(foundAppointments, staff, exceptedUserCVId, getLocale(authentication));
        return new BaseResp<>(appointmentDTOS);
    }

    private List<Appointment> findAppointmentsByVacancyIdWithRole(Vacancy foundVacancy, Staff staff, boolean isOnlyActive) {
        List<Appointment> foundAppointments;
        Long userProfileId = staff.getUserFit().getUserProfileId();
        if (CompanyRole.ADMIN.getName().equals(staff.getRole().getName())) {
            if (isOnlyActive) {
                foundAppointments = appointmentService.findNotExpiredByVacancyId(foundVacancy.getId());
            } else {
                foundAppointments = appointmentService.findByOpeningVacancyId(foundVacancy.getId());
            }
        } else {
            if (isOnlyActive) {
                foundAppointments = appointmentService.findNotExpiredByVacancyIdAndUserProfileId(foundVacancy.getId(), userProfileId);
            } else {
                foundAppointments = appointmentService.findByVacancyIdAndUserProfileId(foundVacancy.getId(), userProfileId);
            }
        }
        return foundAppointments;
    }

    private List<Appointment> findAppointmentsByCompanyIdWithRole(Staff staff, boolean isOnlyActive) {
        List<Appointment> appointments;
        Long companyId = staff.getCompany().getCompanyId();
        Long userProfileId = staff.getUserFit().getUserProfileId();
        if (CompanyRole.ADMIN.getName().equals(staff.getRole().getName())) {
            if (isOnlyActive) {
                appointments = appointmentService.findNotExpiredByCompanyId(companyId);
            } else {
                appointments = appointmentService.findByCompanyId(companyId);
            }
        } else {
            if (isOnlyActive) {
                appointments = appointmentService.findNotExpiredByCompanyIdAndUserProfileId(companyId, userProfileId);
            } else {
                appointments = appointmentService.findByCompanyIdAndUserProfileId(companyId, userProfileId);
            }
        }
        return appointments;
    }

    //==================================================================
    // Move all candidate C from appointment AP1 -> AP2
    // Only move candidate who are waiting or accepting in AP1 to AP2
    // Cancel event of candidate C in AP1
    // In AP2, event' status of candidate C is :
    // - WAITING: Do nothing
    // - ACCEPTED: Cancel current accepted appointment and send invitation again
    // - DECLINED: Send invitation to C in from AP2 again
    // - Not exists: Send invitation to C in from AP2
    // Confirm: http://deposite.qooco.com/jira/browse/BOOST-1624?focusedCommentId=38676&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-38676
    //==================================================================
    @Override
    public BaseResp deleteAppointment(Long id, Authentication authentication) {
        hasPermissionToEditOrDelete(id, getUser(authentication));
        deleteAppointment(id, getUser(authentication));

        List<AppointmentDetail> appointmentDetails = appointmentDetailService.findByAppointmentIdAndStatuses(id, AppointmentStatus.getAvailableStatus());
        List<AppointmentDetail> results = doCancelAppointmentDetails(appointmentDetails, true);
        doCancelAppointmentDetailOnMongo(results);
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp deleteAppointments(List<Long> ids, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        appointmentService.updateDeleteStatus(ids, true, user.getId(), DateUtils.nowUtcForOracle());
        DeleteAppointmentInMongo deleteAppointmentInMongo = new DeleteAppointmentInMongo();
        deleteAppointmentInMongo.setAppointmentIds(ids);
        boostActorManager.deleteAppointmentInMongo(deleteAppointmentInMongo);

        List<AppointmentDetail> appointmentDetails = appointmentDetailService.findByAppointmentIdAndStatuses(ids, AppointmentStatus.getAvailableStatus());
        List<AppointmentDetail> results = doCancelAppointmentDetails(appointmentDetails, true);
        doCancelAppointmentDetailOnMongo(results);
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    public void assignAppointmentToNewManagerAfterChangeRole(Staff oldManager, Staff newManager, Authentication authentication) {
        //Assign all appointment/event of oldManager to newManager
        // Step1: Get all appointment and events of oldManager (Pending and Accept) => cancel all -> delete appointment
        // Step2: Create new appointment (the same oldManager's appointment, only change manager)
        // Step3: Send invitation to candidate with newManager
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Long userId = user.getId();
        List<Long> availableAppointmentIdsToDelete = appointmentService.findByStaff(oldManager.getStaffId());
        List<Appointment> appointmentsToAssign = appointmentService.findAvailableByIdsAndAppointmentDetailStatus(availableAppointmentIdsToDelete, AppointmentStatus.getAvailableStatus());
        List<AppointmentRemoveSDO> appointmentRemoveSDOS = new ArrayList<>();

        availableAppointmentIdsToDelete.forEach(id -> {
            Appointment appointment = cloneAppointment(appointmentsToAssign, id, newManager, userId);
            Optional.ofNullable(appointment).ifPresent(a -> {
                AppointmentRemoveSDO appointmentRemoveSDO = new AppointmentRemoveSDO();
                appointmentRemoveSDO.setDeleteId(id);
                appointmentRemoveSDO.setAppointment(a);
                appointmentRemoveSDOS.add(appointmentRemoveSDO);
            });
        });

        List<Appointment> newAppointments = appointmentRemoveSDOS.stream()
                .filter(apr -> Objects.nonNull(apr.getAppointment()))
                .map(AppointmentRemoveSDO::getAppointment)
                .collect(Collectors.toList());
        doCreateAppointment(newAppointments);
        appointmentRemoveSDOS.forEach(apSDO -> deleteAppointmentAndAssignToAnother(apSDO.getDeleteId(), apSDO.getAppointment().getId(), Const.Vacancy.CancelAppointmentReason.UNASSIGNED_ROLE, authentication));
    }

    private Appointment cloneAppointment(List<Appointment> source, Long id, Staff newManager, Long ownerId) {
        if (CollectionUtils.isNotEmpty(source)) {
            Optional<Appointment> appointmentOptional = source.stream().filter(ap -> id.equals(ap.getId())).findFirst();
            if (appointmentOptional.isPresent()) {
                Appointment original = appointmentOptional.get();

                Appointment appointmentCloned = new Appointment(ownerId);
                appointmentCloned.setVacancy(original.getVacancy());
                appointmentCloned.setLocation(original.getLocation());
                appointmentCloned.setManager(newManager);

                List<AppointmentDateRange> appointmentDateRanges = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(original.getAppointmentDateRange())) {
                    original.getAppointmentDateRange().forEach(dateRange -> {
                        AppointmentDateRange appointmentDateRange = new AppointmentDateRange(ownerId);
                        appointmentDateRange.setAppointment(appointmentCloned);
                        appointmentDateRange.setAppointmentDate(dateRange.getAppointmentDate());
                        appointmentDateRanges.add(appointmentDateRange);
                    });
                }

                List<AppointmentTimeRange> appointmentTimeRanges = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(original.getAppointmentTimeRange())) {
                    original.getAppointmentTimeRange().forEach(timeRange -> {
                        AppointmentTimeRange appointmentTimeRange = new AppointmentTimeRange(ownerId);
                        appointmentTimeRange.setAppointment(appointmentCloned);
                        appointmentTimeRange.setAppointmentTime(timeRange.getAppointmentTime());
                        appointmentTimeRanges.add(appointmentTimeRange);
                    });
                }
                appointmentCloned.setDateRangeTimeRange(appointmentDateRanges, appointmentTimeRanges);
                return appointmentCloned;
            }
        }
        return null;
    }
    @Override
    @Transactional
    public BaseResp deleteAppointmentWithNewOrChangeOption(AppointmentRemoveReq request, Authentication authentication) {
        if (Objects.isNull(request.getDeleteId())) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        ResponseStatus result = ResponseStatus.BAD_REQUEST;
        if (Objects.nonNull(request.getTargetId())) {
            deleteAppointmentAndAssignToAnother(request.getDeleteId(), request.getTargetId(), authentication);
            result = ResponseStatus.SUCCESS;
        } else if (Objects.nonNull(request.getAppointment())) {
            Appointment appointment = doCreateOrUpdateAppointment(null, request.getAppointment(), authentication);
            if (Objects.nonNull(appointment)) {
                deleteAppointmentAndAssignToAnother(request.getDeleteId(), appointment.getId(), authentication);
                result = ResponseStatus.SUCCESS;
            }
        }
        return new BaseResp(result);
    }

    @Override
    public List<Appointment> findAppointmentInSuspendedRange(Vacancy vacancy) {
        return appointmentService.findAppointmentInSuspendedRangeOfVacancy(vacancy);
    }

    @Override
    public BaseResp getAppointmentsByCompanyId(Long companyId, boolean isOnlyActive, Authentication authentication, Long exceptedUserCVId) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        Long userProfileId = authenticatedUser.getId();
        businessValidatorService.checkExistsCompany(companyId);
        Staff staff = businessValidatorService.checkHasPermissionToCreateVacancy(companyId, userProfileId);
        List<Appointment> appointments = findAppointmentsByCompanyIdWithRole(staff, isOnlyActive);
        List<AppointmentDTO> appointmentDTOS = convertAppointmentsWithEventsToDTO(appointments, staff, exceptedUserCVId, getLocale(authentication));
        return new BaseResp<>(appointmentDTOS);
    }

    @Override
    public BaseResp getAppointmentByVacancyIdOrCompanyId(Long vacancyId, Long companyId, boolean isOnlyActive, Authentication authentication, Long exceptedUserCVId) {
        if (Objects.nonNull(vacancyId)) {
            return getAppointmentsByVacancyId(vacancyId, isOnlyActive, authentication, exceptedUserCVId);
        } else if (Objects.nonNull(companyId)) {
            return getAppointmentsByCompanyId(companyId, isOnlyActive, authentication, exceptedUserCVId);
        }
        return new BaseResp(ResponseStatus.BAD_REQUEST);
    }

    @Override
    public List<Appointment> validateAppointmentRequests(List<? extends AppointmentBaseReq> appointmentReqs, Company company, Authentication authentication) {
        List<Appointment> results = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(appointmentReqs)) {
            AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
            if (appointmentReqs.stream().distinct().count() < appointmentReqs.size()) {
                throw new InvalidParamException(ResponseStatus.APPOINTMENTS_IS_DUPLICATE);
            }

            List<Long> managerIds = appointmentReqs.stream()
                    .filter(ap -> Objects.nonNull(ap.getManagerId()))
                    .map(AppointmentBaseReq::getManagerId).collect(Collectors.toList());


            List<Long> locationIds = appointmentReqs.stream()
                    .filter(ap -> Objects.nonNull(ap.getLocationId()))
                    .map(AppointmentBaseReq::getLocationId).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(managerIds) || managerIds.size() < appointmentReqs.size()
                    || CollectionUtils.isEmpty(locationIds) || locationIds.size() < appointmentReqs.size()) {
                throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
            }

            if (appointmentReqs.stream().anyMatch(ap -> CollectionUtils.isEmpty(ap.getAppointmentDateRange()) || CollectionUtils.isEmpty(ap.getAppointmentTimeRange()))) {
                throw new InvalidParamException(ResponseStatus.APPOINTMENT_DATE_TIME_REQUIRED);
            }


            if (Objects.isNull(company.getCompanyId())) {
                throw new InvalidParamException(ResponseStatus.COMPANY_ID_IS_EMPTY);
            }

            List<Long> appointmentIds = appointmentReqs.stream()
                    .filter(ap -> Objects.nonNull(ap.getId()))
                    .map(AppointmentBaseReq::getId).distinct()
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(appointmentIds)) {
                businessValidatorService.existsAppointment(appointmentIds);
            }

            List<Staff> managers;
            Long companyId = company.getCompanyId();
            Staff creator = businessValidatorService.checkExistsStaffInApprovedCompany(companyId, user.getId());
            try {
                List<String> roleNames = CompanyRole.valueOf(creator.getRole().getName()).getRolesEqualOrLessNoAnalyst();
                managerIds = managerIds.stream().distinct().collect(Collectors.toList());
                managers = staffService.findByIdsAndCompanyIdAndRoleNames(companyId, managerIds, roleNames);
                if (CollectionUtils.isEmpty(managers) || managerIds.size() > managers.size()) {
                    throw new InvalidParamException(ResponseStatus.USER_IS_NOT_JOIN_COMPANY);
                }
            } catch (IllegalArgumentException ex) {
                throw new InvalidParamException(ResponseStatus.NOT_FOUND_ROLE);
            }

            locationIds = locationIds.stream().distinct().collect(Collectors.toList());
            List<Location> locations = businessValidatorService.checkExistsLocations(companyId, locationIds);

            Appointment appointment;
            for (AppointmentBaseReq appointmentReq : appointmentReqs) {
                List<Date> dateRange = appointmentReq.getAppointmentDateRange();
                Collections.sort(dateRange);
                List<Date> timeRange = appointmentReq.getAppointmentTimeRange();
                Collections.sort(timeRange);

                Date localNowDate = DateUtils.convertUTCByTimeZone(DateUtils.nowUtc(), user.getTimezone());
                Date startDate = DateUtils.atStartOfDate(localNowDate);
                boolean isPast = appointmentReq.getAppointmentDateRange().stream().anyMatch(date -> date.before(startDate));
                if (isPast) {
                    throw new InvalidParamException(ResponseStatus.START_DATE_RANGE_IS_PAST);
                }

                appointment = new Appointment(user.getId());
                appointment.setId(appointmentReq.getId());
                appointment.setType(appointmentReq.getType());
                appointment.setDateRangeTimeRange(createAppointmentDateRange(appointment, dateRange), createAppointmentTimeRange(appointment, timeRange));
                Optional<Location> optionalLocation = locations.stream()
                        .filter(l -> l.getLocationId().equals(appointmentReq.getLocationId())).findFirst();
                if (optionalLocation.isPresent()) {
                    appointment.setLocation(optionalLocation.get());
                }
                Optional<Staff> optionalStaff = managers.stream()
                        .filter(m -> m.getStaffId().equals(appointmentReq.getManagerId())).findFirst();
                if (optionalStaff.isPresent()) {
                    appointment.setManager(optionalStaff.get());
                }

                results.add(appointment);
            }
        }
        return results;
    }

    @Override
    public BaseResp assignCandidateToAppointment(long id, long userCvId, Authentication authentication) {
        UserCurriculumVitae curriculumVitae = businessValidatorService.checkExistsUserCurriculumVitae(userCvId);
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Appointment appointment = validateAppointmentBeforeSavingEvent(id, user);
        List<Long> userCVIds = Lists.newArrayList(userCvId);
        List<Integer> statuses = Lists.newArrayList(AppointmentStatus.PENDING.getValue());
        List<AppointmentDetail> appointmentDetails = appointmentDetailService.findByAppointmentIdAndUserCVIdAndStatus(id, userCVIds, statuses);
        if (CollectionUtils.isNotEmpty(appointmentDetails)) {
            throw new NoPermissionException(ResponseStatus.APPOINTMENT_INVITATION_IS_SENT_ALREADY);
        }
        AppointmentDetail appointmentDetail = new AppointmentDetail(appointment, curriculumVitae, AppointmentStatus.PENDING.getValue(), user.getId());
        Staff staff = staffService.findByUserProfileAndCompany(getCompanyId(authentication), getUserId(authentication))
                .stream().findFirst().orElseGet(() -> appointmentDetail.getAppointment().getManager());
        processCandidateInVacancy(curriculumVitae, staff, appointmentDetail);
        saveAppointmentDetailsInMongoAndOracle(Lists.newArrayList(appointmentDetail));
        AppointmentDetailFullDTO appointmentDetailDTO = new AppointmentDetailFullDTO(appointmentDetail, getLocale(authentication));
        return new BaseResp<>(appointmentDetailDTO);
    }

    @Override
    public BaseResp getAppointmentsInManagement(Long vacancyId, Long userProfileId, int page, int size, boolean isCurrent, Authentication authentication) {
        Vacancy foundVacancy = businessValidatorService.checkOpeningVacancy(vacancyId);
        Long companyId = foundVacancy.getCompany().getCompanyId();
        Staff staff = businessValidatorService.checkHasPermissionToCreateVacancy(companyId, userProfileId);
        // TODO update
        Page<Appointment> foundAppointments = doGetByVacancyAndUserAndRoles(foundVacancy, userProfileId, staff, isCurrent, page, size);
        if (Objects.nonNull(foundAppointments) && CollectionUtils.isNotEmpty(foundAppointments.getContent())) {
            List<AppointmentDTO> appointmentDTOS = convertToAppointmentDTO(foundAppointments.getContent(), staff, isCurrent, getLocale(authentication));
            return new BaseResp<>(new PagedResultV2<>(appointmentDTOS, page, foundAppointments));
        }
        return new BaseResp<>(new PagedResultV2<>(new ArrayList<>()));
    }

    @Override
    public BaseResp countLatestAppointment(Long companyId, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        String appId = authenticatedUser.getAppId();
        Long userProfileId = authenticatedUser.getId();
        AppointmentReminderDTO result = null;
        List<Integer> pendingAcceptedStatus = Lists.newArrayList(AppointmentStatus.PENDING_ACCEPTED.getValue());
        if (SELECT_APP.appId().equals(appId)) {
            if (Objects.isNull(companyId)) {
                AppointmentDetail latestEvent = appointmentDetailService.findAvailableByUserProfileIdAndStatuses(userProfileId, pendingAcceptedStatus);
                result = getAppointmentReminderDTO(latestEvent, getLocale(authentication));
            } else {
                AppointmentDetail latestEvent = appointmentDetailService.findAvailableLatestEventByUserProfileIdAndCompanyIdAndStatuses(userProfileId, companyId, pendingAcceptedStatus);
                result = getAppointmentReminderDTO(latestEvent, getLocale(authentication));
            }
        } else if (PROFILE_APP.appId().equals(appId)) {
            List<AppointmentDetail> appointmentDetails = appointmentDetailService.findByCandidateAndStatus(userProfileId, pendingAcceptedStatus);
            if (CollectionUtils.isNotEmpty(appointmentDetails)) {
                return new BaseResp<>(new AppointmentReminderDTO(appointmentDetails, getLocale(authentication)));
            }
        }
        return new BaseResp<>(result);
    }

    private void deleteAppointment(Long id, AuthenticatedUser user) {
        appointmentService.updateDeleteStatus(id, true, user.getId(), DateUtils.nowUtcForOracle());
        DeleteAppointmentInMongo deleteAppointmentInMongo = new DeleteAppointmentInMongo();
        deleteAppointmentInMongo.setAppointmentIds(Lists.newArrayList(id));
        boostActorManager.deleteAppointmentInMongo(deleteAppointmentInMongo);
    }

    private void deleteAppointmentAndAssignToAnother(Long id, Long targetId, Authentication authentication) {
        deleteAppointmentAndAssignToAnother(id, targetId, Const.Vacancy.CancelAppointmentReason.UNKNOWN, authentication);
    }

    private void deleteAppointmentAndAssignToAnother(Long id, Long targetId, Integer reasonToCancel, Authentication authentication) {
        hasPermissionToEditOrDelete(id, getUser(authentication));
        List<AppointmentDetail> appointmentDetails = appointmentDetailService.findByAppointmentIdAndStatuses(id, AppointmentStatus.getAvailableStatus());
        deleteAppointment(id, getUser(authentication));
        if (Objects.nonNull(targetId)) {
            Appointment targetAppointment = appointmentService.findById(targetId);
            doCancelAndAssignToTarget(appointmentDetails, targetAppointment, reasonToCancel, authentication);
        }
    }

    private AppointmentReminderDTO getAppointmentReminderDTO(AppointmentDetail latestEvent, String locale) {
        if (Objects.isNull(latestEvent)) {
            return null;
        }
        int count = appointmentDetailService.countAvailableByAppointmentIdAndStatuses(latestEvent.getAppointment().getId(), Lists.newArrayList(latestEvent.getStatus()));
        return new AppointmentReminderDTO(latestEvent, count, locale);
    }


    private Map<Long, List<Long>> getAppointmentDetailDisallowed(Long ownedId, List<Long> appointmentIds, List<Integer> statuses, List<String> roleNames) {
        Map<Long, List<Long>> result = new HashMap<>();
        List<Object[]> idGroup = null;

        if (CollectionUtils.isNotEmpty(roleNames)) {
            idGroup = appointmentDetailService.findIdByUpdaterWithStatusAndRoles(ownedId, appointmentIds, statuses, roleNames);
        }

        if (CollectionUtils.isNotEmpty(idGroup)) {
            idGroup.forEach(ids -> {
                if (Objects.nonNull(ids[APPOINTMENT_ID_INDEX]) && Objects.nonNull(ids[APPOINTMENT_DETAIL_ID_INDEX])) {
                    Long appointmentId = Long.valueOf(ids[APPOINTMENT_ID_INDEX].toString());
                    Long appointmentDetailId = Long.valueOf(ids[APPOINTMENT_DETAIL_ID_INDEX].toString());
                    List<Long> appointmentDetailIds = result.get(appointmentId);
                    if (CollectionUtils.isEmpty(appointmentDetailIds)) {
                        appointmentDetailIds = new ArrayList<>();
                    }
                    appointmentDetailIds.add(appointmentDetailId);
                    result.put(appointmentId, appointmentDetailIds);
                }
            });
        }
        return result;
    }

    private List<AppointmentDTO> convertToAppointmentDTO(List<Appointment> foundAppointments, Staff staff, boolean isCurrent, String locale) {
        if (CollectionUtils.isNotEmpty(foundAppointments)) {
            List<Long> appointmentIds = foundAppointments.stream().map(Appointment::getId).distinct().collect(Collectors.toList());
            List<AppointmentDTO> appointmentDTOS = foundAppointments.stream().map(it -> new AppointmentDTO(it, locale)).collect(Collectors.toList());
            if (Objects.nonNull(staff)) {
                List<AppointmentDetail> appointmentDetails = appointmentDetailService.findByAppointmentIds(appointmentIds);

                List<String> roleNames = CompanyRole.valueOf(staff.getRole().getName()).getRolesLarger();
                Map<Long, List<Long>> appointmentMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(roleNames)) {
                    appointmentMap = getAppointmentDetailDisallowed(staff.getUserFit().getUserProfileId(), appointmentIds,
                            AppointmentStatus.getAcceptedStatus(), roleNames);
                }
                for (AppointmentDTO appointmentDTO : appointmentDTOS) {
                    if (CollectionUtils.isNotEmpty(appointmentDetails)) {
                        Long id = appointmentDTO.getId();
                        boolean isLocked = appointmentDetails.stream().anyMatch(ad -> id.equals(ad.getAppointmentId()));
                        appointmentDTO.setLockedStatus(isLocked ? AppointmentLockedStatus.LOCKED_EDIT.getValue() : AppointmentLockedStatus.NONE.getValue());

                        List<Long> lockedEventIds = appointmentMap.get(id);
                        if (CollectionUtils.isNotEmpty(lockedEventIds)) {
                            appointmentDTO.setLockedStatus(AppointmentLockedStatus.LOCKED_DELETE.getValue());
                        }
                        List<AppointmentDetailDTO> events = new ArrayList<>();
                        appointmentDetails.stream().distinct().filter(ad -> id.equals(ad.getAppointmentId())
                                && AppointmentStatus.getAcceptedStatus().contains(ad.getStatus())
                                && (isCurrent ? DateUtils.nowUtc().before(ad.getAppointmentTime())
                                : DateUtils.nowUtc().after(ad.getAppointmentTime()))).forEach(ad -> events.add(new AppointmentDetailDTO(ad, locale)));
                        if (CollectionUtils.isNotEmpty(events)) {
                            events.sort(Comparator.comparing(AppointmentDetailDTO::getEventTime));
                            appointmentDTO.setEvents(events);
                        }
                    }
                }
            }
            return CollectionUtils.isEmpty(appointmentDTOS) ? new ArrayList<>() : appointmentDTOS;
        }
        return new ArrayList<>();
    }

    private Page<Appointment> doGetByVacancyAndUserAndRoles(Vacancy foundVacancy, Long userProfileId, Staff staff, boolean isCurrent, int page, int size) {
        Page<Appointment> foundAppointments;
        List<Integer> statuses = AppointmentStatus.getAcceptedStatus();
        List<Long> roleIds = CompanyRole.valueOf(staff.getRole().getName()).getRolesSmallerOrAdminRole();
        if (CollectionUtils.isNotEmpty(roleIds)) {
            foundAppointments = isCurrent ?
                    appointmentService.findCurrentByVacancyAndUserAndStatusesAndRoles(foundVacancy.getId(), userProfileId, statuses, roleIds, page, size)
                    : appointmentService.findPastByVacancyAndUserAndStatusesAndRoles(foundVacancy.getId(), userProfileId, statuses, roleIds, page, size);
        } else {
            foundAppointments = isCurrent ? appointmentService.findCurrentByVacancyAndUserAndStatuses(foundVacancy.getId(), userProfileId, statuses, page, size)
                    : appointmentService.findPastByVacancyAndUserAndStatuses(foundVacancy.getId(), userProfileId, statuses, page, size);
        }
        return foundAppointments;
    }

    private List<AppointmentDTO> convertAppointmentsWithEventsToDTO(List<Appointment> foundAppointments, Staff staff, Long exceptedUserCVId, String locale) {
        List<AppointmentDTO> appointmentDTOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(foundAppointments)) {
            List<Long> appointmentIds = foundAppointments.stream().map(Appointment::getId).distinct().collect(Collectors.toList());
            if (Objects.nonNull(staff)) {
                List<AppointmentDetail> appointmentDetails = appointmentDetailService.findByAppointmentIds(appointmentIds);

                List<Appointment> appointmentsIncludingUserCVId = new ArrayList<>();
                if (Objects.nonNull(exceptedUserCVId) && CollectionUtils.isNotEmpty(appointmentDetails)) {
                    List<AppointmentDetail> appointmentDetailsIncludedUserCVId = appointmentDetails.stream()
                            .filter(ad -> exceptedUserCVId.equals(ad.getUserCurriculumVitae().getCurriculumVitaeId())).collect(Collectors.toList());
                    appointmentsIncludingUserCVId = appointmentDetailsIncludedUserCVId.stream().distinct().map(AppointmentDetail::getAppointment).collect(Collectors.toList());
                    appointmentDetails.removeAll(appointmentDetailsIncludedUserCVId);
                }

                foundAppointments.removeAll(appointmentsIncludingUserCVId);

                List<String> roleNames = CompanyRole.valueOf(staff.getRole().getName()).getRolesLarger();
                Map<Long, List<Long>> appointmentMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(roleNames)) {
                    appointmentMap = getAppointmentDetailDisallowed(staff.getUserFit().getUserProfileId(), appointmentIds, AppointmentStatus.getAvailableStatus(), roleNames);
                }

                for (Appointment appointment : foundAppointments) {
                    AppointmentDTO appointmentDTO = new AppointmentDTO(appointment, locale);
                    List<Long> lockedAppointmentDetails = appointmentMap.get(appointmentDTO.getId());

                    boolean isLocked = CollectionUtils.isNotEmpty(appointmentDetails)
                            && appointmentDetails.stream().anyMatch(ad -> appointmentDTO.getId().equals(ad.getAppointment().getId()));
                    appointmentDTO.setLockedStatus(isLocked ? AppointmentLockedStatus.LOCKED_EDIT.getValue() : AppointmentLockedStatus.NONE.getValue());

                    if (CollectionUtils.isNotEmpty(appointmentDetails)) {
                        List<AppointmentDetailDTO> eventList = new ArrayList<>();
                        appointmentDetails.forEach(ad -> {
                            if (appointment.getId().equals(ad.getAppointment().getId())) {
                                AppointmentDetailDTO appointmentDetailDTO = new AppointmentDetailDTO(ad, locale);

                                if (CollectionUtils.isNotEmpty(lockedAppointmentDetails) && lockedAppointmentDetails.contains(appointmentDetailDTO.getId())) {
                                    appointmentDetailDTO.setLocked(true);
                                }

                                if (AppointmentStatus.getAcceptedStatus().contains(appointmentDetailDTO.getStatus())) {
                                    eventList.add(appointmentDetailDTO);
                                }
                            }
                        });
                        appointmentDTO.setEvents(eventList);
                    }

                    appointmentDTOS.add(appointmentDTO);
                }
            } else {
                appointmentDTOS = foundAppointments.stream().map(it -> new AppointmentDTO(it, locale)).collect(Collectors.toList());
            }
        }
        return appointmentDTOS;
    }

    private void processCandidateInVacancy(@NonNull UserCurriculumVitae userCurriculumVitae, @NonNull Staff staff, @NonNull AppointmentDetail appointmentDetail) {
        MessageCenterDoc messageCenterDoc = messageCenterDocService.findByVacancy(
                appointmentDetail.getAppointment().getVacancy().getId());
        Optional hasApplicantMessage = Optional.ofNullable(messageCenterDoc).filter(
                mc -> messageDocService.hasApplicantMessage(
                        mc.getId(),userCurriculumVitae.getUserProfile().getUserProfileId()));
        if (!hasApplicantMessage.isPresent()) {
            VacancyProcessing vacancyProcessing = new VacancyProcessing(userCurriculumVitae, appointmentDetail.getAppointment().getVacancy(), staff, Const.Vacancy.ClassifyAction.APPLIED);
            vacancyProcessingService.save(Lists.newArrayList(vacancyProcessing));
        }
    }
}
