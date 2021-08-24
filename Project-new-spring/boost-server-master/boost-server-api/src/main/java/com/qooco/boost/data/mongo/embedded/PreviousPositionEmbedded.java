package com.qooco.boost.data.mongo.embedded;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 8/6/2018 - 4:43 PM
*/
@Getter
@Setter
public class PreviousPositionEmbedded {
    private Long id;
    private Date startDate;
    private Date endDate;
    private Long salary;
    private String contactPerson;
    private String companyName;
    private String positionName;
    private List<String> photos;
    private CurrencyEmbedded currency;
}
