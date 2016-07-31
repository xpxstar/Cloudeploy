package cn.ac.iscas.cloudeploy.v2.model.entity.component;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_import_action")
public class ImportAction extends IdEntity{
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "action_id")
	private Action action;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "imported_action_id")
	private Action importedAction;

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Action getImportedAction() {
		return importedAction;
	}

	public void setImportedAction(Action importedAction) {
		this.importedAction = importedAction;
	}
}
