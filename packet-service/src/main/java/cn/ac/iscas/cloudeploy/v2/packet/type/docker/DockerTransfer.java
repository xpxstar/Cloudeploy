package cn.ac.iscas.cloudeploy.v2.packet.type.docker;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import cn.ac.iscas.cloudeploy.v2.model.dao.typeTrans.TypeImpleDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.typeTrans.TypeImplemention;
import cn.ac.iscas.cloudeploy.v2.packet.type.TypeTransfer;
import cn.ac.iscas.cloudeploy.v2.packet.util.JsonUtils;
import cn.ac.iscas.cloudeploy.v2.util.ForEachHelper;

@Component
public class DockerTransfer implements TypeTransfer {
	private static String strategy = "docker";
	
	@Autowired
	private TypeImpleDAO typeImplDao;
	@Override
	public List<TypeImplemention> getTypeImplementions(String nodeType) {
		return typeImplDao.findByStartegyAndNodeType(strategy, nodeType);
	}

	@Override
	public Map<String, TypeImplemention> getImplementions(String nodeType) {
		List<TypeImplemention> implementions = typeImplDao.findByStartegyAndNodeType(strategy, nodeType);
		Map<String,TypeImplemention> result = Maps.newHashMap();
		for(TypeImplemention typeIm: ForEachHelper.of(implementions)){
			result.put(typeIm.getInterfaceName(), typeIm);
		}
		return result;
	}
	
	public ImageInfo getImageInfo(TypeImplemention impl) throws IOException{
		Preconditions.checkNotNull(impl, "can't find a type implemention");
		return JsonUtils.convertToObject(impl.getMetaJson(), ImageInfo.class);
	}
	
}
