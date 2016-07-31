package cn.ac.iscas.cloudeploy.v2.model.service.script.factory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import cn.ac.iscas.cloudeploy.rmi.DeployService;
import cn.ac.iscas.cloudeploy.rmi.entity.Agent;
import cn.ac.iscas.cloudeploy.rmi.entity.Result;
import cn.ac.iscas.cloudeploy.v2.model.graph.EdgeType;
import cn.ac.iscas.cloudeploy.v2.model.graph.Graph;
import cn.ac.iscas.cloudeploy.v2.model.service.file.FileService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.ScriptService;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;
import cn.ac.iscas.cloudeploy.v2.util.Constants;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration("/application_context.xml")
@Transactional
@Ignore
public class OperationDefineTest {
	private static final int SUCCESS_CODE = 2000;
	private String serverHost;
	private int serverPort;
	private String host;
	@Autowired
	private ToolFactory toolFactory;
	
	@Autowired
	@Qualifier("edgeTypedGraphScriptService")
	private ScriptService scriptService;
	
	@Autowired
	private FileService fileService;
	
	@Before
	public void setup(){
		serverHost="133.133.10.29";
		serverPort=9090;
		host="133.133.134.183";
	}
	
	@Test
//	@Ignore
	public void TestOperationDefineByServer() throws FileNotFoundException {
		toolFactory.getAllProtype();
		List<Operation> operations = toolFactory.produceOperations("src/main/resources/operationDefine/tomcat_define.yaml");
		Graph<Operation, EdgeType> graph;
		for (Operation operation : operations) {
			operation.setNodeName("operationDefine_node");
			graph=new Graph<Operation, EdgeType>();
			graph.addVertex(operation);
			String scripts=scriptService.createTask(graph, host);
			try {
				 ByteSource byteSource = fileService.findFile(scripts);
				 CharSource charSource = byteSource.asCharSource(Charset
							.forName(Constants.FILE_DEFAULT_CHARSET));
				 applyExecutable(host,charSource.read(),serverHost,serverPort);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static boolean applyExecutable(String hostName,String script,String serverHost,int serverPort) {
		Agent agent = new Agent(hostName,hostName);
		try {
			TTransport transport;
			transport = new TSocket(serverHost, serverPort);
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			DeployService.Client client = new DeployService.Client(protocol);
			Result result = client.applyExecutable(agent, script);
			transport.close();
			System.out.println(result.code);
			System.out.println(result.info);
			if (result.code != SUCCESS_CODE) {
				return false;
			}
			return true;
		} catch (TException x) {
			x.printStackTrace();
			return false;
		}
	}
}
