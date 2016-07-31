package cn.ac.iscas.cloudeploy.v2.model.service.proxy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudeploy.rmi.DeployService;
import cn.ac.iscas.cloudeploy.rmi.entity.Agent;
import cn.ac.iscas.cloudeploy.rmi.entity.Result;
import cn.ac.iscas.cloudeploy.v2.config.ConfigKeys;
import cn.ac.iscas.cloudeploy.v2.config.ConfigService;
import cn.ac.iscas.cloudeploy.v2.exception.BadRequestException;
import cn.ac.iscas.cloudeploy.v2.exception.InternalServerErrorException;
import cn.ac.iscas.cloudeploy.v2.exception.NotFoundException;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Action;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ActionParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.ImportAction;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.model.graph.EdgeType;
import cn.ac.iscas.cloudeploy.v2.model.graph.Graph;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.ScriptService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.ParamValue;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ParamType;
import cn.ac.iscas.cloudeploy.v2.util.Constants;
import cn.ac.iscas.cloudeploy.v2.util.RandomUtils;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

@Service
public class DefaultProxyServiceImpl implements ProxyService {
	private static final int SUCCESS_CODE = 2000;

	private String serverHost;

	private int serverPort;

	@Autowired
	private ConfigService configService;

	@Autowired
	private FileService fileService;

	@Autowired
	@Qualifier("edgeTypedGraphScriptService")
	private ScriptService scriptService;

	@PostConstruct
	private void init() {
		serverHost = configService
				.getConfigAsString(ConfigKeys.DEPLOY_SERVER_HOST);
		serverPort = configService
				.getConfigAsInt(ConfigKeys.DEPLOY_SERVER_PORT);
	}

	@Override
	public void applyExecutable(DHost host, String executableKey) {
		if (host == null){
			throw new BadRequestException("unknown host");
		}
		Agent agent = new Agent(host.getHostName(), host.getHostIP());
		try {
			TTransport transport;
			transport = new TSocket(serverHost, serverPort);
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			DeployService.Client client = new DeployService.Client(protocol);
			
			ByteSource byteSource = fileService.findFile(executableKey);
			CharSource charSource = byteSource.asCharSource(Charset
					.forName(Constants.FILE_DEFAULT_CHARSET));
			Result result = client.applyExecutable(agent, charSource.read());
			transport.close();
			System.out.println(result.code);
			if (result.code != SUCCESS_CODE) {
				throw new InternalServerErrorException(
						"fail to apply the operation on remote host");
			}
		} catch (TException x) {
			x.printStackTrace();
			throw new InternalServerErrorException(
					"fail to apply the operation on remote host");
		} catch (IOException x) {
			throw new NotFoundException("operation definition not found");
		}
	}

	private List<Operation> getImports(Action action) {
		List<Operation> imports = new ArrayList<>();
		List<ImportAction> localImports = action.getImports();
		if (localImports != null) {
			for (ImportAction impAction : localImports) {
				Action ia = impAction.getImportedAction();
				Operation operation = new Operation();
				operation.setName(ia.getName());
				operation.setNodeName(RandomUtils.randomString());
				Map<String, ParamValue> params = new HashMap<>();
				// use the default values
				for (ActionParam p : ia.getParams()) {
					ParamValue value = new ParamValue();
					value.setValue(p.getDefaultValue());
					value.setType(ParamType.typeByString(p.getParamType()));
					params.put(p.getParamKey(), value);
				}
				operation.setParamsWithType(params);
				operation.setDefineMd5(ia.getDefineFileKey());
				operation.setImports(getImports(ia));
				imports.add(operation);
			}
		}
		return imports;
	}

	@Override
	public void applyActionOnHost(Action action, Map<String, String> params,
			DHost host) {
		if (action == null || host == null) {
			throw new BadRequestException("unknown operation or host");
		}
		Operation operation = new Operation();
		operation.setName(action.getName());
		operation.setNodeName(RandomUtils.randomString());
		operation.setDefineMd5(action.getDefineFileKey());
		operation.setImports(getImports(action));
		Map<String, ParamValue> operationParams = new HashMap<>();
		for (ActionParam ap : action.getParams()) {
			ParamValue pv = new ParamValue(ap.getDefaultValue(),
					ParamType.typeByString(ap.getParamType()));
			operationParams.put(ap.getParamKey(), pv);
		}
		for (Entry<String, String> entry : params.entrySet()) {
			if (operationParams.containsKey(entry.getKey())) {
				ParamValue pv = operationParams.get(entry.getKey());
				pv.setValue(entry.getValue());
			}
		}
		operation.setParamsWithType(operationParams);
		Graph<Operation, EdgeType> graph = new Graph<>();
		graph.addVertex(operation);

		String scriptFileKey = scriptService
				.createTask(graph, host.getHostIP());
		applyExecutable(host, scriptFileKey);
	}

}
