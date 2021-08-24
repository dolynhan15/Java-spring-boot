package com.qooco.boost.models.request;

import lombok.*;

import javax.validation.constraints.Min;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimestampReq {
    @Getter
    @Setter
    private Long timestamp;

    @Min(0)
    @Setter
    private int size;

    public int getSize() {
        return size == 0 ? Integer.MAX_VALUE : size ;
    }
}
