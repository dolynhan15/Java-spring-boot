package com.qooco.boost.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PagedResultV2<T, E> extends PagedResult<T> {
    public PagedResultV2(List<T> results, int page, Page<E> pages) {
        super(results, page, pages.getSize(), pages.getTotalPages(), pages.getTotalElements(), pages.hasNext(), pages.hasPrevious());
    }

    public PagedResultV2(List<T> results) {
        super(results, 0, 0,1, results.size(), false, false);
    }
}
