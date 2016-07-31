package cn.ac.iscas.cloudeploy.v2.model.dao.application;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.NodeTemplatePacket;

public interface NodeTemplatePacketDAO 
	extends PagingAndSortingRepository<NodeTemplatePacket,Long>{
	public NodeTemplatePacket findByPacketStrategyAndTemplateName(
			String packetStrategy, String templateName);
}
