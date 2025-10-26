package Algoritm;

import java.util.*;
import Models.*;

public class Kruskal {
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

    private static class UnionFind {
        int[] parent;
        int[] rank;
        long finds = 0, unions = 0;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        int find(int x) {
            finds++;
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        boolean union(int a, int b) {
            unions++;
            int ra = find(a);
            int rb = find(b);
            if (ra == rb)
                return false;

            if (rank[ra] < rank[rb]) {
                parent[ra] = rb;
            } else if (rank[rb] < rank[ra]) {
                parent[rb] = ra;
            } else {
                parent[rb] = ra;
                rank[ra]++;
            }
            return true;
        }
    }

    public static Result run(Graphs g) {
        long startTime = System.nanoTime();

        List<Edges> edges = new ArrayList<>(g.edges());
        Collections.sort(edges);

        int V = g.vertices();
        UnionFind uf = new UnionFind(V);
        List<Edges> mst = new ArrayList<>();
        long comparisons = 0;

        for (Edges e : edges) {
            comparisons++;
            if (uf.find(e.u) != uf.find(e.v)) {
                if (uf.union(e.u, e.v)) {
                    mst.add(e);
                    if (mst.size() == V - 1)
                        break;
                }
            }
        }

        double total = mst.stream().mapToDouble(x -> x.w).sum();
        long endTime = System.nanoTime();
        long timeMs = (endTime - startTime) / 1_000_000;

        Map<String, Long> ops = new HashMap<>();
        ops.put("comparisons", comparisons);
        ops.put("finds", uf.finds);
        ops.put("unions", uf.unions);

        return new Result(mst, total, timeMs, ops);
    }

}
