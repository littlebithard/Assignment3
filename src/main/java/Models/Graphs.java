package Models;

import java.util.*;

public class Graphs {
    private int V;
    private List<Edges> edges;
    private List<List<Edges>> adj;

    public Graphs(int V) {
        this.V = V;
        this.edges = new ArrayList<>();
        this.adj = new ArrayList<>(V);
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
    }


    public int vertices() {
        return V;
    }

    public int edgeCount() {
        return edges.size();
    }

    public void addEdge(Edges e) {
        edges.add(e);
        adj.get(e.u).add(e);
        adj.get(e.v).add(e);
    }

    public List<Edges> edges() {
        return Collections.unmodifiableList(edges);
    }

    public List<Edges> adj(int v) {
        return Collections.unmodifiableList(adj.get(v));
    }
}
