package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.core.Room;
import model.elements.Item;

public class ItemParser {

  /**
   * Parses the "items" section from the root JSON object.
   * For each item entry, this method extracts item data, clamps missing values,
   * creates an Item instance, and assigns it to the corresponding room.
   *
   * @param root     the root JSON object containing items
   * @param worldMap the map of room IDs to Room objects
   */
  public static void parseItems(JsonObject root, Map<Integer, Room> worldMap, List<Item> allItems) {
    if (!root.has("items") || !root.get("items").isJsonArray()) {
      System.err.println("Warning: No 'items' array found.");
      return;
    }

    JsonArray itemsArray = root.getAsJsonArray("items");

    for (JsonElement element : itemsArray) {
      if (!element.isJsonObject()) continue;
      JsonObject itemObj = element.getAsJsonObject();

      try {
// Extract item fields and handle missing data (clamping)
        String name = itemObj.has("name") ? itemObj.get("name").getAsString() : "Unnamed Item";  // Clamp missing name
        String desc = itemObj.has("description") ? itemObj.get("description").getAsString() : "No description available";  // Clamp missing description
        double weight = itemObj.has("weight") ? itemObj.get("weight").getAsDouble() : 0.0;  // Clamp missing weight
        int maxUses = itemObj.has("max_uses") ? itemObj.get("max_uses").getAsInt() : 1;  // Clamp missing max_uses
        int usesRemaining = itemObj.has("uses_remaining") ? itemObj.get("uses_remaining").getAsInt() : maxUses;  // Clamp missing uses_remaining
        int value = itemObj.has("value") ? itemObj.get("value").getAsInt() : 0;  // Clamp missing value
        String whenUsed = itemObj.has("when_used") ? itemObj.get("when_used").getAsString() : "No information";  // Clamp missing when_used

        Item item = new Item(name, desc, weight, maxUses, usesRemaining, value, whenUsed);
        allItems.add(item);
      } catch (Exception e) {
        System.err.println("Skipping invalid item: " + e.getMessage());
      }
    }
    // 🛠️ 2. 塞回各房间
    for (Room room : worldMap.values()) {
      String raw = room.getRawField("items");
      if (raw == null) continue;

      List<Item> roomItems = new ArrayList<>();
      for (String name : raw.split(",")) {
        String trimmed = name.trim();
        for (Item item : allItems) {
          if (item.getName().equalsIgnoreCase(trimmed)) {
            roomItems.add(item);
            break;
          }
        }
      }
      room.setItems(roomItems);
    }
  }
}

