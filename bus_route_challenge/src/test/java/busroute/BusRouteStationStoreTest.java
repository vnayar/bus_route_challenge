package busroute;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import com.google.common.collect.ImmutableSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by vnayar on 11/15/16.
 */
public class BusRouteStationStoreTest {

  StringReader routes1;

  public BusRouteStationStoreTest() {
    String input1 = new StringBuilder()
        .append("3\n")
        .append("0 0 1 2 3 4\n")
        .append("1 3 1 6 5\n")
        .append("2 0 6 4\n")
        .toString();
    routes1 = new StringReader(input1);
  }

  @Test
  public void test_loadBusRoutes() throws IOException {
    BusRouteStationStore stationStore = new BusRouteStationStore();
    stationStore.loadBusRoutes(routes1);

    BusRouteStationStore.Station station3 = stationStore.getStation(3);
    Assert.assertEquals(ImmutableSet.<Integer>of(0, 1), station3.arrivals);
    Assert.assertEquals(ImmutableSet.<Integer>of(0, 1), station3.departures);

    BusRouteStationStore.Station station6 = stationStore.getStation(6);
    Assert.assertEquals(ImmutableSet.<Integer>of(1, 2), station6.arrivals);
    Assert.assertEquals(ImmutableSet.<Integer>of(1, 2), station6.departures);
  }

  @Test
  public void test_hasDirectRoute() throws IOException {
    BusRouteStationStore stationStore = new BusRouteStationStore();
    stationStore.loadBusRoutes(routes1);

    assertTrue(stationStore.hasDirectRoute(3, 6));
    // We are assuming that routes are directed.  There may be a case where Bus A goes
    // from station 1, 2, 3, and then changes route to Bus B before continuing.  This means
    // that the bus makes no return trip for that route.
    assertFalse(stationStore.hasDirectRoute(2, 5));
  }

}
