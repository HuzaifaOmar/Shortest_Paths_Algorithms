import java.util.*;
import java.io.*;

public class Graph {
  private int V;
  private int E;
  private ArrayList<ArrayList<Edge>> adjList;
  private ArrayList<ArrayList<Integer>> adjMatrix = new ArrayList<>();
  private List<CompleteEdge> edges = new ArrayList<>();
  final static int infinity = 10000000;

  /**
   * Constructor to create a graph from a file.
   * 
   * @param filePath Path to the graph file.
   */
  Graph(String filePath) {
    try {
      File file = new File(filePath);
      Scanner scanner = new Scanner(file);

      String[] firstLine = scanner.nextLine().trim().split(" ");
      V = Integer.parseInt(firstLine[0]);
      E = Integer.parseInt(firstLine[1]);

      adjList = new ArrayList<>(V);
      for (int i = 0; i < V; i++) {
        adjList.add(new ArrayList<>());
      }

      for (int i = 0; i < E; i++) {
        String[] edgeLine = scanner.nextLine().trim().split(" ");
        int source = Integer.parseInt(edgeLine[0]);
        int destination = Integer.parseInt(edgeLine[1]);
        int weight = Integer.parseInt(edgeLine[2]);

        Edge edge = new Edge(destination, weight);
        adjList.get(source).add(edge);
        edges.add(new CompleteEdge(source, destination, weight));
      }
      listToMatrix();
      scanner.close();
    } catch (FileNotFoundException e) {
      throw new RuntimeException("File not found: " + filePath, e);
    } catch (Exception e) {
      throw new RuntimeException("Error reading the graph file: " + filePath, e);
    }
  }

  /**
   * Function to get the neighbors of a vertex.
   * 
   * @param vertex The vertex whose neighbors are to be retrieved.
   * @return List of edges representing the neighbors of the vertex.
   */
  private ArrayList<Edge> getNeighbors(int vertex) {
    return adjList.get(vertex);
  }

  /**
   * Function to get the number of nodes in the graph.
   * 
   * @param V Number of vertices.
   */
  public int size() {
    return V;
  }

  /**
   * Class represinting node to be used in the priority queue.
   * 
   * It implements Comparable interface to allow comparison based on distance.
   */
  class Node implements Comparable<Node> {
    int vertex;
    int distance;

    Node(int vertex, int distance) {
      this.vertex = vertex;
      this.distance = distance;
    }

    @Override
    public int compareTo(Node other) {
      return Integer.compare(this.distance, other.distance);
    }
  }

  /**
   * Implementation of Dijkestra Algorithm.
   * 
   * @param vertex  The vertex whose cost is to be retrieved.
   * @param costs   The list of costs to each vertex.
   * @param parents The list of parent vertices for each vertex.
   */
  public void Dijkstra(int src, ArrayList<Integer> costs, ArrayList<Integer> parents) {
    for (int i = 0; i < costs.size(); i++) {
      costs.set(i, Integer.MAX_VALUE);
      parents.set(i, -1);
    }
    costs.set(src, 0);
    PriorityQueue<Node> pq = new PriorityQueue<>();
    pq.add(new Node(src, 0));
    while (!pq.isEmpty()) {
      Node currNode = pq.poll();
      if (currNode.distance > costs.get(currNode.vertex)) {
        continue;
      }
      ArrayList<Edge> currNodeNeighbours = getNeighbors(currNode.vertex);
      for (Edge edge : currNodeNeighbours) {
        int dist = edge.getDist();
        int weight = edge.getWeight();
        int newDist = weight + currNode.distance;
        if (newDist < costs.get(dist)) {
          costs.set(dist, newDist);
          parents.set(dist, currNode.vertex);
          pq.add(new Node(dist, newDist));
        }
      }
    }
  }

