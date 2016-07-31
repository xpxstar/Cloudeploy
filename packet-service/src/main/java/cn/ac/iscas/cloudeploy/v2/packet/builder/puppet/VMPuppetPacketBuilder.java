package cn.ac.iscas.cloudeploy.v2.packet.builder.puppet;

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
/**
 * build all packets for application using puppet strategy.
 * @author RichardLcc
 *
 */
@Component
public class VMPuppetPacketBuilder extends PacketBuilder{
	private Logger logger = LoggerFactory.getLogger(VMPuppetPacketBuilder.class);
	
	@Autowired
	@Qualifier("puppetTransfer")
	private TypeTransfer scriptsService;
	/**
	 * used to store packet to file system;
	 */
	@Autowired
	private FileService fileService;
	/**
	 * @throws IllegalArgumentException
	 */
	@Override
	public Deployment buildDeployment(ServiceTemplate application) throws IOException {
		Preconditions.checkArgument(application != null, "failed to buildDeployment for application because of serviceTemplate can't be null");
		Deployment deployment = new Deployment();
		TopologyTemplate topology = application.getTopologytemplate();
		if(topology != null){
			NodeTemplatePacketBuilder packetBuilder = new VMNodeTemplatePacketBuilder(scriptsService, fileService, application.getNodetypes(), 
					application.getRelationshiptypes(),topology.getRelationshiptemplates());
			for(NodeTemplate nodetemplate : ForEachHelper.of(topology.getNodetemplates())){
//				try {
////					Optional<Packet> nodePacket = packetBuilder.buildPacket(nodetemplate);
////					deployment.addPacket(nodePacket.get());
////				} catch (IOException e) {
////					logger.error("failed to build packet for " + nodetemplate.getName());
////					e.printStackTrace();
////					throw e;
//				}
			}
		}
		return deployment;
	}
	
	public NodeTemplatePacketBuilder createPacketBuilder(ServiceTemplate application){
		TopologyTemplate topology = application.getTopologytemplate();
		NodeTemplatePacketBuilder packetBuilder = new VMNodeTemplatePacketBuilder(scriptsService, 
				fileService, application.getNodetypes(), application.getRelationshiptypes(), topology.getRelationshiptemplates());
		return packetBuilder;
	}
}