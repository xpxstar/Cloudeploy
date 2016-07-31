package cn.ac.iscas.cloudeploy.v2.model.entity.component;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_action")
public class Action extends IdEntity {
	@Column(unique = true)
	private String name;
	private String displayName;
	private String defineFileKey;
	
	@OneToMany(mappedBy = "action")
	private List<ActionParam> params;

	@ManyToMany(mappedBy = "action")
	private List<ImportAction> imports;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "component_id")
	private Component component;

	@Column(nullable = false, columnDefinition = "INT DEFAULT 1")
	private Boolean showInResourceView;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<ActionParam> getParams() {
		return params;
	}

	public void setParams(List<ActionParam> params) {
		this.params = params;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public String getDefineFileKey() {
		return defineFileKey;
	}

	public void setDefineFileKey(String defineFileKey) {
		this.defineFileKey = defineFileKey;
	}

	public List<ImportAction> getImports() {
		return imports;
	}

	public void setImports(List<ImportAction> imports) {
		this.imports = imports;
	}

	public Boolean getShowInResourceView() {
		return showInResourceView;
	}

	public void setShowInResourceView(Boolean showInResourceView) {
		this.showInResourceView = showInResourceView;
	}
}
