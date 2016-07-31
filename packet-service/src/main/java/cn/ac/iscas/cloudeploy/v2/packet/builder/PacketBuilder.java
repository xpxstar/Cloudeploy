package cn.ac.iscas.cloudeploy.v2.packet.builder;

import java.io.IOException;

import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate;
import cn.ac.iscas.cloudeploy.v2.workflowEngine.entity.Deployment;

public abstract class PacketBuilder {
	public abstract Deployment buildDeployment(ServiceTemplate application) throws IOException;
}