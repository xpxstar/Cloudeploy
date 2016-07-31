package cn.ac.iscas.cloudeploy.v2.model.entity.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_task_node_param")
public class NodeParam extends IdEntity {
	private String paramKey;
	@Column(columnDefinition = "varchar(1000)")
	private String paramValue;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "node_id")
	private TaskNode taskNode;

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public TaskNode getTaskNode() {
		return taskNode;
	}

	public void setTaskNode(TaskNode taskNode) {
		this.taskNode = taskNode;
	}
}
