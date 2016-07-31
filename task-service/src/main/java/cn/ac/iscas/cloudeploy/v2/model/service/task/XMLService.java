package cn.ac.iscas.cloudeploy.v2.model.service.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;

import cn.ac.iscas.cloudeploy.v2.config.ConfigKeys;
import cn.ac.iscas.cloudeploy.v2.config.ConfigService;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ActionDAO;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.host.HostService;
import cn.ac.iscas.cloudeploy.v2.util.RandomUtils;

@Service
public class XMLService {
	@Autowired
	private HostService hostService;

	@Autowired
	private ActionDAO actionDAO;

	@Autowired
	private ConfigService configService;

	@Autowired
	private FileService fileService;

	public String taskToXML(TaskViews.Graph task) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("task");
		root.addAttribute("name", task.taskName);
		addOperationsToTask(task.nodes, task.edges, root);
		OutputFormat format = new OutputFormat("    ", true);
		try {
			String tmpFilePath = configService
					.getConfigAsString(ConfigKeys.SERVICE_FILE_TMP_ROOT);
			File tmpRoot = new File(tmpFilePath);
			FileUtils.forceMkdir(tmpRoot);
			File file = new File(tmpRoot, RandomUtils.randomString());
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			writer.write(doc);
			return fileService.saveFile(Files.asByteSource(file));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void addOperationsToTask(List<TaskViews.Node> nodes,
			List<TaskViews.Edge> edges, Element task) {
		Map<Long, List<Long>> dependencyCache = getDependencies(edges);
		for (TaskViews.Node node : nodes) {
			Element operation = task.addElement("operation");
			operation.addAttribute("id", String.valueOf(node.id));
			operation.addAttribute("name", actionDAO.findOne(node.actionId)
					.getName());
			if (node.params != null && node.params.size() > 0) {
				addParametersToOperation(node.params, operation);
			}

			if (node.hostIds != null && node.hostIds.size() > 0) {
				addHostsToOperation(node.hostIds, operation);
			}

			if (dependencyCache.size() > 0
					&& dependencyCache.get(node.id) != null) {

				Element dependencies = operation.addElement("dependencies");
				for (Long id : dependencyCache.get(node.id)) {
					Element dependency = dependencies.addElement("dependency");
					dependency.addText(String.valueOf(id));
				}
			}
		}
	}

	private void addParametersToOperation(List<TaskViews.Param> params,
			Element operation) {
		Element parameters = operation.addElement("parameters");
		for (TaskViews.Param param : params) {
			Element parameter = parameters.addElement("parameter");
			parameter.addAttribute("key", param.key);
			parameter.addText(param.value);
		}
	}

	private void addHostsToOperation(List<Long> hostIds, Element operation) {
		Element hosts = operation.addElement("hosts");
		for (Long hostId : hostIds) {
			Element host = hosts.addElement("host");
			host.addText(hostService.findDHost(hostId).getHostIP());
		}
	}

	private Map<Long, List<Long>> getDependencies(List<TaskViews.Edge> edges) {
		Map<Long, List<Long>> res = new HashMap<Long, List<Long>>();
		for (TaskViews.Edge edge : edges) {
			if (res.get(edge.to) == null) {
				res.put(edge.to, new ArrayList<Long>());
			}
			res.get(edge.to).add(edge.from);
		}
		return res;
	}

}
