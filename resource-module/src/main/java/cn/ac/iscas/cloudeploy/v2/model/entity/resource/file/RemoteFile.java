package cn.ac.iscas.cloudeploy.v2.model.entity.resource.file;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

@Entity
@Table(name = "d_remote_file")
public class RemoteFile extends BasicFileResource {
	private String path;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "host_id")
	private DHost host;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public DHost getHost() {
		return host;
	}

	public void setHost(DHost host) {
		this.host = host;
	}
}
