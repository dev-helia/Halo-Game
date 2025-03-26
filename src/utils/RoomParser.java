package utils;

import com.google.gson.JsonObject;

import java.util.List;

import model.core.Room;

/**
 * The type Room parser.
 */
public class RoomParser {
  /**
   * Parse the specific room.
   *
   * @param obj the obj
   * @return the room
   */
// todo sue
  /**
   * Parse a single Room object from a JSON object.
   * { "room_name":"Courtyard", "room_number": "1",
   *  "description":"A beautiful courtyard with flowers on both sides of the stone walkway. \nThe walkway leads north. A billboard is in the distance.",
   * "N": "2", "S": "0", "E": "0", "W": "0","puzzle": null, "monster": null, "items": "Hair Clippers", "fixtures": "Billboard","picture": "courtyard.png" },
   * }
   */
  // 👇 解析单个 Room 对象 提取基本字段（名字/编号/出口）
  static Room parseRoom(JsonObject obj) {
    // Get the room number from JSON file.
    int num = obj.get("room_number").getAsInt();
    // Get the room name from JSON file.
    String name = obj.get("room_name").getAsString();
    // Set the room description if it exists
    String description = (obj.has("description") && !obj.get("description").isJsonNull())
            ? obj.get("description").getAsString()
            : null;
    // Create a Room object using room number and name.
    Room r = new Room(num, name, description);

    // Set exits (N/S/E/W) to their target room numbers
    for (String dir : List.of("N", "S", "E", "W")) {
      if (obj.has(dir) && !obj.get(dir).isJsonNull()) {
        try {
          int targetRoom = obj.get(dir).getAsInt();
          if (targetRoom != 0) {
            r.setExit(dir, targetRoom); // only set exit if it's not 0
          }
        } catch (NumberFormatException e) {
          System.err.println("Exit from direction " + dir + " in room " + num + "is not valid");
        }
      }
    }

    // Temporarily store the raw "items" string (e.g. "Lamp, Key")
    if (obj.has("items") && !obj.get("items").isJsonNull()) {
      r.setRawField("items", obj.get("items").getAsString()); // You'll implement this in the Room class
    }

    // Temporarily store the raw "fixtures" string (e.g. "Desk, Painting")
    if (obj.has("fixtures") && !obj.get("fixtures").isJsonNull()) {
      r.setRawField("fixtures", obj.get("fixtures").getAsString()); // Also to be added in Room class
    }

    // Done parsing this Room
    return r;
  }
}
