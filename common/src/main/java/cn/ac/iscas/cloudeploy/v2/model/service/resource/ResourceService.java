package cn.ac.iscas.cloudeploy.v2.model.service.resource;

import java.util.List;
import java.util.Map;

import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

public interface ResourceService {

	/**
	 * 创建一个软件资源实例
	 * 
	 * @param action
	 *            安装软件的action 对象
	 * @param hosts
	 *            安装软件的目标机器
	 * @param params
	 *            软件安装参数
	 * @return <tt>true</tt> 如果创建资源成功
	 */
	public boolean createSoftwareInstance(Action action, List<DHost> hosts,
			Map<String, String> params);

	/**
	 * 更改软件配置
	 * 
	 * @param action
	 *            更改配置的action 对象
	 * @param hosts
	 *            软件所在的目标机器
	 * @param identifiers
	 *            软件实例的唯一标识符
	 * @param params
	 *            要修改的参数
	 * @return <tt>true</tt> 如果配置操作成功
	 */
	public boolean changeSoftwareInstanceStatus(Action action,
			List<DHost> hosts, Map<String, String> identifiers, String status);

	/**
	 * 更改软件配置
	 * 
	 * @param action
	 *            更改配置的action 对象
	 * @param hosts
	 *            软件所在的目标机器
	 * @param identifiers
	 *            软件实例的唯一标识符
	 * @param params
	 *            要修改的参数
	 * @return <tt>true</tt> 如果配置操作成功
	 */
	public boolean configSoftwareInstance(Action action, List<DHost> hosts,
			Map<String, String> identifiers, Map<String, String> params);

	/**
	 * 删除软件
	 * 
	 * @param action
	 *            删除软件的action 对象
	 * @param hosts
	 *            软件所在的目标机器
	 * @param identifiers
	 *            软件实例的唯一标识符
	 * @return <tt>true</tt> 如果删除操作成功
	 */
	public boolean removeSoftwareInstance(Action action, List<DHost> hosts,
			Map<String, String> identifiers);

}
