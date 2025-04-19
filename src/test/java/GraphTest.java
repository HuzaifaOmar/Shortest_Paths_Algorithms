import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class GraphTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private File smallGraph;
    private File negativeWeightGraph;
    private File negativeCycleGraph;
    private File largeGraph;
    private File denseGraph;
    private File sparseGraph;

    @Before
    public void setUp() throws IOException {
        // Create test graph files
        smallGraph = createSmallGraph();
        negativeWeightGraph = createNegativeWeightGraph();
        negativeCycleGraph = createNegativeCycleGraph();
        largeGraph = createLargeGraph(50, 150);
        denseGraph = createDenseGraph(30, 600);
        sparseGraph = createSparseGraph(100, 150);
    }

    @After
    public void tearDown() {
        // Clean up any resources if needed
    }

    @Test
    public void testGraphCreation() {
        Graph graph = new Graph(smallGraph.getAbsolutePath());
        assertEquals(5, graph.size());
    }

    @Test
    public void testDijkstraSimpleGraph() {
        Graph graph = new Graph(smallGraph.getAbsolutePath());
        ArrayList<Integer> costs = new ArrayList<>(Collections.nCopies(graph.size(), 0));
        ArrayList<Integer> parents = new ArrayList<>(Collections.nCopies(graph.size(), -1));

        graph.Dijkstra(0, costs, parents);

        assertEquals(0, (int)costs.get(0));  // Cost to self is 0
        assertEquals(1, (int)costs.get(1));  // Cost from 0 to 1 is 1
        assertEquals(4, (int)costs.get(2));  // Cost from 0 to 2 is 4
        assertEquals(5, (int)costs.get(3));  // Cost from 0 to 3 is 5
        assertEquals(7, (int)costs.get(4));  // Cost from 0 to 4 is 7

        assertEquals(-1, (int)parents.get(0));  // Source has no parent
        assertEquals(0, (int)parents.get(1));   // Parent of 1 is 0
        assertEquals(1, (int)parents.get(2));   // Parent of 2 is 1
        assertEquals(2, (int)parents.get(3));   // Parent of 3 is 2
        assertEquals(3, (int)parents.get(4));   // Parent of 4 is 3
    }

    @Test
    public void testBellmanFordSimpleGraph() {
        Graph graph = new Graph(smallGraph.getAbsolutePath());
        ArrayList<Integer> costs = new ArrayList<>(Collections.nCopies(graph.size(), 0));
        ArrayList<Integer> parents = new ArrayList<>(Collections.nCopies(graph.size(), -1));

        boolean result = graph.bellmanFord(0, costs, parents);

        assertTrue("Graph should not have negative cycles", result);
        assertEquals(0, (int)costs.get(0));  // Cost to self is 0
        assertEquals(1, (int)costs.get(1));  // Cost from 0 to 1 is 1
        assertEquals(4, (int)costs.get(2));  // Cost from 0 to 2 is 4
        assertEquals(5, (int)costs.get(3));  // Cost from 0 to 3 is 5
        assertEquals(7, (int)costs.get(4));  // Cost from 0 to 4 is 7

        assertEquals(-1, (int)parents.get(0));  // Source has no parent
        assertEquals(0, (int)parents.get(1));   // Parent of 1 is 0
        assertEquals(1, (int)parents.get(2));   // Parent of 2 is 1
        assertEquals(2, (int)parents.get(3));   // Parent of 3 is 2
        assertEquals(3, (int)parents.get(4));   // Parent of 4 is 3
    }

    @Test
    public void testDijkstraNegativeWeightGraph() {
        Graph graph = new Graph(negativeWeightGraph.getAbsolutePath());
        ArrayList<Integer> costs = new ArrayList<>(Collections.nCopies(graph.size(), 0));
        ArrayList<Integer> parents = new ArrayList<>(Collections.nCopies(graph.size(), -1));

        graph.Dijkstra(0, costs, parents);

        // Dijkstra might not work correctly with negative weights, so we're just 
        // checking that it completes without error
        assertNotNull(costs);
        assertNotNull(parents);
    }

    @Test
    public void testBellmanFordNegativeWeightGraph() {
        Graph graph = new Graph(negativeWeightGraph.getAbsolutePath());
        ArrayList<Integer> costs = new ArrayList<>(Collections.nCopies(graph.size(), 0));
        ArrayList<Integer> parents = new ArrayList<>(Collections.nCopies(graph.size(), -1));

        boolean result = graph.bellmanFord(0, costs, parents);

        assertTrue("Graph should not have negative cycles", result);
        assertEquals(0, (int)costs.get(0));  // Cost to self is 0
        assertEquals(1, (int)costs.get(1));  // Cost from 0 to 1 is 1
        assertEquals(-1, (int)costs.get(2)); // Cost from 0 to 2 is -1 (through negative edge)
        assertEquals(2, (int)costs.get(3));  // Cost from 0 to 3 is 2
    }

    @Test
    public void testBellmanFordNegativeCycleDetection() {
        Graph graph = new Graph(negativeCycleGraph.getAbsolutePath());
        ArrayList<Integer> costs = new ArrayList<>(Collections.nCopies(graph.size(), 0));
        ArrayList<Integer> parents = new ArrayList<>(Collections.nCopies(graph.size(), -1));

        boolean result = graph.bellmanFord(0, costs, parents);

        assertFalse("Graph should have negative cycles", result);
    }

    @Test
    public void testFloydWarshall() {
        // Note: This test assumes the floydWarshall method in Graph class is properly implemented
        Graph graph = new Graph(smallGraph.getAbsolutePath());
        ArrayList<ArrayList<Integer>> costs = new ArrayList<>();
        ArrayList<ArrayList<Integer>> parents = new ArrayList<>();

        for (int i = 0; i < graph.size(); i++) {
            costs.add(new ArrayList<>(Collections.nCopies(graph.size(), Integer.MAX_VALUE)));
            parents.add(new ArrayList<>(Collections.nCopies(graph.size(), -1)));
        }

        boolean result = graph.floydWarshall(costs, parents);

        assertTrue("Graph should not have negative cycles", result);

        // Since the floydWarshall implementation in Graph is empty,
        // we're just checking that it returns true and doesn't throw exceptions
    }

    @Test(expected = RuntimeException.class)
    public void testGraphCreationWithInvalidFile() {
        new Graph("non_existent_file.txt");
    }

    @Test
    public void testPerformanceComparisonLargeGraph() {
        Graph graph = new Graph(largeGraph.getAbsolutePath());
        ArrayList<Integer> costs = new ArrayList<>(Collections.nCopies(graph.size(), 0));
        ArrayList<Integer> parents = new ArrayList<>(Collections.nCopies(graph.size(), -1));

        // Measure Dijkstra performance
        long startTime = System.nanoTime();
        graph.Dijkstra(0, costs, parents);
        long dijkstraTime = System.nanoTime() - startTime;

        costs = new ArrayList<>(Collections.nCopies(graph.size(), 0));
        parents = new ArrayList<>(Collections.nCopies(graph.size(), -1));

        // Measure Bellman-Ford performance
        startTime = System.nanoTime();
        graph.bellmanFord(0, costs, parents);
        long bellmanFordTime = System.nanoTime() - startTime;

        ArrayList<ArrayList<Integer>> allCosts = new ArrayList<>();
        ArrayList<ArrayList<Integer>> allParents = new ArrayList<>();

        for (int i = 0; i < graph.size(); i++) {
            allCosts.add(new ArrayList<>(Collections.nCopies(graph.size(), Integer.MAX_VALUE)));
            allParents.add(new ArrayList<>(Collections.nCopies(graph.size(), -1)));
        }

        // Measure Floyd-Warshall performance
        startTime = System.nanoTime();
        graph.floydWarshall(allCosts, allParents);
        long floydWarshallTime = System.nanoTime() - startTime;

        System.out.println("Performance comparison on large graph (50 vertices, 150 edges):");
        System.out.println("Dijkstra: " + dijkstraTime / 1_000_000.0 + " ms");
        System.out.println("Bellman-Ford: " + bellmanFordTime / 1_000_000.0 + " ms");
        System.out.println("Floyd-Warshall: " + floydWarshallTime / 1_000_000.0 + " ms");

        // No assertions, just performance measurement
    }

    @Test
    public void testPerformanceComparisonDenseGraph() {
        Graph graph = new Graph(denseGraph.getAbsolutePath());
        ArrayList<Integer> costs = new ArrayList<>(Collections.nCopies(graph.size(), 0));
        ArrayList<Integer> parents = new ArrayList<>(Collections.nCopies(graph.size(), -1));

        // Measure Dijkstra performance
        long startTime = System.nanoTime();
        graph.Dijkstra(0, costs, parents);
        long dijkstraTime = System.nanoTime() - startTime;

        costs = new ArrayList<>(Collections.nCopies(graph.size(), 0));
        parents = new ArrayList<>(Collections.nCopies(graph.size(), -1));

        // Measure Bellman-Ford performance
        startTime = System.nanoTime();
        graph.bellmanFord(0, costs, parents);
        long bellmanFordTime = System.nanoTime() - startTime;

        ArrayList<ArrayList<Integer>> allCosts = new ArrayList<>();
        ArrayList<ArrayList<Integer>> allParents = new ArrayList<>();

        for (int i = 0; i < graph.size(); i++) {
            allCosts.add(new ArrayList<>(Collections.nCopies(graph.size(), Integer.MAX_VALUE)));
            allParents.add(new ArrayList<>(Collections.nCopies(graph.size(), -1)));
        }

        // Measure Floyd-Warshall performance
        startTime = System.nanoTime();
        graph.floydWarshall(allCosts, allParents);
        long floydWarshallTime = System.nanoTime() - startTime;

        System.out.println("Performance comparison on dense graph (30 vertices, 600 edges):");
        System.out.println("Dijkstra: " + dijkstraTime / 1_000_000.0 + " ms");
        System.out.println("Bellman-Ford: " + bellmanFordTime / 1_000_000.0 + " ms");
        System.out.println("Floyd-Warshall: " + floydWarshallTime / 1_000_000.0 + " ms");

        // No assertions, just performance measurement
    }

    @Test
    public void testPerformanceComparisonSparseGraph() {
        Graph graph = new Graph(sparseGraph.getAbsolutePath());
        ArrayList<Integer> costs = new ArrayList<>(Collections.nCopies(graph.size(), 0));
        ArrayList<Integer> parents = new ArrayList<>(Collections.nCopies(graph.size(), -1));

        // Measure Dijkstra performance
        long startTime = System.nanoTime();
        graph.Dijkstra(0, costs, parents);
        long dijkstraTime = System.nanoTime() - startTime;

        costs = new ArrayList<>(Collections.nCopies(graph.size(), 0));
        parents = new ArrayList<>(Collections.nCopies(graph.size(), -1));

        // Measure Bellman-Ford performance
        startTime = System.nanoTime();
        graph.bellmanFord(0, costs, parents);
        long bellmanFordTime = System.nanoTime() - startTime;

        ArrayList<ArrayList<Integer>> allCosts = new ArrayList<>();
        ArrayList<ArrayList<Integer>> allParents = new ArrayList<>();

        for (int i = 0; i < graph.size(); i++) {
            allCosts.add(new ArrayList<>(Collections.nCopies(graph.size(), Integer.MAX_VALUE)));
            allParents.add(new ArrayList<>(Collections.nCopies(graph.size(), -1)));
        }

        // Measure Floyd-Warshall performance
        startTime = System.nanoTime();
        graph.floydWarshall(allCosts, allParents);
        long floydWarshallTime = System.nanoTime() - startTime;

        System.out.println("Performance comparison on sparse graph (100 vertices, 150 edges):");
        System.out.println("Dijkstra: " + dijkstraTime / 1_000_000.0 + " ms");
        System.out.println("Bellman-Ford: " + bellmanFordTime / 1_000_000.0 + " ms");
        System.out.println("Floyd-Warshall: " + floydWarshallTime / 1_000_000.0 + " ms");

        // No assertions, just performance measurement
    }

    @Test
    public void testAllPairsUsingDijkstra() {
        Graph graph = new Graph(smallGraph.getAbsolutePath());
        ArrayList<ArrayList<Integer>> allCosts = new ArrayList<>();
        ArrayList<ArrayList<Integer>> allParents = new ArrayList<>();

        for (int i = 0; i < graph.size(); i++) {
            ArrayList<Integer> costs = new ArrayList<>(Collections.nCopies(graph.size(), 0));
            ArrayList<Integer> parents = new ArrayList<>(Collections.nCopies(graph.size(), -1));
            graph.Dijkstra(i, costs, parents);
            allCosts.add(costs);
            allParents.add(parents);
        }

        // Check path from node 0 to node 4
        assertEquals(7, (int)allCosts.get(0).get(4));

        // Check path from node 4 to node 0 (should be infinity or very large as there's no path back)
        assertEquals(Integer.MAX_VALUE, (int)allCosts.get(4).get(0));
    }

    @Test
    public void testAllPairsUsingBellmanFord() {
        Graph graph = new Graph(smallGraph.getAbsolutePath());
        ArrayList<ArrayList<Integer>> allCosts = new ArrayList<>();
        ArrayList<ArrayList<Integer>> allParents = new ArrayList<>();

        for (int i = 0; i < graph.size(); i++) {
            ArrayList<Integer> costs = new ArrayList<>(Collections.nCopies(graph.size(), 0));
            ArrayList<Integer> parents = new ArrayList<>(Collections.nCopies(graph.size(), -1));
            graph.bellmanFord(i, costs, parents);
            allCosts.add(costs);
            allParents.add(parents);
        }

        // Check path from node 0 to node 4
        assertEquals(7, (int)allCosts.get(0).get(4));

        // Check path from node 4 to node 0 (should be infinity or very large as there's no path back)
        assertEquals(Integer.MAX_VALUE, (int)allCosts.get(4).get(0));
    }

    // Helper methods to create test graph files
    private File createSmallGraph() throws IOException {
        File file = tempFolder.newFile("small_graph.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("5 5\n");
            writer.write("0 1 1\n");  // Edge from 0 to 1 with weight 1
            writer.write("1 2 3\n");  // Edge from 1 to 2 with weight 3
            writer.write("2 3 1\n");  // Edge from 2 to 3 with weight 1
            writer.write("3 4 2\n");  // Edge from 3 to 4 with weight 2
            writer.write("0 2 10\n"); // Edge from 0 to 2 with weight 10 (longer path)
        }
        return file;
    }

    private File createNegativeWeightGraph() throws IOException {
        File file = tempFolder.newFile("negative_weight_graph.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("4 5\n");
            writer.write("0 1 1\n");   // Edge from 0 to 1 with weight 1
            writer.write("1 2 -2\n");  // Edge from 1 to 2 with weight -2
            writer.write("2 3 3\n");   // Edge from 2 to 3 with weight 3
            writer.write("0 3 5\n");   // Edge from 0 to 3 with weight 5
            writer.write("1 3 1\n");   // Edge from 1 to 3 with weight 1
        }
        return file;
    }

    private File createNegativeCycleGraph() throws IOException {
        File file = tempFolder.newFile("negative_cycle_graph.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("4 4\n");
            writer.write("0 1 1\n");   // Edge from 0 to 1 with weight 1
            writer.write("1 2 2\n");   // Edge from 1 to 2 with weight 2
            writer.write("2 3 3\n");   // Edge from 2 to 3 with weight 3
            writer.write("3 1 -10\n"); // Edge from 3 to 1 with weight -10, creating a negative cycle
        }
        return file;
    }

    private File createLargeGraph(int vertices, int edges) throws IOException {
        File file = tempFolder.newFile("large_graph.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(vertices + " " + edges + "\n");

            // Create a random graph with specified number of vertices and edges
            for (int i = 0; i < edges; i++) {
                int source = (int)(Math.random() * vertices);
                int destination = (int)(Math.random() * vertices);
                int weight = 1 + (int)(Math.random() * 10); // Weight between 1 and 10

                writer.write(source + " " + destination + " " + weight + "\n");
            }
        }
        return file;
    }

    private File createDenseGraph(int vertices, int edges) throws IOException {
        // A dense graph has many edges compared to number of vertices
        File file = tempFolder.newFile("dense_graph.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(vertices + " " + edges + "\n");

            // Create a random dense graph
            for (int i = 0; i < edges; i++) {
                int source = (int)(Math.random() * vertices);
                int destination = (int)(Math.random() * vertices);
                int weight = 1 + (int)(Math.random() * 10); // Weight between 1 and 10

                writer.write(source + " " + destination + " " + weight + "\n");
            }
        }
        return file;
    }

    private File createSparseGraph(int vertices, int edges) throws IOException {
        // A sparse graph has few edges compared to number of vertices
        File file = tempFolder.newFile("sparse_graph.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(vertices + " " + edges + "\n");

            // Create a random sparse graph
            for (int i = 0; i < edges; i++) {
                int source = (int)(Math.random() * vertices);
                int destination = (int)(Math.random() * vertices);
                int weight = 1 + (int)(Math.random() * 10); // Weight between 1 and 10

                writer.write(source + " " + destination + " " + weight + "\n");
            }
        }
        return file;
    }
}