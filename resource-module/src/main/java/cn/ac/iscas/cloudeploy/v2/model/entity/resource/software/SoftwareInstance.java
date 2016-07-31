package cn.ac.iscas.cloudeploy.v2.model.entity.resource.software;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

import com.google.common.collect.ImmutableList;

/**
 * 
 * @author gaoqiang
 *
 */
@Entity
@Table(name = "d_software_instance")
public class SoftwareInstance extends IdEntity {
	@Transient
	public static final String PERMISSION_KEY = "instance";

	private String alias;

	@ManyToOne
	@JoinColumn(name = "host_id")
	private DHost host;

	@ManyToOne
	@JoinColumn(name = "component_id")
	private Component component;

	@OneToMany(mappedBy = "softwareInstance")
	private List<InstanceParam> params;

	@Enumerated(EnumType.STRING)
	private InstanceStatus status;

	public enum InstanceStatus {
		RUNNING, STOPPED;
		public static InstanceStatus of(String value) {
			for (InstanceStatus v : InstanceStatus.values()) {
				if (v.name().equalsIgnoreCase(value))
					return v;
			}
			return null;
		}
	}

	public SoftwareInstance() {
		this.status = InstanceStatus.STOPPED;
		this.params = ImmutableList.<InstanceParam> of();
	}

	public DHost getHost() {
		return host;
	}

	public void setHost(DHost host) {
		this.host = host;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public InstanceStatus getStatus() {
		return status;
	}

	public void setStatus(InstanceStatus status) {
		this.status = status;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public List<InstanceParam> getParams() {
		return params;
	}

	public void setParams(List<InstanceParam> params) {
		this.params = params;
	}

}
