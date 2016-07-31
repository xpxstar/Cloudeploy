package cn.ac.iscas.cloudeploy.v2.model.entity.task;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

@Entity
@Table(name = "d_task_node_host")
public class NodeHostRelation extends IdEntity {

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "node_id")
	private TaskNode taskNode;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "host_id")
	private DHost host;

	public TaskNode getTaskNode() {
		return taskNode;
	}

	public void setTaskNode(TaskNode taskNode) {
		this.taskNode = taskNode;
	}

	public DHost getHost() {
		return host;
	}

	public void setHost(DHost host) {
		this.host = host;
	}
}
