package Util;

import Models.*;
import java.io.*;
import java.util.*;

public class JsonIO {
    public static List<GraphData> readGraphs(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line.trim());
            }
        }

        List<GraphData> graphs = new ArrayList<>();
        String json = content.toString();

        int graphsStart = json.indexOf("\"graphs\"");
        if (graphsStart == -1)
            return graphs;

        int arrayStart = json.indexOf("[", graphsStart);
        int arrayEnd = findMatchingBracket(json, arrayStart);

        String graphsArray = json.substring(arrayStart + 1, arrayEnd);

        List<String> graphObjects = splitGraphObjects(graphsArray);

        for (String graphObj : graphObjects) {
            String name = extractStringValue(graphObj, "name");
            int vertices = extractIntValue(graphObj, "vertices");

            Graphs g = new Graphs(vertices);

            int edgesStart = graphObj.indexOf("\"edges\"");
            int edgesArrayStart = graphObj.indexOf("[", edgesStart);
            int edgesArrayEnd = findMatchingBracket(graphObj, edgesArrayStart);
            String edgesArray = graphObj.substring(edgesArrayStart + 1, edgesArrayEnd);

            List<String> edgeObjects = splitEdgeObjects(edgesArray);

            for (String edgeObj : edgeObjects) {
                int u = extractIntValue(edgeObj, "u");
                int v = extractIntValue(edgeObj, "v");
                double w = extractDoubleValue(edgeObj, "w");
                g.addEdge(new Edges(u, v, w));
            }

            graphs.add(new GraphData(name, g));
        }

        return graphs;
    }

    public static void writeResults(String filename, List<ResultRecord> results) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("{");
            writer.println("  \"results\": [");

            for (int i = 0; i < results.size(); i++) {
                ResultRecord rec = results.get(i);
                writer.println("    {");
                writer.println("      \"graphName\": \"" + rec.graphName + "\",");
                writer.println("      \"vertices\": " + rec.vertices + ",");
                writer.println("      \"edges\": " + rec.edges + ",");

                writer.println("      \"prim\": {");
                writer.println("        \"totalCost\": " + rec.primTotalCost + ",");
                writer.println("        \"timeMs\": " + rec.primTimeMs + ",");
                writer.println("        \"operations\": {");
                writeOperations(writer, rec.primOps, "          ");
                writer.println("        }");
                writer.println("      },");

                writer.println("      \"kruskal\": {");
                writer.println("        \"totalCost\": " + rec.kruskalTotalCost + ",");
                writer.println("        \"timeMs\": " + rec.kruskalTimeMs + ",");
                writer.println("        \"operations\": {");
                writeOperations(writer, rec.kruskalOps, "          ");
                writer.println("        }");
                writer.println("      }");

                if (i < results.size() - 1) {
                    writer.println("    },");
                } else {
                    writer.println("    }");
                }
            }

            writer.println("  ]");
            writer.println("}");
        }
    }

    private static void writeOperations(PrintWriter writer, Map<String, Long> ops, String indent) {
        int count = 0;
        int size = ops.size();
        for (Map.Entry<String, Long> entry : ops.entrySet()) {
            count++;
            writer.print(indent + "\"" + entry.getKey() + "\": " + entry.getValue());
            if (count < size) {
                writer.println(",");
            } else {
                writer.println();
            }
        }
    }

    private static List<String> splitGraphObjects(String array) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = 0;

        for (int i = 0; i < array.length(); i++) {
            char c = array.charAt(i);
            if (c == '{') {
                if (depth == 0)
                    start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    objects.add(array.substring(start, i + 1));
                }
            }
        }
        return objects;
    }

    private static List<String> splitEdgeObjects(String array) {
        return splitGraphObjects(array);
    }

    private static String extractStringValue(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = json.indexOf(search);
        if (keyIndex == -1)
            return "";

        int colonIndex = json.indexOf(":", keyIndex);
        int quoteStart = json.indexOf("\"", colonIndex);
        int quoteEnd = json.indexOf("\"", quoteStart + 1);

        return json.substring(quoteStart + 1, quoteEnd);
    }

    private static int extractIntValue(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = json.indexOf(search);
        if (keyIndex == -1)
            return 0;

        int colonIndex = json.indexOf(":", keyIndex);
        int numStart = colonIndex + 1;

        while (numStart < json.length() && Character.isWhitespace(json.charAt(numStart))) {
            numStart++;
        }

        int numEnd = numStart;
        while (numEnd < json.length() && (Character.isDigit(json.charAt(numEnd)) || json.charAt(numEnd) == '-')) {
            numEnd++;
        }

        return Integer.parseInt(json.substring(numStart, numEnd));
    }

    private static double extractDoubleValue(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = json.indexOf(search);
        if (keyIndex == -1)
            return 0.0;

        int colonIndex = json.indexOf(":", keyIndex);
        int numStart = colonIndex + 1;

        while (numStart < json.length() && Character.isWhitespace(json.charAt(numStart))) {
            numStart++;
        }

        int numEnd = numStart;
        while (numEnd < json.length() &&
                (Character.isDigit(json.charAt(numEnd)) ||
                        json.charAt(numEnd) == '.' ||
                        json.charAt(numEnd) == '-')) {
            numEnd++;
        }

        return Double.parseDouble(json.substring(numStart, numEnd));
    }

    private static int findMatchingBracket(String json, int openIndex) {
        char open = json.charAt(openIndex);
        char close = (open == '[') ? ']' : '}';
        int depth = 1;

        for (int i = openIndex + 1; i < json.length(); i++) {
            if (json.charAt(i) == open)
                depth++;
            else if (json.charAt(i) == close) {
                depth--;
                if (depth == 0)
                    return i;
            }
        }
        return json.length() - 1;
    }

    public static class GraphData {
        public String name;
        public Graphs graph;

        public GraphData(String name, Graphs graph) {
            this.name = name;
            this.graph = graph;
        }
    }
}
