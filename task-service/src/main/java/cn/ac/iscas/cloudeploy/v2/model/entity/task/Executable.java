package cn.ac.iscas.cloudeploy.v2.model.entity.task;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

/**
 * sub executable task on a certain machine
 * 
 * @author gaoqiang
 *
 */
@Entity
@Table(name = "d_task_executable")
public class Executable extends IdEntity {
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "task_id")
	private Task task;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "host_id")
	private DHost host;
	private String executableFileKey;

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public DHost getHost() {
		return host;
	}

	public void setHost(DHost host) {
		this.host = host;
	}

	public String getExecutableFileKey() {
		return executableFileKey;
	}

	public void setExecutableFileKey(String executableFileKey) {
		this.executableFileKey = executableFileKey;
	}
}
