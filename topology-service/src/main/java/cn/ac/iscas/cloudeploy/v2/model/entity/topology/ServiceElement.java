package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServiceElement extends RootElement{
	private String ip;
	private String port;
	private String check;
	private String serviceId;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getCheck() {
		return check;
	}
	public void setCheck(String check) {
		this.check = check;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	@Builder
	public ServiceElement(String description, String name, String ip, String port, String check, String serviceId) {
		super(description, name);
		this.ip = ip;
		this.port = port;
		this.check = check;
		this.serviceId = serviceId;
	}
}
