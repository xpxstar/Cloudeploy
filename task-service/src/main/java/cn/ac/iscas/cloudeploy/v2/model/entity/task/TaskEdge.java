package cn.ac.iscas.cloudeploy.v2.model.entity.task;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_task_edge")
public class TaskEdge extends IdEntity  implements Cloneable{
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "from_id")
	private TaskNode from;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "to_id")
	private TaskNode to;

	@Enumerated(EnumType.STRING)
	private Relation relation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName = "id", name = "task_id")
	private Task task;

	/*************************GY************************************/
	public Object clone(){
		TaskEdge o = null;
	     try{ 
	    	 o = (TaskEdge)super.clone();
	     }catch(CloneNotSupportedException ex){
	    	 ex.printStackTrace();
	     }
	        return o;
	 }
	/**************************GY************************************/
	
	public enum Relation {
		BEFORE, LISTEN
	}

	public TaskNode getFrom() {
		return from;
	}

	public void setFrom(TaskNode from) {
		this.from = from;
	}

	public TaskNode getTo() {
		return to;
	}

	public void setTo(TaskNode to) {
		this.to = to;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
}
