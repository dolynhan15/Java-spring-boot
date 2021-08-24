package com.qooco.boost.data.oracle.entities;

import com.qooco.boost.constants.Const;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "VACANCY_SEAT")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Accessors(chain = true)
public class VacancySeat implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VACANCY_SEAT_SEQUENCE")
    @SequenceGenerator(sequenceName = "VACANCY_SEAT_SEQ", allocationSize = 1, name = "VACANCY_SEAT_SEQUENCE")
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "VACANCY_ID", nullable = false)
    private Long vacancyId;

    @Column(name = "STATUS", nullable = false)
    @Setter
    private int status;

    @Column(name = "CREATED_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "CLOSED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @Setter
    private Date closedDate;

    @Column(name = "SUSPEND_FROM_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @Setter
    private Date suspendFromDate;

    @Column(name = "END_SUSPEND_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @Setter
    private Date endSuspendDate;

    @Column(name = "SUSPENDED_DAYS")
    @Setter
    private Integer suspendedDays;

    @Column(name = "RESPONSIBLE_STAFF_ID", nullable = false)
    @Setter
    private Long responsibleStaffId;

    @Column(name = "CLOSED_STAFF_ID")
    @Setter
    private Long closedStaffId;

    @Column(name = "USER_CV_ID")
    @Setter
    private Long userCvId;

    @JoinColumn(name = "VACANCY_ID", referencedColumnName = "VACANCY_ID", updatable = false, insertable = false)
    @ManyToOne(optional = false)
    private Vacancy vacancy;

    public VacancySeat(long vacancyId, Date createdDate, long responsibleStaffId) {
        this.vacancyId = vacancyId;
        this.createdDate = createdDate;
        this.responsibleStaffId = responsibleStaffId;
        this.status = Const.VacancySeatStatus.OPENING;
        this.vacancy = new Vacancy(vacancyId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacancySeat vacancySeat = (VacancySeat) o;
        return Objects.equals(id, vacancySeat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
