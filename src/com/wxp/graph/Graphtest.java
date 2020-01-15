package com.wxp.graph;

public class Graphtest {
public static void main(String[] args) {
	Graph graph = new Graph(5);
	graph.insertvertex("A");
	graph.insertvertex("B");
	graph.insertvertex("C");
	graph.insertvertex("D");
	graph.insertvertex("E");
	graph.insertEdge(0, 1, 1);
	graph.insertEdge(0, 2, 1);
	graph.insertEdge(1, 2, 1);
	graph.insertEdge(1, 3, 1);
	graph.insertEdge(1, 4, 1);
	graph.showGraph();
	System.out.println(graph.getNumberOfEdges());
	System.out.println(graph.getnumberofvertex());
	//graph.bfs();
	graph.Graphdfs();
}

}
