package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessStatisticService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.model.StaffStatistic;
import com.qooco.boost.data.model.count.CountByDate;
import com.qooco.boost.data.model.count.CountCandidateProcessing;
import com.qooco.boost.data.model.count.CountVacancySeat;
import com.qooco.boost.data.mongo.embedded.RejectedUserCvEmbedded;
import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.data.mongo.services.ViewProfileDocService;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.data.oracle.services.views.ViewStatisticVacancySeatService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResultV2;
import com.qooco.boost.models.dto.statistic.*;
import com.qooco.boost.models.request.DurationRequest;
import com.qooco.boost.models.response.StatisticResp;
import com.qooco.boost.models.transfer.ViewProfileTransfer;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoConverters;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.qooco.boost.utils.DateUtils.ONE_DAY_IN_MILLISECOND;
import static com.qooco.boost.utils.MongoConverters.convertToUserProfileCvEmbedded;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@Service
public class BusinessStatisticServiceImpl extends BaseBusinessStatisticServiceImpl implements BusinessStatisticService {
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserFitService userFitService;
    @Autowired
    private UserCurriculumVitaeService userCurriculumVitaeService;
    @Autowired
    private ViewProfileDocService viewProfileDocService;
    @Autowired
    private VacancyDocService vacancyDocService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private UserCvDocService userCvDocService;
    @Autowired
    private BoostActorManager boostActorManager;
    @Autowired
    private VacancySeatService vacancySeatService;
    @Autowired
    private ViewStatisticVacancySeatService viewStatisticVacancySeatService;
    @Autowired
    private VacancyProcessingService vacancyProcessingService;
    @Autowired
    private AppointmentFeedbackService appointmentFeedbackService;
    @Autowired
    private StaffWorkingService staffWorkingService;
    @Autowired
    private BusinessValidatorService businessValidatorService;

    @Value(ApplicationConstant.BOOST_PATA_VACANCY_REJECTED_LIMIT_TIME)
    private int rejectedLimitTime;

    @Override
    public BaseResp saveViewProfile(Long candidateId, Long vacancyId, Long viewerId) {
        if (Objects.isNull(candidateId) || Objects.isNull(viewerId)) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        if (candidateId.equals(viewerId)) {
            throw new InvalidParamException(ResponseStatus.CAN_NOT_VIEW_YOURSELF);
        }

        UserFit viewerProfile = userFitService.findById(viewerId);
        UserProfile candidateProfile = userProfileService.findById(candidateId);
        if (Objects.isNull(viewerProfile) || Objects.isNull(candidateProfile)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_PROFILE);
        }

        UserCurriculumVitae candidateCV = userCurriculumVitaeService.findByUserProfile(candidateProfile);
        UserCvDoc userCvDoc = ofNullable(candidateCV).map(it -> userCvDocService.findByUserProfileId(candidateId)).orElse(null);

        ViewProfileTransfer resp = new ViewProfileTransfer();
        resp.setCandidate(ofNullable(userCvDoc).map(MongoConverters::convertToUserProfileCvEmbedded).orElseGet(() -> convertToUserProfileCvEmbedded(candidateProfile)));
        resp.setViewer(MongoConverters.convertToUserProfileEmbedded(viewerProfile));
        resp.getViewer().setUserType(UserType.SELECT);

