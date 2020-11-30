import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Graph {
        Hashtable<String, List<String>> adjacencyList;
        List<Edge> edges;

        public Graph() {
            adjacencyList = new Hashtable<>();
            edges = new ArrayList<>();
        }

        public class Edge {
            String source;
            String destination;
            int weightM;
            int weightT;

            public Edge (String source, String destination, int dist, int time) {
                this.source = source;
                this.destination = destination;
                weightM = dist;
                weightT = time;
            }
            public String getSource(){
                return this.source;
            }
            public String getDestination(){
                return this.destination;
            }
            public int getTime(){
                return this.weightT;
            }
            public int getWeight(){
                return this.weightM;
            }
        }

        public void addVertex(String location) {
            adjacencyList.putIfAbsent(location, new ArrayList<>());
        }

        public void addEdge(String start, String end, int dist, int time) {
            addVertex(start);
            addVertex(end);
            adjacencyList.get(start).add(end);
            adjacencyList.get(end).add(start);
            edges.add(new Edge(start, end, dist, time));
        }
        public Hashtable<String, List<String>> get(){
            return adjacencyList;
        }
        public List<Edge> getEdges(){
            return edges;
        }


        @Override
        public String toString () {
            String connection="";
            for (Edge edge : edges) {
                connection+= edge.source+ " ==> " +edge.destination +"     " +edge.weightM+"   " +edge.weightT+"\n";
            }
            return connection;
        }


    }