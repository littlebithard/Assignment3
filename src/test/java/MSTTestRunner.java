import Algoritm.Kruskal;
import Algoritm.Prim;
import Models.Graphs;
import Util.JsonIO;
import Util.ResultRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MSTTestRunner {

    public static void main(String[] args) {
        try {
            System.out.println("=== MST Algorithm Test Runner ===\n");

            String[] inputFiles = {
                    "src/main/resources/small.json",
                    "src/main/resources/medium.json",
                    "src/main/resources/large.json"
            };
            List<ResultRecord> allResults = new ArrayList<>();

            for (String inputFile : inputFiles) {
                System.out.println("Processing: " + inputFile);
                System.out.println("-".repeat(60));

                List<JsonIO.GraphData> graphs = JsonIO.readGraphs(inputFile);

                for (JsonIO.GraphData gd : graphs) {
                    Graphs g = gd.graph;
                    String name = gd.name;

                    System.out.println("\nGraph: " + name);
                    System.out.println("  Vertices: " + g.vertices());
                    System.out.println("  Edges: " + g.edgeCount());

                    Prim.Result primResult = Prim.run(g, 0);
                    System.out.println("\n  Prim's Algorithm:");
                    System.out.println("    Total Cost: " + primResult.totalCost);
                    System.out.println("    Time: " + primResult.timeMs + " ms");
                    System.out.println("    Operations: " + primResult.ops);
                    System.out.println("    MST Edges: " + primResult.mst.size());

                    Kruskal.Result kruskalResult = Kruskal.run(g);
                    System.out.println("\n  Kruskal's Algorithm:");
                    System.out.println("    Total Cost: " + kruskalResult.totalCost);
                    System.out.println("    Time: " + kruskalResult.timeMs + " ms");
                    System.out.println("    Operations: " + kruskalResult.ops);
                    System.out.println("    MST Edges: " + kruskalResult.mst.size());

                    boolean costsMatch = Math.abs(primResult.totalCost - kruskalResult.totalCost) < 0.001;
                    boolean edgeCountCorrect = primResult.mst.size() == g.vertices() - 1
                            && kruskalResult.mst.size() == g.vertices() - 1;

                    System.out.println("\n  Verification:");
                    System.out.println("    Costs match: " + (costsMatch ? "✓" : "✗"));
                    System.out.println("    Edge count correct (V-1): " + (edgeCountCorrect ? "✓" : "✗"));

                    ResultRecord record = new ResultRecord(
                            name, g.vertices(), g.edgeCount(),
                            primResult.totalCost, primResult.timeMs, primResult.ops,
                            kruskalResult.totalCost, kruskalResult.timeMs, kruskalResult.ops);
                    allResults.add(record);
                }
                System.out.println();
            }

            System.out.println("\nWriting results to output.json...");
            JsonIO.writeResults("output.json", allResults);
            System.out.println("✓ output.json created successfully");

            System.out.println("Writing results to results.csv...");
            writeResultsToCSV("results.csv", allResults);
            System.out.println("✓ results.csv created successfully");

            printSummaryTable(allResults);

            System.out.println("\n=== Test Runner Completed Successfully ===");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void writeResultsToCSV(String filename, List<ResultRecord> results) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Graph Name,Vertices,Edges," +
                    "Prim Total Cost,Prim Time (ms),Prim Operations," +
                    "Kruskal Total Cost,Kruskal Time (ms),Kruskal Operations," +
                    "Costs Match,Time Difference (ms)");

            for (ResultRecord r : results) {
                long primTotalOps = r.primOps.values().stream().mapToLong(Long::longValue).sum();
                long kruskalTotalOps = r.kruskalOps.values().stream().mapToLong(Long::longValue).sum();
                boolean costsMatch = Math.abs(r.primTotalCost - r.kruskalTotalCost) < 0.001;
                long timeDiff = Math.abs(r.primTimeMs - r.kruskalTimeMs);

                writer.printf("%s,%d,%d,%.2f,%d,%d,%.2f,%d,%d,%s,%d%n",
                        r.graphName,
                        r.vertices,
                        r.edges,
                        r.primTotalCost,
                        r.primTimeMs,
                        primTotalOps,
                        r.kruskalTotalCost,
                        r.kruskalTimeMs,
                        kruskalTotalOps,
                        costsMatch ? "Yes" : "No",
                        timeDiff);
            }
        }
    }

    private static void printSummaryTable(List<ResultRecord> results) {
        System.out.println("\n=== SUMMARY TABLE ===\n");
        System.out.printf("%-25s %8s %8s | %12s %10s %10s | %12s %10s %10s%n",
                "Graph", "Vertices", "Edges",
                "Prim Cost", "Time(ms)", "Ops",
                "Kruskal Cost", "Time(ms)", "Ops");
        System.out.println("-".repeat(120));

        for (ResultRecord r : results) {
            long primTotalOps = r.primOps.values().stream().mapToLong(Long::longValue).sum();
            long kruskalTotalOps = r.kruskalOps.values().stream().mapToLong(Long::longValue).sum();

            System.out.printf("%-25s %8d %8d | %12.2f %10d %10d | %12.2f %10d %10d%n",
                    r.graphName,
                    r.vertices,
                    r.edges,
                    r.primTotalCost,
                    r.primTimeMs,
                    primTotalOps,
                    r.kruskalTotalCost,
                    r.kruskalTimeMs,
                    kruskalTotalOps);
        }

        System.out.println("-".repeat(120));
    }
}