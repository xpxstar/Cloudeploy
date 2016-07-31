package cn.ac.iscas.cloudeploy.v2.controller.host;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ac.iscas.cloudeploy.v2.controller.BasicController;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.host.HostViews;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.util.Constants;

@Controller
@Transactional
@RequestMapping(value = { "v2/hosts" })
public class HostController extends BasicController {

	@Autowired
	private HostService hostService;

	/**
	 * get the managed host list
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<HostViews.Item> getHostList(
			@RequestHeader(Constants.HTTP_HEADERS_TOKEN) String token) {
		return HostViews.listViewOf(hostService.findDHostsByUser(currentUser()
				.getId()));
	}
}
