package cn.ac.iscas.cloudeploy.v2.packet.type;

import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeType;
import cn.ac.iscas.cloudeploy.v2.model.entity.typeTrans.TypeImplemention;
import cn.ac.iscas.cloudeploy.v2.packet.Packet.PacketStrategy;

public interface TypeTransferService {
	public TypeImplemention getTypeImplemention(PacketStrategy strategy, 
			NodeType nodeType, String opName);
}
