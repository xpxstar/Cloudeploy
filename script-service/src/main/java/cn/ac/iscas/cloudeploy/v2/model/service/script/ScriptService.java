package cn.ac.iscas.cloudeploy.v2.model.service.script;

import java.util.List;
import java.util.Map;

import cn.ac.iscas.cloudeploy.v2.model.graph.EdgeType;
import cn.ac.iscas.cloudeploy.v2.model.graph.Graph;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Node;
import cn.ac.iscas.cloudeploy.v2.model.service.script.entity.Operation;

/**
 * 
 * Using for transform a graph to a executable script
 *
 * @author RichardLcc
 * @date 2015年1月28日-下午3:10:26
 */
public interface ScriptService {
	/**
	 * create a new operation for a component
	 * @param graph
	 * @param operationName name of the new operation
	 * @param extractParams parameters of the new operation, but this parameters must come from the
	 * operation or node in the graph.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Operation createOperation(Graph<Node, EdgeType> graph,String operationName,Map extractParams);
	
	/**
	 * create a new operation for a component using a adjacency lists graph
	 * @param graph a adjacency lists graph
	 * @param operationName name of the new operation
	 * @param extractParams parameters of the new operation, but this parameters must come from the
	 * operation or node in the graph.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Operation createOperation(List<List<? extends Node>> graph,
			String operationName, Map extractParams);
    /**
     * 
     * Use this graph to create a executable task script for the host
     * @param graph
     * @param hostname
     * @return md5 key of the task script file
     */
	public String createTask(Graph<Operation, EdgeType> graph,String hostname);
	
	/**
     * 
     * Use a adjacency lists graph to create a executable task script for the host 
     * @param graph a adjacency lists graph
     * @param hostname the host you want to configure
     * @return md5 key of the task script file
     */
	public String createTask(List<List<Operation>> graph, String hostname);
	
	/**
	 * analysis a module, get all define,class and their parameters defined in this module.
	 * @param moduleFilePath the absolute path of this module
	 * @param moduleName the name of this module
	 * @return a node list
	 */
	public List<Node> analysisModule(String moduleFilePath,String moduleName);

	
}
