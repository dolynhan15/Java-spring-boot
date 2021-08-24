package com.qooco.boost.data.oracle.entities.views;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "VIEW_STATISTIC_VACANCY_SEAT")
@XmlRootElement
@Getter
@NoArgsConstructor
public class ViewStatisticVacancySeat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "REPORT_DATE", nullable = false)
    private Date reportDate;

    @Column(name = "STAFF_ID", nullable = false)
    private long staffId;

    @Column(name = "COUNT_RESULT", nullable = false)
    private int countResult;

    @Column(name = "TYPE", nullable = false)
    private int type;
}
