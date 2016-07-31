package cn.ac.iscas.cloudeploy.v2.test.service.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class TaskAlgorithmTest {
	/**
	 * 获得一个图的拓补排序
	 * 
	 * @param startNodes
	 *            图中入度为0的节点
	 * @return
	 */
	public static List<Node> getTopoloy(List<Node> startNodes) {
		Map<Node, Set<Node>> parents = getParentMap(startNodes);
		List<Node> topology = new ArrayList<>();
		Queue<Node> potentials = new LinkedList<>();
		potentials.addAll(startNodes);
		while (!potentials.isEmpty()) {
			Node current = potentials.poll();
			topology.add(current);
			for (Node child : current.children) {
				parents.get(child).remove(current);
				if (parents.get(child).isEmpty()) {
					potentials.add(child);
				}
			}
		}
		return topology;
	}

	/**
	 * 合并superNodes中相邻且在同一host上的的superNode
	 * 
	 * @param superNodes
	 * @return
	 */
	public static List<SuperNode> mergeAdjSame(List<SuperNode> superNodes) {
		List<SuperNode> res = new ArrayList<>();
		SuperNode current = null;
		for (SuperNode sn : superNodes) {
			if (current == null && sn.subNodes.size() > 0) {
				current = new SuperNode(sn.subNodes);
			} else if (sn.subNodes.size() > 0) {
				if (sn.isSimilarTo(current)) {
					current.subNodes.addAll(sn.subNodes);
				} else {
					res.add(current);
					current = new SuperNode(sn.subNodes);
				}
			}
		}
		res.add(current);
		return res;
	}

	public static List<SuperNode> mergeNodes(List<Node> topology) {
		List<SuperNode> res = new ArrayList<>();
		for (Node node : topology) {
			res.add(new SuperNode(node));
		}
		//res = mergeAdjSame(res);
		Map<Node, Integer> supers = new HashMap<>();
		for (int i = 0; i < res.size(); i++) {
			for (Node node : res.get(i).subNodes) {
				supers.put(node, i);
			}
		}
		for (int i=0;i<res.size();i++) {
			SuperNode sn = res.get(i);
			for (Node n : sn.subNodes) {
				for (Node child : n.children) {
					Integer superId = supers.get(child);
					sn.children.add(superId);
					res.get(superId).parents.add(i);
				}
			}
		}
		boolean changed = true;
		while (changed) {
			changed = false;
			for (int i = 0; i < res.size() - 1; i++) {
				SuperNode sn = res.get(i);
				if (sn.subNodes.size() == 0)
					continue;
				int j = 1;
				while (i + j < res.size()
						&& (res.get(i + j).isEmpty() || (!sn.isSimilarTo(res
								.get(i + j)) && !sn.children.contains(i + j)))) {
					j++;
				}
				if (i + j < res.size() && sn.isSimilarTo(res.get(i + j))) {
					SuperNode nextsn = res.get(i + j);
					nextsn.subNodes.addAll(0, sn.subNodes);
					sn.subNodes.clear();
					nextsn.children.addAll(sn.children);
					sn.children.clear();
					for(Integer superId: sn.parents){
						res.get(superId).children.add(i+j);
					}
					nextsn.parents.addAll(sn.parents);
					sn.parents.clear();
					changed = true;
				}
			}
			//System.out.println(res);
		}

		return mergeAdjSame(res);
	}

	private static List<Node> getGraph() {
		List<Node> nodes = new ArrayList<>();
		for (int i = 0; i <= 13; i++) {
			Node node = new Node(i, 1);
			if (i > 5) {
				node.host = 2;
			}
			if (i > 11) {
				node.host = 3;
			}
			nodes.add(node);
		}
		nodes.get(0).addChild(nodes.get(2));
		nodes.get(1).addChild(nodes.get(2));
		nodes.get(2).addChild(nodes.get(3)).addChild(nodes.get(4))
				.addChild(nodes.get(7));
		nodes.get(3).addChild(nodes.get(5));
		nodes.get(4).addChild(nodes.get(3));
		nodes.get(6).addChild(nodes.get(7));
		nodes.get(7).addChild(nodes.get(8));
		nodes.get(7).addChild(nodes.get(12));
		nodes.get(8).addChild(nodes.get(4)).addChild(nodes.get(5))
				.addChild(nodes.get(9)).addChild(nodes.get(10));
		nodes.get(9).addChild(nodes.get(5));
		nodes.get(11).addChild(nodes.get(7));
		nodes.get(12).addChild(nodes.get(13));
		nodes.get(13).addChild(nodes.get(8));
		return Arrays.asList(nodes.get(0), nodes.get(1), nodes.get(6),
				nodes.get(11));
	}

	@SuppressWarnings("unused")
	private static List<Node> getGraph_topology() {
		List<Node> nodes = new ArrayList<>();
		for (int i = 0; i <= 5; i++) {
			Node node = new Node(i, 1);
			if (i > 1) {
				node.host = 2;
			}
			if (i > 3) {
				node.host = 3;
			}
			nodes.add(node);
		}
		nodes.get(0).addChild(nodes.get(2));
		nodes.get(4).addChild(nodes.get(1));
		return Arrays.asList(nodes.get(0), nodes.get(2), nodes.get(4),
				nodes.get(1), nodes.get(3), nodes.get(5));
	}

	private static Map<Node, Set<Node>> getParentMap(List<Node> startNodes) {
		Map<Node, Set<Node>> parents = new HashMap<>();
		Set<Node> visited = new HashSet<>();
		Queue<Node> next = new LinkedList<>();
		next.addAll(startNodes);
		while (!next.isEmpty()) {
			Node current = next.poll();
			if (visited.contains(current)) {
				continue;
			}
			for (Node child : current.children) {
				if (!parents.containsKey(child)) {
					parents.put(child, new HashSet<Node>());
				}
				parents.get(child).add(current);
				if (!visited.contains(child)) {
					next.add(child);
				}
			}
		}
		return parents;
	}

	public static void main(String[] args) {
		List<Node> topology = getTopoloy(getGraph());
		//System.out.println(topology);
		// List<Node> topology = getGraph_topology();
		// System.out.println(topology);
		System.out.println(mergeNodes(topology));
		int i=1;
		i=- - -i;
		System.out.println(i);
	}
}
