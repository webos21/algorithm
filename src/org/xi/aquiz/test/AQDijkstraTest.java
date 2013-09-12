package org.xi.aquiz.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xi.aquiz.dijkstra.Edge;
import org.xi.aquiz.dijkstra.Engine;
import org.xi.aquiz.dijkstra.Graph;
import org.xi.aquiz.dijkstra.Vertex;

public class AQDijkstraTest {
	private List<Vertex> nodes;
	private List<Edge> edges;

	public AQDijkstraTest() {
		nodes = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
	}

	private void addLane(String laneId, int sourceLocNo, int destLocNo,
			int duration) {
		Edge lane = new Edge(laneId, nodes.get(sourceLocNo),
				nodes.get(destLocNo), duration);
		edges.add(lane);
	}

	public void executeTest() {
		for (int i = 0; i < 11; i++) {
			Vertex location = new Vertex("Node_" + i, "Node_" + i);
			nodes.add(location);
		}

		addLane("Edge_0", 0, 1, 85);
		addLane("Edge_1", 0, 2, 217);
		addLane("Edge_2", 0, 4, 173);
		addLane("Edge_3", 2, 6, 186);
		addLane("Edge_4", 2, 7, 103);
		addLane("Edge_5", 3, 7, 183);
		addLane("Edge_6", 5, 8, 250);
		addLane("Edge_7", 8, 9, 84);
		addLane("Edge_8", 7, 9, 167);
		addLane("Edge_9", 4, 9, 502);
		addLane("Edge_10", 9, 10, 40);
		addLane("Edge_11", 1, 10, 600);

		// Lets check from location Loc_1 to Loc_10
		Graph graph = new Graph(nodes, edges);
		Engine dijkstra = new Engine(graph);
		dijkstra.execute(nodes.get(0));
		LinkedList<Vertex> path = dijkstra.getPath(nodes.get(10));

		if (path == null || path.size() <= 0) {
			throw new RuntimeException("cannot get the shortest path!!");
		}

		for (Vertex vertex : path) {
			System.out.println(vertex);
		}
	}

	public static void main(String[] args) {
		AQDijkstraTest test = new AQDijkstraTest();
		test.executeTest();
	}
}
