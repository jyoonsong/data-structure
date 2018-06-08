import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Subway {

    /*
     *  Global Variables
     *  @param database     based on hashtable<code, vertex> using chaining
     *  @param indexTable   based on hashtable<name, vertices>
     */
    public static Hashtable<String, Vertex> database = new Hashtable<>();
    public static Hashtable<String, LinkedList<Vertex>> indexTable = new Hashtable<>();
    public static Dijkstra dijkstra;
    private static final int TRANSFER_TIME = 5;

    /*
     * Get command and name of data file from console
     * @param args
     */
    public static void main(String[] args) {
        // init data
        init(args[0]);

        // modification from skeleton code of Matching
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                String input = br.readLine();
                if (input.compareTo("QUIT") == 0)
                    break;

                command(input);

            } catch (IOException e) {
                System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
            }
        }
    }

    /*
     * Initialize subway data
     * @param dataFile
     */
    private static void init(String dataFile) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));

            // part 1: station data to vertex
            initStationData(br);

            // part 2: distance data to edge
            initDistanceData(br);

        } catch (IOException e) {
            System.out.println("파일이 잘못되었습니다. 오류 : " + e.toString());
        }

    }

    private static void initDistanceData(BufferedReader br) throws IOException {
        String input = br.readLine();
        while (input != null) {
            String[] distanceData = input.split("\\s+");
            String start = distanceData[0];
            String end = distanceData[1];
            long time = Long.parseLong(distanceData[2]);

            // save distance data in adjacency array
            database.get(start).addEdge(database.get(end), time);

            // read new line
            input = br.readLine();
        }
    }

    private static void initStationData(BufferedReader br) throws IOException {
        String input = br.readLine();
        dijkstra = new Dijkstra();

        while (input != null) {
            // detect blank line
            if (input.isEmpty() || input.charAt(0) == '\n')
                return;

            String[] stationData = input.split("\\s+");
            String code = stationData[0];
            String name = stationData[1];
            String line = stationData[2];

            // 1-1: save stations as vertices to database
            Vertex station = new Vertex(code, name, line);
            database.put(code, station);

            // 1-2: save name and code to index table
            LinkedList<Vertex> stationList = indexTable.get(name);
            if (stationList == null) {
                // create new index
                stationList = new LinkedList<>();
                stationList.add(station);
                indexTable.put(name, stationList);
            }
            else {
                // if transfer add the station to existing index
                stationList.add(station);
                initTransferableEdge(stationList, station);
            }

            // read next line
            input = br.readLine();
        }

    }

    private static void initTransferableEdge(LinkedList<Vertex> stationList, Vertex station) {
        for (int i = 0; i < stationList.size(); i++) {
            Vertex sameStation = stationList.get(i);
            Edge thisToSame = new Edge(sameStation, TRANSFER_TIME, true);
            Edge sameToThis = new Edge(station, TRANSFER_TIME, true);

            // add edge to both stations
            station.addEdge(thisToSame);
            sameStation.addEdge(sameToThis);
        }
    }

    /*
     * Process command and compute corresponding path
     * @param input
     */
    private static void command(String input) {
        String[] stations = input.split("\\s");

        LinkedList<Vertex> start = indexTable.get(stations[0]);
        LinkedList<Vertex> end = indexTable.get(stations[1]);

        if (stations.length == 2)
            dijkstra.getShortestPath(start, end);
        else if (stations.length == 3 && stations[2].equals("!"))
            dijkstra.getMinimumTransferPath(start, end);

        initGraph();
    }

    private static void initGraph() {
        for (Vertex v: database.values())
            v.init();
    }

}

/*
 * Dijkstra Algorithm
 * modification from PPT slides & '쉽게 배우는 알고리즘' by Prof.Moon
 */
class Dijkstra {
    private PriorityQueue<Vertex> heap;
    private final long MAX_TIME = 2000000000;

    private void initHeap(LinkedList<Vertex> start) {
        heap = new PriorityQueue<>();
        for (int i = 0; i < start.size(); i++) {
            Vertex startStation = start.get(i);
            startStation.setTime(0);
            startStation.setTransfer(0);
            heap.add(startStation);
        }
    }

