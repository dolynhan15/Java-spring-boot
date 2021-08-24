package com.example.demo.model.request;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkRequest {
    private String workName;
    private Date startingDate;
    private Date endingDate;
    private int status;
}
