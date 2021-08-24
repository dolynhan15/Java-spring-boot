package com.qooco.boost.data.oracle.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Data
@Table(name = "USER_PREFERRED_HOTEL")
@NoArgsConstructor
public class UserPreferredHotel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_PREFERRED_HOTEL_SEQUENCE")
    @SequenceGenerator(sequenceName = "USER_PREFERRED_HOTEL_SEQ", allocationSize = 1, name = "USER_PREFERRED_HOTEL_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Long id;

    @JoinColumn(name = "CURRICULUM_VITAE_ID", referencedColumnName = "CURRICULUM_VITAE_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserCurriculumVitae userCurriculumVitae;

    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID")
    @ManyToOne(optional = false)
    private Company hotel;

    public UserPreferredHotel(UserPreferredHotel userPreferredHotel) {
        super(userPreferredHotel.getCreatedBy());
        this.id = userPreferredHotel.getId();
        this.userCurriculumVitae = userPreferredHotel.getUserCurriculumVitae();
        this.hotel = userPreferredHotel.getHotel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPreferredHotel that = (UserPreferredHotel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
