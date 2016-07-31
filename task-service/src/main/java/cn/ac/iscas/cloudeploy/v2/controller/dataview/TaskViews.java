package cn.ac.iscas.cloudeploy.v2.controller.dataview;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews.Graph;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews.Node;
import cn.ac.iscas.cloudeploy.v2.controller.dataview.TaskViews.Param;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.NodeHostRelation;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.NodeParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.Task;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.TaskEdge;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.TaskEdge.Relation;
import cn.ac.iscas.cloudeploy.v2.model.entity.task.TaskNode;

public class TaskViews {
	public static class Param {
		public String key;
		public String value;
	}

	public static class ParamBuilder {
		private String key;
		private String value;

		public ParamBuilder() {
		}

		public ParamBuilder setKey(String key) {
			this.key = key;
			return this;
		}

		public ParamBuilder setValue(String value) {
			this.value = value;
			return this;
		}

		public Param build() {
			Param output = new Param();
			output.key = this.key;
			output.value = this.value;
			return output;
		}
	}

	public static class Node {
		public Long id;
		public Integer xPos;
		public Integer yPos;
		public Long componentId;
		public Long actionId;
		public Long operation;
		public List<Param> params;
		public List<Long> hostIds;
	}

	public static class NodeBuilder {
		private Long id;
		private Integer xPos;
		private Integer yPos;
		private Long componentId;
		private Long actionId;
		private Long  operation;
		private List<Param> params;
		private List<Long> hostIds;

		public NodeBuilder() {
			params = new ArrayList<>();
			hostIds = new ArrayList<>();
		}

		public NodeBuilder setId(Long id) {
			this.id = id;
			return this;
		}

		public NodeBuilder setXPos(Integer xPos) {
			this.xPos = xPos;
			return this;
		}

		public NodeBuilder setYPos(Integer yPos) {
			this.yPos = yPos;
			return this;
		}

		public NodeBuilder setComponentId(Long id) {
			this.componentId = id;
			return this;
		}

		public NodeBuilder setActionId(Long id) {
			this.actionId = id;
			return this;
		}
		public NodeBuilder setOperation(Long operation) {
			this.operation = operation;
			return this;
		}
		public NodeBuilder addParam(Param param) {
			this.params.add(param);
			return this;
		}

		public NodeBuilder addHost(Long id) {
			this.hostIds.add(id);
			return this;
		}

		public Node build() {
			Node output = new Node();
			output.id = this.id;
			output.xPos = this.xPos;
			output.yPos = this.yPos;
			output.componentId = this.componentId;
			output.actionId = this.actionId;
			output.params = this.params;
			output.hostIds = this.hostIds;
			output.operation = this.operation;
			return output;
		}

		public void addParam(List<Param> params2) {
			if(params == null) params = new ArrayList<TaskViews.Param>();
			this.params.addAll(params2);
		}
	}

	public static class Edge {
		public Long from;
		public Long to;
		public Relation relation;
	}

	public static class EdgeBuilder {
		private Long from;
		private Long to;
		private Relation relation;

		public EdgeBuilder() {
		}

		public EdgeBuilder setFrom(Long from) {
			this.from = from;
			return this;
		}

		public EdgeBuilder setTo(Long to) {
			this.to = to;
			return this;
		}

		public EdgeBuilder setRelation(Relation relation) {
			this.relation = relation;
			return this;
		}

		public Edge build() {
			Edge output = new Edge();
			output.from = this.from;
			output.to = this.to;
			output.relation = this.relation;
			return output;
		}
	}

	public static class Graph {
		public String taskName;
		public int operation;
		public int complex;
		public List<Node> nodes;
		public List<Edge> edges;
	}
	
	public static class GraphBuilder {
		private String taskName;
		private int operation;
		private int complex;
		private List<Node> nodes;
		private List<Edge> edges;
		
		public void setTaskName(String taskName) {
			this.taskName = taskName;
		}

