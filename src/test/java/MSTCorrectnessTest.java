import Algoritm.Kruskal;
import Algoritm.Prim;
import Models.Edges;
import Models.Graphs;
import Util.JsonIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MST Correctness Tests")
public class MSTCorrectnessTest {

    @Test
    @DisplayName("Total cost should be identical for Prim and Kruskal")
    public void testIdenticalTotalCost() {
        Graphs g = createTestGraph1();

        Prim.Result primResult = Prim.run(g, 0);
        Kruskal.Result kruskalResult = Kruskal.run(g);

        assertEquals(primResult.totalCost, kruskalResult.totalCost, 0.001,
                "Prim and Kruskal should produce same total cost");
    }

    @Test
    @DisplayName("MST should have exactly V-1 edges")
    public void testEdgeCountEqualsVMinus1() {
        Graphs g = createTestGraph1();
        int V = g.vertices();

        Prim.Result primResult = Prim.run(g, 0);
        Kruskal.Result kruskalResult = Kruskal.run(g);

        assertEquals(V - 1, primResult.mst.size(),
                "Prim MST should have V-1 edges");
        assertEquals(V - 1, kruskalResult.mst.size(),
                "Kruskal MST should have V-1 edges");
    }

    @Test
    @DisplayName("MST should be acyclic (no cycles)")
    public void testMSTIsAcyclic() {
        Graphs g = createTestGraph1();

        Prim.Result primResult = Prim.run(g, 0);
        Kruskal.Result kruskalResult = Kruskal.run(g);

        assertFalse(hasCycle(primResult.mst, g.vertices()),
                "Prim MST should contain no cycles");
        assertFalse(hasCycle(kruskalResult.mst, g.vertices()),
                "Kruskal MST should contain no cycles");
    }

    @Test
    @DisplayName("MST should connect all vertices (single connected component)")
    public void testMSTConnectsAllVertices() {
        Graphs g = createTestGraph1();

        Prim.Result primResult = Prim.run(g, 0);
        Kruskal.Result kruskalResult = Kruskal.run(g);

        assertTrue(isConnected(primResult.mst, g.vertices()),
                "Prim MST should connect all vertices");
        assertTrue(isConnected(kruskalResult.mst, g.vertices()),
                "Kruskal MST should connect all vertices");
    }

    @Test
    @DisplayName("Disconnected graph should be handled gracefully")
    public void testDisconnectedGraphHandling() {
        Graphs g = new Graphs(6);
        g.addEdge(new Edges(0, 1, 1.0));
        g.addEdge(new Edges(1, 2, 2.0));
        g.addEdge(new Edges(3, 4, 3.0));
        g.addEdge(new Edges(4, 5, 4.0));

        Prim.Result primResult = Prim.run(g, 0);
        Kruskal.Result kruskalResult = Kruskal.run(g);

        System.out.println("Disconnected graph test:");
        System.out.println("  Prim MST size: " + primResult.mst.size());
        System.out.println("  Kruskal MST size: " + kruskalResult.mst.size());

        assertTrue(primResult.mst.size() < g.vertices() - 1,
                "Prim MST of disconnected graph should have < V-1 edges");
        assertTrue(kruskalResult.mst.size() < g.vertices() - 1,
                "Kruskal MST of disconnected graph should have < V-1 edges");

        assertEquals(2, primResult.mst.size(),
                "Prim should find MST of component containing start vertex (2 edges)");

        assertEquals(4, kruskalResult.mst.size(),
                "Kruskal should find MST edges for all components (4 edges total)");

        assertNotNull(primResult.mst, "Prim should return valid MST");
        assertNotNull(kruskalResult.mst, "Kruskal should return valid MST");

        assertEquals(3.0, primResult.totalCost, 0.001,
                "Prim cost should be 1+2=3 for component 1");
        assertEquals(10.0, kruskalResult.totalCost, 0.001,
                "Kruskal cost should be 1+2+3+4=10 for both components");
    }
    @Test
    @DisplayName("All test datasets should satisfy correctness properties")
    public void testAllDatasetsSatisfyProperties() throws IOException {
        String[] inputFiles = {
                "src/main/resources/small.json",
                "src/main/resources/medium.json",
                "src/main/resources/large.json"
        };

        for (String inputFile : inputFiles) {
            List<JsonIO.GraphData> graphs = JsonIO.readGraphs(inputFile);

            for (JsonIO.GraphData gd : graphs) {
                Graphs g = gd.graph;
                String name = gd.name;

                Prim.Result primResult = Prim.run(g, 0);
                Kruskal.Result kruskalResult = Kruskal.run(g);

                assertEquals(primResult.totalCost, kruskalResult.totalCost, 0.001,
                        name + ": Costs should be identical");

                assertEquals(g.vertices() - 1, primResult.mst.size(),
                        name + ": Prim should have V-1 edges");
                assertEquals(g.vertices() - 1, kruskalResult.mst.size(),
                        name + ": Kruskal should have V-1 edges");

                assertFalse(hasCycle(primResult.mst, g.vertices()),
                        name + ": Prim MST should be acyclic");
                assertFalse(hasCycle(kruskalResult.mst, g.vertices()),
                        name + ": Kruskal MST should be acyclic");

                assertTrue(isConnected(primResult.mst, g.vertices()),
                        name + ": Prim MST should connect all vertices");
                assertTrue(isConnected(kruskalResult.mst, g.vertices()),
                        name + ": Kruskal MST should connect all vertices");
            }
        }
    }

