package cn.ac.iscas.cloudeploy.v2.model.entity.task;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class SuperNode{
	private Set<Integer> children;
	private Set<Integer> parents;
	private LinkedList<TaskNode> subNodes;
	private String id;
	
	public SuperNode(String id, TaskNode node) {
		this(Arrays.asList(node));
		this.setId(id);
	}

	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public SuperNode(TaskNode node) {
		this(Arrays.asList(node));
	}

	public SuperNode(List<TaskNode> nodes) {
		this.children = new HashSet<>();
		this.parents = new HashSet<>();
		this.subNodes = new LinkedList<>();
		this.subNodes.addAll(nodes);
	}
	
	public SuperNode(String id, List<TaskNode> nodes) {
		this.children = new HashSet<>();
		this.parents = new HashSet<>();
		this.subNodes = new LinkedList<>();
		this.subNodes.addAll(nodes);
		this.setId(id);
	}

	@Override
	public String toString() {
		return "SuperNode [subNodes=" + subNodes + "]";
	}

	public boolean isSimilarTo(SuperNode node) {
		return !this.isEmpty() && !node.isEmpty()
				&& this.subNodes.get(0).getHostInfo().get(0).getHost().equals(node.subNodes.get(0).getHostInfo().get(0).getHost());
	}

	public boolean isEmpty() {
		return this.subNodes.size() == 0;
	}
	
	public Set<Integer> getChildren() {
		return children;
	}

	public void setChildren(Set<Integer> children) {
		this.children = children;
	}

	public Set<Integer> getParents() {
		return parents;
	}

	public void setParents(Set<Integer> parents) {
		this.parents = parents;
	}

	public void setSubNodes(LinkedList<TaskNode> subNodes) {
		this.subNodes = subNodes;
	}
	
	public LinkedList<TaskNode> getSubNodes() {
		return subNodes;
	}
}
