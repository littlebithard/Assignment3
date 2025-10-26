package Models;

public class Edges implements Comparable<Edges> {
    public int u;
    public int v;
    public double w;

    public Edges(int u, int v, double w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    public int other(int vertex) {
        return (vertex == u) ? v : u;
    }

    public int compareTo(Edges o) {
        return Double.compare(this.w, o.w);
    }

    public String toString() {
        return String.format("%d %d %.2f", u, v, w);
    }
}