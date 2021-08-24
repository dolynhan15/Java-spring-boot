package com.qooco.boost.models.dto.attribute;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.UserAttribute;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.Objects;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldNameConstants
public class AttributeShortDTO {
    private Long id;
    private String name;
    private String description;
    private int level;

    public AttributeShortDTO(UserAttribute userAttribute) {
        this.id = userAttribute.getId();
        this.name = userAttribute.getAttribute().getName();
        this.description = userAttribute.getAttribute().getDescription();
        this.level = userAttribute.getLevel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeShortDTO that = (AttributeShortDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
