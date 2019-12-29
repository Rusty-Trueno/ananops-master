package com.ananops.core.generator;

import com.ananops.base.constant.GlobalConstant;
import com.ananops.core.registry.base.CoordinatorRegistryCenter;
import com.ananops.core.registry.base.RegisterDto;
import org.apache.curator.retry.RetryNTimes;

/**
 * FrameworkID 的保存器.(整个项目框架不同服务注册在zookeeper中心的唯一ID)
 *
 * @author gaohongtao
 */
public class IncrementIdGenerator implements IdGenerator {

	private static Long serviceId = null;
	private final RegisterDto registerDto;

	/**
	 * Instantiates a new Increment id generator.
	 * 实例化一个新的id自增生成器
	 * @param registerDto the register dto
	 */
	public IncrementIdGenerator(RegisterDto registerDto) {
		this.registerDto = registerDto;
	}

	/**
	 * Next id long.
	 *
	 * @return the long
	 */
	@Override
	public Long nextId() {
		String app = this.registerDto.getApp();
		String host = this.registerDto.getHost();
		//以uac服务为例 path="/ananops/registry/id/ananops-provider-uac/10.0.75.1"
		CoordinatorRegistryCenter regCenter = this.registerDto.getCoordinatorRegistryCenter();
		String path = GlobalConstant.ZK_REGISTRY_ID_ROOT_PATH + GlobalConstant.Symbol.SLASH + app + GlobalConstant.Symbol.SLASH + host;
		if (regCenter.isExisted(path)) {
			// 如果已经有该节点，表示已经为当前的host上部署的该app分配的编号（应对某个服务重启之后编号不变的问题），直接获取该id，而无需生成
			return Long.valueOf(regCenter.getDirectly(GlobalConstant.ZK_REGISTRY_ID_ROOT_PATH + GlobalConstant.Symbol.SLASH + app + GlobalConstant.Symbol.SLASH + host));
		} else {
			// 节点不存在，那么需要生成id，利用zk节点的版本号每写一次就自增的机制来实现
			regCenter.increment(GlobalConstant.ZK_REGISTRY_SEQ, new RetryNTimes(2000, 3));
			// 生成id
			Integer id = regCenter.getAtomicValue(GlobalConstant.ZK_REGISTRY_SEQ, new RetryNTimes(2000, 3)).postValue();
			// 将数据写入节点
			regCenter.persist(path);
			regCenter.persist(path, String.valueOf(id));
			return Long.valueOf(id);
		}
	}

	/**
	 * Gets service id.
	 *
	 * @return the service id
	 */
	public static Long getServiceId() {
		return serviceId;
	}

	/**
	 * Sets service id.
	 *
	 * @param serviceId the service id
	 */
	public static void setServiceId(Long serviceId) {
		IncrementIdGenerator.serviceId = serviceId;
	}
}
