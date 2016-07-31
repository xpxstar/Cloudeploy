package cn.ac.iscas.cloudeploy.v2.model.entity.typeTrans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "d_typeImplemention")
public class TypeImplemention extends IdEntity{
	private String nodeType;
	private String startegy;
	private String interfaceFilename;
	private String interfaceName;
	private String definedFile;
	@Column(columnDefinition = "TEXT")
	private String metaJson;
	
	public String getDefinedFile() {
		return definedFile;
	}
	public void setDefinedFile(String definedFile) {
		this.definedFile = definedFile;
	}
	public String getStartegy() {
		return startegy;
	}
	public void setStartegy(String startegy) {
		this.startegy = startegy;
	}
	public String getInterfaceFilename() {
		return interfaceFilename;
	}
	public void setInterfaceFilename(String interfaceFilename) {
		this.interfaceFilename = interfaceFilename;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getMetaJson() {
		return metaJson;
	}
	public void setMetaJson(String metaJson) {
		this.metaJson = metaJson;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodetype) {
		this.nodeType = nodetype;
	}
}
