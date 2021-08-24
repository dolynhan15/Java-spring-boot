package com.qooco.boost.threads.actors;


import akka.actor.UntypedAbstractActor;
import com.qooco.boost.constants.Const;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.models.request.demo.SelectDataReq;
import com.qooco.boost.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.qooco.boost.constants.Const.Vacancy.CandidateStatus.RECRUITED;
import static java.util.stream.Collectors.toList;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class CreateSelectDataDemoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(CreateSelectDataDemoActor.class);
    public static final String ACTOR_NAME = "createSelectDataDemoActor";

    private final StaffService staffService;
    private final VacancyService vacancyService;
    private final VacancySeatService vacancySeatService;
    private final VacancyProcessingService vacancyProcessingService;
    private final StaffWorkingService staffWorkingService;
    private final AppointmentFeedbackService appointmentFeedbackService;
    private final AppointmentDetailService appointmentDetailService;

    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 1;
    private Random rand = new Random();

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof SelectDataReq) {
            SelectDataReq req = (SelectDataReq) message;
            List<Vacancy> vacancies = vacancyService.getByCompanyId(req.getCompanyId());
            if(CollectionUtils.isEmpty(vacancies)) return;

            Optional.of(getStaff(req.getCompanyId())).ifPresent(staffs -> staffs.forEach(staff -> {
                long vacancyId = vacancies.get(rand.nextInt(vacancies.size())).getId();
                logger.info("Start create data for recruiter: " + staff.getStaffId());
                var openSeats = genOpeningSeat(vacancyId, staff.getStaffId(), req.getOpenSeatRanges()[MIN_INDEX], req.getOpenSeatRanges()[MAX_INDEX], req.getStartDate(), req.getEndDate());
                logger.info("Finish open seat");
                var closedSeats = genClosedSeat(openSeats, staff.getStaffId(), req.getClosedSeatRanges()[MIN_INDEX], req.getClosedSeatRanges()[MAX_INDEX]);
                logger.info("Finish closed seat");

                Vacancy vacancy = vacancyService.findValidById(vacancyId);
                AppointmentDetail appointmentDetail = appointmentDetailService.findOneByVacancy(vacancy.getId());
                Optional.ofNullable(appointmentDetail).ifPresent(it -> {
                    var appointmentNumberMap = new HashMap<Long, Integer>();
                    closedSeats.forEach((day, seats) -> appointmentNumberMap.put(day, seats.size()));
                    genAppointmentFeedback(it, staff, appointmentNumberMap);
                });
                logger.info("Finish appointment");
                genCandidateProcessing(vacancy, staff, req.getCandidateProcessRanges()[MIN_INDEX], req.getCandidateProcessRanges()[MAX_INDEX], req.getStartDate(), req.getEndDate());
                logger.info("Finish processing candidate");
                genApplicationActivity(staff, req.getStartDate(), req.getEndDate());
                logger.info("Finish ap activity");
            }));
        } else if(message instanceof Long){
            Long companyId = (Long) message;
            List<Vacancy> vacancies = vacancyService.getByCompanyId(companyId);
            Optional.of(vacancies).filter(CollectionUtils::isNotEmpty).ifPresent(it -> {
                vacancySeatService.deleteAllByVacancyId(it.stream().map(Vacancy::getId).collect(toImmutableList()));
                vacancyProcessingService.deleteAllByVacancyId(it.stream().map(Vacancy::getId).collect(toImmutableList()));
            });

            Optional.of(getStaff(companyId)).filter(CollectionUtils::isNotEmpty).ifPresent(it -> {
                appointmentFeedbackService.deleteAllByStaffId(it.stream().map(Staff::getStaffId).collect(toImmutableList()));
                staffWorkingService.deleteAllByStaffId(it.stream().map(Staff::getStaffId).collect(toImmutableList()));
            });
        }

    }

    private List<Staff> getStaff(long companyId){
        Page<Staff> staffs = staffService.findCompanyStaffsByRoles(companyId, CompanyRole.HEAD_RECRUITER.getRolesEqualOrLessNoAnalyst(), 0, Integer.MAX_VALUE );
        return staffs.getContent();
    }

    private void genAppointmentFeedback(AppointmentDetail appointmentDetail, Staff staff, Map<Long, Integer> appointmentNumberMap) {
        var appointmentFeedBackMap = new HashMap<Long, List<AppointmentFeedback>>();
        appointmentNumberMap.forEach((day, appointmentNumber) -> {
            var appointmentFeedBacks = new ArrayList<AppointmentFeedback>();
            IntStream.range(0, appointmentNumber).boxed().forEach(count -> {
                var appointmentFB = new AppointmentFeedback().toBuilder()
                        .appointmentDetail(appointmentDetail)
                        .staff(staff)
                        .feedbackDate(new Date(day))
                        .status(RECRUITED)
                        .build();

                appointmentFB.setCreatedBy(staff.getUserFit().getUserProfileId());
                appointmentFB.setUpdatedBy(staff.getUserFit().getUserProfileId());
                appointmentFeedBacks.add(appointmentFB);
            });
            appointmentFeedBackMap.put(day, appointmentFeedBacks);
        });

        appointmentFeedbackService.save(new ArrayList<>(appointmentFeedBackMap.values().stream().flatMap(Collection::stream).collect(toList())));
    }

    private void genCandidateProcessing(Vacancy vacancy, Staff staff, int minSeat, int maxSeat, long startDate, long endDate) {
        int[] processType = {1, 2, 3, 4, 5};

        var vacancyProcessingMap = new HashMap<Long, List<VacancyProcessing>>();
        long day = startDate;
        while (day < endDate) {
            var vacancyProcessings = new ArrayList<VacancyProcessing>();
            long finalDay = day;
            IntStream.range(0, rand.ints(minSeat, maxSeat).findFirst().getAsInt()).boxed().forEach(count ->
                    vacancyProcessings.add(
                            VacancyProcessing.builder()
                                    .vacancy(vacancy)
                                    .staff(staff)
                                    .createdDate(new Date(finalDay))
                                    .type(processType[rand.nextInt(processType.length)])
                                    .build()

                    ));
            vacancyProcessingMap.put(day, vacancyProcessings);
            day = DateUtils.addDays(new Date(day), 1).getTime();
        }

        vacancyProcessingService.save(vacancyProcessingMap.values().stream().flatMap(Collection::stream).collect(toList()));
    }

    private void genApplicationActivity(Staff staff, long startDate, long endDate) {
        int minWorkingDuration = 60 * 60 * 1000; //millisecond
        int maxWorkingDuration = 4 * minWorkingDuration;
        var staffWorkings = new ArrayList<StaffWorking>();
        long day = startDate;
        while (day < endDate) {
            int duration = rand.ints(minWorkingDuration, maxWorkingDuration).findFirst().getAsInt();
            staffWorkings.add(new StaffWorking(staff, day, new Date(day + duration).getTime()));
            day = DateUtils.addDays(new Date(day), 1).getTime();
        }
        staffWorkingService.save(staffWorkings);
    }

    private Map<Long, List<VacancySeat>> genClosedSeat(Map<Long, List<VacancySeat>> openSeats, long staffId, int minSeat, int maxSeat) {
        var closedSeatMap = new HashMap<Long, List<VacancySeat>>();
        openSeats.forEach((day, seats) -> {
            var closedSeats = new ArrayList<VacancySeat>();
            IntStream.range(0, rand.ints(minSeat, maxSeat).findFirst().getAsInt()).boxed().forEach(count ->
                    closedSeats.add(
                            seats.get(count)
                                    .setClosedStaffId(staffId)
                                    .setClosedDate(new Date(day))
                                    .setStatus(Const.VacancySeatStatus.CLOSED))
            );

            closedSeatMap.put(day, closedSeats);
        });

        vacancySeatService.save(new ArrayList<>(closedSeatMap.values().stream().flatMap(Collection::stream).collect(toList())));
        return closedSeatMap;
    }

    private Map<Long, List<VacancySeat>> genOpeningSeat(long vacancyId, long staffId, int minSeat, int maxSeat, long startDate, long endDate) {
        var openSeatsMap = new HashMap<Long, List<VacancySeat>>();
        long day = startDate;

        while (day < endDate) {
            var openSeats = new ArrayList<VacancySeat>();
            long finalDay = day;
            IntStream.range(0, rand.ints(minSeat, maxSeat).findFirst().getAsInt()).boxed().forEach(count ->
                    openSeats.add(
                            VacancySeat.builder()
                                    .vacancyId(vacancyId)
                                    .responsibleStaffId(staffId)
                                    .createdDate(new Date(finalDay))
                                    .status(Const.VacancySeatStatus.OPENING)
                                    .build()));

            openSeatsMap.put(day, openSeats);
            day = DateUtils.addDays(new Date(day), 1).getTime();
        }

        vacancySeatService.save(new ArrayList<>(openSeatsMap.values().stream().flatMap(Collection::stream).collect(toList())));
        return openSeatsMap;
    }
}
