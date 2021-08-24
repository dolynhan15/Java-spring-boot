package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.models.dto.attribute.AttributeShortDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
public class UserCVAttributeDTO extends UserCurriculumVitaeDTO {
    private List<AttributeShortDTO> topAttributes;

    @ApiModelProperty(notes = "The number attribute which user reach after finishing the action")
    private long attribute;
    @ApiModelProperty(notes = "The number attribute of system")
    private long totalAttribute;

    public UserCVAttributeDTO(UserCurriculumVitae userCurriculumVitae, String locale) {
        super(userCurriculumVitae, locale);
    }
}
