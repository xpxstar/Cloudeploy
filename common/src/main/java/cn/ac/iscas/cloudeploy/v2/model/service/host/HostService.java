package cn.ac.iscas.cloudeploy.v2.model.service.host;

import java.util.List;

import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;

public interface HostService {

	public DHost findDHost(Long dHostId);

	public List<DHost> findDHostsByUser(Long userId);

	public DHost findDHostByIdAndUser(Long hostId, Long userId);

	public List<DHost> findDHostsByIdsAndUser(List<Long> hostIds, Long userId);
	
	public DHost findByName(String hostName);
	
	public DHost findByHostIp(String hostIP);
}
