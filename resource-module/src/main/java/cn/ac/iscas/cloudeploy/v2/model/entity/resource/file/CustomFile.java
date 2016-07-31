package cn.ac.iscas.cloudeploy.v2.model.entity.resource.file;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "d_custom_file")
public class CustomFile extends BasicFileResource {
	private String fileMd5;

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}
}
