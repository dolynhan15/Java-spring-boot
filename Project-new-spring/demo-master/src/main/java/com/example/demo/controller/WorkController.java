package com.example.demo.controller;

import com.example.demo.constant.URLConstant;
import com.example.demo.model.BaseResp;
import com.example.demo.model.request.PageRequest;
import com.example.demo.model.request.WorkRequest;
import com.example.demo.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(URLConstant.WORK_PATH)
public class WorkController {
    @Autowired
    private WorkService workService;

    @GetMapping()
    public Object getWorks(@Valid PageRequest pageRequest) {
        return workService.getWorks(pageRequest.getPage(), pageRequest.getSize());
    }

    @PostMapping()
    public Object addWork(@RequestBody WorkRequest workRequest) {
        return workService.addWork(workRequest);
    }

    @PutMapping(URLConstant.ID)
    public Object addWork(@PathVariable(value = "id") Integer id,
                          @RequestBody WorkRequest workRequest) {
        return workService.editWork(id, workRequest);
    }

    @DeleteMapping(URLConstant.ID)
    public Object deleteWork(@PathVariable(value = "id") Integer id) {
        return workService.deleteWork(id);
    }
}
