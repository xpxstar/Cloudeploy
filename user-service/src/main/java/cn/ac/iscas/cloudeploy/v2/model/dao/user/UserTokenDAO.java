package cn.ac.iscas.cloudeploy.v2.model.dao.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.user.UserToken;

public interface UserTokenDAO extends PagingAndSortingRepository<UserToken, Long> {
	UserToken findByContent(String content);
}
