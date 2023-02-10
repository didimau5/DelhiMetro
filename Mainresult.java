
import java.util.HashMap;
import java.util.ArrayList;
import java.io.*;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.List;


public class Mainresult
{
    static Metro delhi=new Metro();

    public static void main(String[] args)
    {
        delhi.readLines("lines.txt");
        System.out.println("number of stations:" + delhi.stations().size());
        Metro.printStationListWithLines("\nList of hubs:\n",delhi.getAllConnectionHubs());
        if (args.length==2)
        {
            Metro.printStationList("\nShortest Path Stations: "+args[0]+" => "+args[1]+"\n",delhi.shortestPath(args[0],args[1]));
            long startTime = System.currentTimeMillis();
            ArrayList<ArrayList<Station>> res=delhi.allPaths(args[0],args[1]);
            long endTime = System.currentTimeMillis();
            System.out.println("\n Number of different paths found between "+args[0]+" and "+args[1]+":"+res.size()+"\n");
            System.out.println("Calculation took " + (endTime - startTime) + " ms!");
        }

    }
       
}

class Station
{
//class Station
boolean visited=false;
String _name;
ArrayList<Line> _lines=new ArrayList<Line>();
Station (String n) {_name=n;}
//accessors
public String name(){return _name;}
public ArrayList<Line> lines(){return _lines;}
public void setVisited() {visited=true;}
public void setUnvisited() {visited=false;}
public boolean hasBeenVisited() {return visited;}
public ArrayList<Station> neighbourStations()
{
//code to be completed

        ArrayList<Station> all_neighbours = new ArrayList<Station>();

        for (Line l: this._lines) {

            int this_station_idx = l.index(this);
            ArrayList<Station> line_stations = l.stations();

            int left_neighbour = this_station_idx-1;
            int right_neighbour = this_station_idx+1;

            if (left_neighbour < line_stations.size() && left_neighbour >= 0)
                all_neighbours.add(line_stations.get(left_neighbour));
            if (right_neighbour < line_stations.size())
                all_neighbours.add(line_stations.get(right_neighbour));

        }

        return all_neighbours;
}

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Station)) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(_name, station._name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_name);
    }
}

class Line
{
//class Line
String _name;
ArrayList<Station> stations=new ArrayList<Station>();
HashMap<Station,Integer> stationMap =new HashMap<Station,Integer>(); //needed to quickly access the index of the station in the ArrayList
Line(String name){ this._name=new String(name);}
//accessors
public String name(){return _name;}
public ArrayList<Station>  stations(){return stations;}
public int index(Station s)
{
//code to be completed
        if (this.stationMap.containsKey(s))
    return this.stationMap.get(s);

        return -1;
    }

public void addStation(Station s)
{
//code to be completed

        this.stations.add(s);
        this.stationMap.put(s, this.stations.size()-1);
}

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Line)) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(_name, line._name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_name);
    }

}

class Metro
{
//class Metro
    ArrayList<Line> _lines = new ArrayList<Line>();
    ArrayList<Station> _stations= new ArrayList<Station>();

    //accessors
    ArrayList<Line> lines() {return _lines;}
    public ArrayList<Station> stations() {return _stations;}
    public HashMap<String,Station> stationMap =new HashMap<String,Station>(); //needed to quickly access the index of the station in the ArrayList

    public static void printStationList(String message, ArrayList<Station> slist)
    {
    System.out.println(message);
    for (Station s:slist){
        System.out.println("=>"+s.name());
        System.out.println("\t("+slist.size()+" stations)\n");
        }
    }

    public static void printStationListWithLines(String message, ArrayList<Station> slist)
    {
    System.out.println(message);
    for (Station s:slist)
    {
        System.out.print("=>"+s.name()+"\t\t");
        for (Line l:s.lines()){
            System.out.print("\t*"+l.name());
            System.out.println();
        }
    }
    System.out.println("\t("+slist.size()+" stations)\n");
    }

    public Station getStation(String name)
    {
    return stationMap.get(name);
    }

    //this function getAllConnectionHubs is to be completed. It shall returns the list of all the stations that are a hub.
    public ArrayList<Station>  getAllConnectionHubs()
    {

    //code to be completed
    return new ArrayList<Station>(this.stations().stream().filter(s -> s.lines().size() > 1).collect(Collectors.toList()));

    }

