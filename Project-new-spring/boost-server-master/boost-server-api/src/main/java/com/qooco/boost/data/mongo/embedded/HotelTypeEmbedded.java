package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.oracle.entities.HotelType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 8/7/2018 - 1:39 PM
*/
@Getter
@Setter
@NoArgsConstructor
public class HotelTypeEmbedded {

    private Long id;
    private String name;

    public HotelTypeEmbedded(HotelType hotelType) {
        if (Objects.nonNull(hotelType)) {
            this.id = hotelType.getHotelTypeId();
            this.name = hotelType.getHotelTypeName();
        }
    }
}
