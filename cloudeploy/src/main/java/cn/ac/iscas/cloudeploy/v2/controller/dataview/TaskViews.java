package cn.ac.iscas.cloudeploy.v2.controller.dataview;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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

	/**
	 * @description node that match node in frontend.
	 * @author changed by xpxstar@gmail.com
	 * 2015年11月11日 下午3:11:19
	 */
	public static class Node {
		public Long id;
		public Integer xPos;
		public Integer yPos;
		/**
		 * componentId that action belongs.0 if not.
		 */
		public Long componentId;
		/**
		 * actionId that belongs this node.0 if not.
		 */
		public Long actionId;
		/**
		 * operationId(taskId) that belongs this node.0 if not.
		 */
		public Long operation;
		public List<Param> params;
		public List<Long> hostIds;
	}

	/**
	 * @description the tool to create a node
	 * @author changed by xpxstar@gmail.com
	 * 2015年11月11日 下午3:14:08
	 */
	public static class NodeBuilder {
		private Long id;
		private Integer xPos;
		private Integer yPos;
		private Long componentId;
		private Long actionId;
		/**
		 * operationId(taskId) that belongs this node.0 if not.
		 */
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

	/**
	 * @description a datastructure that match the graph 
	 * @author changed by  xpxstar@gmail.com
	 * 2015年11月11日 下午3:14:58
	 */
	public static class Graph {
		public String taskName;
		/**
		 * 1/0 to sign if this graph is(not)an operation
		 */
		public int operation;
		/**
		 * 1/0 to sign if this graph has(no) operations.
		 */
		public int complex;
		public List<Node> nodes;
		public List<Edge> edges;
	}


	/**
	 * @description a temp method that can convert tast or opertion to graph.
	 * @author changed by xpxstar@gmail.com
	 * 2015年11月11日 下午3:19:17
	 */
	private static Function<Task, Graph> GRAPH_VIEW_TRANSFORMER = 
	new Function<Task, Graph>() {
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

	/**
	 * @description simple info of task that showed in listView .
	 * @author changed by xpxstar@gmail.com
	 * 2015年11月11日 下午3:21:13
	 */
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
 
	/**
	 * @description  a temp method that can convert simple tast or opertion to item.
	 * @author changed by xpxstar@gmail.com
	 * 2015年11月11日 下午3:22:10
	 */
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
			if (1 == view.operation) {
				view.params = this.getAllParam(input);
			}
			view.createdAt = input.getCreateTime() == null ? null : input
					.getCreateTime().getTime();
			view.updatedAt = input.getUpdateTime() == null ? null : input
					.getUpdateTime().getTime();
			view.executedAt = input.getExecutedTime() == null ? null : input
					.getExecutedTime().getTime();
			return view;
		}
		//use action DisplayName to distinguish same param-key in different node.
		private List<Param> getAllParam(Task input){
			List<Param> params = new ArrayList<>();
			for (TaskNode tn : input.getNodes()) {
				for (NodeParam np : tn.getParams()) {
					ParamBuilder pBuilder = new ParamBuilder();
					pBuilder.setKey(np.getParamKey()+'-'+tn.getAction().getDisplayName())
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
