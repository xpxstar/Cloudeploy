package cn.ac.iscas.cloudeploy.v2.model.service.resource.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.v2.config.ConfigKeys;
import cn.ac.iscas.cloudeploy.v2.config.ConfigService;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.resource.file.CustomFileDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.resource.file.RemoteFileDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.file.CustomFile;
import cn.ac.iscas.cloudeploy.v2.model.entity.resource.file.RemoteFile;
import cn.ac.iscas.cloudeploy.v2.model.entity.user.User;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.model.service.resource.ApplyService;
import cn.ac.iscas.cloudeploy.v2.model.service.resource.BasicResourceService;
import cn.ac.iscas.cloudeploy.v2.model.service.resource.exec.ExecService;
import cn.ac.iscas.cloudeploy.v2.model.service.user.UserService;

@Service
public class FileResourceService extends BasicResourceService {
	@Autowired
	private CustomFileDAO customFileDAO;

	@Autowired
	private RemoteFileDAO remoteFileDAO;

	@Autowired
	private UserService userService;

	@Autowired
	private HostService hostService;

	@Autowired
	private ConfigService configService;

	@Autowired
	private ExecService execService;

	@Autowired
	private FileService fileService;

	@Autowired
	private ApplyService applyService;

	@Autowired
	private ActionDAO actionDAO;

	public CustomFile createCustomFile(Long userId, String name, String fileKey) {
		return createCustomFile(userService.findUserById(userId), name, fileKey);
	}

	public CustomFile createCustomFile(User user, String name, String fileKey) {
		CustomFile file = new CustomFile();
		file.setName(name);
		file.setFileMd5(fileKey);
		file.setUser(user);
		return customFileDAO.save(file);
	}

	public List<CustomFile> findCustomFiles(Long userId) {
		return customFileDAO.findByUser(userId);
	}

	public List<CustomFile> findCustomFiles(User user) {
		return findCustomFiles(user.getId());
	}

	public CustomFile updateCustomFile(Long fileId, String fileKey) {
		CustomFile file = customFileDAO.findOne(fileId);
		file.setFileMd5(fileKey);
		return customFileDAO.save(file);
	}

	public void removeCustomFile(Long fileId) {
		customFileDAO.delete(fileId);
	}

	public RemoteFile createRemoteFile(Long userId, String name, String path,
			Long hostId) {
		return createRemoteFile(userService.findUserById(userId), name, path,
				hostId);
	}

	public RemoteFile createRemoteFile(User user, String name, String path,
			Long hostId) {
		RemoteFile file = new RemoteFile();
		file.setName(name);
		file.setPath(path);
		file.setUser(user);
		file.setHost(hostService.findDHost(hostId));
		return remoteFileDAO.save(file);
	}

	public List<RemoteFile> findRemoteFiles(Long userId) {
		return remoteFileDAO.findByUser(userId);
	}

	public List<RemoteFile> findRemoteFilesByHost(Long hostId) {
		return remoteFileDAO.findByHost(hostId);
	}

	public List<RemoteFile> findRemoteFiles(User user) {
		return findRemoteFiles(user.getId());
	}

	public RemoteFile updateRemoteFile(Long fileId, String path) {
		RemoteFile file = remoteFileDAO.findOne(fileId);
		file.setPath(path);
		return remoteFileDAO.save(file);
	}

	public void removeRemoteFile(Long fileId) {
		remoteFileDAO.delete(fileId);
	}

	/**
	 * pull remote file to cloudeploy
	 * 
	 * @param remoteFileId
	 * @param accessKey
	 * @param user
	 */
	public void sendFileRequest(Long remoteFileId, String accessKey, User user) {
		RemoteFile remoteFile = remoteFileDAO.findOne(remoteFileId);
		String curlRequest = String
				.format(configService
						.getConfigAsString(ConfigKeys.SERVICE_FILE_TMP_UPLOAD_REQUEST_FORMAT),
						remoteFile.getPath(), accessKey, configService
								.getConfigAsString(ConfigKeys.SERVER_ROOT));

		execService.runExec(user, remoteFile.getHost().getId(),
				new String[] { curlRequest });
	}

	/**
	 * replace remote file
	 * 
	 * @param remoteFileId
	 * @param fileKey
	 */
	public void pushFileToRemote(Long remoteFileId, String fileKey) {
		RemoteFile remoteFile = remoteFileDAO.findOne(remoteFileId);
		String actionName = configService
				.getConfigAsString(ConfigKeys.ACTION_NAME_FILE_UPLOAD);
		List<String> keys = configService
				.getParamConfigAsString(getActionKey(actionName));
		Map<String, String> params = new HashMap<String, String>();
		String[] values = new String[] {
				fileService.generateDownloadURL(fileKey), remoteFile.getPath(),
				"true" };
		for (int i = 0, j = 0; i < keys.size() && j < values.length; i++, j++) {
			params.put(keys.get(i), values[j]);
		}
		applyService.applyActionOnHost(actionDAO.findByName(actionName),
				params, remoteFile.getHost());
	}

}
