package cn.ac.iscas.cloudeploy.v2.model.entity.application;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_app_relation")
public class Relation extends IdEntity {
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "from_id")
	private Container from;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "to_id")
	private Container to;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "app_id")
	private Application application;

	@SuppressWarnings("unused")
	private Relation() {

	}

	public Relation(Container from, Container to, Application application) {
		this.from = from;
		this.to = to;
		this.application = application;
	}

	public Container getFrom() {
		return from;
	}

	public Container getTo() {
		return to;
	}

	public Application getApplication() {
		return application;
	}

}
