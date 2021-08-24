package com.qooco.boost.models.dto.attribute;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.UserAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import static com.qooco.boost.data.constants.AttributeConstant.MIN_LEVEL_SCORE;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldNameConstants
public class AttributeDTO extends AttributeShortDTO {
    private int score;
    private int scoreRequired;


    public AttributeDTO(UserAttribute userAttribute) {
        super(userAttribute);
        this.score = userAttribute.getScore() > MIN_LEVEL_SCORE[MIN_LEVEL_SCORE.length - 1] ? MIN_LEVEL_SCORE[MIN_LEVEL_SCORE.length - 1] : userAttribute.getScore();
        this.scoreRequired = MIN_LEVEL_SCORE[this.getLevel() >= MIN_LEVEL_SCORE.length - 1 ? MIN_LEVEL_SCORE.length - 1 : this.getLevel() + 1];
    }
}
