package Assignment3.test;

import Assignment3.src.java.algorithm.*;
import Assignment3.src.java.model.*;
import java.util.List;

public class MSTtest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=== Running MST Tests ===\n");

        MSTtest test = new MSTtest();

        test.correctnessPrimKruskalTotalCostEqual();
        test.edgesCountIsVminus1();
        test.acyclicAndConnected();
        test.executionTimeNonNegative();
        test.operationCountsNonNegative();
        test.testSmallGraph();
        test.testDisconnectedGraph();
        test.testSingleVertex();
        test.testTwoVertices();

        System.out.println("\n=== Test Results ===");
        System.out.println("Tests Passed: " + testsPassed);
        System.out.println("Tests Failed: " + testsFailed);
        System.out.println("Total Tests: " + (testsPassed + testsFailed));

        if (testsFailed == 0) {
            System.out.println("\n All tests passed!");
        } else {
            System.out.println("\n Some tests failed!");
        }
    }

    private Graphs sampleGraph() {
        Graphs g = new Graphs(6);
        g.addEdge(new Edges(0, 1, 6));
        g.addEdge(new Edges(0, 2, 1));
        g.addEdge(new Edges(0, 3, 5));
        g.addEdge(new Edges(1, 2, 5));
        g.addEdge(new Edges(1, 4, 3));
        g.addEdge(new Edges(2, 3, 5));
        g.addEdge(new Edges(2, 4, 6));
        g.addEdge(new Edges(3, 4, 2));
        g.addEdge(new Edges(3, 5, 4));
        g.addEdge(new Edges(4, 5, 6));
        return g;
    }

    public void correctnessPrimKruskalTotalCostEqual() {
        String testName = "correctnessPrimKruskalTotalCostEqual";
        try {
            Graphs g = sampleGraph();
            Prim.Result p = Prim.run(g, 0);
            Kruskal.Result k = Kruskal.run(g);

            if (Math.abs(p.totalCost - k.totalCost) < 1e-9) {
                pass(testName);
            } else {
                fail(testName, "Costs not equal: Prim=" + p.totalCost + ", Kruskal=" + k.totalCost);
            }
        } catch (Exception e) {
            fail(testName, e.getMessage());
        }
    }

    public void edgesCountIsVminus1() {
        String testName = "edgesCountIsVminus1";
        try {
            Graphs g = sampleGraph();
            Prim.Result p = Prim.run(g, 0);
            Kruskal.Result k = Kruskal.run(g);

            boolean primOk = (p.mst.size() == g.vertices() - 1);
            boolean kruskalOk = (k.mst.size() == g.vertices() - 1);

            if (primOk && kruskalOk) {
                pass(testName);
            } else {
                fail(testName, "Edge count mismatch: Prim=" + p.mst.size() +
                        ", Kruskal=" + k.mst.size() + ", Expected=" + (g.vertices() - 1));
            }
        } catch (Exception e) {
            fail(testName, e.getMessage());
        }
    }

    public void acyclicAndConnected() {
        String testName = "acyclicAndConnected";
        try {
            Graphs g = sampleGraph();
            Prim.Result p = Prim.run(g, 0);

            if (!checkAcyclicAndConnected(p.mst, g.vertices())) {
                fail(testName, "Prim MST has cycles or is disconnected");
                return;
            }

            Kruskal.Result k = Kruskal.run(g);
            if (!checkAcyclicAndConnected(k.mst, g.vertices())) {
                fail(testName, "Kruskal MST has cycles or is disconnected");
                return;
            }

            pass(testName);
        } catch (Exception e) {
            fail(testName, e.getMessage());
        }
    }

    private boolean checkAcyclicAndConnected(List<Edges> edges, int V) {
        int[] parent = new int[V];
        for (int i = 0; i < V; i++)
            parent[i] = i;

        for (Edges e : edges) {
            int a = find(parent, e.u);
            int b = find(parent, e.v);
            if (a == b) {
                System.out.println("    Cycle detected!");
                return false;
            }
            parent[a] = b;
        }

        int root = find(parent, 0);
        for (int i = 1; i < V; i++) {
            if (find(parent, i) != root) {
                System.out.println("    Graph not connected!");
                return false;
            }
        }

        return true;
    }

    private int find(int[] p, int x) {
        return p[x] == x ? x : (p[x] = find(p, p[x]));
    }

    public void executionTimeNonNegative() {
        String testName = "executionTimeNonNegative";
        try {
            Graphs g = sampleGraph();
            Prim.Result p = Prim.run(g, 0);
            Kruskal.Result k = Kruskal.run(g);

            if (p.timeMs >= 0 && k.timeMs >= 0) {
                pass(testName);
            } else {
                fail(testName, "Negative time: Prim=" + p.timeMs + "ms, Kruskal=" + k.timeMs + "ms");
            }
        } catch (Exception e) {
            fail(testName, e.getMessage());
        }
    }

    public void operationCountsNonNegative() {
        String testName = "operationCountsNonNegative";
        try {
            Graphs g = sampleGraph();
            Prim.Result p = Prim.run(g, 0);
            Kruskal.Result k = Kruskal.run(g);

            boolean primOk = p.ops.values().stream().allMatch(v -> v >= 0);
            boolean kruskalOk = k.ops.values().stream().allMatch(v -> v >= 0);

            if (primOk && kruskalOk) {
                pass(testName);
            } else {
                fail(testName, "Negative operation counts found");
            }
        } catch (Exception e) {
            fail(testName, e.getMessage());
        }
    }

    public void testSmallGraph() {
        String testName = "testSmallGraph";
        try {
            Graphs g = new Graphs(4);
            g.addEdge(new Edges(0, 1, 1.0));
            g.addEdge(new Edges(0, 2, 2.0));
            g.addEdge(new Edges(1, 2, 3.0));
            g.addEdge(new Edges(1, 3, 4.0));

            Prim.Result p = Prim.run(g, 0);
            Kruskal.Result k = Kruskal.run(g);

            boolean costsOk = Math.abs(p.totalCost - 7.0) < 1e-9 &&
                    Math.abs(k.totalCost - 7.0) < 1e-9;
            boolean sizesOk = p.mst.size() == 3 && k.mst.size() == 3;

            if (costsOk && sizesOk) {
                pass(testName);
            } else {
                fail(testName, "Expected cost=7.0, size=3. Got Prim: cost=" +
                        p.totalCost + ", size=" + p.mst.size() +
                        ", Kruskal: cost=" + k.totalCost + ", size=" + k.mst.size());
            }
        } catch (Exception e) {
            fail(testName, e.getMessage());
        }
    }

    public void testDisconnectedGraph() {
        String testName = "testDisconnectedGraph";
        try {
            Graphs g = new Graphs(4);
            g.addEdge(new Edges(0, 1, 1.0));

            Prim.Result p = Prim.run(g, 0);
            Kruskal.Result k = Kruskal.run(g);

            if (p.mst.size() == 1 && k.mst.size() == 1) {
                pass(testName);
            } else {
                fail(testName, "Expected 1 edge in MST. Got Prim=" +
                        p.mst.size() + ", Kruskal=" + k.mst.size());
            }
        } catch (Exception e) {
            fail(testName, e.getMessage());
        }
    }

    public void testSingleVertex() {
        String testName = "testSingleVertex";
        try {
            Graphs g = new Graphs(1);

            Prim.Result p = Prim.run(g, 0);
            Kruskal.Result k = Kruskal.run(g);

            boolean costsOk = Math.abs(p.totalCost) < 1e-9 && Math.abs(k.totalCost) < 1e-9;
            boolean sizesOk = p.mst.size() == 0 && k.mst.size() == 0;

            if (costsOk && sizesOk) {
                pass(testName);
            } else {
                fail(testName, "Single vertex should have cost=0, size=0");
            }
        } catch (Exception e) {
            fail(testName, e.getMessage());
        }
    }

    public void testTwoVertices() {
        String testName = "testTwoVertices";
        try {
            Graphs g = new Graphs(2);
            g.addEdge(new Edges(0, 1, 5.0));

            Prim.Result p = Prim.run(g, 0);
            Kruskal.Result k = Kruskal.run(g);

            boolean costsOk = Math.abs(p.totalCost - 5.0) < 1e-9 &&
                    Math.abs(k.totalCost - 5.0) < 1e-9;
            boolean sizesOk = p.mst.size() == 1 && k.mst.size() == 1;

            if (costsOk && sizesOk) {
                pass(testName);
            } else {
                fail(testName, "Expected cost=5.0, size=1");
            }
        } catch (Exception e) {
            fail(testName, e.getMessage());
        }
    }

    private void pass(String testName) {
        testsPassed++;
        System.out.println("PASS: " + testName);
    }

    private void fail(String testName, String reason) {
        testsFailed++;
        System.out.println("FAIL: " + testName);
        System.out.println("  Reason: " + reason);
    }
}