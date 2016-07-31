package cn.ac.iscas.cloudeploy.v2.packet.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.model.dao.typeTrans.TypeImpleDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeType;
import cn.ac.iscas.cloudeploy.v2.model.entity.typeTrans.TypeImplemention;
import cn.ac.iscas.cloudeploy.v2.packet.Packet.PacketStrategy;

@Service
public class DefaultTypeTransferService implements TypeTransferService {
	@Autowired
	private TypeImpleDAO typeImpleDao;
	@Override
	public TypeImplemention getTypeImplemention(PacketStrategy strategy, NodeType nodeType, String opName) {
		return typeImpleDao.findByStartegyAndNodeTypeAndInterfaceName(strategy, nodeType,opName);
	}
}
