package com.example.demo.service;

import com.example.demo.model.BaseResp;
import com.example.demo.model.request.WorkRequest;

public interface WorkService {
    BaseResp getWorks(int page, int size);
    BaseResp addWork(WorkRequest workRequest);
    BaseResp editWork(Integer id, WorkRequest workRequest);
    BaseResp deleteWork(Integer id);
}
