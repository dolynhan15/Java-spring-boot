package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.model.count.CountByDate;
import com.qooco.boost.data.oracle.entities.AppointmentFeedback;
import com.qooco.boost.data.oracle.reposistories.AppointmentFeedbackRepository;
import com.qooco.boost.data.oracle.services.AppointmentFeedbackService;
import com.qooco.boost.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentFeedbackServiceImpl implements AppointmentFeedbackService {

    @Autowired
    private AppointmentFeedbackRepository repository;
    @Override
    public List<AppointmentFeedback> save(Iterable<AppointmentFeedback> appointmentFeedbacks) {
        return repository.saveAll(appointmentFeedbacks);
    }

    @Override
    public Optional<AppointmentFeedback> findLastFeedbackByVacancyAndCandidate(long vacancyId, long userCvId) {
        Sort sort = Sort.by(Sort.Order.desc("feedbackDate"));
        PageRequest pageRequest = PageRequest.of(0, 1 , sort);
        Page<AppointmentFeedback> result = repository.findAppointmentFeedbackByVacancyAndCandidate(vacancyId, userCvId, pageRequest);
        return result.get().findFirst();
    }

    @Override
    public int countByCompanyInDuration(long companyId, long startDate, long endDate) {
        return repository.countByCompanyInDuration(companyId, DateUtils.toUtcForOracle(new Date(startDate)), DateUtils.toUtcForOracle(new Date(endDate)));
    }

    @Override
    public List<CountByDate> countByStaffInDurationInEachDay(long staffId, long startDate, long endDate) {
        int days = DateUtils.countDays(startDate, endDate);
        List<Object[]> result = repository.countByStaffInDurationInEachDay(
                staffId, DateUtils.toUtcForOracle(new Date(startDate)), DateUtils.toUtcForOracle(new Date(endDate)), days);
        return result.stream().map(
                item -> new CountByDate((Date) item[0], ((BigDecimal) item[1]).intValue())).collect(Collectors.toList());
    }

    @Override
    public void deleteAllByStaffId(List<Long> staffId) {
        repository.deleteAllByStaffId(staffId);
    }
}
