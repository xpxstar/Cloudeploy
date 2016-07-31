package cn.ac.iscas.cloudeploy.v2.test.service.algorithm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SuperNode {
	public Set<Integer> children;
	public Set<Integer> parents;
	public LinkedList<Node> subNodes;

	public SuperNode(Node node) {
		this(Arrays.asList(node));
	}

	public SuperNode(List<Node> nodes) {
		this.children = new HashSet<>();
		this.parents = new HashSet<>();
		this.subNodes = new LinkedList<>();
		this.subNodes.addAll(nodes);
	}

	@Override
	public String toString() {
		return "SuperNode [subNodes=" + subNodes + "]";
	}

	public boolean isSimilarTo(SuperNode node) {
		return !this.isEmpty() && !node.isEmpty()
				&& this.subNodes.get(0).host == node.subNodes.get(0).host;
	}

	public boolean isEmpty() {
		return this.subNodes.size() == 0;
	}
}
