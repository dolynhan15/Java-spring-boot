package com.qooco.boost.models.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.BoostHelperEmbedded;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoostHelperDTO {
    private String avatar;
    private String name;
    private String description;

    public BoostHelperDTO(BoostHelperEmbedded embedded) {
        this.avatar = ServletUriUtils.getAbsoluteResourcePath(embedded.getAvatar());
        this.name = embedded.getName();
        this.description = embedded.getDescription();
    }
}