  /**
   * Implementation of Bellman-Ford Algorithm.
   * 
   * @param src     The source vertex.
   * @param costs   The list of costs to each vertex.
   * @param parents The list of parent vertices for each vertex.
   * 
   * @return true if graph has negative cycle otherwise return false.
   */

  public boolean bellmanFord(int src, ArrayList<Integer> costs, ArrayList<Integer> parents) {
    if (src < 0 || src >= V)
      throw new IllegalArgumentException("Source vertex must be between 0 and " + (V - 1));

    for (int i = 0; i < costs.size(); i++) {
      costs.set(i, Integer.MAX_VALUE);
      parents.set(i, -1);
    }
    costs.set(src, 0);

    for (int i = 0; i < V - 1; i++) {
      for (CompleteEdge edge : edges) {
        int from = edge.source();
        int to = edge.destination();
        int weight = edge.weight();
        if (costs.get(from) != Integer.MAX_VALUE && costs.get(from) + weight < costs.get(to)) {
          costs.set(to, costs.get(from) + weight);
          parents.set(to, from);
        }
      }
    }

    for (CompleteEdge edge : edges) {
      int from = edge.source();
      int to = edge.destination();
      int weight = edge.weight();
      if (costs.get(from) != Integer.MAX_VALUE && costs.get(from) + weight < costs.get(to)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Implementation of Floyd-Warshaal Algorithm.
   * 
   * @param costMatrix    matrix of costs form and to every node.
   * @param parentsMatrix matrix of parent of every node.
   * 
   * @return true if graph has negative cycle otherwise return false.
   */
  public boolean floydWarshall(ArrayList<ArrayList<Integer>> costMatrix,
      ArrayList<ArrayList<Integer>> parentsMatrix) {
    for (int i = 0; i < V; i++) {
      for (int j = 0; j < V; j++) {
        costMatrix.get(i).set(j, infinity);
        parentsMatrix.get(i).add(-1);
      }
    }

    for (int i = 0; i < V; i++) {
      for (int j = 0; j < V; j++) {
        costMatrix.get(i).set(j, adjMatrix.get(i).get(j));
      }
    }

    for (int i = 0; i < V; i++) {
      costMatrix.get(i).set(i, 0);
    }

    for (int k = 0; k < V; k++) {
      for (int i = 0; i < V; i++) {
        for (int j = 0; j < V; j++) {
          if (costMatrix.get(i).get(k) + costMatrix.get(k).get(j) < costMatrix.get(i).get(j)) {
            costMatrix.get(i).set(j, costMatrix.get(i).get(k) + costMatrix.get(k).get(j));
            parentsMatrix.get(i).set(j, parentsMatrix.get(k).get(j));
          }
        }
      }
    }
    for (int i = 0; i < V; i++) {
      if (costMatrix.get(i).get(i) < 0) {
        return false;
      }
    }
    return true;
  }

  public void printGraph() {
    System.out.println("Graph Representation (Adjacency List):");
    System.out.println("Vertices: " + V + ", Edges: " + E);

    for (int i = 0; i < V; i++) {
      System.out.print("Vertex " + i + " -> ");
      ArrayList<Edge> edges = adjList.get(i);

      if (edges.isEmpty()) {
        System.out.println("No outgoing edges");
      } else {
        for (int j = 0; j < edges.size(); j++) {
          Edge edge = edges.get(j);
          System.out.print("[" + edge.getDist() + ", weight=" + edge.getWeight() + "]");

          if (j < edges.size() - 1) {
            System.out.print(" | ");
          }
        }
        System.out.println();
      }
    }
  }

  private void listToMatrix() {
    for (int i = 0; i < V; ++i) {
      adjMatrix.add(new ArrayList<>());
      for (int j = 0; j < V; ++j) {
        adjMatrix.get(i).add(infinity);
      }
    }
    for (int i = 0; i < V; ++i) {
      for (Edge e : adjList.get(i)) {
        adjMatrix.get(i).set(e.getDist(), e.getWeight());
      }
    }
  }
}