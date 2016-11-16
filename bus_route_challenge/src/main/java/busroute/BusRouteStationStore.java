package busroute;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by vnayar on 11/15/16.
 */
public class BusRouteStationStore {

  private Map<Integer, Station> stationMap;

  public void loadBusRoutes(Reader reader) throws IOException {
    BufferedReader bufferedReader = new BufferedReader(reader);
    int numStations = Integer.valueOf(bufferedReader.readLine());

    stationMap = new HashMap<>(numStations);  // Provide the initial capacity.
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      String[] vals = line.split(" ");
      assert vals.length >= 3 : "Bus route and at least 2 stations expected on input line:\n" + line;

      // The bus route is the first value in the line.
      Integer routeId = Integer.valueOf(vals[0]);

      Station prevStation = getStation(Integer.valueOf(vals[1]));
      prevStation.arrivals.add(routeId);
      for (int i = 2; i < vals.length; i++) {
        Station curStation = getStation(Integer.valueOf(vals[i]));

        prevStation.departures.add(routeId);
        curStation.arrivals.add(routeId);

        prevStation = curStation;
      }
    }
  }

  public boolean hasDirectRoute(int departStationId, int arriveStationId) {
    Station departStation = getStation(departStationId);
    Station arriveStation = getStation(arriveStationId);

    Set<Integer> intersection = new HashSet<>(departStation.departures);
    intersection.retainAll(arriveStation.arrivals);

    return intersection.size() > 0;
  }

  public Station getStation(Integer stationId) {
    if (stationMap.containsKey(stationId)) {
      return stationMap.get(stationId);
    } else {
      Station station = new Station(stationId);
      stationMap.put(stationId, station);
      return station;
    }
  }

  public class Station {
    public int id;
    public Set<Integer> departures;
    public Set<Integer> arrivals;

    public Station(int id) {
      this.id = id;
      this.departures = new HashSet<Integer>();
      this.arrivals = new HashSet<Integer>();
    }
  }

}
