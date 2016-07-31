package cn.ac.iscas.cloudeploy.v2.model.service.resource.exec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.config.ConfigKeys;
import cn.ac.iscas.cloudeploy.v2.config.ConfigService;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.resource.exec.ExecDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.exec.Exec;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.model.service.resource.ApplyService;
import cn.ac.iscas.cloudeploy.v2.model.service.resource.BasicResourceService;
import cn.ac.iscas.cloudeploy.v2.model.service.user.UserService;

@Service
public class ExecService extends BasicResourceService{
	@Autowired
	private ExecDAO execDAO;

	@Autowired
	private UserService userService;

	@Autowired
	private HostService hostService;

	@Autowired
	private ConfigService configService;

	@Autowired
	private ApplyService applyService;

	@Autowired
	private ActionDAO actionDAO;

	public Exec createExec(Long userId, String name, String content) {
		return createExec(userService.findUserById(userId), name, content);
	}

	public Exec createExec(User user, String name, String content) {
		Exec exec = new Exec();
		exec.setName(name);
		exec.setContent(content);
		exec.setUser(user);
		return execDAO.save(exec);
	}

	public List<Exec> findExecs(Long userId) {
		return execDAO.findByUser(userId);
	}

	public List<Exec> findExecs(User user) {
		return findExecs(user.getId());
	}

	public Exec updateExec(Long execId, String content) {
		Exec exec = execDAO.findOne(execId);
		exec.setContent(content);
		return execDAO.save(exec);
	}

	public void removeExec(Long execId) {
		execDAO.delete(execId);
	}

	/**
	 * 执行脚本
	 * 
	 * @param user
	 * @param hostId
	 * @param params
	 */
	public void runExec(User user, Long hostId, Map<String, String> params) {
		Action action = actionDAO.findByName(configService
				.getConfigAsString(ConfigKeys.ACTION_NAME_EXEC_RUN));
		DHost host = hostService.findDHostByIdAndUser(hostId, user.getId());
		applyService.applyActionOnHost(action, params, host);
	}

	public void runExec(User user, Long hostId, String[] values) {
		String actionName = configService
				.getConfigAsString(ConfigKeys.ACTION_NAME_EXEC_RUN);
		List<String> keys = configService
				.getParamConfigAsString(getActionKey(actionName));
		Map<String, String> params = new HashMap<String, String>();
		for (int i = 0, j = 0; i < keys.size() && j < values.length; i++, j++) {
			params.put(keys.get(i), values[j]);
		}
		runExec(user, hostId, params);
	}

}
