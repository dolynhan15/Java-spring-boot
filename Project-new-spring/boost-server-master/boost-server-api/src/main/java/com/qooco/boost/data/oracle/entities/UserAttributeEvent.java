package com.qooco.boost.data.oracle.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "USER_ATTRIBUTE_EVENT")
@Data
@SuperBuilder(toBuilder = true)
@Setter(PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserAttributeEvent extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -7403165562598380218L;

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "USER_ATTRIBUTE_EVENT_SEQUENCE")
    @SequenceGenerator(sequenceName = "USER_ATTRIBUTE_EVENT_SEQ", allocationSize = 1, name = "USER_ATTRIBUTE_EVENT_SEQUENCE")
    @Column(name = "ID", nullable = false)
    private Long id;

    @JoinColumn(name = "USER_PROFILE_ID", nullable = false)
    @ManyToOne(optional = false)
    private UserProfile userProfile;

    @JoinColumn(name = "PROFILE_ATTRIBUTE_ID")
    @ManyToOne
    private ProfileAttribute attribute;

    @Column(name = "EVENT_CODE", nullable = false)
    private int eventCode;

    @Setter
    @Accessors(chain = true)
    @Column(name = "COUNT", nullable = false)
    private int count;
}
