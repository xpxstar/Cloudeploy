package cn.ac.iscas.cloudeploy.v2.packet.builder.docker;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;


import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.TopologyTemplate;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.packet.Packet;
import cn.ac.iscas.cloudeploy.v2.packet.builder.NodeTemplatePacketBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.builder.PacketBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.type.TypeTransfer;
import cn.ac.iscas.cloudeploy.v2.util.ForEachHelper;
import cn.ac.iscas.cloudeploy.v2.workflowEngine.entity.Deployment;

@Component
public class DockerPacketBuilder extends PacketBuilder{
	private static Logger logger = LoggerFactory.getLogger(DockerPacketBuilder.class);
	
	@Autowired
	@Qualifier("dockerTransfer")
	private TypeTransfer scriptsService;
	/**
	 * used to store packet to file system;
	 */
	@Autowired
	private FileService fileService;
	
	@Override
	public Deployment buildDeployment(ServiceTemplate application) throws IOException {
		Preconditions.checkArgument(application != null,
				"failed to buildDeployment for application because of serviceTemplate can't be null");
		Deployment deployment = new Deployment();
		TopologyTemplate topology = application.getTopologytemplate();
		if (topology != null) {
			NodeTemplatePacketBuilder packetBuilder = createPacketBuilder(application);
			for (NodeTemplate nodetemplate : ForEachHelper.of(topology.getNodetemplates())) {
				int init = nodetemplate.getMinInstance(), max = nodetemplate.getMaxInstance();
				for(int i = 0; i < init && i < max; i++){
					Optional<Packet> packet = buildPacket(nodetemplate, packetBuilder,i);
					deployment.addPacket(packet.get());
				}
			}
		}
		return deployment;
	}
	
	public Optional<Packet> buildPacket(NodeTemplate nodetemplate, NodeTemplatePacketBuilder packetBuilder, int initNum) throws IOException{
		Optional<Packet> nodePacket;
		try {
			nodePacket = packetBuilder.buildPacket(nodetemplate, initNum);
			return nodePacket;
		} catch (IOException e) {
			logger.error("failed to build packet for " + nodetemplate.getName());
			e.printStackTrace();
			throw e;
		}
	}
	
	public NodeTemplatePacketBuilder createPacketBuilder(ServiceTemplate application){
		TopologyTemplate topology = application.getTopologytemplate();
		NodeTemplatePacketBuilder packetBuilder = new DockerNodeTemplatePacketBuilder(scriptsService, fileService,
				application.getNodetypes(), application.getRelationshiptypes(),
				topology.getRelationshiptemplates());
		return packetBuilder;
	}
}