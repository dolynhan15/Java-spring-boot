package com.qooco.boost.data.oracle.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "USER_PREVIOUS_POSITION")
@Setter @Getter
@AllArgsConstructor
public class UserPreviousPosition extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_PREVIOUS_POSITION_SEQUENCE")
    @SequenceGenerator(sequenceName = "USER_PREVIOUS_POSITION_SEQ", allocationSize = 1, name = "USER_PREVIOUS_POSITION_SEQUENCE")
    @Id
    @Basic(optional = false)
    @NotNull

    @Column(name = "USER_PREVIOUS_POSITION_ID")
    private Long id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Basic(optional = false)
    @Column(name = "SALARY")
    private Long salary;

    @Basic(optional = false)
    @Size(max = 1000)
    @Column(name = "CONTACT_PERSON")
    private String contactPerson;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1000)
    @Column(name = "COMPANY_NAME", columnDefinition = "NVARCHAR2")
    private String companyName;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1000)
    @Column(name = "POSITION_NAME", columnDefinition = "NVARCHAR2")
    private String positionName;

    @Lob
    @Column(name = "PHOTO")
    private String photo;

    @JoinColumn(name = "USER_CURRICULUM_VITAE_ID", referencedColumnName = "CURRICULUM_VITAE_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnoreProperties
    private UserCurriculumVitae userCurriculumVitae;

    @JoinColumn(name = "CURRENCY_ID", referencedColumnName = "CURRENCY_ID")
    @ManyToOne()
    private Currency currency;

    public UserPreviousPosition() {
        super();
    }

    public UserPreviousPosition(Long previousPositionId) {
        this();
        this.id = previousPositionId;
    }

    public UserPreviousPosition(UserPreviousPosition previousPosition) {
        this();
        this.id = previousPosition.getId();
        this.startDate = previousPosition.getStartDate();
        this.endDate = previousPosition.getEndDate();
        this.salary = previousPosition.getSalary();
        this.contactPerson = previousPosition.getContactPerson();
        this.companyName = previousPosition.getCompanyName();
        this.positionName = previousPosition.getPositionName();
        this.photo = previousPosition.getPhoto();
        this.userCurriculumVitae = previousPosition.getUserCurriculumVitae();
        this.currency = previousPosition.getCurrency();
    }

    public boolean isCurrent() {
        if (Objects.nonNull(endDate)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPreviousPosition that = (UserPreviousPosition) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + " : " + positionName;
    }

}
