package Assignment3.src.java.util;

import java.io.*;
import java.util.*;

public class CSVwriter {
    public static void writeResultsToCSV(String filename, List<ResultRecord> results) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(
                    "Graph Name,Vertices,Edges,Algorithm,Total Cost,Time (ms),Comparisons,Additional Operations");

            for (ResultRecord rec : results) {
                writer.printf("%s,%d,%d,Prim,%.2f,%d,%d,\"%s\"%n",
                        escapeCsv(rec.graphName),
                        rec.vertices,
                        rec.edges,
                        rec.primTotalCost,
                        rec.primTimeMs,
                        rec.primOps.getOrDefault("comparisons", 0L),
                        formatOperations(rec.primOps, "comparisons"));

                writer.printf("%s,%d,%d,Kruskal,%.2f,%d,%d,\"%s\"%n",
                        escapeCsv(rec.graphName),
                        rec.vertices,
                        rec.edges,
                        rec.kruskalTotalCost,
                        rec.kruskalTimeMs,
                        rec.kruskalOps.getOrDefault("comparisons", 0L),
                        formatOperations(rec.kruskalOps, "comparisons"));
            }
        }
    }

    public static void writeComparisonToCSV(String filename, List<ResultRecord> results) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(
                    "Graph Name,Vertices,Edges,Prim Cost,Prim Time (ms),Kruskal Cost,Kruskal Time (ms),Cost Match,Time Difference (ms)");

            for (ResultRecord rec : results) {
                boolean costMatch = Math.abs(rec.primTotalCost - rec.kruskalTotalCost) < 0.001;
                long timeDiff = rec.kruskalTimeMs - rec.primTimeMs;

                writer.printf("%s,%d,%d,%.2f,%d,%.2f,%d,%s,%d%n",
                        escapeCsv(rec.graphName),
                        rec.vertices,
                        rec.edges,
                        rec.primTotalCost,
                        rec.primTimeMs,
                        rec.kruskalTotalCost,
                        rec.kruskalTimeMs,
                        costMatch ? "YES" : "NO",
                        timeDiff);
            }
        }
    }

    public static void writeOperationsToCSV(String filename, List<ResultRecord> results) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Graph Name,Algorithm,Comparisons,Extracts,DecreaseKeys,Finds,Unions");

            for (ResultRecord rec : results) {
                writer.printf("%s,Prim,%d,%d,%d,-,-%n",
                        escapeCsv(rec.graphName),
                        rec.primOps.getOrDefault("comparisons", 0L),
                        rec.primOps.getOrDefault("extracts", 0L),
                        rec.primOps.getOrDefault("decreaseKeys", 0L));

                writer.printf("%s,Kruskal,%d,-,-,%d,%d%n",
                        escapeCsv(rec.graphName),
                        rec.kruskalOps.getOrDefault("comparisons", 0L),
                        rec.kruskalOps.getOrDefault("finds", 0L),
                        rec.kruskalOps.getOrDefault("unions", 0L));
            }
        }
    }

    public static void writePerformanceSummaryToCSV(String filename, List<ResultRecord> results) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(
                    "Graph Category,Avg Vertices,Avg Edges,Avg Prim Time (ms),Avg Kruskal Time (ms),Faster Algorithm");

            Map<String, List<ResultRecord>> categories = new LinkedHashMap<>();
            categories.put("Small", new ArrayList<>());
            categories.put("Medium", new ArrayList<>());
            categories.put("Large", new ArrayList<>());

            for (ResultRecord rec : results) {
                if (rec.graphName.startsWith("Small")) {
                    categories.get("Small").add(rec);
                } else if (rec.graphName.startsWith("Medium")) {
                    categories.get("Medium").add(rec);
                } else if (rec.graphName.startsWith("Large")) {
                    categories.get("Large").add(rec);
                }
            }

            for (Map.Entry<String, List<ResultRecord>> entry : categories.entrySet()) {
                String category = entry.getKey();
                List<ResultRecord> recs = entry.getValue();

                if (recs.isEmpty())
                    continue;

                double avgVertices = recs.stream().mapToInt(r -> r.vertices).average().orElse(0);
                double avgEdges = recs.stream().mapToInt(r -> r.edges).average().orElse(0);
                double avgPrimTime = recs.stream().mapToLong(r -> r.primTimeMs).average().orElse(0);
                double avgKruskalTime = recs.stream().mapToLong(r -> r.kruskalTimeMs).average().orElse(0);
                String faster = avgPrimTime < avgKruskalTime ? "Prim" : "Kruskal";

                writer.printf("%s,%.1f,%.1f,%.2f,%.2f,%s%n",
                        category, avgVertices, avgEdges, avgPrimTime, avgKruskalTime, faster);
            }
        }
    }

    private static String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static String formatOperations(Map<String, Long> ops, String excludeKey) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Long> entry : ops.entrySet()) {
            if (!entry.getKey().equals(excludeKey)) {
                if (sb.length() > 0)
                    sb.append(", ");
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        return sb.toString();
    }
}