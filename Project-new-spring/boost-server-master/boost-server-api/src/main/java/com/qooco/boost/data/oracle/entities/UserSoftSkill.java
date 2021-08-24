package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "USER_SOFT_SKILL")
public class UserSoftSkill extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SOFT_SKILL_SEQUENCE")
    @SequenceGenerator(sequenceName = "USER_SOFT_SKILL_SEQ", allocationSize = 1, name = "USER_SOFT_SKILL_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Setter @Getter
    @Column(name = "USER_SOFT_SKILL_ID")
    private Long userSoftSkillId;

    @Setter @Getter
    @JoinColumn(name = "USER_PROFILE_ID", referencedColumnName = "CURRICULUM_VITAE_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserCurriculumVitae userCurriculumVitae;

    @Setter @Getter
    @JoinColumn(name = "SOFT_SKILL_ID", referencedColumnName = "SOFT_SKILL_ID")
    @ManyToOne(optional = false)
    private SoftSkill softSkill;

    public UserSoftSkill() {
        super();
    }

    public UserSoftSkill(UserSoftSkill userSoftSkill) {
        super(userSoftSkill.getCreatedBy());
        this.userSoftSkillId = userSoftSkill.getUserSoftSkillId();
        this.userCurriculumVitae = userSoftSkill.getUserCurriculumVitae();
        this.softSkill = userSoftSkill.getSoftSkill();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSoftSkill that = (UserSoftSkill) o;
        return Objects.equals(userSoftSkillId, that.userSoftSkillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userSoftSkillId);
    }

    @Override
    public String toString() {
        return String.valueOf(userSoftSkillId);
    }

}