    public ArrayList<Station>  shortestPath(String a, String b)
    {
    //code to be completed

            ArrayList<Station> path = new ArrayList<Station>();

            String src_station = a;
            String dest_station = b;
            ArrayList<String> stations_queue = new ArrayList<String>(List.of(src_station));
            HashMap<String, Boolean> visited = new HashMap<>();
            HashMap<String, Integer> distances = new HashMap<>();
            HashMap<String, String> predecessors = new HashMap<>();

            for (Station s: this._stations) {
                visited.put(s.name(), false);
                distances.put(s.name(), Integer.MAX_VALUE);
                predecessors.put(a, "none");
            }

            visited.put(src_station, true);
            distances.put(src_station, 0);
            stations_queue.add(src_station);

            boolean found_path = false;

            while (!found_path && stations_queue.size() > 0) {

                String current_station = stations_queue.remove(0);

                for (Station adj_station: this.getStation(current_station).neighbourStations()) {

                    String adjacent_station_name = adj_station.name();

                    if (visited.get(adjacent_station_name) == false) {

                        visited.put(adjacent_station_name, true);
                        distances.put(adjacent_station_name, distances.get(current_station) + 1);
                        predecessors.put(adjacent_station_name, current_station);
                        stations_queue.add(adjacent_station_name);

                        if (adjacent_station_name.equals(dest_station)) {
                            found_path = true;
                            break;
                        }
                    }
                }
            }

            if (found_path == true) {

                String reverse_search_station = dest_station;
                path.add(this.getStation(reverse_search_station));
                String predecessor = predecessors.get(reverse_search_station);
                while (!predecessor.equals("none")) {
                    path.add(this.getStation(predecessor));
                    reverse_search_station = predecessor;
                    predecessor = predecessors.get(reverse_search_station);
                }
            }
                ArrayList<Station> result = new ArrayList<>();

                for (int i = path.size()-1; i >= 0 ; i--) {
                    result.add(path.get(i));
                }



            // Collections.reverse(path);

    // return path;
            return result;
    }

        public void dfs(String src, String dst, HashMap<String, Boolean> visited, ArrayList<Station> current_path, ArrayList<ArrayList<Station>> all_paths) {

            if (src.equals(dst)) {
                all_paths.add(current_path);
                return;
            }

            visited.put(src, true);

            for (Station adj_station: this.getStation(src).neighbourStations()) {

                if (!visited.get(adj_station.name())) {

                    current_path.add(adj_station);
                    int last_pos = current_path.size() - 1;
                    dfs(adj_station.name(), dst, visited, current_path, all_paths);
                    current_path.remove(last_pos);
                }
            }

            visited.put(src, false);

            return;
        }

    public ArrayList<ArrayList<Station>>  allPaths(String a, String b)
    {
    //code to be completed

            ArrayList<ArrayList<Station>> all_paths = new ArrayList<>();

            HashMap<String, Boolean> visited = new HashMap<>();
            ArrayList<Station> current_path = new ArrayList<>();

            for (Station s: this._stations) {
                visited.put(s.name(), false);
            }

            dfs(a, b, visited, current_path, all_paths);

            return all_paths;
    }

    void readLines(String txt)
    {
    //reading the metro lines
    try (BufferedReader br = new BufferedReader(new FileReader(txt))) {
    String line;
    Line currentLine=null;
    while ((line = br.readLine()) != null)
    {
    if (line.length()>0)
    if (line.contains(":"))
    {   //We have a new line
    currentLine=new Line(line.replace(":",""));
    lines().add(currentLine);
    }
    else
    {
    //we have a station
    Station s=stationMap.get(line);
    if (s==null)
    {
    //station is new
    s=new Station(line);
    _stations.add(s);
    stationMap.put(line,s); //Adding a station into the hashmap so that it can be retrieved quickly.
    }
    s.lines().add(currentLine);
    currentLine.addStation(s);
    }
      // process the line.
    }
    }
    catch (IOException e)
    {
        e.printStackTrace();
    }
}
}



