package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.HotelType;
import com.qooco.boost.enumeration.ApplicationContextHolder;
import com.qooco.boost.localization.HotelTypeDatabaseMessageSource;
import com.qooco.boost.localization.ProvinceDatabaseMessageSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HotelTypeDTO {
    private Long id;
    private String name;

    public HotelTypeDTO(HotelType hotelType, String locale) {
        if (Objects.nonNull(hotelType)) {
            this.id = hotelType.getHotelTypeId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(HotelTypeDatabaseMessageSource.class).getMessage(hotelType.getHotelTypeId().toString(), locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = hotelType.getHotelTypeName();
            }
        }
    }
}
