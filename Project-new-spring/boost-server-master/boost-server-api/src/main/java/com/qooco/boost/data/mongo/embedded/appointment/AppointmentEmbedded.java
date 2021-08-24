package com.qooco.boost.data.mongo.embedded.appointment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qooco.boost.data.mongo.embedded.LocationEmbedded;
import com.qooco.boost.data.mongo.embedded.StaffShortEmbedded;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentEmbedded {
    private Long id;
    private LocationEmbedded location;
    private StaffShortEmbedded manager;
    private List<Date> dateRanges;
    private List<Date> timeRanges;
    private Date appointmentDate;
    private Date fromDate;
    private Date toDate;
    private int type;
    private boolean isDeleted;

    public AppointmentEmbedded(Long id) {
        this.id = id;
    }

    public AppointmentEmbedded(AppointmentEmbedded embedded) {
        if (Objects.nonNull(embedded)) {
            this.id = embedded.getId();
            this.location = Objects.nonNull(embedded.getLocation()) ? new LocationEmbedded(embedded.getLocation()) : null;
            this.manager = Objects.nonNull(embedded.getManager()) ? new StaffShortEmbedded(embedded.getManager()) : null;
            this.appointmentDate = embedded.getAppointmentDate();
            this.dateRanges = embedded.getDateRanges();
            this.timeRanges = embedded.getTimeRanges();
            this.type = embedded.type;
            this.isDeleted = embedded.isDeleted();
            this.fromDate = getFromDateOfAppointment();
            this.toDate = getToDateOfAppointment();
        }
    }

    @JsonIgnore
    public List<Date> getAppointmentTimeRange(){
        List<Date> result = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(dateRanges) && CollectionUtils.isNotEmpty(timeRanges)){
            dateRanges.forEach(dr -> timeRanges.forEach(tr -> result.add(DateUtils.setHourByTimeStamp(dr, tr))));
            this.fromDate = getFromDateOfAppointment();
            this.toDate = getToDateOfAppointment();
        }
        return result;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentEmbedded that = (AppointmentEmbedded) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public Date getFromDateOfAppointment() {
        if (CollectionUtils.isNotEmpty(dateRanges)) {
            return dateRanges.stream().max(Date::compareTo).orElse(null);
        }
        return null;
    }

    public Date getToDateOfAppointment() {
        if (CollectionUtils.isNotEmpty(dateRanges)) {
            Date maxDate = dateRanges.stream().max(Date::compareTo).orElse(null);
            if (Objects.nonNull(maxDate) && CollectionUtils.isNotEmpty(timeRanges)) {
                Date maxHourDate = timeRanges.stream().max(Date::compareTo).orElse(null);
                if (Objects.nonNull(maxHourDate)) {
                    maxDate = DateUtils.setHourByTimeStamp(maxDate, maxHourDate);
                    if (maxDate.before(maxHourDate)) {
                        return DateUtils.atEndOfHour(DateUtils.addDays(maxDate, 1));
                    }
                    return DateUtils.atEndOfHour(maxDate);
                }
                return DateUtils.atEndOfHour(maxDate);
            }
        }
        return null;
    }

    public void setFromDateAndToDate() {
        this.fromDate = getFromDateOfAppointment();
        this.toDate = getToDateOfAppointment();
    }
}
