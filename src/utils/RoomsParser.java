package utils;

import com.google.gson.*;

import java.util.Map;

import model.core.Room;

import static utils.RoomParser.parseRoom;

/**
 * Parses an entire collection of rooms from a JSON root object.
 */
public class RoomsParser {
  /**
   * Safely parse rooms from root object.
   * Be tolerant of missing/invalid data.
   *
   * @param root the root json object
   */
  public static void parseRooms(JsonObject root, Map<Integer, Room> worldMap) {
    if (!root.has("rooms") || !root.get("rooms").isJsonArray()) {
      System.err.println("Warning: No 'rooms' field found or not an array. Skipping room parsing.");
      return;
    }

    JsonArray roomsArray = root.getAsJsonArray("rooms");

    for (JsonElement element : roomsArray) {
      if (!element.isJsonObject()) {
        System.err.println("Skipping non-object element in rooms array: " + element);
        continue;
      }

      JsonObject roomObj = element.getAsJsonObject();

      try {
        Room room = parseRoom(roomObj);
        if (room == null) {
          System.err.println("Skipping room: parseRoom returned null for: " + roomObj);
          continue;
        }

        String[] rawKeys = {"items", "fixtures", "monster", "puzzle", "picture"};
        for (String key : rawKeys) {
          if (roomObj.has(key) && !roomObj.get(key).isJsonNull()) {
            room.setRawField(key, roomObj.get(key).getAsString());
          }
        }

        int number = room.getRoomNumber();
        if (worldMap.containsKey(number)) {
          System.err.println("Duplicate room number: " + number + ", skipping.");
          continue;
        }

        worldMap.put(number, room);

      } catch (Exception e) {
        System.err.println("Failed to parse a room, skipping. Reason: " + e.getMessage());
      }
    }
  }

}
