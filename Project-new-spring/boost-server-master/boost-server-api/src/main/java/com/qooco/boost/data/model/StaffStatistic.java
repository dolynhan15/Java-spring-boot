package com.qooco.boost.data.model;

import com.qooco.boost.data.oracle.entities.Country;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class StaffStatistic {
    private long id;
    private String username;
    private String lastName;
    private String firstName;
    private String avatar;
    private String phone;
    private String nationalId;
    private Country country;

    private int closedCandidates;
    private int openSeats;
    private int candidateProcessing;
    private int appointments;
    private int activeTimes;

    public StaffStatistic(long id, String username, String firstName, String lastName, String avatar, String phone,
                          String nationalId, Long countryId, String countryName, String countryCode, String phoneCode,
                          int closedCandidates, int openSeats, int candidateProcessing, int appointments, int activeTimes) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.phone = phone;
        this.nationalId = nationalId;
        Optional.ofNullable(countryId).ifPresent(country -> this.country = new Country(country, countryName, countryCode, phoneCode));

        this.closedCandidates = closedCandidates;
        this.openSeats = openSeats;
        this.candidateProcessing = candidateProcessing;
        this.appointments = appointments;
        this.activeTimes = activeTimes;
    }
}
