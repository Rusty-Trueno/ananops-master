package com.ananops.provider.service.impl;

import com.ananops.provider.service.ApproveService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzq
 * on 2019/12/05
 */

@Service
public class ApproveServiceImpl implements ApproveService {

    @Override
    public void complete(String taskid, String Uid) {
        String variableName = "name";
        Map<String, Object> map = new HashMap<>();
        map.put(variableName, Uid);
        processEngine.getTaskService().complete(taskid, map);
    }

    @Override
    public void aprrove(String taskId, String Uid) {
        String variableName = "agreement";
        Map<String, Object> map = new HashMap<>();
        map.put(variableName, "同意");
        map.put("name",Uid);
        processEngine.getTaskService()
                .complete(taskId, map);
    }

    @Override
    public void disapprove(String taskId) {
        String variableName = "agreement";
        Map<String, Object> map = new HashMap<>();
        map.put(variableName, "不同意");
        processEngine.getTaskService()
                .complete(taskId, map);
    }


}
