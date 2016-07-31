package cn.ac.iscas.cloudeploy.v2.packet.type;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.ac.iscas.cloudeploy.v2.model.entity.typeTrans.TypeImplemention;

@Component
public class PuppetTransfer implements TypeTransfer {

	@Override
	public List<TypeImplemention> getTypeImplementions(String nodeType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, TypeImplemention> getImplementions(String nodeType) {
		// TODO Auto-generated method stub
		return null;
	}

}