    public void getShortestPath(LinkedList<Vertex> start, LinkedList<Vertex> end) {
        initHeap(start);
        Vertex curr;

        while (!heap.isEmpty()) {
            // Get the minimum as next
            curr = heap.remove();

            // Check outgoing edges of e
            for (Edge e : curr.adjacencyArray()) {
                Vertex to = e.getTo();
                long newTime = curr.getTime() + e.getTime();

                if (newTime < to.getTime())
                    updateVertex(curr, to, newTime);
            }
        }

        Vertex minEnd = findMin(end);
        printPath(minEnd);
    }

    public void getMinimumTransferPath(LinkedList<Vertex> start, LinkedList<Vertex> end) {
        initHeap(start);
        Vertex curr;

        while (!heap.isEmpty()) {
            // Get the minimum as next
            curr = heap.remove();

            // Check outgoing edges of e
            for (Edge e : curr.adjacencyArray()) {
                Vertex to = e.getTo();
                long newTime = curr.getTime() + e.getTime();

                // if transfer add max_time * 2
                // modification from an idea in StackOverFlow
                if (e.isTransfer())
                    newTime += MAX_TIME;

                if (newTime < to.getTime())
                    updateVertex(curr, to, newTime);
            }
        }

        Vertex minEnd = findMin(end);
        printPath(minEnd);
    }

    private void updateVertex(Vertex from, Vertex to, long newTime) {
        // update values
        to.setTime(newTime);
        to.setPrev(from);
        // and percolate the heap
        heap.remove(to);
        heap.add(to);
    }

    private Vertex findMin(LinkedList<Vertex> end) {
        // find appropriate end vertex among the same name but different code stations
        Vertex minEnd = end.getFirst();
        for (Vertex v : end)
            if (v.getTime() < minEnd.getTime())
                minEnd = v;
        return minEnd;
    }

    private void printPath(Vertex minEnd) {
        // invert the order using stack
        LinkedList<String> path = new LinkedList<>();
        Vertex curr = minEnd;
        while (curr != null) {
            path.push(curr.getName());
            curr = curr.getPrev();
        }

        // convert path to string
        String result = "";
        while (!path.isEmpty()) {
            String station = path.pop();

            // if transferred station print with braces
            if (station.equals(path.peek()))
                station = "[" + path.pop() + "]";

            result += (station + " ");
        }

        // print out the resulted path and time
        System.out.println( result.substring(0, result.length() - 1) );
        System.out.println( minEnd.getTime() % MAX_TIME );
    }
}

class Vertex implements Comparable<Vertex> {

    private final String code; // station code
    private final String name; // station name
    private final String line; // subway line
    private ArrayList<Edge> adjacencyArray; // sorted array

    private long time;
    private long transfer;
    private Vertex prev;

    public Vertex(String code, String name, String line) {
        this.code = code;
        this.name = name;
        this.line = line;
        this.adjacencyArray = new ArrayList<>();
        init();
    }

    public void init() {
        this.time = this.transfer = Long.MAX_VALUE;
        this.prev = null;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Edge> adjacencyArray() {
        return adjacencyArray;
    }

    public void addEdge(Vertex end, long time) {
        adjacencyArray.add(new Edge(end, time));
    }

    public void addEdge(Edge newEdge) {
        adjacencyArray.add(newEdge);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Vertex getPrev() {
        return prev;
    }

    public void setPrev(Vertex prev) {
        this.prev = prev;
    }

    @Override
    public int compareTo(Vertex other) {
        return Long.compare(this.time, other.time);
    }

    public long getTransfer() {
        return transfer;
    }

    public void setTransfer(long transfer) {
        this.transfer = transfer;
    }
}

class Edge {
    private final Vertex to;
    private final long time;
    private final boolean isTransfer; // if true, this edge is both directed

    public Edge(Vertex to, long time, boolean isTransfer) {
        this.to = to;
        this.time = time;
        this.isTransfer = isTransfer;
    }

    public Edge(Vertex to, long time) {
        this(to, time, false);
    }

    public Vertex getTo() {
        return to;
    }

    public long getTime() {
        return time;
    }

    public boolean isTransfer() {
        return isTransfer;
    }
}
