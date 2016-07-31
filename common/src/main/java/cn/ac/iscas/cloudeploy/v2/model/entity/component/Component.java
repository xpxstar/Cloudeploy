package cn.ac.iscas.cloudeploy.v2.model.entity.component;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_component")
public class Component extends IdEntity {
	public Component() {
	}
	public Component(Long id) {
		this.id = id;
	}

	@Transient
	public static final String IDENTIFIER_SEPARATOR = ",,";

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "component_type_id")
	private ComponentType type;
	private String name;
	private String displayName;
	private Long userId;

	@OneToMany(mappedBy = "component")
	private List<Action> actions;

	@Column(nullable = false, columnDefinition = "INT DEFAULT 0")
	private Boolean repeatable;

	// separated by IDENTIFIER_SEPARATOR
	private String identifiers;
	
	private String defineFileKey;

	public ComponentType getType() {
		return type;
	}

	public void setType(ComponentType type) {
		this.type = type;
	}

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

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public boolean isRepeatable() {
		return repeatable;
	}

	public void setRepeatable(boolean repeatable) {
		this.repeatable = repeatable;
	}

	public String getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(String identifiers) {
		this.identifiers = identifiers;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Boolean getRepeatable() {
		return repeatable;
	}

	public void setRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;
	}
	public String getDefineFileKey() {
		return defineFileKey;
	}
	public void setDefineFileKey(String defineFileKey) {
		this.defineFileKey = defineFileKey;
	}
	//for topologyTemplate transform to Task, this method return the install action of this component;
//	public Action getAction(String regex) {
//		for(Action action : actions){
//			if(action.getName().contains(regex)){
//				return action;
//			}
//		}
//		return null;
//	}
}
