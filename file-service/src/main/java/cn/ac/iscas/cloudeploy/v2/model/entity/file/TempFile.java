package cn.ac.iscas.cloudeploy.v2.model.entity.file;

import javax.persistence.Entity;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_temp_file")
public class TempFile extends IdEntity {
	private String accessKey;

	// md5 link to file source
	private String link;
	private Long timeToLive = 1000 * 60 * 5L;

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Long getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(Long timeToLive) {
		this.timeToLive = timeToLive;
	}
}
