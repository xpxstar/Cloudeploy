package cn.ac.iscas.cloudeploy.v2.model.service.resource;

import cn.ac.iscas.cloudeploy.v2.util.Constants;
import cn.ac.iscas.cloudeploy.v2.util.DeployUtils;

public class BasicResourceService {
	protected String getActionKey(String actionName) {
		return DeployUtils.getLastSegmentsOfName(actionName, 2,
				Constants.NAME_SEPARATOR, Constants.JSON_CONFIG_PATH_SEPARATOR);
	}
}
