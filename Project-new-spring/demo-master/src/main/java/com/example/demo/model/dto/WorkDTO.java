package com.example.demo.model.dto;

import com.example.demo.model.entity.Work;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkDTO {
    private Integer id;
    private String workName;
    private Date startingDate;
    private Date endingDate;
    private int status;

    public WorkDTO(Work work) {
        this.id = work.getId();
        this.workName = work.getWorkName();
        this.startingDate = work.getStartingDate();
        this.endingDate = work.getEndingDate();
        this.status = work.getStatus();
    }
}
