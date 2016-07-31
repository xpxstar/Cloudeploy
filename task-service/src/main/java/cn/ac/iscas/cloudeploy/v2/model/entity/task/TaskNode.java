package cn.ac.iscas.cloudeploy.v2.model.entity.task;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;

@Entity
@Table(name = "d_task_node")
public class TaskNode extends IdEntity  implements Cloneable{
	@Column(unique = true)
	private String identifier;

	@Column(columnDefinition = "INT(11) DEFAULT 0")
	private Integer xPos;
	@Column(columnDefinition = "INT(11) DEFAULT 0")
	private Integer yPos;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName = "id", name = "operation")
	private Task operation;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "action_id")
	private Action action;

	@OneToMany(mappedBy = "taskNode")
	private List<NodeParam> params;

	@OneToMany(mappedBy = "taskNode")
	private List<NodeHostRelation> hostInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName = "id", name = "task_id")
	private Task task;
	
	/***********************************gy修改部分*****************************/
	//保存该节点的孩子节点
	@Transient
	public List<TaskNode> children;
	
	
	
	public List<TaskNode> getChildren() {
		if(children==null){
			children = new ArrayList<>();
		}
		return children;
	}
	public void setChildren(List<TaskNode> children) {
		this.children = children;
	}
	public TaskNode addChild(TaskNode child) {
		if(children==null){
			children = new ArrayList<>();
		}
		this.children.add(child);
		return this;
	}
	
	 public Object clone(){
		 TaskNode o = null;
	     try{ 
	    	 o = (TaskNode)super.clone();
	     }catch(CloneNotSupportedException ex){
	    	 ex.printStackTrace();
	     }
	        return o;
	 }
	 
	 @Override
	 public boolean equals(Object obj) {
		 if (obj instanceof TaskNode) {   
	        	TaskNode t = (TaskNode) obj;   
	        	return this.id.equals(t.getId())
	        			&&this.identifier.equals(t.getIdentifier())
	        			&&this.hostInfo.equals(t.getHostInfo())
	        			&&this.task.equals(t.getTask());

	        }   
	        return super.equals(obj);  
	 }
	 
	/***********************************gy修改部分*****************************/

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public List<NodeParam> getParams() {
		return params;
	}

	public void setParams(List<NodeParam> params) {
		this.params = params;
	}

	public List<NodeHostRelation> getHostInfo() {
		return hostInfo;
	}

	public void setHostInfo(List<NodeHostRelation> hostInfo) {
		this.hostInfo = hostInfo;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Integer getxPos() {
		return xPos;
	}

	public void setxPos(Integer xPos) {
		this.xPos = xPos;
	}

	public Integer getyPos() {
		return yPos;
	}

	public void setyPos(Integer yPos) {
		this.yPos = yPos;
	}
	public Task getOperation() {
		return operation;
	}
	public void setOperation(Task operation) {
		this.operation = operation;
	}
	
	
}
