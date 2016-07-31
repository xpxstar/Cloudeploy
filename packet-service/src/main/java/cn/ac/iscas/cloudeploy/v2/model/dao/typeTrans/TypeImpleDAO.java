package cn.ac.iscas.cloudeploy.v2.model.dao.typeTrans;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeType;
import cn.ac.iscas.cloudeploy.v2.model.entity.typeTrans.TypeImplemention;
import cn.ac.iscas.cloudeploy.v2.packet.Packet.PacketStrategy;

public interface TypeImpleDAO extends PagingAndSortingRepository<TypeImplemention, Long>{
	public List<TypeImplemention> findByStartegyAndNodeType(String strategy, String nodeType);

	public TypeImplemention findByStartegyAndNodeTypeAndInterfaceName(PacketStrategy strategy, NodeType nodeType,
			String interfaceName);
}
