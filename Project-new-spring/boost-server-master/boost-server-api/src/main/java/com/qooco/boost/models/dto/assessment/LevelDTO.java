package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.LevelEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/15/2018 - 5:19 PM
 */
@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LevelDTO {
    private Integer value;
    private String name;
    private String descr;

    public LevelDTO(LevelEmbedded levelEmbedded) {
        if (Objects.nonNull(levelEmbedded)) {
            if (StringUtils.isNotBlank(levelEmbedded.getValue())) {
                this.value = NumberUtils.toInt(levelEmbedded.getValue());
            }
            this.name = levelEmbedded.getName();
            this.descr = levelEmbedded.getDescr();
        }
    }
}
