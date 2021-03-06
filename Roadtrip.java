import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.BufferedReader;
import java.io.*;


public class Roadtrip extends Graph{
    Hashtable<String, String> attracionsAl; //<K,V> == <Attraction, Location>
    Hashtable<String, Boolean> visited; //<K,V> == <Location, True/False>, "Known"
    Hashtable<String, String> recent; //<K,V> == <Location, Previous Location> "Path"
    Hashtable<String, Integer> distance;//<K,V> == <Location, Distance relative to start> "Cost"
    HashSet<String> cities; //List of all cities used for the visited iterator
    Graph graph; //Holds the Edges/Vertices and the Adjacency List
    int milesTravelled; 
    int timeTaken;

    public Roadtrip() {
        attracionsAl = new Hashtable<>(143);
        visited = new Hashtable<>();
        recent = new Hashtable<>();
        distance = new Hashtable<>();
        graph = new Graph();
        cities = new HashSet<>();
        milesTravelled = 0;
        timeTaken = 0;
    }
    List<String> route(String sCity, String eCity, List<String> attractions) {
        //Array list gps will contai nthe route from starting city to end city with cities travlled from attractions list
        //Created a hashtable contain falses fro each city so it would turn true once visitning the city
        //made data in distances to equalthe max number possible and make starting city 0 so it would become starting point
        //Dijkstra algorithm used to travel city to city
        //use variables to keep track of distance and time taken to travel

        ArrayList<String> gps = new ArrayList<>();
        Hashtable<String, List<String>> adjacencyList= graph.get();

        graph.addEdge(sCity, sCity, 0, 0);

        for (String city : cities) {
            if (city != null) {
                visited.put(city, false);
                distance.put(city, Integer.MAX_VALUE);
            }
        }

        distance.put(sCity, 0);

        for (String city : cities) {
            while (!visited.get(city)) {
                String vertex = least_cost_unknown_vertex();
                known(vertex);
                for (String v : adjacencyList.get(vertex)) {
                    int weight = edge_weight(vertex, v);
                    if (distance.get(v) > distance.get(vertex) + weight && !v.equals(vertex)) { //Vertex's cost greater, update
                        distance.put(v, distance.get(vertex) + weight);
                        recent.put(v, vertex);
                    }
                }
            }
        }

        //This block of code orders attractions needed to visit by how close they are from start distance/ location
        //meaning the closer the attraction is to the most recent visited city it takes in 
        //and then keeps checking for closes attraction to the nearest attraction visited
        //Keeps places in hashtable to better get data
        ArrayList<Integer> attractionToSort = new ArrayList<>();
        ArrayList<String> attractionsSorted = new ArrayList<>();
        Hashtable<Integer, String> converter = new Hashtable<>();

        for (String attraction : attractions) {
            attractionToSort.add(distance.get(attracionsAl.get(attraction)));
            converter.put(distance.get(attracionsAl.get(attraction)), attraction);
        }
        Collections.sort(attractionToSort);

        for (int rank : attractionToSort) {
            attractionsSorted.add(attracionsAl.get(converter.get(rank)));
        }

        attractionsSorted.add(0, sCity);

        //Visits the end location at the end if the attraction is present there
        if (attractionsSorted.contains(eCity)) {
            attractionsSorted.remove(eCity);
            attractionsSorted.add(eCity);
        }else{
            attractionsSorted.add(eCity);
        }

        Stack <String>stitch = new Stack<String>();


        for (int i = 0; i < attractionsSorted.size()-1; i++) {
            String current = attractionsSorted.get(i);
            String nextVertex = attractionsSorted.get(i + 1);
            String nextVertexTemp = attractionsSorted.get(i + 1);

            stitch.add(nextVertex);
            while (!current.equals(nextVertex)) {
                String prevCity = recent.get(nextVertex);

                milesTravelled += edge_weight(nextVertex, prevCity);
                timeTaken += edge_time(nextVertex, prevCity);
                stitch.add(prevCity);
                nextVertex = prevCity;
            }

            while (!stitch.isEmpty()) {
                gps.add((String) stitch.pop());
            }

            //after adding route of attraction to attraction it resets hashtables and get added dsitances from starting point to keep path updated
            visited = new Hashtable<>();
            recent = new Hashtable<>();
            distance = new Hashtable<>();

            for (String city : cities) {
                if (city != null) {
                    visited.put(city, false);
                    distance.put(city, Integer.MAX_VALUE);
                }
            }

            distance.put(nextVertexTemp, 0);

            for (String city : cities) {
                while (!visited.get(city)) {
                    String vertex = least_cost_unknown_vertex();
                    known(vertex);
                    for (String v : adjacencyList.get(vertex)) {
                        int weight = edge_weight(vertex, v);
                        if (distance.get(v) > distance.get(vertex) + weight && !v.equals(vertex)) {
                            distance.put(v, distance.get(vertex) + weight);
                            recent.put(v, vertex);
                        }
                    }
                }
            }
        }

        return gps;
    }