		public void addNode(List<Node> registerAttributesNode) {
			if(nodes == null) nodes = new ArrayList<TaskViews.Node>();
			nodes.addAll(registerAttributesNode);
		}

		public void addNode(Node installNode) {
			if(nodes == null)
				nodes = new ArrayList<TaskViews.Node>();
			nodes.add(installNode);
		}

		public Graph build() {
			Graph graph = new Graph();
			graph.taskName = taskName;
			graph.operation = operation;
			graph.complex = complex;
			graph.nodes = nodes;
			graph.edges = edges;
			return graph;
		}
	}

	private static Function<Task, Graph> GRAPH_VIEW_TRANSFORMER = new Function<Task, Graph>() {
		@Override
		public Graph apply(Task input) {
			if (input == null)
				return null;
			Graph view = new Graph();
			List<Node> nodes = new ArrayList<Node>();
			for (TaskNode tNode : input.getNodes()) {
				NodeBuilder builder = new NodeBuilder();
				Long actionId = 0l;
				Long componentId = -1l;
				Long operation = 0l;
				if (null != tNode.getAction()) {
					
					actionId = tNode.getAction().getId();
					componentId = tNode.getAction().getComponent().getId();
				}else if (null != tNode.getOperation()) {
					operation =  tNode.getOperation().getId();
				}
				builder.setActionId(actionId)
						.setXPos(tNode.getxPos())
						.setYPos(tNode.getyPos())
						.setComponentId(
								componentId)
						.setOperation(operation)
						.setId(Long.valueOf(tNode.getIdentifier()));
				for (NodeParam p : tNode.getParams()) {
					ParamBuilder pBuilder = new ParamBuilder();
					pBuilder.setKey(p.getParamKey())
							.setValue(p.getParamValue());
					builder.addParam(pBuilder.build());
				}
				for (NodeHostRelation relation : tNode.getHostInfo()) {
					builder.addHost(relation.getHost().getId());
				}
				nodes.add(builder.build());
			}
			view.nodes = nodes;
			List<Edge> edges = new ArrayList<Edge>();
			for (TaskEdge tEdge : input.getEdges()) {
				EdgeBuilder builder = new EdgeBuilder();
				builder.setFrom(Long.valueOf(tEdge.getFrom().getIdentifier()))
						.setTo(Long.valueOf(tEdge.getTo().getIdentifier()))
						.setRelation(tEdge.getRelation());
				edges.add(builder.build());
			}
			view.edges = edges;
			view.taskName = input.getName();
			return view;
		}
	};

	public static Graph graphViewOf(Task input) {
		return input == null ? null : GRAPH_VIEW_TRANSFORMER.apply(input);
	}

	public static class Item {
		public Long id;
		public String name;
		public String xmlFile;
		public List<Param> params;
		public int operation;
		public Long createdAt;
		public Long updatedAt;
		public Long executedAt;
	}

	private static Function<Task, Item> ITEM_VIEW_TRANSFORMER = new Function<Task, Item>() {
		@Override
		public Item apply(Task input) {
			if (input == null)
				return null;
			Item view = new Item();
			view.id = input.getId();
			view.name = input.getName();
			view.xmlFile = input.getXmlFileKey();
			view.operation = input.getOperation();
			view.params = this.getAllParam(input);
			view.createdAt = input.getCreateTime() == null ? null : input
					.getCreateTime().getTime();
			view.updatedAt = input.getUpdateTime() == null ? null : input
					.getUpdateTime().getTime();
			view.executedAt = input.getExecutedTime() == null ? null : input
					.getExecutedTime().getTime();
			return view;
		}
		private List<Param> getAllParam(Task input){
			List<Param> params = new ArrayList<>();
			for (TaskNode tn : input.getNodes()) {
				for (NodeParam np : tn.getParams()) {
					ParamBuilder pBuilder = new ParamBuilder();
					pBuilder.setKey(np.getParamKey())
							.setValue(np.getParamValue());
					params.add(pBuilder.build());
				}
			}
			return params;
		}
	};

	public static Item viewOf(Task input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> listViewOf(List<Task> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}
}
