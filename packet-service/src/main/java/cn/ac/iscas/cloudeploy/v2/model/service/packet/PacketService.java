package cn.ac.iscas.cloudeploy.v2.model.service.packet;

import java.io.IOException;

import com.google.common.base.Optional;

import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate;
import cn.ac.iscas.cloudeploy.v2.packet.Packet;
import cn.ac.iscas.cloudeploy.v2.packet.Packet.PacketStrategy;

public interface PacketService {
	public Optional<Packet> createPacketForNodeTemplate(ServiceTemplate template, 
			NodeTemplate nTemplate, PacketStrategy strategy, int instanceNum) throws IOException;
	
	public Packet getPacketForNodeTemplate(NodeTemplate nTemplate, PacketStrategy strategy);
}
