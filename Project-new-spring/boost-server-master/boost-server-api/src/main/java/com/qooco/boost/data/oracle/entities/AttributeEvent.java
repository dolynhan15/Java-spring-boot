package com.qooco.boost.data.oracle.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ATTRIBUTE_EVENT")
@Data
@SuperBuilder(toBuilder = true)
@Setter(PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class AttributeEvent extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 2908499116223707075L;

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "ATTRIBUTE_EVENT_SEQUENCE")
    @SequenceGenerator(sequenceName = "ATTRIBUTE_EVENT_SEQ", allocationSize = 1, name = "ATTRIBUTE_EVENT_SEQUENCE")
    @Column(name = "ID", nullable = false)
    private Long id;

    @JoinColumn(name = "PROFILE_ATTRIBUTE_ID", nullable = false)
    @ManyToOne(optional = false)
    private ProfileAttribute attribute;

    @Column(name = "EVENT_CODE", nullable = false)
    private int eventCode;

    @Column(name = "SCORE", nullable = false)
    private int score;

    @Column(name = "IS_REPEATABLE", nullable = false)
    private boolean repeatable;
}
