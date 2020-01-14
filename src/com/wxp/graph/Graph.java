package com.wxp.graph;

import java.util.ArrayList;

public class Graph {
 private ArrayList<String> vertexList; //储存顶点的集合
 private int [][] edges;//储存图所对应的邻接矩阵
 private int numOfEdges;//表示边的数目
 private boolean[] isvisited;//表示是否访问过
 
//构造器
	public Graph(int n) {
		//初始化矩阵和vertexList
		edges = new int[n][n];
		vertexList = new ArrayList<String>(n);
		numOfEdges = 0;
		isvisited = new boolean[n];
	}
	/**
	 * 插入顶点的值
	 */
	public void insertvertex(String vertex) {
		vertexList.add(vertex);	
	}
 /**
	 * 
	 * @param v1 表示点的下标即使第几个顶点  "A"-"B" "A"->0 "B"->1
	 * @param v2 第二个顶点对应的下标
	 * @param weight 表示 
	 */
	public void insertEdge(int v1, int v2, int weight) {
		edges[v1][v2] = weight;
		edges[v2][v1] = weight;
		numOfEdges++;
	}
	/**
	 * 查看有多少个节点
	 */
	public int getnumberofvertex() {
		return vertexList.size();
	}
	/**
	 * 得到边的数目
	 */
	public int getNumberOfEdges() {
		return numOfEdges;
	}
	/**
	 * 得到下标对应的值
	 */
	public String getValueByindex(int i) {
		return vertexList.get(i);
	}
	/**
	 * 显示整个矩阵
	 */
	public void showGraph() {
		for (int i = 0; i < edges.length; i++) {
			for (int j = 0; j < edges[i].length; j++) {
				System.out.print( edges[i][j]);
			}
			System.out.println();
		}
	}
	/**
	 * 图的深度优先遍历(dfs)
	 */
	private void graphdfs(boolean [] isvisited,int i) {
		//首先访问该节点
		System.out.print(getValueByindex(i)+"-->");
		isvisited[i]=true;
		//查找节点i的第一个邻接结点w
		int w = getFirstNeighbor(i);
		while (w!=-1) {
		if (!isvisited[w]) {
			graphdfs(isvisited, w);
		}
		//如果w已经被访问过,就访问邻接结点的下一个；
		w =getNextNeighbor(i, w);
		}
	}
	/**
	 * 遍历所有的结点都进行深度优先遍历
	 */
	public void Graphdfs() {
		for (int i = 0; i < vertexList.size(); i++) {
			if(!isvisited[i]) {
				graphdfs(isvisited,i);
			}
		}
	}
	/**
	 * 得到第一个临接节点的下标,如果存在就返回对应的下标，如果没有就返回－１＇；
	 */
	public int getFirstNeighbor(int index) {
		for (int i = 0; i < vertexList.size(); i++) {
			if(edges[index][i]>0) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * 根据前一个邻接节点的下标，来获取下一个邻接节点。
	 */
	public int getNextNeighbor(int v1,int v2) {
		for (int i =v2+1 ; i < vertexList.size(); i++) {
			if(edges[v1][i]>0) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * ------end-----
	 */		
}
