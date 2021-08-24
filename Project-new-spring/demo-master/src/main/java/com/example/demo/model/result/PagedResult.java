package com.example.demo.model.result;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class PagedResult<T> {
    private int page;
    private int size;
    private int totalPage;
    private long total;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private List<T> results;

    public PagedResult(List<T> results, int page, int size, int totalPage, long total, boolean hasNext, boolean hasPrevious) {
        super();
        this.total = total;
        this.totalPage = totalPage;
        this.results = results;
        this.page = page;
        this.size = size;
        hasNextPage = hasNext;
        hasPreviousPage = hasPrevious;
    }
}
