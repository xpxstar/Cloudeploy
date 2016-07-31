package cn.ac.iscas.cloudeploy.v2.model.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.exception.NotFoundException;
import cn.ac.iscas.cloudeploy.v2.model.dao.user.UserDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.user.UserTokenDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.UserToken;
import cn.ac.iscas.cloudeploy.v2.model.service.BasicService;
import cn.ac.iscas.cloudeploy.v2.user.security.SecurityUtils;

@Service
public class DefaultUserServiceImpl extends BasicService implements UserService {

	@Autowired
	private UserDAO uDAO;

	@Autowired
	private UserTokenDAO tokenDAO;

	@Override
	public User findUserByToken(String tokenContent) {
		UserToken token = tokenDAO.findByContent(tokenContent);
		if (token == null) {
			throw new NotFoundException("user not found exception");
		}
		return token.getUser();
	}

	@Override
	public User findUserById(Long id) {
		return (User) verifyResourceExists(uDAO.findOne(id));
	}

	@Override
	public User getCurrentUser() {
		if (SecurityUtils.getSubject() != null) {
			return SecurityUtils.getSubject().getUser();
		}
		throw new NotFoundException("no current user found");
	}

}
