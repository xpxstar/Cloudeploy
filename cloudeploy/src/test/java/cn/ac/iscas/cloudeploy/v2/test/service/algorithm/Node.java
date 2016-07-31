package cn.ac.iscas.cloudeploy.v2.test.service.algorithm;

import java.util.ArrayList;
import java.util.List;

public class Node {
	public Node(int id, int host) {
		this.id = id;
		this.host = host;
		this.children = new ArrayList<>();
	}

	public Node addChild(Node child) {
		this.children.add(child);
		return this;
	}

	@Override
	public String toString() {
		return "Node [" + id + "] ";
	}

	public int id;
	public int host;
	public List<Node> children;
}
