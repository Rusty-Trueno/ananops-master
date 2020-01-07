package com.ananops.provider.service;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.provider.model.dto.WorkOrderQueryDto;
import com.ananops.provider.model.dto.WorkOrderStatusQueryDto;
import com.ananops.provider.model.vo.WorkOrderDetailVo;
import com.ananops.provider.model.vo.WorkOrderVo;

import java.util.List;

/**
 * 操作加盟服务商WordOrder的Service接口
 *
 * Created by bingyueduan on 2020/1/3.
 */
public interface SpcWorkOrderService {

    /**
     * 查询服务商下工单列表信息
     *
     * @param workOrderStatusQueryDto 查询参数
     *
     * @param loginAuthDto 登录者信息
     *
     * @return 返回工单信息列表
     */
    List<WorkOrderVo> queryAllWorkOrders(WorkOrderStatusQueryDto workOrderStatusQueryDto, LoginAuthDto loginAuthDto);

    /**
     * 根据工单Id查询工单详情
     *
     * @param workorderId
     *
     * @return
     */
    WorkOrderDetailVo queryByWorkOrderId(WorkOrderQueryDto workOrderQueryDto);
}