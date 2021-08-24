package com.qooco.boost.models;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/11/2018 - 5:13 PM
 */

public class BasePagerResp<T> extends BaseResp <T>{
    private int totalPages;
    private int size;
    private long totalRecords;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }
}
