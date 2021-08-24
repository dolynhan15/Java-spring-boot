package com.qooco.boost.models.request;

import lombok.*;

import javax.validation.constraints.Min;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LastRequest {
    @Min(0)
    @Getter
    @Setter
    private long lastTime;

    @Min(0)
    @Setter
    private int limit;

    public int getSize() {
        return limit == 0 ? Integer.MAX_VALUE : limit ;
    }
}
