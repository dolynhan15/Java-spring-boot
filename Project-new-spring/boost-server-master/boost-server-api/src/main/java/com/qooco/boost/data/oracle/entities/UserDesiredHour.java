package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "USER_DESIRED_HOUR")
@NamedQueries({
    @NamedQuery(name = "UserDesiredHour.findAll", query = "SELECT u FROM UserDesiredHour u")
    })
public class UserDesiredHour extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_DESIRED_HOUR_SEQUENCE")
    @SequenceGenerator(sequenceName = "USER_DESIRED_HOUR_SEQ", allocationSize = 1, name = "USER_DESIRED_HOUR_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Getter @Setter
    @Column(name = "USER_DESIRED_HOUR_ID")
    private Long userDesiredHourId;

    @Getter @Setter
    @JoinColumn(name = "CURRICULUM_VITAE_ID", referencedColumnName = "CURRICULUM_VITAE_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserCurriculumVitae userCurriculumVitae;

    @Getter @Setter
    @JoinColumn(name = "WORKING_HOUR_ID", referencedColumnName = "WORKING_HOUR_ID")
    @ManyToOne(optional = false)
    private WorkingHour workingHour;

    public UserDesiredHour() {
        super();
    }

    public UserDesiredHour(UserDesiredHour userDesiredHour) {
        super(userDesiredHour.getCreatedBy());
        this.userDesiredHourId = userDesiredHour.getUserDesiredHourId();
        this.userCurriculumVitae = userDesiredHour.getUserCurriculumVitae();
        this.workingHour = userDesiredHour.getWorkingHour();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDesiredHour that = (UserDesiredHour) o;
        return Objects.equals(userDesiredHourId, that.userDesiredHourId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userDesiredHourId);
    }
}
