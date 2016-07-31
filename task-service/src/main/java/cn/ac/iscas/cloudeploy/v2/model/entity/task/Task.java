package cn.ac.iscas.cloudeploy.v2.model.entity.task;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;

@Entity
@Table(name = "d_task")
public class Task extends IdEntity implements Cloneable{
	@Transient
	public static final String PERMISSION_KEY = "task";

	private String name;
	@OneToMany(mappedBy = "task")
	private List<TaskNode> nodes;

	@OneToMany(mappedBy = "task")
	private List<TaskEdge> edges;

	@OneToMany(mappedBy = "task")
	private List<Executable> executables;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "user_id")
	private User user;
	
	private int operation;

	private Date executedTime;
	
	private String xmlFileKey;
	
	
	/*************************GY************************************/
	public Object clone(){
		Task o = null;
	     try{ 
	    	 o = (Task)super.clone();
	     }catch(CloneNotSupportedException ex){
	    	 ex.printStackTrace();
	     }
	        return o;
	 }
	/**************************GY************************************/

	public List<TaskNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<TaskNode> nodes) {
		this.nodes = nodes;
	}

	public List<TaskEdge> getEdges() {
		return edges;
	}

	public void setEdges(List<TaskEdge> edges) {
		this.edges = edges;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Executable> getExecutables() {
		return executables;
	}

	public void setExecutables(List<Executable> executables) {
		this.executables = executables;
	}

	public Date getExecutedTime() {
		return executedTime;
	}

	public void setExecutedTime(Date executedTime) {
		this.executedTime = executedTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getOperation() {
		return operation;
	}
	
	public void setOperation(int operation) {
		this.operation = operation;
	}
	
	public String getXmlFileKey() {
		return xmlFileKey;
	}

	public void setXmlFileKey(String xmlFileKey) {
		this.xmlFileKey = xmlFileKey;
	}
}
