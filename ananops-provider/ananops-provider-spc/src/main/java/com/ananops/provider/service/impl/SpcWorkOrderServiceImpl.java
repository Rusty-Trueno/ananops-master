package com.ananops.provider.service.impl;

import com.ananops.base.dto.LoginAuthDto;
import com.ananops.provider.mapper.SpcCompanyEngineerMapper;
import com.ananops.provider.mapper.SpcCompanyMapper;
import com.ananops.provider.mapper.SpcEngineerMapper;
import com.ananops.provider.model.domain.MdmcTask;
import com.ananops.provider.model.domain.SpcCompany;
import com.ananops.provider.model.dto.*;
import com.ananops.provider.model.dto.group.GroupSaveDto;
import com.ananops.provider.model.dto.user.UserInfoDto;
import com.ananops.provider.model.service.UacGroupFeignApi;
import com.ananops.provider.model.service.UacUserFeignApi;
import com.ananops.provider.model.vo.CompanyVo;
import com.ananops.provider.model.vo.EngineerVo;
import com.ananops.provider.model.vo.WorkOrderDetailVo;
import com.ananops.provider.model.vo.WorkOrderVo;
import com.ananops.provider.service.*;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作加盟服务商WorkOrder的Service接口实现类
 *
 * Created by bingyueduan on 2020/1/3.
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SpcWorkOrderServiceImpl implements SpcWorkOrderService {

    @Resource
    private SpcEngineerMapper spcEngineerMapper;

    @Resource
    private SpcCompanyMapper spcCompanyMapper;

    @Resource
    private SpcCompanyEngineerMapper spcCompanyEngineerMapper;

    @Resource
    private ImcTaskFeignApi imcTaskFeignApi;

    @Resource
    private ImcItemFeignApi imcItemFeignApi;

    @Resource
    private MdmcTaskFeignApi mdmcTaskFeignApi;

    @Resource
    private PmcProjectFeignApi pmcProjectFeignApi;

    @Resource
    private UacGroupFeignApi uacGroupFeignApi;

    @Resource
    private SpcEngineerService spcEngineerService;

    private UacUserFeignApi uacUserFeignApi;

    @Override
    public List<WorkOrderVo> queryAllWorkOrders(WorkOrderStatusQueryDto workOrderStatusQueryDto, LoginAuthDto loginAuthDto) {
        List<WorkOrderVo> workOrderVos = new ArrayList<>();
        Long groupId = loginAuthDto.getGroupId();
        String workOrderType = workOrderStatusQueryDto.getType();
        Integer workOrderStatus = workOrderStatusQueryDto.getStatus();

        TaskQueryDto taskQueryDto = new TaskQueryDto();
        taskQueryDto.setUserId(groupId);
        log.info("登录用户的GroupId=" + groupId);
        List<TaskDto> imcTaskDtos = imcTaskFeignApi.getByFacilitatorId(taskQueryDto).getResult();
        log.info("巡检任务查询结果：imcTaskDtos=" + imcTaskDtos);
        MdmcQueryDto mdmcQueryDto = new MdmcQueryDto();
        mdmcQueryDto.setId(groupId);
        mdmcQueryDto.setRoleCode("fac_service");
        List<MdmcTask> mdmcTaskDtos = mdmcTaskFeignApi.getTaskListByIdAndStatus(mdmcQueryDto).getResult();
        log.info("巡检任务查询结果：mdmcTaskDtos=" + mdmcTaskDtos);

        // 按工单状态筛选
        if (!StringUtils.isEmpty(workOrderStatus)) {
            if (mdmcTaskDtos != null) {
                List<MdmcTask> newMdmcTaskDtos = new ArrayList<>();
                for (MdmcTask mdmcTaskDto : mdmcTaskDtos) {
                    if (workOrderStatus.equals(mdmcTaskDto.getStatus())) {
                        newMdmcTaskDtos.add(mdmcTaskDto);
                    }
                }
                mdmcTaskDtos = newMdmcTaskDtos;
            }
            if (imcTaskDtos != null) {
                List<TaskDto> newImcTaskDtos = new ArrayList<>();
                for (TaskDto imcTaskDto : imcTaskDtos) {
                    if (workOrderStatus.equals(imcTaskDto.getStatus())) {
                        newImcTaskDtos.add(imcTaskDto);
                    }
                }
                imcTaskDtos = newImcTaskDtos;
            }

        }

        // 按工单类型筛选
        if (!StringUtils.isEmpty(workOrderType) && "maintain".equals(workOrderType)) {
            if (mdmcTaskDtos != null) {
                for (MdmcTask mdmcTaskDto : mdmcTaskDtos) {
                    WorkOrderVo workOrderVo = new WorkOrderVo();
                    workOrderVo.setType("maintain");
                    try {
                        BeanUtils.copyProperties(workOrderVo, mdmcTaskDto);
                    } catch (Exception e) {
                        log.error("维修维护工单Dto与工单Dto属性拷贝异常");
                        e.printStackTrace();
                    }
                    workOrderVos.add(workOrderVo);
                }
            }
        } else if (!StringUtils.isEmpty(workOrderType) && "inspection".equals(workOrderType)) {
            if (imcTaskDtos != null) {
                for (TaskDto imcTaskDto : imcTaskDtos) {
                    WorkOrderVo workOrderVo = new WorkOrderVo();
                    workOrderVo.setType("inspection");
                    try {
                        BeanUtils.copyProperties(workOrderVo, imcTaskDto);
                    } catch (Exception e) {
                        log.error("巡检工单Dto与工单Dto属性拷贝异常");
                        e.printStackTrace();
                    }
                    workOrderVos.add(workOrderVo);
                }
            }
        } else {
            if (mdmcTaskDtos != null) {
                for (MdmcTask mdmcTaskDto : mdmcTaskDtos) {
                    WorkOrderVo workOrderVo = new WorkOrderVo();
                    workOrderVo.setType("maintain");
                    try {
                        BeanUtils.copyProperties(workOrderVo, mdmcTaskDto);
                    } catch (Exception e) {
                        log.error("维修维护工单Dto与工单Dto属性拷贝异常");
                        e.printStackTrace();
                    }
                    workOrderVos.add(workOrderVo);
                }
            }
            if (imcTaskDtos != null) {
                for (TaskDto imcTaskDto : imcTaskDtos) {
                    WorkOrderVo workOrderVo = new WorkOrderVo();
                    workOrderVo.setType("inspection");
                    try {
                        BeanUtils.copyProperties(workOrderVo, imcTaskDto);
                    } catch (Exception e) {
                        log.error("巡检工单Dto与工单Dto属性拷贝异常");
                        e.printStackTrace();
                    }
                    workOrderVos.add(workOrderVo);
                }
            }
        }

        // 填充名字信息
        decorateWorkOrder(workOrderVos);

        return workOrderVos;
    }

    /**
     * 填充名字信息
     *
     * @param workOrderVos
     */
    private void decorateWorkOrder(List<WorkOrderVo> workOrderVos) {

        if (workOrderVos != null) {
            for (WorkOrderVo workOrderVo : workOrderVos) {
                // 填充项目名称,客户负责人名称名称
                Long projectId = workOrderVo.getProjectId();
                if (projectId != null) {
                    PmcProjectDto pmcProjectDto = pmcProjectFeignApi.getProjectByProjectId(projectId).getResult();
                    String projectName = pmcProjectDto.getProjectName();
                    String aOneName = pmcProjectDto.getAOneName();
                    String aTwoName = pmcProjectDto.getATwoName();
                    String aThreeName = pmcProjectDto.getAThreeName();
                    workOrderVo.setProjectName(projectName);
                    if (!StringUtils.isEmpty(aOneName)) {
                        workOrderVo.setFacilitatorName(aOneName);
                    } else if (!StringUtils.isEmpty(aTwoName)) {
                        workOrderVo.setFacilitatorName(aTwoName);
                    } else if (!StringUtils.isEmpty(aThreeName)) {
                        workOrderVo.setFacilitatorName(aThreeName);
                    }
                }
                // 填充服务商名称
                Long groupId = workOrderVo.getFacilitatorId();
                if (groupId != null) {
                    GroupSaveDto groupSaveDto = uacGroupFeignApi.getUacGroupById(groupId).getResult();
                    workOrderVo.setFacilitatorName(groupSaveDto.getGroupName());
                }
                // 填充工程师名称
                Long userId = workOrderVo.getMaintainerId();
                if (userId != null) {
                    UserInfoDto userInfoDto = uacUserFeignApi.getUacUserById(userId).getResult();
                    workOrderVo.setMaintainerName(userInfoDto.getUserName());
                }
            }
        }
    }

    /**
     * 根据工单Id查询工单信息
     *
     * @param workOrderQueryDto
     *
     * @return
     */
    @Override
    public WorkOrderDetailVo queryByWorkOrderId(WorkOrderQueryDto workOrderQueryDto) {
        WorkOrderDetailVo workOrderDetailVo = new WorkOrderDetailVo();
        Long taskId = workOrderQueryDto.getId();//获取工单Id
        Long projectId = null;
        Long groupId = null;
        String workOrderType = workOrderQueryDto.getType();//获取工单类型
        // 填充工单信息
        if (!Strings.isNullOrEmpty(workOrderType) && "inspection".equals(workOrderType)) {
            log.info("查询巡检工单：taskId=" + taskId);
            TaskDto taskDto = imcTaskFeignApi.getTaskByTaskId(taskId).getResult();
            workOrderDetailVo.setType("inspection");
            workOrderDetailVo.setInspectionTask(taskDto);
            projectId = taskDto.getProjectId();
            groupId = taskDto.getFacilitatorId();
        } else if (!Strings.isNullOrEmpty(workOrderType) && "maintain".equals(workOrderType)) {
            log.info("查询维修维护工单：taskId=" + taskId);
            MdmcTask mdmcTaskDto = mdmcTaskFeignApi.getTaskByTaskId(taskId).getResult();
            workOrderDetailVo.setType("maintain");
            workOrderDetailVo.setMaintainTask(mdmcTaskDto);
            projectId = mdmcTaskDto.getProjectId();
            groupId = mdmcTaskDto.getFacilitatorId();
        }
        // 填充项目信息
        log.info("工单项目ID：projectId=" + projectId);
        PmcProjectDto pmcProjectDto = pmcProjectFeignApi.getProjectByProjectId(projectId).getResult();
        workOrderDetailVo.setPmcProjectDto(pmcProjectDto);
        // 填充服务商信息
        CompanyVo companyVo = new CompanyVo();
        SpcCompany queryC = new SpcCompany();
        queryC.setGroupId(groupId);
        SpcCompany spcCompany = spcCompanyMapper.selectOne(queryC);
        if (!StringUtils.isEmpty(groupId)) {//如果组织Id非空
            GroupSaveDto groupSaveDto = uacGroupFeignApi.getUacGroupById(groupId).getResult();
            try {
                BeanUtils.copyProperties(companyVo, spcCompany);
                BeanUtils.copyProperties(companyVo, groupSaveDto);
            } catch (Exception e) {
                log.error("queryByCompanyId 服务商Dto与用户组Dto属性拷贝异常");
                e.printStackTrace();
            }
        }
        //填充相关工程师信息
        log.info("工单项目ID：projectId=" + projectId);
        List<Long> engineerIdList = new ArrayList<>();
        List<EngineerVo> engineerVos = new ArrayList<>();
        if(!Strings.isNullOrEmpty(workOrderType) && "inspection".equals(workOrderType)){
            //如果当前是巡检任务
            engineerIdList.add(mdmcTaskFeignApi.getTaskByTaskId(taskId).getResult().getMaintainerId());
        }else if(!Strings.isNullOrEmpty(workOrderType) && "maintain".equals(workOrderType)){
            //如果当前是维修维护任务
            List<ItemDto> itemDtoList = imcTaskFeignApi.getTaskByTaskId(taskId).getResult().getItemDtoList();
            itemDtoList.forEach(itemDto -> {
                engineerIdList.add(itemDto.getMaintainerId());
            });
        }
        engineerIdList.forEach(engineerId->{//根据工单对应的全部工程师Id获取全部的工程师
            EngineerVo engineerVo = spcEngineerService.queryByEngineerId(engineerId);
            engineerVos.add(engineerVo);
        });
        workOrderDetailVo.setEngineerVos(engineerVos);
        workOrderDetailVo.setCompanyVo(companyVo);
        return workOrderDetailVo;
    }
}