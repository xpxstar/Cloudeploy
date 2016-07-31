package cn.ac.iscas.cloudeploy.v2.model.service.application;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.dataview.application.ApplicationView;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;

public interface ApplicationService {
	/**
	 * 创建应用
	 * 
	 * @param view
	 * @return
	 */
	public Application createApplication(ApplicationView.DetailedItem view);

	/**
	 * 获取包括全部应用的列表
	 * 
	 * @return
	 */
	public List<Application> getAllApplications();

	/**
	 * 部署应用
	 * 
	 * @param applicationId
	 * @return
	 */
	public Application deployApplication(Long applicationId);

	/**
	 * 获取应用详细信息
	 * 
	 * @param applicationId
	 * @return
	 */
	public Application getApplication(Long applicationId);

	/**
	 * 在容器实例上做操作
	 * 
	 * @param containerInatanceId
	 * @param operation
	 * @return
	 */
	public boolean doOperationOnContainerInstance(Long containerInatanceId,
			ContainerInstance.Operation operation);

	/**
	 * 移除应用
	 * 
	 * @param applicationId
	 * @return
	 */
	public boolean removeApplication(Long applicationId);

	/**
	 * 修改应用
	 * 
	 * @param applicationId
	 * @param view
	 * @return
	 */
	public Application modifyApplication(Long applicationId,
			ApplicationView.DetailedItem view);
}
