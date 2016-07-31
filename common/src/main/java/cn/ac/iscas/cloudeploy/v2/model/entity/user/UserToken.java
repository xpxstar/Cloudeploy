package cn.ac.iscas.cloudeploy.v2.model.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import cn.ac.iscas.cloudeploy.v2.util.Uniques;

@Entity
@Table(name = "d_user_token")
public class UserToken extends IdEntity {

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(unique = true, nullable = false)
	private String content;

	public UserToken(){
		this.content = Uniques.getUniqueString();
	}
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public void refreshContent() {
		this.content = Uniques.getUniqueString();
	}
}
