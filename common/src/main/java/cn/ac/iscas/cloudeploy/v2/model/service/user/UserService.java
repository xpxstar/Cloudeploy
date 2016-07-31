package cn.ac.iscas.cloudeploy.v2.model.service.user;

import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;

public interface UserService {

	public User findUserByToken(String tokenContent);
	
	public User findUserById(Long id);
	
	public User getCurrentUser();

}
