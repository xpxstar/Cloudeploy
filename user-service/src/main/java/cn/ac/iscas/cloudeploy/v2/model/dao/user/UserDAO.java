package cn.ac.iscas.cloudeploy.v2.model.dao.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;

public interface UserDAO extends PagingAndSortingRepository<User, Long> {
}
