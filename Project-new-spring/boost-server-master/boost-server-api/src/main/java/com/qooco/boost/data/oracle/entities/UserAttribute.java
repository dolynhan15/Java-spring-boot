package com.qooco.boost.data.oracle.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

import static java.util.Optional.ofNullable;
import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "USER_ATTRIBUTE")
@Data
@SuperBuilder(toBuilder = true)
@Setter(PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserAttribute extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 4849051014602416622L;

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "USER_ATTRIBUTE_SEQUENCE")
    @SequenceGenerator(sequenceName = "USER_ATTRIBUTE_SEQ", allocationSize = 1, name = "USER_ATTRIBUTE_SEQUENCE")
    @Column(name = "ID", nullable = false)
    private Long id;

    @JoinColumn(name = "USER_PROFILE_ID", nullable = false)
    @ManyToOne(optional = false)
    private UserProfile userProfile;

    @JoinColumn(name = "PROFILE_ATTRIBUTE_ID", nullable = false)
    @ManyToOne(optional = false)
    private ProfileAttribute attribute;

    @Column(name = "\"LEVEL\"", nullable = false)
    private int level;

    @Column(name = "SCORE", nullable = false)
    private int score;

    @Column(name = "IS_NEW_LEVEL", nullable = false)
    private boolean newLevel;

    public UserAttribute (ProfileAttribute attribute, Integer level, Integer score){
        this.attribute = attribute;
        this.level = ofNullable(level).orElse(0);
        this.score = ofNullable(score).orElse(0);

    }
}