        VacancyDoc vacancyDoc = vacancyDocService.findById(vacancyId);
        if (Objects.isNull(vacancyDoc)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_VACANCY);
        }

        Long userIdOfContactPerson = vacancyDoc.getContactPerson().getUserProfile().getUserProfileId();
        StaffEmbedded createdBy = vacancyDoc.getCreatedByStaff();
        List<String> roles = Lists.newArrayList(CompanyRole.ADMIN.getName(), CompanyRole.HEAD_RECRUITER.getName(), CompanyRole.RECRUITER.getName());
        List<String> adminAndHeadRecruiterRoles = Lists.newArrayList(CompanyRole.ADMIN.getName(), CompanyRole.HEAD_RECRUITER.getName());
        List<Staff> staffs = staffService.findByUserProfileAndCompanyApprovalAndRoles(viewerId, vacancyDoc.getCompany().getId(), adminAndHeadRecruiterRoles);
        if (!(viewerId.equals(userIdOfContactPerson)
                || CollectionUtils.isNotEmpty(staffs)
                || (Objects.nonNull(createdBy) && viewerId.equals(createdBy.getUserProfile().getUserProfileId()) && roles.contains(createdBy.getRoleCompany().getName())))) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }

        if (CollectionUtils.isNotEmpty(vacancyDoc.getAppliedUserCvId())) {
            boolean isApplied = vacancyDoc.getAppliedUserCvId().contains(resp.getCandidate().getUserProfileCvId());
            if (isApplied) {
                throw new InvalidParamException(ResponseStatus.CANDIDATE_IS_ALREADY_APPLIED);
            }
        }
        if (CollectionUtils.isNotEmpty(vacancyDoc.getRejectedUserCv())) {
            Optional<RejectedUserCvEmbedded> rejectedUser = vacancyDoc.getRejectedUserCv().stream()
                    .filter(rejectUser -> rejectUser.getUserCvId().equals(resp.getCandidate().getUserProfileCvId())).findFirst();
            if (rejectedUser.isPresent()) {
                long limitTime = rejectedUser.get().getRejectedDate().getTime() - rejectedLimitTime * 1000;
                long nowTime = (new Date()).getTime();
                if (nowTime < limitTime) {
                    throw new InvalidParamException(ResponseStatus.CANDIDATE_IS_ALREADY_REJECTED);
                }
            }
        }
        resp.setVacancy(MongoConverters.convertToVacancyEmbedded(vacancyDoc));

        boostActorManager.saveViewProfileInMongo(resp);
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp getStatisticByUserProfileId(String timeZone, Long userProfileId) {
        if (StringUtils.isBlank(timeZone)) {
            timeZone = TimeZone.getDefault().getDisplayName();
        }
        LocalDateTime localDateTime = DateUtils.convertDateBetweenTimeZones(new Date(), timeZone);
        if (Objects.isNull(localDateTime)) {
            throw new InvalidParamException(ResponseStatus.TIME_ZONE_INVALID);
        }

        LocalDateTime startWeekLocal = DateUtils.getStartDayOfWeek(localDateTime);
        long millisecondDayOfWeek = DateUtils.getMillisecond(startWeekLocal, ZoneId.systemDefault());
        Date startDateOfWeekInMongo = DateUtils.convertMillisecondToDate(millisecondDayOfWeek);

        LocalDateTime startMonthLocal = DateUtils.getStartDayOfMonth(localDateTime);
        long millisecondDayOfMonth = DateUtils.getMillisecond(startMonthLocal, ZoneId.systemDefault());
        Date startDateOfMonthInMongo = DateUtils.convertMillisecondToDate(millisecondDayOfMonth);

        StatisticResp resp = new StatisticResp();
        long viewsPerWeek = viewProfileDocService.countViewProfileByCandidateIdAndTimestamp(userProfileId, startDateOfWeekInMongo);
        resp.setViewsPerWeek(viewsPerWeek);
        long viewsPerMonth = viewProfileDocService.countViewProfileByCandidateIdAndTimestamp(userProfileId, startDateOfMonthInMongo);
        resp.setViewsPerMonth(viewsPerMonth);
        return new BaseResp<>(resp);
    }

    @Override
    public BaseResp getCompanyDashboard(DurationRequest request, Authentication auth) {
        validateDateRange(request.getStartDate(), request.getEndDate());
        boolean hasPermission = hasPermissionAccessStatisticDashboard(auth);
        if (!hasPermission) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        return new BaseResp<>(CompanyDashboardDTO.builder()
                .seats(genderCompanyVacancySeat(request.getStartDate(), request.getEndDate(), auth))
                .bestEmployees(getBestEmployeeBaseOnClosedSeat(request, auth))
                .build());
    }

    private List<BestEmployeeDTO> getBestEmployeeBaseOnClosedSeat(DurationRequest request, Authentication auth) {
        int staffIdIndex = 0;
        int totalClosedSeatIndex = 1;
        int numberBestEmployeeLimit = 3;

        List<Long[]> bestEmployees = viewStatisticVacancySeatService.findTopEmployeeByCompany(getCompanyId(auth), request.getStartDate(), request.getEndDate(), numberBestEmployeeLimit);
        bestEmployees = bestEmployees.stream().filter(it -> it[totalClosedSeatIndex] > 0).collect(Collectors.toList());

        Map<Long, Integer> closedSeats = new HashMap<>();
        bestEmployees.forEach(it -> closedSeats.put(it[staffIdIndex], it[totalClosedSeatIndex].intValue()));

        List<BestEmployeeDTO> result = new ArrayList<>();
        if (MapUtils.isNotEmpty(closedSeats)) {
            List<Staff> staffs = staffService.findById(new ArrayList<>(closedSeats.keySet()));
            Map<Long, Integer> starOfBestEmployees = calculatorStarForBestEmployee(closedSeats);
            starOfBestEmployees.forEach((staffId, numberStar) -> staffs.stream()
                    .filter(staff -> staff.getStaffId().equals(staffId)).findFirst()
                    .ifPresent(st -> result.add(new BestEmployeeDTO(st, starOfBestEmployees.get(staffId), getLocale(auth)))));
        }
        return result;
    }

    private LinkedHashMap<Long, Integer> calculatorStarForBestEmployee(Map<Long, Integer> closedSeats) {
        LinkedHashMap<Long, Integer> starOfBestEmployees = new LinkedHashMap<>();
        AtomicInteger lastNumberSeat = new AtomicInteger(Integer.MAX_VALUE);

        LinkedHashMap<Long, Integer> mapSorted = closedSeats.entrySet()
                .stream()
                .sorted((Map.Entry.<Long, Integer>comparingByValue().reversed()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        //Current only one start for best employee
        mapSorted.forEach((staffId, numberSeat) -> {
            int numberStart = MapUtils.isEmpty(starOfBestEmployees) || (numberSeat == lastNumberSeat.get() && lastNumberSeat.get() > 0) ? 1 : 0;
            lastNumberSeat.set(numberSeat);
            starOfBestEmployees.put(staffId, numberStart);
        });
        return starOfBestEmployees;
    }

    @Override
    public BaseResp getEmployeeDashboard(DurationRequest request, int page, int size, Authentication auth) {
        validateDateRange(request.getStartDate(), request.getEndDate());
        if (page < 0 || size <= 0) {
            throw new InvalidParamException(ResponseStatus.INVALID_PAGINATION);
        }
        boolean hasPermission = hasPermissionAccessStatisticDashboard(auth);
        if (!hasPermission) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        long now = new Date().getTime();
        Page<StaffStatistic> staffStatistics;
        int totalClosedSeats = 0;
        int totalOpenSeats = 0;
        int totalProcessedCandidates = vacancyProcessingService.countByCompanyInDuration(getCompanyId(auth), request.getStartDate(), request.getEndDate());
        int totalAppointments = appointmentFeedbackService.countByCompanyInDuration(getCompanyId(auth), request.getStartDate(), request.getEndDate());
        int totalActiveMinutes = staffWorkingService.sumByCompanyInDuration(getCompanyId(auth), request.getStartDate(), request.getEndDate());
        businessValidatorService.checkExistsStaffInCompany(getCompanyId(auth), getUserId(auth));
        if (now >= request.getEndDate()) {
            // get in past
            List<CountVacancySeat> countVacancySeats = vacancySeatService.countVacancySeatInRangeByCompany(
                    getCompanyId(auth), request.getStartDate(), request.getEndDate());
            totalClosedSeats = countVacancySeats.stream().mapToInt(CountVacancySeat::getClosedSeats).sum();
            totalOpenSeats = countVacancySeats.get(countVacancySeats.size() - 1).getOpenSeats();
            staffStatistics = staffService.findStaffStatisticByCompany(
                    getCompanyId(auth), request.getStartDate(),
                    request.getEndDate() - ONE_DAY_IN_MILLISECOND + 1,
                    request.getEndDate(), page, size);
        } else if (now < request.getStartDate()) {
            // get in future: return 0 for staff
            staffStatistics = staffService.findStaffStatisticByCompany(
                    getCompanyId(auth), DateUtils.MAX_DATE_ORACLE - ONE_DAY_IN_MILLISECOND,
                    DateUtils.MAX_DATE_ORACLE - ONE_DAY_IN_MILLISECOND + 1,
                    DateUtils.MAX_DATE_ORACLE, page, size);
        } else {
            // get int pass to now
            long beginOfNow = 0;
            for (long d = request.getStartDate(); d <= now; d += ONE_DAY_IN_MILLISECOND) {
                beginOfNow = d;
            }
            staffStatistics = staffService.findStaffStatisticByCompany(
                    getCompanyId(auth), request.getStartDate(), beginOfNow, now, page, size);

            List<CountVacancySeat> countVacancySeats = vacancySeatService.countVacancySeatInRangeByCompany(
                    getCompanyId(auth), request.getStartDate(), now);
            totalClosedSeats = countVacancySeats.stream().mapToInt(CountVacancySeat::getClosedSeats).sum();
            if (countVacancySeats.size() > 0) {
                totalOpenSeats = countVacancySeats.get(countVacancySeats.size() - 1).getOpenSeats();
            }
        }
        List<EmployeeDashboardDTO> result = new ArrayList<>();

        int finalTotalClosedSeats = totalClosedSeats;
        int finalTotalOpenSeats = totalOpenSeats;
        int maxClose = viewStatisticVacancySeatService.countMaxClosedSeatsInCompany(getCompanyId(auth), request.getStartDate(), request.getEndDate());
        staffStatistics.get().forEach(staffStatistic -> result.add(EmployeeDashboardDTO.builder()
                .staff(new BestEmployeeDTO(staffStatistic, staffStatistic.getClosedCandidates() == maxClose && maxClose > 0 ? 1 : 0, getLocale(auth)))
                .closedSeats(staffStatistic.getClosedCandidates())
                .openSeats(staffStatistic.getOpenSeats())
                .processedCandidates(staffStatistic.getCandidateProcessing())
                .appointments(staffStatistic.getAppointments())
                .activeMinutes(staffStatistic.getActiveTimes())
                .totalClosedSeats(finalTotalClosedSeats)
                .totalOpenSeats(finalTotalOpenSeats)
                .totalProcessedCandidates(totalProcessedCandidates)
                .totalAppointments(totalAppointments)
                .totalActiveMinutes(totalActiveMinutes)
                .build()));
        PagedResultV2 pagedResultV2 = new PagedResultV2<>(result, page, staffStatistics);
        return new BaseResp<>(pagedResultV2);
    }

    @Override
    public BaseResp getEmployeeDashboardDetail(Long staffId, DurationRequest request, Authentication authentication) {
        validateDateRange(request.getStartDate(), request.getEndDate());
        boolean hasPermission = hasPermissionAccessStatisticDashboard(authentication);
        if (!hasPermission) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        ofNullable(staffService.findByIdNotCheckDeleted(staffId)).orElseThrow(() -> new EntityNotFoundException(ResponseStatus.NOT_FOUND_STAFF));
        String timeZone = getTimeZone(authentication);

        List<CountVacancySeat> countVacancySeats = vacancySeatService.countVacancySeatInRangeByStaff(staffId, request.getStartDate(), request.getEndDate());
//        Map<String, VacancySeatDTO> resultVacancySeats = LongStream.iterate(request.getStartDate(), d -> d <= request.getEndDate(), d -> d + ONE_DAY_IN_MILLISECOND).collect(toMap(
//                d -> DateUtils.formatShortLocalTimeByTimeZone(new Date(d), timeZone),
//                d -> new VacancySeatDTO()
//        ));

        Map<String, VacancySeatDTO> resultVacancySeats = new HashMap<>();
        if (countVacancySeats.isEmpty()) {
            for (long d = request.getStartDate(); d <= request.getEndDate(); d += DateUtils.ONE_DAY_IN_MILLISECOND) {
                resultVacancySeats.put(DateUtils.formatShortLocalTimeByTimeZone(new Date(d), timeZone), VacancySeatDTO.builder().build());
            }
        } else {
            resultVacancySeats = getCountVacancySeatsDTO(countVacancySeats, timeZone);
        }

        List<CountCandidateProcessing> countCandidateProcessing = vacancyProcessingService.countByStaffInDurationInEachDay(staffId, request.getStartDate(), request.getEndDate());
        Map<String, ProcessedCandidateDTO> resultCandidateProcessing = new HashMap<>();

        if (countCandidateProcessing.isEmpty()) {
            for (long d = request.getStartDate(); d <= request.getEndDate(); d += ONE_DAY_IN_MILLISECOND) {
                resultCandidateProcessing.put(DateUtils.formatShortLocalTimeByTimeZone(new Date(d), timeZone), ProcessedCandidateDTO.builder().build());
            }
        } else {
            resultCandidateProcessing = getCountCandidateProcessingDTO(countCandidateProcessing, timeZone);
        }

        List<CountByDate> countAppointments = appointmentFeedbackService.countByStaffInDurationInEachDay(staffId, request.getStartDate(), request.getEndDate());
        Map<String, AppointmentStatDTO> resultAppointments = new HashMap<>();
        if (countAppointments.isEmpty()) {
            for (long d = request.getStartDate(); d <= request.getEndDate(); d += ONE_DAY_IN_MILLISECOND) {
                resultAppointments.put(DateUtils.formatShortLocalTimeByTimeZone(new Date(d), timeZone), AppointmentStatDTO.builder().build());
            }
        } else {
            resultAppointments = getCountAppointmentStatDTO(countAppointments, timeZone);
        }

        List<CountByDate> sumActiveMins = staffWorkingService.sumByStaffInDurationInEachDay(staffId, request.getStartDate(), request.getEndDate());
        Map<String, Integer> resultActiveMinutes = new HashMap<>();
        if (sumActiveMins.isEmpty()) {
            for (long d = request.getStartDate(); d <= request.getEndDate(); d += ONE_DAY_IN_MILLISECOND) {
                resultActiveMinutes.put(DateUtils.formatShortLocalTimeByTimeZone(new Date(d), timeZone), 0);
            }
        } else {
            resultActiveMinutes = getSumAppointmentStatDTO(sumActiveMins, timeZone);
        }

        EmployeeDashboardDetailDTO result = EmployeeDashboardDetailDTO.builder().seats(resultVacancySeats)
                .processedCandidates(resultCandidateProcessing)
                .appointmentStats(resultAppointments)
                .activeMinutes(resultActiveMinutes)
                .build();
        return new BaseResp<>(result);
    }

    private Map<String, Integer> getSumAppointmentStatDTO(List<CountByDate> sumActiveMins, String timeZone) {
        Date now = new Date();
        return sumActiveMins.stream().collect(toMap(
                countItem -> DateUtils.formatShortLocalTimeByTimeZone(DateUtils.getUtcForOracle(countItem.getEventDate()), timeZone),
                countItem -> now.after(countItem.getEventDate()) ? countItem.getTotal() : 0
        ));
    }

    private Map<String, AppointmentStatDTO> getCountAppointmentStatDTO(List<CountByDate> countAppointments, String timeZone) {
        Date now = new Date();
        return countAppointments.stream().collect(toMap(
                countItem -> DateUtils.formatShortLocalTimeByTimeZone(DateUtils.getUtcForOracle(countItem.getEventDate()), timeZone),
                countItem -> now.after(countItem.getEventDate()) ? AppointmentStatDTO.builder()
                        .personals(countItem.getTotal())
                        .videos(0).build()
                        : new AppointmentStatDTO()));
    }

    private Map<String, ProcessedCandidateDTO> getCountCandidateProcessingDTO(List<CountCandidateProcessing> countCandidateProcessing, String timeZone) {
        Date now = new Date();
        return countCandidateProcessing.stream().collect(toMap(
                countItem -> DateUtils.formatShortLocalTimeByTimeZone(DateUtils.getUtcForOracle(countItem.getCountOnDate()), timeZone),
                countItem -> now.after(countItem.getCountOnDate()) ? ProcessedCandidateDTO.builder()
                        .applied(countItem.getAppliedCandidates())
                        .rejected(countItem.getRejectedCandidates()).build()
                        : new ProcessedCandidateDTO()));
    }

    private Map<String, VacancySeatDTO> getCountVacancySeatsDTO(List<CountVacancySeat> countVacancySeats, String timeZone) {
        Date now = new Date();
        return countVacancySeats.stream().collect(toMap(
                countItem -> DateUtils.formatShortLocalTimeByTimeZone(DateUtils.getUtcForOracle(countItem.getCountOnDate()), timeZone),
                countItem -> now.after(countItem.getCountOnDate()) ? VacancySeatDTO.builder()
                        .openSeat(countItem.getOpenSeats())
                        .closedSeat(countItem.getClosedSeats()).build()
                        : new VacancySeatDTO()));
    }

    private Map<String, VacancySeatDTO> genderCompanyVacancySeat(long startDate, long endDate, Authentication auth) {
        List<CountVacancySeat> countVacancySeats = vacancySeatService.countVacancySeatInRangeByCompany(getCompanyId(auth), startDate, endDate);
        Map<String, VacancySeatDTO> result = new HashMap<>();
        String timeZone = getTimeZone(auth);

        if (countVacancySeats.isEmpty()) {
            for (long d = startDate; d <= endDate; d += ONE_DAY_IN_MILLISECOND) {
                result.put(DateUtils.formatShortLocalTimeByTimeZone(new Date(d), timeZone), VacancySeatDTO.builder().build());
            }
        } else {
            result = getCountVacancySeatsDTO(countVacancySeats, timeZone);
        }

        return result;
    }
}
