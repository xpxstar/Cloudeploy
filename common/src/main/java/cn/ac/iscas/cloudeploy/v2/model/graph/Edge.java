package cn.ac.iscas.cloudeploy.v2.model.graph;

public class Edge<Vertex,Edge_Type> {
	private Vertex source;
	private Vertex target;
	private Edge_Type type;
	public Edge(Vertex sourceVertex, Vertex targetVertex, Edge_Type type) {
		this.source=sourceVertex;
		this.target=targetVertex;
		this.type=type;
	}
	public Vertex getSource() {
		return source;
	}
	public void setSource(Vertex source) {
		this.source = source;
	}
	public Vertex getTarget() {
		return target;
	}
	public void setTarget(Vertex target) {
		this.target = target;
	}
	public Edge_Type getType() {
		return type;
	}
	public void setType(Edge_Type type) {
		this.type = type;
	}
}
