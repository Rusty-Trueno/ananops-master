/*
 * Copyright (c) 2018. ananops.net All Rights Reserved.
 * 项目名称：ananops快速搭建企业级分布式微服务平台
 * 类名称：TpcMqConsumerService.java
 * 创建人：刘兆明
 * 联系方式：ananops.net@gmail.com
 * 开源地址: https://github.com/ananops
 * 博客地址: http://blog.ananops.net
 * 项目官网: http://ananops.net
 */

package com.ananops.provider.service;

import com.ananops.core.support.IService;
import com.ananops.provider.model.domain.TpcMqConsumer;
import com.ananops.provider.model.vo.TpcMqConsumerVo;
import com.ananops.provider.model.vo.TpcMqSubscribeVo;

import java.util.List;

/**
 * The interface Tpc mq consumer service.
 *
 * @author ananops.net @gmail.com
 */
public interface TpcMqConsumerService extends IService<TpcMqConsumer> {
	/**
	 * 查询Mq消费者列表.
	 *
	 * @param tpcMqConsumer the tpc mq consumer
	 *
	 * @return the list
	 */
	List<TpcMqConsumerVo> listConsumerVoWithPage(TpcMqConsumer tpcMqConsumer);

	/**
	 * 查询订阅者列表.
	 *
	 * @param tpcMqConsumer the tpc mq consumer
	 *
	 * @return the list
	 */
	List<TpcMqSubscribeVo> listSubscribeVoWithPage(TpcMqConsumer tpcMqConsumer);

	/**
	 * Delete by tag id.
	 *
	 * @param tagId the tag id
	 *
	 * @return the int
	 */
	int deleteSubscribeTagByTagId(Long tagId);

	/**
	 * 根据消费者ID删除消费者.
	 *
	 * @param id the id
	 *
	 * @return the int
	 */
	int deleteConsumerById(Long id);

	/**
	 * List subscribe vo list.
	 *
	 * @param subscribeIdList the subscribe id list
	 *
	 * @return the list
	 */
	List<TpcMqSubscribeVo> listSubscribeVo(List<Long> subscribeIdList);

	/**
	 * List consumer group by topic list.
	 *
	 * @param topic the topic
	 *
	 * @return the list
	 */
	List<String> listConsumerGroupByTopic(String topic);

	/**
	 * 根据cid更新生产者状态为在线.
	 *
	 * @param consumerGroup the consumer group
	 */
	void updateOnLineStatusByCid(String consumerGroup);

	/**
	 * 根据cid更新生产者状态为离线.
	 *
	 * @param consumerGroup the consumer group
	 */
	void updateOffLineStatusByCid(String consumerGroup);
}
