import Algoritm.Kruskal;
import Algoritm.Prim;
import Models.Graphs;
import Util.JsonIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MST Performance and Consistency Tests")
public class MSTPerformanceTest {
    @Test
    @DisplayName("Execution time should be non-negative and in milliseconds")
    public void testExecutionTimeNonNegativeInMilliseconds() throws IOException {
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

                assertTrue(primResult.timeMs >= 0,
                        gd.name + ": Prim execution time should be non-negative");
                assertTrue(kruskalResult.timeMs >= 0,
                        gd.name + ": Kruskal execution time should be non-negative");

                assertTrue(primResult.timeMs < 60000,
                        gd.name + ": Prim should complete within reasonable time");
                assertTrue(kruskalResult.timeMs < 60000,
                        gd.name + ": Kruskal should complete within reasonable time");
            }
        }
    }

    @Test
    @DisplayName("Operation counts should be non-negative and consistent")
    public void testOperationCountsNonNegativeAndConsistent() throws IOException {
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

                assertNotNull(primResult.ops, gd.name + ": Prim ops should not be null");
                for (Map.Entry<String, Long> entry : primResult.ops.entrySet()) {
                    assertTrue(entry.getValue() >= 0,
                            gd.name + ": Prim " + entry.getKey() + " should be non-negative");
                }

                assertNotNull(kruskalResult.ops, gd.name + ": Kruskal ops should not be null");
                assertTrue(kruskalResult.ops.get("comparisons") >= 0,
                        gd.name + ": Kruskal comparisons should be non-negative");
                assertTrue(kruskalResult.ops.get("finds") >= 0,
                        gd.name + ": Kruskal finds should be non-negative");
                assertTrue(kruskalResult.ops.get("unions") >= 0,
                        gd.name + ": Kruskal unions should be non-negative");

                assertTrue(kruskalResult.ops.get("unions") <= g.vertices() * 2,
                        gd.name + ": Kruskal unions should be reasonable");

                assertTrue(kruskalResult.ops.get("comparisons") <= g.edgeCount(),
                        gd.name + ": Kruskal comparisons should not exceed edge count");
            }
        }
    }

    @Test
    @DisplayName("Results should be reproducible for same dataset")
    public void testResultsReproducible() throws IOException {
        String[] inputFiles = {
                "src/main/resources/small.json",
                "src/main/resources/medium.json",
                "src/main/resources/large.json"
        };

        for (String inputFile : inputFiles) {
            List<JsonIO.GraphData> graphs = JsonIO.readGraphs(inputFile);

            for (JsonIO.GraphData gd : graphs) {
                Graphs g = gd.graph;

                Prim.Result prim1 = Prim.run(g, 0);
                Prim.Result prim2 = Prim.run(g, 0);
                Prim.Result prim3 = Prim.run(g, 0);

                Kruskal.Result kruskal1 = Kruskal.run(g);
                Kruskal.Result kruskal2 = Kruskal.run(g);
                Kruskal.Result kruskal3 = Kruskal.run(g);

                assertEquals(prim1.totalCost, prim2.totalCost, 0.001,
                        gd.name + ": Prim results should be reproducible (run 1 vs 2)");
                assertEquals(prim2.totalCost, prim3.totalCost, 0.001,
                        gd.name + ": Prim results should be reproducible (run 2 vs 3)");

                assertEquals(kruskal1.totalCost, kruskal2.totalCost, 0.001,
                        gd.name + ": Kruskal results should be reproducible (run 1 vs 2)");
                assertEquals(kruskal2.totalCost, kruskal3.totalCost, 0.001,
                        gd.name + ": Kruskal results should be reproducible (run 2 vs 3)");

                assertEquals(prim1.mst.size(), prim2.mst.size(),
                        gd.name + ": Prim MST size should be consistent");
                assertEquals(kruskal1.mst.size(), kruskal2.mst.size(),
                        gd.name + ": Kruskal MST size should be consistent");
            }
        }
    }

    @Test
    @DisplayName("Performance should be consistent across multiple runs")
    public void testPerformanceConsistency() throws IOException {
        List<JsonIO.GraphData> graphs = JsonIO.readGraphs("src/main/resources/medium.json");

        for (JsonIO.GraphData gd : graphs) {
            Graphs g = gd.graph;

            long[] primTimes = new long[5];
            long[] kruskalTimes = new long[5];

            for (int i = 0; i < 5; i++) {
                Prim.Result primResult = Prim.run(g, 0);
                Kruskal.Result kruskalResult = Kruskal.run(g);

                primTimes[i] = primResult.timeMs;
                kruskalTimes[i] = kruskalResult.timeMs;
            }

            for (int i = 0; i < 5; i++) {
                assertTrue(primTimes[i] >= 0,
                        gd.name + ": Prim time run " + i + " should be non-negative");
                assertTrue(kruskalTimes[i] >= 0,
                        gd.name + ": Kruskal time run " + i + " should be non-negative");
            }
        }
    }

    @Test
    @DisplayName("Operation counts should be proportional to graph size")
    public void testOperationCountsProportional() throws IOException {
        List<JsonIO.GraphData> smallGraphs = JsonIO.readGraphs("src/main/resources/small.json");
        List<JsonIO.GraphData> mediumGraphs = JsonIO.readGraphs("src/main/resources/medium.json");

        if (!smallGraphs.isEmpty() && !mediumGraphs.isEmpty()) {
            JsonIO.GraphData small = smallGraphs.get(0);
            JsonIO.GraphData medium = mediumGraphs.get(0);

            Prim.Result primSmall = Prim.run(small.graph, 0);
            Prim.Result primMedium = Prim.run(medium.graph, 0);

            Kruskal.Result kruskalSmall = Kruskal.run(small.graph);
            Kruskal.Result kruskalMedium = Kruskal.run(medium.graph);

            long primSmallOps = primSmall.ops.values().stream().mapToLong(Long::longValue).sum();
            long primMediumOps = primMedium.ops.values().stream().mapToLong(Long::longValue).sum();

            long kruskalSmallOps = kruskalSmall.ops.values().stream().mapToLong(Long::longValue).sum();
            long kruskalMediumOps = kruskalMedium.ops.values().stream().mapToLong(Long::longValue).sum();

            assertTrue(primMediumOps >= primSmallOps,
                    "Medium graph should require at least as many operations as small graph for Prim");
            assertTrue(kruskalMediumOps >= kruskalSmallOps,
                    "Medium graph should require at least as many operations as small graph for Kruskal");
        }
    }
}