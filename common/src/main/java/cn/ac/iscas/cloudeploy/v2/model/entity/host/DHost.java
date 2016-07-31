package cn.ac.iscas.cloudeploy.v2.model.entity.host;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;

/**
 * DHost defines the physical/virtual machines managed by a user
 * 
 * @author gaoqiang
 *
 */
@Entity
@Table(name = "d_host")
public class DHost extends IdEntity {
	@Transient
	public static final String PERMISSION_KEY = "host";
	private String hostName;
	private String hostIP;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getHostIP() {
		return hostIP;
	}

	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}
}
