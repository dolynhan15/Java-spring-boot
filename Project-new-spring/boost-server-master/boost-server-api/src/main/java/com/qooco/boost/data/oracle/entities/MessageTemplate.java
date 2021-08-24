package com.qooco.boost.data.oracle.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter @Setter
@Table(name = "MESSAGE_TEMPLATE")
public class MessageTemplate implements Serializable {

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MESSAGE_TEMPLATE_SEQUENCE")
    @SequenceGenerator(sequenceName = "MESSAGE_TEMPLATE_SEQ", allocationSize = 1, name = "MESSAGE_TEMPLATE_SEQUENCE")
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "LANGUAGE_CODE", columnDefinition = "VARCHAR2", nullable = false)
    @Size(max = 255)
    private String languageCode;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "IS_DELETED", nullable = false)
    private boolean isDeleted;

    @Basic(optional = false)
    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Basic(optional = false)
    @Column(name = "UPDATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageTemplate pataFile = (MessageTemplate) o;
        return Objects.equals(id, pataFile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
