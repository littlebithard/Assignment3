import java.io.IOException;
import java.util.*;
import Algoritm.*;
import Models.*;
import Util.*;

public class Main {
    public static void main(String[] args) {
        try {
            String[] inputFiles = {
                    "src/main/resources/small.json",
                    "src/main/resources/medium.json",
                    "src/main/resources/large.json"
            };
            List<ResultRecord> allResults = new ArrayList<>();

            System.out.println("=== MST Algorithm Comparison ===\n");

            for (String inputFile : inputFiles) {
                System.out.println("Processing: " + inputFile);
                System.out.println("-".repeat(50));

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

                    if (Math.abs(primResult.totalCost - kruskalResult.totalCost) < 0.001) {
                        System.out.println("\n  ✓ Both algorithms produce same total cost!");
                    } else {
                        System.out.println("\n  ✗ WARNING: Costs differ!");
                    }

                    ResultRecord record = new ResultRecord(
                            name, g.vertices(), g.edgeCount(),
                            primResult.totalCost, primResult.timeMs, primResult.ops,
                            kruskalResult.totalCost, kruskalResult.timeMs, kruskalResult.ops);
                    allResults.add(record);
                }
            }

            printSummaryTable(allResults);

            System.out.println("\nWriting results to output.json...");
            JsonIO.writeResults("output.json", allResults);
            System.out.println("✓ Results written to output.json");

            System.out.println("Writing results to results.csv...");
            CSVwriter.writeCSV("results.csv", allResults);
            System.out.println("✓ Results written to results.csv");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printSummaryTable(List<ResultRecord> results) {
        System.out.println("\n=== SUMMARY TABLE ===\n");
        System.out.printf("%-20s %8s %8s | %12s %8s | %12s %8s%n",
                "Graph", "Vertices", "Edges", "Prim(ms)", "Cost", "Kruskal(ms)", "Cost");
        System.out.println("-".repeat(85));

        for (ResultRecord r : results) {
            System.out.printf("%-20s %8d %8d | %12d %8.2f | %12d %8.2f%n",
                    r.graphName, r.vertices, r.edges,
                    r.primTimeMs, r.primTotalCost,
                    r.kruskalTimeMs, r.kruskalTotalCost);
        }
    }
}