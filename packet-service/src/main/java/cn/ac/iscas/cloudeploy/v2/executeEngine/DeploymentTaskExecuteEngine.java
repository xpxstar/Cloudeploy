package cn.ac.iscas.cloudeploy.v2.executeEngine;

import cn.ac.iscas.cloudeploy.v2.workflowEngine.entity.Deployment;

public interface DeploymentTaskExecuteEngine {
	public void execute(Deployment deployment);
}
