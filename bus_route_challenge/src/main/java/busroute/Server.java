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
 * Created by vnayar on 11/15/16.
 */
public class Server {

  private static BusRouteStationStore busRouteStationStore = new BusRouteStationStore();

  private static class ApiDirectResponse {
    @SerializedName("dep_sid") private int depSid;
    @SerializedName("arr_sid") private int arrSid;
    @SerializedName("direct_bus_route") private boolean directBusRoute;

    public ApiDirectResponse(int depSid, int arrSid, boolean directBusRoute) {
      this.depSid = depSid;
      this.arrSid = arrSid;
      this.directBusRoute = directBusRoute;
    }
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Input file name must be provided.");
      return;
    }
    try {
      busRouteStationStore.loadBusRoutes(new FileReader(args[0]));
    } catch(FileNotFoundException e) {
      System.out.println("Could not locate file " + args[0]);
    } catch (IOException e) {
      System.out.println("File format violation in " + args[0]);
    }

    port(8088);
    get("/hello", (request, response) -> "Hello World");
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
