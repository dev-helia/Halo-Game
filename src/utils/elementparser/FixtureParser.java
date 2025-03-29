package utils.elementparser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
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
  public static void parseFixtures(JsonObject root, Map<Integer, Room> worldMap, List<Fixture> allFixtures) {
    if (!root.has("fixtures") || !root.get("fixtures").isJsonArray()) {
      System.err.println("Warning: No 'fixtures' array found.");
      return;
    }

    JsonArray fixturesArray = root.getAsJsonArray("fixtures");

    // 🛠️ 1. 先解析所有 fixtures
    for (JsonElement element : fixturesArray) {
      if (!element.isJsonObject()) continue;
      JsonObject fixtureObj = element.getAsJsonObject();

      try {
// Extract fixture fields and handle missing data (clamping)
        String name = fixtureObj.has("name") ? fixtureObj.get("name").getAsString() : "Unnamed Fixture";  // Clamp missing name
        String desc = fixtureObj.has("description") ? fixtureObj.get("description").getAsString() : "No description available";  // Clamp missing description
        double weight = fixtureObj.has("weight") ? fixtureObj.get("weight").getAsDouble() : 0.0;  // Clamp missing weight

        Fixture fixture = new Fixture(name, desc, weight);
        allFixtures.add(fixture);
      } catch (Exception e) {
        System.err.println("Skipping invalid fixture: " + e.getMessage());
      }
    }

    // 🛠️ 2. 塞回每个 room
    for (Room room : worldMap.values()) {
      String raw = room.getRawField("fixtures");
      if (raw == null) continue;

      List<Fixture> roomFixtures = new ArrayList<>();
      for (String name : raw.split(",")) {
        String trimmed = name.trim();
        for (Fixture fix : allFixtures) {
          if (fix.getName().equalsIgnoreCase(trimmed)) {
            roomFixtures.add(fix);
            break;
          }
        }
      }
      room.setFixtures(roomFixtures);
    }
  }

}