    private String least_cost_unknown_vertex() {
        String vertex = "";
        int min = Integer.MAX_VALUE;

        for (String city : cities) {
            if (!visited.get(city) && distance.get(city) <= min) {
                min = distance.get(city);
                vertex = city;
            }
        }
        return vertex;
    }

    private void known(String v) {
        if (v != null) {
            visited.put(v, true);
        }
    }

    public int edge_weight(String v1, String v2) {
        int weight = 0;
        List<Edge> graphEdges=graph.getEdges();
        for (Edge edge : graphEdges) {
            if (edge.getSource().equals(v1) && edge.getDestination().equals(v2)) {
                return edge.getWeight();
            }
            else if (edge.getSource().equals(v2) && edge.getDestination().equals(v1)) {
                return edge.getWeight();
            }
        }
        return weight;
    }

    public int edge_time(String v1, String v2) {
        int time = 0;
        List<Edge> graphEdges=graph.getEdges();
        for (Edge edge : graphEdges) {
            if (edge.getSource().equals(v1) && edge.getDestination().equals(v2)) {
                return edge.getTime();
            }
            else if (edge.getSource().equals(v2) && edge.getDestination().equals(v1)) {
                return edge.getTime();
            }
        }
        return time;
    }

    public void print(List<String> path) {
        System.out.println(path.toString());
        System.out.println("The total distance travelled: " + milesTravelled + " miles");
        System.out.println("The time taken for the trip: " + timeTaken + " minutes");
    }
    public void funZones(String attractionFile) {
        String attraction = attractionFile;
        String attractionContent = "";

        try (BufferedReader read = new BufferedReader(new FileReader(attraction))) {
            while ((attractionContent = read.readLine()) != null) {
                String[] line = attractionContent.split(",");
                attracionsAl.put(line[0],line[1]);
            }
        } catch (Exception e) {
            System.out.println("Error file is not found: attractions.csv");
            System.exit(0);
        }
        attracionsAl.remove("Attraction");
    }
    public void roadReader(String roadFile){
        String roads = roadFile;
        String roadContent = "";
        

        try (BufferedReader read = new BufferedReader(new FileReader(roads))) {
            while ((roadContent = read.readLine()) != null) {
                String[] line = roadContent.split(",");
                Integer distance = Integer.parseInt(line[2]);
                if(line[3].equals("10a")){
                    line[3]="100";
                }
                Integer time = Integer.parseInt(line[3]);
                if (line[0] != null && line[1] != null) {
                    graph.addEdge(line[0], line[1], distance, time);
                    cities.add(line[0]);
                    cities.add(line[1]);
                }
            }
        } catch (Exception e) {
            System.out.println("Error file not found roads.csv");
        }
    }
    public static void main(String args[]) throws FileNotFoundException {
        Roadtrip roadtrip = new Roadtrip();

        String roads = "roads.csv";
        String places = "attractions.csv";
        roadtrip.roadReader(roads);
        roadtrip.funZones(places);

        
        Scanner sc2= new Scanner(System.in);
        //Inputs from user as case sensitve so input correct name of place and attractions
        System.out.println("Choose a starting point");
        String sCity= sc2.nextLine();
        System.out.println("Choose an end point");
        String eCity= sc2.nextLine();
        List<String> attractions = new ArrayList<>();
        System.out.println("Add an Attraction");
        String fun= sc2.nextLine();
        attractions.add(fun);
        System.out.println("Want to add another? Enter Y for yes, N for no");
        String choice = sc2.nextLine();
        if(choice.equals("Y")){
            while(choice.equals("Y")){
                System.out.println("Add attraction");
                fun= sc2.nextLine();
                attractions.add(fun);
                System.out.println("Add another attracion? Enter Y for yes, N for no");
                choice= sc2.nextLine();
            }
        }
        List<String> path = roadtrip.route(sCity, eCity, attractions);
        roadtrip.print(path);

    }


}