package Util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CSVwriter {

    public static void writeCSV(String filename, List<ResultRecord> results) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("Graph Name,Vertices,Edges," +
                    "Prim Total Cost,Prim Time (ms),Prim Total Operations," +
                    "Kruskal Total Cost,Kruskal Time (ms),Kruskal Total Operations," +
                    "Costs Match,Faster Algorithm,Time Difference (ms)");

            // Write data
            for (ResultRecord r : results) {
                long primTotalOps = r.primOps.values().stream().mapToLong(Long::longValue).sum();
                long kruskalTotalOps = r.kruskalOps.values().stream().mapToLong(Long::longValue).sum();
                boolean costsMatch = Math.abs(r.primTotalCost - r.kruskalTotalCost) < 0.001;

                String fasterAlgorithm;
                long timeDiff;
                if (r.primTimeMs < r.kruskalTimeMs) {
                    fasterAlgorithm = "Prim";
                    timeDiff = r.kruskalTimeMs - r.primTimeMs;
                } else if (r.kruskalTimeMs < r.primTimeMs) {
                    fasterAlgorithm = "Kruskal";
                    timeDiff = r.primTimeMs - r.kruskalTimeMs;
                } else {
                    fasterAlgorithm = "Equal";
                    timeDiff = 0;
                }

                writer.printf("%s,%d,%d,%.2f,%d,%d,%.2f,%d,%d,%s,%s,%d%n",
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
                        fasterAlgorithm,
                        timeDiff);
            }
        }
    }
}