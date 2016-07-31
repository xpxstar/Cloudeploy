package cn.ac.iscas.cloudeploy.v2.model.entity.component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_action_param")
public class ActionParam extends IdEntity {
	private String paramKey;
	@Column(columnDefinition = "varchar(1000)")
	private String defaultValue;
	private String paramType;
	private String description;

	@Column(nullable = false, columnDefinition = "varchar(20) DEFAULT 'view:text'")
	private String viewType;
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "action_id")
	private Action action;

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}
}
