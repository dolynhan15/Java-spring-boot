package com.qooco.boost.data.oracle.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PROFILE_ATTRIBUTE")
@Data
@SuperBuilder(toBuilder = true)
@Setter(PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ProfileAttribute extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -7469673320656797877L;

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "PROFILE_ATTRIBUTE_SEQUENCE")
    @SequenceGenerator(sequenceName = "PROFILE_ATTRIBUTE_SEQ", allocationSize = 1, name = "PROFILE_ATTRIBUTE_SEQUENCE")
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", columnDefinition = "VARCHAR2", nullable = false)
    @Size(max = 255)
    private String name;

    @Column(name = "DESCRIPTION", columnDefinition = "VARCHAR2", nullable = false)
    @Size(max = 255)
    private String description;
}
