package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "USER_BENEFIT")
public class UserBenefit extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_BENEFIT_SEQUENCE")
    @SequenceGenerator(sequenceName = "USER_BENEFIT_SEQ", allocationSize = 1, name = "USER_BENEFIT_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Setter @Getter
    @Column(name = "USER_BENEFIT_ID")
    private Long userBenefitId;

    @Setter @Getter
    @JoinColumn(name = "CURRICULUM_VITAE_ID", referencedColumnName = "CURRICULUM_VITAE_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserCurriculumVitae userCurriculumVitae;

    @Setter @Getter
    @JoinColumn(name = "BENEFIT_ID", referencedColumnName = "BENEFIT_ID")
    @ManyToOne(optional = false)
    private Benefit benefit;

    public UserBenefit() {
        super();
    }

    public UserBenefit(UserBenefit userBenefit) {
        super(userBenefit.getCreatedBy());
        this.userBenefitId = userBenefit.getUserBenefitId();
        this.userCurriculumVitae = userBenefit.getUserCurriculumVitae();
        this.benefit = userBenefit.getBenefit();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserBenefit that = (UserBenefit) o;
        return Objects.equals(userBenefitId, that.userBenefitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userBenefitId);
    }

    @Override
    public String toString() {
        return benefit.getName();
    }
}
