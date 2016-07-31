package cn.ac.iscas.cloudeploy.v2.model.entity.file;

import javax.persistence.Entity;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_file_source")
public class FileSource extends IdEntity {
	private String md5;
	private String credential;
	private String typ;

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}
	
}
