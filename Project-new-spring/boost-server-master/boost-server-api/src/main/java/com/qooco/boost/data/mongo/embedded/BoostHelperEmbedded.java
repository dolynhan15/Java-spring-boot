package com.qooco.boost.data.mongo.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BoostHelperEmbedded {
    private String avatar;
    private String name;
    private String description;
}
