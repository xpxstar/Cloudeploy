package cn.ac.iscas.cloudeploy.v2.model.service.config;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;
import com.orbitz.consul.model.health.ServiceHealth;
import com.orbitz.consul.model.kv.Value;

import cn.ac.iscas.cloudeploy.v2.model.dao.application.ApplicationDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.application.ContainerDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.event.ConfigEvent;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.AttributeElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.AttributeElement.AttributeElementBuilder;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.PropertyElement;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.PropertyElement.PropertyElementBuilder;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.service.event.EventService;
import cn.ac.iscas.cloudeploy.v2.model.service.key.KeyGenerator;
import cn.ac.iscas.cloudeploy.v2.util.ForEachHelper;

@Service
public class RegisterCenterService {
	@Autowired
	private Consul consul;
	@Autowired
	private ApplicationDAO applicationDAO;
	@Autowired
	private ContainerDAO containerDAO;
	@Autowired
	private KeyGenerator keyGenerator;
	@Autowired
	private EventService eventService;
	
	/**
	 * @param serviceName
	 * @return
	 */
	public List<CatalogService> findServiceInstance(String serviceName){
		CatalogClient catalogClient = consul.catalogClient();
		ConsulResponse<List<CatalogService>> responses = catalogClient.getService(serviceName);
		List<CatalogService> catalogServices = responses.getResponse();
		return catalogServices;
	}
	
	/**
	 * get the health check result of a terminate service in all nodes
	 * @param serviceName
	 * @return
	 */
	public List<ServiceHealth> findServiceInstanceWithStatus(String serviceName){
		HealthClient healthClient = consul.healthClient();
		ConsulResponse<List<ServiceHealth>> responses = healthClient.getAllServiceInstances(serviceName);
		List<ServiceHealth> healthServices = responses.getResponse();
		return healthServices;
	}
	
	public List<PropertyElement> findProperties(Long appId, Long containerId, User user){
		Application app = applicationDAO.findOne(appId);
		Container container = containerDAO.findOne(containerId);
		String appKey = keyGenerator.serviceTemplateNameKey(app.getName());
		String containerKey = keyGenerator.nodeTemplateKey(container.getName(), container.getIdentifier());
		String key = KeyGenerator.appPropertiesKey(appKey, containerKey, user);
		KeyValueClient client = consul.keyValueClient();
		List<Value> values = null;
		try{
			values= client.getValues(key);
		}catch(NotFoundException nfe){
			values = new ArrayList<Value>();
		}
		List<PropertyElement> properties = new ArrayList<>();
		PropertyElementBuilder builder = PropertyElement.builder();
		for(Value value : ForEachHelper.of(values)){
			String proKey = KeyGenerator.getPropertyNameFromKey(value.getKey());
			builder.defaultValue(value.getValueAsString().get()).name(proKey);
			properties.add(builder.build());
		}
		return properties;
	}
	
	public List<AttributeElement> findAttributes(String appName, String containerName, User user){
		String key = KeyGenerator.appAttributesKey(appName, containerName);
		KeyValueClient client = consul.keyValueClient();
		List<Value> values = null;
		try{
			values= client.getValues(key);
		}catch(NotFoundException nfe){
			values = new ArrayList<Value>();
		}
		List<AttributeElement> attributes = new ArrayList<>();
		AttributeElementBuilder builder = AttributeElement.builder();
		for(Value value : ForEachHelper.of(values)){
			String attKey = KeyGenerator.getAttributeNameFromKey(value.getKey());
			builder.name(attKey).value(value.getValue().get());
		}
		return attributes;
	}

	public boolean updateProperty(Long appId, Long containerId, String propertyName, String propertyValue, User user) {
		Application app = applicationDAO.findOne(appId);
		Container container = containerDAO.findOne(containerId);
		String appKey = keyGenerator.serviceTemplateNameKey(app.getName());
		String containerKey = keyGenerator.nodeTemplateKey(container.getName(), container.getIdentifier());
		String key = KeyGenerator.appPropertyKey(propertyName, containerKey, appKey, user);
		KeyValueClient client = consul.keyValueClient();
		return client.putValue(key, propertyValue);
	}

	public List<AttributeElement> findAttributes(Long appId,
			Long containerId) {
		Application app = applicationDAO.findOne(appId);
		Container container = containerDAO.findOne(containerId);
		String appKey = keyGenerator.serviceTemplateNameKey(app.getName());
		String containerKey = keyGenerator.nodeTemplateKey(container.getName(), container.getIdentifier());
		String key = KeyGenerator.appAttributesKey(appKey, containerKey);
		KeyValueClient client = consul.keyValueClient();
		
		List<Value> values = null ;
		try{
			values= client.getValues(key);
		}catch(NotFoundException nfe){
			values = new ArrayList<Value>();
		}
		List<AttributeElement> attributes = new ArrayList<>();
		AttributeElementBuilder builder = AttributeElement.builder();
		for(Value value : ForEachHelper.of(values)){
			String attKey = KeyGenerator.getAttributeNameFromKey(value.getKey());
			builder.name(attKey).value(value.getValueAsString().get());
			attributes.add(builder.build());
		}
		return attributes;
	}

	public boolean updateAttribute(Long appId, Long containerId, String attributeName, String attributeValue,
			User currentUser) {
		Container container = containerDAO.findOne(containerId);
		for(ContainerInstance cInstance : ForEachHelper.of(container.getInstances())){
			DHost dhost = cInstance.getMaster();
			ConfigEvent configEvent = ConfigEvent.builder()
					.dhost(dhost)
					.instance(container.getName())
					.key(attributeName)
					.value(attributeValue)
					.build();
			eventService.pubConfigEvent(configEvent);
		}
		//TODO 更新数据库的
		
		return true;
	}
}