    @Test
    @DisplayName("Triangle graph correctness")
    public void testTriangleGraph() {
        Graphs g = new Graphs(3);
        g.addEdge(new Edges(0, 1, 1.0));
        g.addEdge(new Edges(1, 2, 2.0));
        g.addEdge(new Edges(0, 2, 3.0));

        Prim.Result primResult = Prim.run(g, 0);
        Kruskal.Result kruskalResult = Kruskal.run(g);

        assertEquals(2, primResult.mst.size());
        assertEquals(2, kruskalResult.mst.size());
        assertEquals(3.0, primResult.totalCost, 0.001);
        assertEquals(3.0, kruskalResult.totalCost, 0.001);
        assertFalse(hasCycle(primResult.mst, 3));
        assertFalse(hasCycle(kruskalResult.mst, 3));
        assertTrue(isConnected(primResult.mst, 3));
        assertTrue(isConnected(kruskalResult.mst, 3));
    }

    @Test
    @DisplayName("Complete graph correctness")
    public void testCompleteGraph() {
        Graphs g = new Graphs(4);
        g.addEdge(new Edges(0, 1, 1.0));
        g.addEdge(new Edges(0, 2, 4.0));
        g.addEdge(new Edges(0, 3, 3.0));
        g.addEdge(new Edges(1, 2, 2.0));
        g.addEdge(new Edges(1, 3, 5.0));
        g.addEdge(new Edges(2, 3, 6.0));

        Prim.Result primResult = Prim.run(g, 0);
        Kruskal.Result kruskalResult = Kruskal.run(g);

        assertEquals(3, primResult.mst.size());
        assertEquals(3, kruskalResult.mst.size());
        assertEquals(6.0, primResult.totalCost, 0.001);
        assertEquals(6.0, kruskalResult.totalCost, 0.001);
        assertFalse(hasCycle(primResult.mst, 4));
        assertFalse(hasCycle(kruskalResult.mst, 4));
        assertTrue(isConnected(primResult.mst, 4));
        assertTrue(isConnected(kruskalResult.mst, 4));
    }

    private Graphs createTestGraph1() {
        Graphs g = new Graphs(5);
        g.addEdge(new Edges(0, 1, 2.0));
        g.addEdge(new Edges(1, 2, 3.0));
        g.addEdge(new Edges(2, 3, 1.0));
        g.addEdge(new Edges(3, 4, 4.0));
        g.addEdge(new Edges(0, 4, 5.0));
        return g;
    }

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