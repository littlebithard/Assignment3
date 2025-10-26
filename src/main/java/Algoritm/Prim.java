package Algoritm;

import java.util.*;
import Models.*;

public class Prim {
    public static class Result {
        public List<Edges> mst;
        public double totalCost;
        public long timeMs;
        public Map<String, Long> ops;

        public Result(List<Edges> mst, double totalCost, long timeMs, Map<String, Long> ops) {
            this.mst = mst;
            this.totalCost = totalCost;
            this.timeMs = timeMs;
            this.ops = ops;
        }
    }

    public static Result run(Graphs g, int start) {
        long startTime = System.nanoTime();
        int V = g.vertices();
        boolean[] inMST = new boolean[V];
        double[] key = new double[V];
        Edges[] parentEdge = new Edges[V];
        Arrays.fill(key, Double.POSITIVE_INFINITY);
        key[start] = 0.0;

        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingDouble(v -> key[v]));
        for (int i = 0; i < V; i++)
            pq.add(i);

        List<Edges> mstEdges = new ArrayList<>();
        long comparisons = 0, extracts = 0, decreaseKeys = 0;

        while (!pq.isEmpty()) {
            int u = pq.poll();
            extracts++;

            if (key[u] == Double.POSITIVE_INFINITY)
                break;

            inMST[u] = true;
            if (parentEdge[u] != null) {
                mstEdges.add(parentEdge[u]);
            }

            for (Edges e : g.adj(u)) {
                int v = e.other(u);
                comparisons++;
                if (!inMST[v] && e.w < key[v]) {
                    key[v] = e.w;
                    parentEdge[v] = e;
                    pq.remove(v);
                    pq.add(v);
                    decreaseKeys++;
                }
            }
        }

        double total = mstEdges.stream().mapToDouble(x -> x.w).sum();
        long endTime = System.nanoTime();
        long timeMs = (endTime - startTime) / 1_000_000;

        Map<String, Long> ops = new HashMap<>();
        ops.put("comparisons", comparisons);
        ops.put("extracts", extracts);
        ops.put("decreaseKeys", decreaseKeys);

        return new Result(mstEdges, total, timeMs, ops);
    }
}
