package cn.ac.iscas.cloudeploy.v2.controller.resource;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ac.iscas.cloudeploy.v2.controller.BasicController;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.resource.FileResourceViews;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.resource.FileResourceViews.CustomFileItem;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.resource.FileResourceViews.RemoteFileItem;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.service.resource.file.FileResourceService;
import cn.ac.iscas.cloudeploy.v2.user.security.authorization.CheckPermission;
import cn.ac.iscas.cloudeploy.v2.util.Constants;

@Controller
@Transactional
@RequestMapping(value = "v2/resources/files")
public class FileResourceController extends BasicController {
	@Autowired
	private FileResourceService fileResourceService;

	@RequestMapping(value = { "custom" }, method = RequestMethod.POST)
	@ResponseBody
	public CustomFileItem createCustomFile(
			@RequestHeader(Constants.HTTP_HEADERS_TOKEN) String token,
			@RequestParam("name") String name,
			@RequestParam("fileKey") String fileKey) {
		return FileResourceViews.customViewOf(fileResourceService
				.createCustomFile(currentUser(), name, fileKey));
	}

	@RequestMapping(value = { "custom" }, method = RequestMethod.GET)
	@ResponseBody
	public List<CustomFileItem> getCustomFile(
			@RequestHeader(Constants.HTTP_HEADERS_TOKEN) String token) {
		return FileResourceViews.customListViewOf(fileResourceService
				.findCustomFiles(currentUser()));
	}

	@RequestMapping(value = { "custom/{fileId}" }, method = RequestMethod.PUT)
	@ResponseBody
	public CustomFileItem updateCustomFile(@PathVariable("fileId") Long fileId,
			@RequestParam("fileKey") String fileKey) {
		return FileResourceViews.customViewOf(fileResourceService
				.updateCustomFile(fileId, fileKey));
	}

	@RequestMapping(value = { "custom/{fileId}" }, method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, String> deleteCustomFile(
			@PathVariable("fileId") Long fileId) {
		fileResourceService.removeCustomFile(fileId);
		return SUCCESS;
	}

	@RequestMapping(value = { "remote" }, method = RequestMethod.POST)
	@ResponseBody
	@CheckPermission(resources = { DHost.PERMISSION_KEY })
	public RemoteFileItem createRemoteFile(
			@RequestHeader(Constants.HTTP_HEADERS_TOKEN) String token,
			@RequestParam("name") String name,
			@RequestParam("remotePath") String path,
			@RequestParam(Constants.HTTP_REQUEST_PARAM_HOST_ID) Long hostId) {
		return FileResourceViews.remoteViewOf(fileResourceService
				.createRemoteFile(currentUser(), name, path, hostId));
	}

	@RequestMapping(value = { "remote" }, method = RequestMethod.GET)
	@ResponseBody
	@CheckPermission(resources = { DHost.PERMISSION_KEY })
	public List<RemoteFileItem> getRemoteFile(
			@RequestHeader(Constants.HTTP_HEADERS_TOKEN) String token,
			@RequestParam(Constants.HTTP_REQUEST_PARAM_HOST_ID) Long hostId) {
		return FileResourceViews.remoteListViewOf(fileResourceService
				.findRemoteFilesByHost(hostId));
	}

	@RequestMapping(value = { "remote/{remoteFileId}/pull" }, method = RequestMethod.POST)
	@ResponseBody
	public Object pullFileFromRemoteHost(
			@PathVariable("remoteFileId") Long fileId,
			@RequestParam("accessKey") String accessKey) {
		fileResourceService.sendFileRequest(fileId, accessKey, currentUser());
		return SUCCESS;
	}

	@RequestMapping(value = { "remote/{remoteFileId}/push" }, method = RequestMethod.POST)
	@ResponseBody
	public Object pushFileToRemoteHost(
			@PathVariable("remoteFileId") Long fileId,
			@RequestParam("fileKey") String fileKey) {
		fileResourceService.pushFileToRemote(fileId, fileKey);
		return SUCCESS;
	}

	@RequestMapping(value = { "remote/{fileId}" }, method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, String> deleteRemoteFile(
			@PathVariable("fileId") Long fileId) {
		fileResourceService.removeRemoteFile(fileId);
		return SUCCESS;
	}
}
