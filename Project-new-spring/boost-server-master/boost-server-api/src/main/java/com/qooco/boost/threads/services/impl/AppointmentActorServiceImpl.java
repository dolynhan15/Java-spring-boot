package com.qooco.boost.threads.services.impl;

import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.threads.services.AppointmentActorService;
import com.qooco.boost.threads.services.StaffActorService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentActorServiceImpl implements AppointmentActorService {

    @Autowired
    private StaffActorService staffActorService;
    @Override
    public Appointment updateLazyValue(Appointment appointment) {
        if(Objects.nonNull(appointment)) {
            Staff manager = staffActorService.updateLazyValue(appointment.getManager());
            appointment.setManager(manager);
        }
        return appointment;
    }

    @Override
    public List<Appointment> updateLazyValue(List<Appointment> appointments) {
        List<Appointment> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(appointments)) {
            List<Staff> staffs = staffActorService.updateLazyValue(appointments.stream().map(Appointment::getManager)
                    .collect(Collectors.toList()));
            appointments.forEach(a -> {
                Optional<Staff> managers = staffs.stream().filter(s -> s.getStaffId().equals(a.getManager().getStaffId()))
                        .findFirst();
                if (managers.isPresent()) {
                    a.setManager(managers.get());
                } else {
                    a.setManager(null);
                }
                result.add(a);
            });
        }
        return result;
    }
}
