import Algoritm.Kruskal;
import Algoritm.Prim;
import Models.Edges;
import Models.Graphs;
import Util.JsonIO;
import Util.ResultRecord;
import Util.CSVwriter;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MST Comprehensive Test Suite")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MSTComprehensiveTest {

    private static List<ResultRecord> testResults = new ArrayList<>();

    @Test
    @Order(1)
    @DisplayName("1a. Total MST cost is identical for both algorithms")
    public void testIdenticalMSTCost() throws IOException {
        System.out.println("\n=== Test 1a: Identical MST Cost ===");
        String[] inputFiles = {
                "src/main/resources/small.json",
                "src/main/resources/medium.json",
                "src/main/resources/large.json"
        };

        for (String inputFile : inputFiles) {
            List<JsonIO.GraphData> graphs = JsonIO.readGraphs(inputFile);

            for (JsonIO.GraphData gd : graphs) {
                Graphs g = gd.graph;
                Prim.Result primResult = Prim.run(g, 0);
                Kruskal.Result kruskalResult = Kruskal.run(g);

                System.out.printf("  %s: Prim=%.2f, Kruskal=%.2f%n",
                        gd.name, primResult.totalCost, kruskalResult.totalCost);

                assertEquals(primResult.totalCost, kruskalResult.totalCost, 0.001,
                        gd.name + ": Total costs should be identical");

                ResultRecord record = new ResultRecord(
                        gd.name, g.vertices(), g.edgeCount(),
                        primResult.totalCost, primResult.timeMs, primResult.ops,
                        kruskalResult.totalCost, kruskalResult.timeMs, kruskalResult.ops);
                testResults.add(record);
            }
        }
        System.out.println("✓ All graphs have identical MST costs");
    }

    @Test
    @Order(2)
    @DisplayName("1b. Number of edges in MST equals V-1")
    public void testMSTEdgeCount() throws IOException {
        System.out.println("\n=== Test 1b: MST Edge Count (V-1) ===");
        String[] inputFiles = {
                "src/main/resources/small.json",
                "src/main/resources/medium.json",
                "src/main/resources/large.json"
        };

        for (String inputFile : inputFiles) {
            List<JsonIO.GraphData> graphs = JsonIO.readGraphs(inputFile);

            for (JsonIO.GraphData gd : graphs) {
                Graphs g = gd.graph;
                int V = g.vertices();

                Prim.Result primResult = Prim.run(g, 0);
                Kruskal.Result kruskalResult = Kruskal.run(g);

                System.out.printf("  %s: V=%d, Prim edges=%d, Kruskal edges=%d%n",
                        gd.name, V, primResult.mst.size(), kruskalResult.mst.size());

                assertEquals(V - 1, primResult.mst.size(),
                        gd.name + ": Prim MST should have V-1 edges");
                assertEquals(V - 1, kruskalResult.mst.size(),
                        gd.name + ": Kruskal MST should have V-1 edges");
            }
        }
        System.out.println("All MSTs have V-1 edges");
    }

    @Test
    @Order(3)
    @DisplayName("1c. MST contains no cycles (acyclic)")
    public void testMSTIsAcyclic() throws IOException {
        System.out.println("\n=== Test 1c: MST is Acyclic ===");
        String[] inputFiles = {
                "src/main/resources/small.json",
                "src/main/resources/medium.json",
                "src/main/resources/large.json"
        };

        for (String inputFile : inputFiles) {
            List<JsonIO.GraphData> graphs = JsonIO.readGraphs(inputFile);

            for (JsonIO.GraphData gd : graphs) {
                Graphs g = gd.graph;

                Prim.Result primResult = Prim.run(g, 0);
                Kruskal.Result kruskalResult = Kruskal.run(g);

                boolean primAcyclic = !hasCycle(primResult.mst, g.vertices());
                boolean kruskalAcyclic = !hasCycle(kruskalResult.mst, g.vertices());

                System.out.printf("  %s: Prim acyclic=%s, Kruskal acyclic=%s%n",
                        gd.name, primAcyclic ? "✓" : "✗", kruskalAcyclic ? "✓" : "✗");

                assertFalse(hasCycle(primResult.mst, g.vertices()),
                        gd.name + ": Prim MST should be acyclic");
                assertFalse(hasCycle(kruskalResult.mst, g.vertices()),
                        gd.name + ": Kruskal MST should be acyclic");
            }
        }
        System.out.println("✓ All MSTs are acyclic");
    }

    @Test
    @Order(4)
    @DisplayName("1d. MST connects all vertices (single connected component)")
    public void testMSTConnectsAllVertices() throws IOException {
        System.out.println("\n=== Test 1d: MST Connects All Vertices ===");
        String[] inputFiles = {
                "src/main/resources/small.json",
                "src/main/resources/medium.json",
                "src/main/resources/large.json"
        };

        for (String inputFile : inputFiles) {
            List<JsonIO.GraphData> graphs = JsonIO.readGraphs(inputFile);

            for (JsonIO.GraphData gd : graphs) {
                Graphs g = gd.graph;

                Prim.Result primResult = Prim.run(g, 0);
                Kruskal.Result kruskalResult = Kruskal.run(g);

                boolean primConnected = isConnected(primResult.mst, g.vertices());
                boolean kruskalConnected = isConnected(kruskalResult.mst, g.vertices());

                System.out.printf("  %s: Prim connected=%s, Kruskal connected=%s%n",
                        gd.name, primConnected ? "✓" : "✗", kruskalConnected ? "✓" : "✗");

                assertTrue(isConnected(primResult.mst, g.vertices()),
                        gd.name + ": Prim MST should connect all vertices");
                assertTrue(isConnected(kruskalResult.mst, g.vertices()),
                        gd.name + ": Kruskal MST should connect all vertices");
            }
        }
        System.out.println("✓ All MSTs connect all vertices");
    }

    @Test
    @Order(5)
    @DisplayName("1e. Disconnected graphs are handled gracefully")
    public void testDisconnectedGraphHandling() {
        System.out.println("\n=== Test 1e: Disconnected Graph Handling ===");

        Graphs g = new Graphs(6);
        g.addEdge(new Edges(0, 1, 1.0));
        g.addEdge(new Edges(1, 2, 2.0));
        g.addEdge(new Edges(3, 4, 3.0));
        g.addEdge(new Edges(4, 5, 4.0));

        Prim.Result primResult = Prim.run(g, 0);
        Kruskal.Result kruskalResult = Kruskal.run(g);

        System.out.printf("  Disconnected graph (2 components): V=%d%n", g.vertices());
        System.out.printf("    Prim MST edges: %d (should be < V-1)%n", primResult.mst.size());
        System.out.printf("    Kruskal MST edges: %d (should be < V-1)%n", kruskalResult.mst.size());

        assertTrue(primResult.mst.size() < g.vertices() - 1,
                "Prim should handle disconnected graph (< V-1 edges)");
        assertTrue(kruskalResult.mst.size() < g.vertices() - 1,
                "Kruskal should handle disconnected graph (< V-1 edges)");

        System.out.println("✓ Disconnected graphs handled gracefully");
    }

    @Test
    @Order(6)
    @DisplayName("2a. Execution time is non-negative and measured in milliseconds")
    public void testExecutionTimeNonNegative() throws IOException {
        System.out.println("\n=== Test 2a: Execution Time Non-Negative (ms) ===");
        String[] inputFiles = {
                "src/main/resources/small.json",
                "src/main/resources/medium.json",
                "src/main/resources/large.json"
        };

        for (String inputFile : inputFiles) {
            List<JsonIO.GraphData> graphs = JsonIO.readGraphs(inputFile);

            for (JsonIO.GraphData gd : graphs) {
                Graphs g = gd.graph;

                Prim.Result primResult = Prim.run(g, 0);
                Kruskal.Result kruskalResult = Kruskal.run(g);

                System.out.printf("  %s: Prim=%dms, Kruskal=%dms%n",
                        gd.name, primResult.timeMs, kruskalResult.timeMs);

                assertTrue(primResult.timeMs >= 0,
                        gd.name + ": Prim execution time should be non-negative");
                assertTrue(kruskalResult.timeMs >= 0,
                        gd.name + ": Kruskal execution time should be non-negative");
            }
        }
        System.out.println("✓ All execution times are non-negative");
    }

    @Test
    @Order(7)
    @DisplayName("2b. Operation counts are non-negative and consistent")
    public void testOperationCountsNonNegative() throws IOException {
        System.out.println("\n=== Test 2b: Operation Counts Non-Negative ===");
        String[] inputFiles = {
                "src/main/resources/small.json",
                "src/main/resources/medium.json",
                "src/main/resources/large.json"
        };

        for (String inputFile : inputFiles) {
            List<JsonIO.GraphData> graphs = JsonIO.readGraphs(inputFile);

            for (JsonIO.GraphData gd : graphs) {
                Graphs g = gd.graph;

                Prim.Result primResult = Prim.run(g, 0);
                Kruskal.Result kruskalResult = Kruskal.run(g);

                long primOps = primResult.ops.values().stream().mapToLong(Long::longValue).sum();
                long kruskalOps = kruskalResult.ops.values().stream().mapToLong(Long::longValue).sum();

                System.out.printf("  %s: Prim ops=%d, Kruskal ops=%d%n",
                        gd.name, primOps, kruskalOps);

                for (Map.Entry<String, Long> entry : primResult.ops.entrySet()) {
                    assertTrue(entry.getValue() >= 0,
                            gd.name + ": Prim " + entry.getKey() + " should be non-negative");
                }

                for (Map.Entry<String, Long> entry : kruskalResult.ops.entrySet()) {
                    assertTrue(entry.getValue() >= 0,
                            gd.name + ": Kruskal " + entry.getKey() + " should be non-negative");
                }
            }
        }
        System.out.println("✓ All operation counts are non-negative");
    }

    @Test
    @Order(8)
    @DisplayName("2c. Results are reproducible for same dataset")
    public void testResultsReproducible() throws IOException {
        System.out.println("\n=== Test 2c: Results Reproducibility ===");
        String[] inputFiles = {
                "src/main/resources/small.json",
                "src/main/resources/medium.json",
                "src/main/resources/large.json"
        };

        for (String inputFile : inputFiles) {
            List<JsonIO.GraphData> graphs = JsonIO.readGraphs(inputFile);

            for (JsonIO.GraphData gd : graphs) {
                Graphs g = gd.graph;

                // Run 3 times
                Prim.Result prim1 = Prim.run(g, 0);
                Prim.Result prim2 = Prim.run(g, 0);
                Prim.Result prim3 = Prim.run(g, 0);

                Kruskal.Result kruskal1 = Kruskal.run(g);
                Kruskal.Result kruskal2 = Kruskal.run(g);
                Kruskal.Result kruskal3 = Kruskal.run(g);

                System.out.printf("  %s: Prim costs=[%.2f, %.2f, %.2f], Kruskal costs=[%.2f, %.2f, %.2f]%n",
                        gd.name, prim1.totalCost, prim2.totalCost, prim3.totalCost,
                        kruskal1.totalCost, kruskal2.totalCost, kruskal3.totalCost);

                assertEquals(prim1.totalCost, prim2.totalCost, 0.001);
                assertEquals(prim2.totalCost, prim3.totalCost, 0.001);
                assertEquals(kruskal1.totalCost, kruskal2.totalCost, 0.001);
                assertEquals(kruskal2.totalCost, kruskal3.totalCost, 0.001);
            }
        }
        System.out.println("✓ All results are reproducible");
    }

    @AfterAll
    @DisplayName("3. Output to JSON and CSV files")
    public static void generateOutputFiles() throws IOException {
        System.out.println("\n=== Test 3: Generate Output Files ===");

        // Write to JSON
        JsonIO.writeResults("output.json", testResults);
        System.out.println("✓ output.json created with " + testResults.size() + " results");

        // Write to CSV
        CSVwriter.writeCSV("results.csv", testResults);
        System.out.println("✓ results.csv created with " + testResults.size() + " results");

        System.out.println("\n=== All Tests Completed Successfully ===");
    }

    // Helper methods

    private boolean isConnected(List<Edges> mst, int V) {
        if (mst.size() != V - 1) return false;

        boolean[] visited = new boolean[V];
        Set<Integer>[] adj = new HashSet[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new HashSet<>();
        }

        for (Edges e : mst) {
            adj[e.u].add(e.v);
            adj[e.v].add(e.u);
        }

        dfs(0, visited, adj);

        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }

    private void dfs(int u, boolean[] visited, Set<Integer>[] adj) {
        visited[u] = true;
        for (int v : adj[u]) {
            if (!visited[v]) {
                dfs(v, visited, adj);
            }
        }
    }

    private boolean hasCycle(List<Edges> mst, int V) {
        if (mst.size() >= V) return true;

        boolean[] visited = new boolean[V];
        Set<Integer>[] adj = new HashSet[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new HashSet<>();
        }

        for (Edges e : mst) {
            adj[e.u].add(e.v);
            adj[e.v].add(e.u);
        }

        for (int i = 0; i < V; i++) {
            if (!visited[i]) {
                if (hasCycleDFS(i, -1, visited, adj)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasCycleDFS(int u, int parent, boolean[] visited, Set<Integer>[] adj) {
        visited[u] = true;
        for (int v : adj[u]) {
            if (!visited[v]) {
                if (hasCycleDFS(v, u, visited, adj)) return true;
            } else if (v != parent) {
                return true;
            }
        }
        return false;
    }
}