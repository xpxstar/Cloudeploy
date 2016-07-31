package cn.ac.iscas.cloudeploy.v2.packet.type;

import java.util.List;
import java.util.Map;

import cn.ac.iscas.cloudeploy.v2.model.entity.typeTrans.TypeImplemention;

public interface TypeTransfer {
	public List<TypeImplemention> getTypeImplementions(String nodeType);
	public Map<String, TypeImplemention> getImplementions(String nodeType);
}
