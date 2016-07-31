package cn.ac.iscas.cloudeploy.v2.model.graph;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimaps;


public class Graph<Vertex,Edge_Type> {
	private LinkedHashMultimap<Vertex,Edge<Vertex, Edge_Type>> vertexs_outCommingMap;
//	private LinkedHashMultimap<Vertex, Edge<Vertex, Edge_Type>> vertexs_inCommingMap;
	public Graph(){
		vertexs_outCommingMap=LinkedHashMultimap.create();
//		vertexs_inCommingMap=LinkedHashMultimap.create();
	}
	
	/**
	 * Adds the specified edge to this graph, going from the source vertex to the target vertex. if targetVertex not exist, it will auto create
	 * a new vertex for targetVertex.
	 * @param sourceVertex
	 * @param targetVertex
	 * @param type
	 * @return
	 */
	public Edge<Vertex,Edge_Type> addEdge(Vertex sourceVertex,Vertex targetVertex,Edge_Type type){	
		Edge<Vertex, Edge_Type> edge=new Edge<Vertex, Edge_Type>(sourceVertex,targetVertex,type);
		vertexs_outCommingMap.put(sourceVertex, edge);
		if(targetVertex!=null&&!vertexs_outCommingMap.containsKey(targetVertex)){
			addVertex(targetVertex);
		}
//		vertexs_inCommingMap.put(targetVertex, edge);
		return edge;
	}
	
	/**
	 * Adds the specified vertex to this graph
	 * @param vertex
	 * @return
	 */
	public void addVertex(Vertex vertex){
		addEdge(vertex, null, null);
	}
	/**
	 * Returns a set of all edges connecting source vertex
	 * @param sourceVertex
	 * @return
	 */
	public Set<Edge<Vertex, Edge_Type>> getAllEdges(Vertex sourceVertex){
		return vertexs_outCommingMap.get(sourceVertex);
	}
	
	public Map<Vertex, Set<Edge<Vertex, Edge_Type>>> getAllVertexEdges(){
		return Multimaps.asMap(vertexs_outCommingMap);
	}

	public int size(){
		return vertexs_outCommingMap.size();
	}
	
}
