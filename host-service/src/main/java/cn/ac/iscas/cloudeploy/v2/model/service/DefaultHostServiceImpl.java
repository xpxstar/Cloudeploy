package cn.ac.iscas.cloudeploy.v2.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.model.dao.host.DHostDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;

@Service
public class DefaultHostServiceImpl extends BasicService implements HostService {
	@Autowired
	private DHostDAO dHostDAO;

	@Override
	public DHost findDHost(Long dHostId) {
		return (DHost) verifyResourceExists(dHostDAO.findOne(dHostId));
	}

	@Override
	public List<DHost> findDHostsByUser(Long userId) {
		return dHostDAO.findByUser(userId);
	}

	@Override
	public DHost findDHostByIdAndUser(Long hostId, Long userId) {
		return (DHost) verifyResourceExists(dHostDAO.findByIdAndUser(hostId,
				userId));
	}

	@Override
	public List<DHost> findDHostsByIdsAndUser(List<Long> hostIds, Long userId) {
		return dHostDAO.findByIdsAndUser(hostIds, userId);
	}

	@Override
	public DHost findByName(String hostName) {
		return (DHost) verifyResourceExists(dHostDAO.findByHostName(hostName));
	}

	@Override
	public DHost findByHostIp(String hostIP) {
		return dHostDAO.findByHostIP(hostIP);
	}
}
