package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import model.core.Room;
import model.elements.Fixture;

public class FixtureParser {

  /**
   * Parses the "fixtures" section from the root JSON object.
   * For each fixture entry, this method extracts fixture data, clamps missing values,
   * creates a Fixture instance, and assigns it to the corresponding room.
   *
   * @param root     the root JSON object containing fixtures
   * @param worldMap the map of room IDs to Room objects
   */
  public static void parseFixtures(JsonObject root, Map<Integer, Room> worldMap) {
    if (root.has("fixtures")) {
      JsonArray fixturesArray = root.getAsJsonArray("fixtures");
      for (JsonElement element : fixturesArray) {
        JsonObject fixtureObj = element.getAsJsonObject();

        // Extract fixture fields and handle missing data (clamping)
        String name = fixtureObj.has("name") ? fixtureObj.get("name").getAsString() : "Unnamed Fixture";  // Clamp missing name
        String description = fixtureObj.has("description") ? fixtureObj.get("description").getAsString() : "No description available";  // Clamp missing description
        double weight = fixtureObj.has("weight") ? fixtureObj.get("weight").getAsDouble() : 0.0;  // Clamp missing weight

        // Create the Fixture object
        Fixture fixture = new Fixture(name, description, (int) weight);

        // Assign the fixture to the appropriate room
        if (fixtureObj.has("room_number")) {
          int roomNumber = fixtureObj.get("room_number").getAsInt();
          Room room = worldMap.get(roomNumber);
          if (room != null) {
            room.addFixture(fixture);  // Add the fixture to the room
          } else {
            System.err.println("Room " + roomNumber + " not found for fixture " + name);  // Error handling for missing room
          }
        }
      }
    }
  }
}

