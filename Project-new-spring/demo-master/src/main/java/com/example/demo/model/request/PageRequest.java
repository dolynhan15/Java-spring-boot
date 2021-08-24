package com.example.demo.model.request;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Min;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class PageRequest {
    @Min(0)
    @Getter
    @Setter
    private int page;

    @Min(0)
    @Setter
    private int size;

    public int getSize() {
        return size == 0 ? Integer.MAX_VALUE : size ;
    }
}
