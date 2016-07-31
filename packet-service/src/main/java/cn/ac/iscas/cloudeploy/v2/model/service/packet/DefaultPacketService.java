package cn.ac.iscas.cloudeploy.v2.model.service.packet;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.TopologyTemplate;
import cn.ac.iscas.cloudeploy.v2.packet.Packet;
import cn.ac.iscas.cloudeploy.v2.packet.Packet.PacketStrategy;
import cn.ac.iscas.cloudeploy.v2.packet.builder.NodeTemplatePacketBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.builder.docker.DockerPacketBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.builder.puppet.VMPuppetPacketBuilder;

@Service
public class DefaultPacketService implements PacketService {
	private static Logger logger = LoggerFactory.getLogger(DefaultPacketService.class);
	
	@Autowired
	private DockerPacketBuilder dockerBuilder;
	
	@Autowired
	private VMPuppetPacketBuilder puppetBuilder;
	
	@Override
	public Optional<Packet> createPacketForNodeTemplate(ServiceTemplate template, NodeTemplate nTemplate,
			PacketStrategy strategy, int initNum) throws IOException {
		Preconditions.checkNotNull(template, "service template can't be null");
		Preconditions.checkNotNull(nTemplate, "node template can't be null");
		Preconditions.checkNotNull(strategy, "strategy can't be null");
		Preconditions.checkNotNull(template.getTopologytemplate(), "topology template"
				+ "can't be null");
		
		TopologyTemplate topology = template.getTopologytemplate();
		NodeTemplatePacketBuilder packetBuilder = null;
		switch(strategy){
		case DOCKER:
			packetBuilder = dockerBuilder.createPacketBuilder(template);
			break;
		case VMPUPPET:
			packetBuilder = puppetBuilder.createPacketBuilder(template);
			break;
		case VMANSIBLE:
			logger.error("startegy {} is not supported", strategy);
			return Optional.absent();
		default:
			logger.error("startegy {} is not supported", strategy);
			return Optional.absent();
		}
		return buildPacket(nTemplate, packetBuilder, initNum);
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
	
	@Override
	public Packet getPacketForNodeTemplate(NodeTemplate nTemplate, PacketStrategy strategy) {
		return null;
	}
}
