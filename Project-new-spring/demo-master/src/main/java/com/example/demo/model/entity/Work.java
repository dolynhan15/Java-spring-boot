package com.example.demo.model.entity;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@FieldNameConstants
@AllArgsConstructor
@Builder
@Data
public class Work {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Basic(optional = false)
    @Column(name = "WORK_NAME")
    private String workName;

    @Basic(optional = false)
    @Column(name = "STARTING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startingDate;

    @Basic(optional = false)
    @Column(name = "ENDING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endingDate;

    @Basic(optional = false)
    @Column(name = "STATUS")
    private int status;

    @Basic(optional = false)
    @Column(name = "IS_DELETED")
    private boolean isDeleted;

    public Work(int id) {
        this.id = id;
    }
}
