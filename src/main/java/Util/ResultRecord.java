package Util;

import java.util.Map;

public class ResultRecord {
    public String graphName;
    public int vertices;
    public int edges;

    public double primTotalCost;
    public long primTimeMs;
    public Map<String, Long> primOps;

    public double kruskalTotalCost;
    public long kruskalTimeMs;
    public Map<String, Long> kruskalOps;

    public ResultRecord(String graphName, int vertices, int edges,
                        double primTotalCost, long primTimeMs, Map<String, Long> primOps,
                        double kruskalTotalCost, long kruskalTimeMs, Map<String, Long> kruskalOps) {
        this.graphName = graphName;
        this.vertices = vertices;
        this.edges = edges;
        this.primTotalCost = primTotalCost;
        this.primTimeMs = primTimeMs;
        this.primOps = primOps;
        this.kruskalTotalCost = kruskalTotalCost;
        this.kruskalTimeMs = kruskalTimeMs;
        this.kruskalOps = kruskalOps;
    }

    public String toString() {
        return String.format("Graph: %s (V=%d, E=%d)\n" +
                        "  Prim:    Cost=%.2f, Time=%dms, Ops=%s\n" +
                        "  Kruskal: Cost=%.2f, Time=%dms, Ops=%s",
                graphName, vertices, edges,
                primTotalCost, primTimeMs, primOps,
                kruskalTotalCost, kruskalTimeMs, kruskalOps);
    }
}
