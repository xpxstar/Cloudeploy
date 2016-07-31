package cn.ac.iscas.cloudeploy.v2.model.service.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.service.user.UserService;
@Service
public class KeyGenerator {
	private static Logger logger = LoggerFactory.getLogger(KeyGenerator.class);
	@Autowired
	private UserService userService;
	
	public static String instanceKey(String appName, String containerInstanceName, User user){
		StringBuilder builder = new StringBuilder();
		builder.append("/").append(user.getId())
			.append("/").append(appName)
			.append("/").append(containerInstanceName);
		return builder.toString();
	}
	
	public static String appPropertiesKey(String appName, String containerName, User user){
		StringBuilder builder = new StringBuilder();
		builder.append("/").append(user.getId())
			.append("/").append(appName)
			.append("/").append(containerName)
			.append("/").append("properties");
		return builder.toString();
	}
	
	public static String appPropertyKey(String propertyName, String containerName, String appName, User user){
		StringBuilder builder = new StringBuilder();
		builder.append("/").append(user.getId())
			.append("/").append(appName)
			.append("/").append(containerName)
			.append("/").append("properties")
			.append("/").append(propertyName);
		return builder.toString();
	}
	
	public static String propertyListenerKey(String propertyName, String containerName, String appName, User user){
		StringBuilder builder = new StringBuilder();
		builder.append("/").append(user.getId())
			.append("/").append(appName)
			.append("/").append(containerName)
			.append("/").append(propertyName)
			.append("/").append("properties")
			.append("/").append("listeners");
		return builder.toString();
	}
	
	public static String containerInstanceKey(String instanceName, String containerName, String appName, User user){
		StringBuilder builder = new StringBuilder();
		builder.append("/").append(user.getId())
			.append("/").append(appName)
			.append("/").append(containerName)
			.append("/").append(instanceName);
		return builder.toString();
	}

	public static String appAttributesKey(String appName, String containerName) {
		StringBuilder builder = new StringBuilder();
		builder
//		.append("/").append(appName)
		.append("/").append(containerName);
//		.append("/").append("attributes");
		return builder.toString();
	}
	
	public static String getPropertyNameFromKey(String key){
		Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key can't be null or empty");
		String[] values = key.split("/");
		Preconditions.checkArgument(values.length > 3, "key(%s) is not a legal property key", key);
		return values[values.length - 1];
	}
	
	public static String getAttributeNameFromKey(String key) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "key can't be null or empty");
		String[] values = key.split("/");
		Preconditions.checkArgument(values.length > 1, "key(%s) is not a legal attribute key", key);
		StringBuilder builder = new StringBuilder();
		for(int i = 1; i < values.length -1; i++){
			builder.append(values[i])
				.append("/");
		}
		builder.append(values[values.length - 1]);
		return builder.toString();
	}
	
	public String serviceTemplateNameKey(String viewName){
		User currentUser = userService.getCurrentUser();
		StringBuilder builder = new StringBuilder();
		builder.append(currentUser.getId())
			.append(viewName);
		return builder.toString();
	}
	
	public String nodeTemplateKey(String viewName, String viewId){
		User currentUser = userService.getCurrentUser();
		StringBuilder builder = new StringBuilder();
		builder.append(currentUser.getId())
			.append(viewName)
			.append(viewId);
		return builder.toString();
	}
	
	public String stmServiceNameKey(String viewName, String nodeId){
		return viewName;
	}
	
	public String stmServiceIdKey(String viewName, String nodeId){
		User currentUser = userService.getCurrentUser();
		StringBuilder builder = new StringBuilder();
		builder.append(currentUser.getId())
			.append(viewName)
			.append(nodeId);
		return builder.toString();
	}
}
