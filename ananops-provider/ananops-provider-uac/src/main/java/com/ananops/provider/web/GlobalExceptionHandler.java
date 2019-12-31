/*
 * Copyright (c) 2019. ananops.com All Rights Reserved.
 * 项目名称：ananops平台
 * 类名称：GlobalExceptionHandler.java
 * 创建人：ananops
 * 平台官网: http://ananops.com
 */

package com.ananops.provider.web;


import com.ananops.base.enums.ErrorCodeEnum;
import com.ananops.base.exception.BusinessException;
import com.ananops.provider.model.dto.GlobalExceptionLogDto;
import com.ananops.provider.service.MdcExceptionLogFeignApi;
import com.ananops.wrapper.WrapMapper;
import com.ananops.wrapper.Wrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;

/**
 * 全局的的异常拦截器
 *
 * @author ananops.com @gmail.com
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@Resource
	private TaskExecutor taskExecutor;
	@Value("${spring.profiles.active}")
	String profile;
	@Value("${spring.application.name}")
	String applicationName;
	@Resource
	private MdcExceptionLogFeignApi mdcExceptionLogFeignApi;

	/**
	 * 参数非法异常.
	 *
	 * @param e the e
	 *
	 * @return the wrapper
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Wrapper illegalArgumentException(IllegalArgumentException e) {
		log.error("参数非法异常={}", e.getMessage(), e);
		return WrapMapper.wrap(ErrorCodeEnum.GL99990100.code(), e.getMessage());
	}

	/**
	 * 业务异常.
	 *
	 * @param e the e
	 *
	 * @return the wrapper
	 */
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Wrapper businessException(BusinessException e) {
		log.error("业务异常={}", e.getMessage(), e);
		return WrapMapper.wrap(e.getCode() == 0 ? Wrapper.ERROR_CODE : e.getCode(), e.getMessage());
	}

	/**
	 * 无权限访问.
	 *
	 * @param e the e
	 *
	 * @return the wrapper
	 */
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public Wrapper unAuthorizedException(AccessDeniedException e) {
		log.error("业务异常={}", e.getMessage(), e);
		return WrapMapper.wrap(ErrorCodeEnum.GL99990401.code(), ErrorCodeEnum.GL99990401.msg());
	}


	/**
	 * 全局异常.
	 *
	 * @param e the e
	 *
	 * @return the wrapper
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Wrapper exception(Exception e) {
		log.info("保存全局异常信息 ex={}", e.getMessage(), e);
		taskExecutor.execute(() -> {
			GlobalExceptionLogDto globalExceptionLogDto = new GlobalExceptionLogDto().getGlobalExceptionLogDto(e, profile, applicationName);
			mdcExceptionLogFeignApi.saveAndSendExceptionLog(globalExceptionLogDto);
		});
		return WrapMapper.error();
	}
}
