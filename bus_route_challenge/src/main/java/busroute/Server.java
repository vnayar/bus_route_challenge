package busroute;

import busroute.BusRouteStationStore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


import static spark.Spark.*;

/**
 * The main server for receiving REST requests.  Currently only the following action is supported:
 * - GET /api/direct?dep_sid=<departure_station_id>&arr_sid=<arrival_station_id>
 */
public class Server {

  private static BusRouteStationStore busRouteStationStore = new BusRouteStationStore();

  /**
   * A Java representation of the response returned from a GET /api/direct request.
   */
  private static class ApiDirectResponse {
    /// Departure station id.
    @SerializedName("dep_sid") private int depSid;
    /// Arrival station id.
    @SerializedName("arr_sid") private int arrSid;
    /// Whether a direct bus route exists or not.
    @SerializedName("direct_bus_route") private boolean directBusRoute;

    public ApiDirectResponse(int depSid, int arrSid, boolean directBusRoute) {
      this.depSid = depSid;
      this.arrSid = arrSid;
      this.directBusRoute = directBusRoute;
    }
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("ERROR: Missing bus route input file!");
      return;
    }
    try {
      busRouteStationStore.loadBusRoutes(new FileReader(args[0]));
    } catch(FileNotFoundException e) {
      System.out.println("ERROR: Could not locate file " + args[0]);
      return;
    } catch (IOException e) {
      System.out.println("ERROR: File format violation in " + args[0]);
      return;
    }

    port(8088);
    // http://localhost:8088/api/direct?dep_sid={}&arr_sid={}
    get("/api/direct", (request, response) -> {
      int departureId;
      int arrivalId;
      try {
        departureId = Integer.valueOf(request.queryParams("dep_sid"));
        arrivalId = Integer.valueOf(request.queryParams("arr_sid"));
      } catch(NumberFormatException e) {
        response.status(400);
        return "Parameters dep_sid and arr_sid must be valid integers.";
      }
      boolean hasDirectRoute = busRouteStationStore.hasDirectRoute(departureId, arrivalId);

      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      response.type("application/json");
      return gson.toJson(new ApiDirectResponse(departureId, arrivalId, hasDirectRoute));
    });
  }
}
