package cn.ac.iscas.cloudeploy.v2.model.entity.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

@Entity
@Table(name = "d_app_container_instance")
public class ContainerInstance extends IdEntity {
	@Column(unique = true)
	private String name;
	@Column
	private int port;

	@Column
	private int seq;
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(columnDefinition = "INT(11) DEFAULT 0")
	private Integer xPos;
	@Column(columnDefinition = "INT(11) DEFAULT 0")
	private Integer yPos;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "master_id")
	private DHost master;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "container_id")
	private Container container;

	@SuppressWarnings("unused")
	private ContainerInstance() {
	}

	public ContainerInstance(int seq, String name, int port, int xPos,
			int yPos, Container container) {
		this.seq = seq;
		this.name = name;
		this.port = port;
		this.status = Status.CREATED;
		this.xPos = xPos;
		this.yPos = yPos;
		this.container = container;
	}

	public enum Status {
		CREATED, RUNNING, STOPPED, ERROR, REMOVED
	}

	public enum Operation {
		START, STOP, REMOVE, COPY, FAIL
	}

	public String getName() {
		return name;
	}

	public int getPort() {
		return port;
	}

	public Status getStatus() {
		return status;
	}

	public Integer getxPos() {
		return xPos;
	}

	public Integer getyPos() {
		return yPos;
	}

	public Container getContainer() {
		return container;
	}

	public int getSeq() {
		return seq;
	}

	public void changeStatusToRunning() {
		status = Status.RUNNING;
	}

	public void changeStatusToStop() {
		status = Status.STOPPED;
	}

	public void changeStatusToError() {
		status = Status.ERROR;
	}

	public DHost getMaster() {
		return master;
	}

	public void setMaster(DHost master) {
		this.master = master;
	}
}
