package model.core;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import model.elements.*;
import model.obstacle.*;
import utils.JsonUtils;

import java.io.*;
import java.util.*;

/**
 * WorldEngine: Responsible for loading JSON data,
 * generating game maps,
 * and providing world status
 */
public class WorldEngine {
  // fields and the default constructor
  private Map<Integer, Room> worldMap; // Whole room map: Room number -> Room object

  /**
   * Constructor (no seed default build)
   * Initialize the world map object:
   * key is the room number and the value is the Room object.
   */
  public WorldEngine() {
    this.worldMap = new HashMap<>();
  }

  /**
   * Main function to generate worlds from JSON files (map + elements).
   * Read the file → parse the room → parse items / fixtures / puzzles → stuff back to the room.
   *
   * @param jsonFilePath the file path of the target json file
   * @throws IOException input and output exception
   */
  public void generateWorld(String jsonFilePath) throws IOException {
    JsonObject root = JsonUtils.safeParseJson(jsonFilePath);
    parseRooms(root);


    //TODO Han
    // 3.解析 items（用于与房间匹配）
    Map<String, Item> globalItems = new HashMap<>();
    if (root.has("items")) {
      JsonArray itemsArray = root.getAsJsonArray("items");
      for (JsonElement element : itemsArray) {
        JsonObject i = element.getAsJsonObject();
        String name = i.get("name").getAsString();
        double weight = i.get("weight").getAsDouble();
        int maxUses = i.get("max_uses").getAsInt();
        int usesRemaining = i.get("uses_remaining").getAsInt();
        int value = i.get("value").getAsInt();
        String whenUsed = i.get("when_used").getAsString();
        globalItems.put(name, new Item(name, weight, maxUses, usesRemaining, value, whenUsed));
      }
    }

    // todo han
    // 4.解析 fixtures（用于装入房间）
    Map<String, Fixture> globalFixtures = new HashMap<>();
    if (root.has("fixtures")) {
      JsonArray fixturesArray = root.getAsJsonArray("fixtures");
      for (JsonElement element : fixturesArray) {
        JsonObject f = element.getAsJsonObject();
        String name = f.get("name").getAsString();
        String desc = f.get("description").getAsString();
        int weight = f.get("weight").getAsInt();
        globalFixtures.put(name, new Fixture(name, desc, weight));
      }
    }


    // todo red bean
    // 5.解析 puzzles（用于匹配房间障碍）
    if (root.has("puzzles")) {
      JsonArray puzzlesArray = root.getAsJsonArray("puzzles");

      for (JsonElement element : puzzlesArray) {
        JsonObject p = element.getAsJsonObject();
        String name = p.get("name").getAsString();
        boolean active = p.get("active").getAsBoolean();
        int value = p.get("value").getAsInt();

        // 谜题机制
        String solution = p.get("solution").getAsString();
        boolean affectsTarget = p.get("affects_target").getAsBoolean();
        boolean affectsPlayer = p.get("affects_player").getAsBoolean();
        String effects = p.get("effects").getAsString();

        // 房间 & 提示
        int targetRoom = parseRoomNumber(p.get("target").getAsString());
        String hintMessage = p.has("hintMessage") && !p.get("hintMessage").isJsonNull()
                ? p.get("hintMessage").getAsString()
                : "";

        Puzzle puzzle = new Puzzle(
                name,
                name,
                active,
                value,
                solution,
                affectsTarget,
                affectsPlayer,
                effects,
                targetRoom,
                hintMessage
        );

        // 谜题与房间连接
        Room r = worldMap.get(targetRoom);
        if (r != null) {
          r.setObstacle(puzzle);
        } else {
          System.err.printf("Room #%d not found — puzzle '%s' not assigned.\n", targetRoom, name);
        }
      }
    }

    // TODO: red veab 解析怪物 monster（方式类似 Puzzle）
    if (root.has("monsters")) {
      JsonArray monstersArray = root.getAsJsonArray("monsters");

      for (JsonElement element : monstersArray) {
        JsonObject m = element.getAsJsonObject();

        String name = m.get("name").getAsString();
        String description = m.get("description").getAsString();
        boolean active = m.get("active").getAsBoolean();
        int value = m.get("value").getAsInt();
        int damage = m.get("damage").getAsInt();
        boolean canAttack = m.get("can_attack").getAsBoolean();
        String attackMessage = m.get("attack_message").getAsString();
        String defeatItem = m.get("defeat_item").getAsString();
        int targetRoom = parseRoomNumber(m.get("target").getAsString());

        // 创建怪物列表
        Monster monster = new Monster(
                name,
                description,
                active,
                value,
                damage,
                canAttack,
                attackMessage,
                defeatItem
        );

        // 怪物与谜题连接
        Room r = worldMap.get(targetRoom);
        if (r != null) {
          r.setObstacle(monster);
        } else {
          System.err.printf("Room #%d not found — monster '%s' not assigned.\n", targetRoom, name);
        }
      }
    }


    // todo ht
    // 第二轮：给房间塞入 items 和 fixtures
    for (Room room : worldMap.values()) {
      // 添加 items
      if (room.getItems() == null) continue;

      for (String itemName : extractNames(room, "items")) {
        if (globalItems.containsKey(itemName)) {
          room.addItem(cloneItem(globalItems.get(itemName))); // 深拷贝
        }
      }

      // 添加 fixtures
      for (String fixName : extractNames(room, "fixtures")) {
        if (globalFixtures.containsKey(fixName)) {
          room.addFixture(globalFixtures.get(fixName)); // fixtures 可以复用
        }
      }
    }
  }

  /**
   * Print the current world map for simple smoke testing.
   */
  public void printWorldMap() {
    System.out.println("=== Game World Map ===");
    // print rooms
    for (Map.Entry<Integer, Room> entry : worldMap.entrySet()) {
      Room room = entry.getValue();
      System.out.println("Room " + room.getRoomNumber() + ": " + room.getName());
      System.out.println("Description " + room.getRoomDescription());
      System.out.println("  Exits -> N: " + room.getExit("N")
              + ", S: " + room.getExit("S")
              + ", E: " + room.getExit("E")
              + ", W: " + room.getExit("W"));

      // items and fixtures in the current room
      if (room.getItems() != null && !room.getItems().isEmpty()) {
        System.out.println("  Items: ");
        for (Item item : room.getItems()) {
          System.out.println("    - " + item.getName());
        }
      }

      if (room.getFixtures() != null && !room.getFixtures().isEmpty()) {
        System.out.println("  Fixtures: ");
        for (Fixture fixture : room.getFixtures()) {
          System.out.println("    - " + fixture.getName());
        }
      }

      System.out.println();
    }
  }

  // ==== getter ====

  /**
   * Room getter.
   *
   * @param roomNumber the room number
   * @return the room
   */
  public Room getRoom(int roomNumber) {
    return worldMap.get(roomNumber);
  }

  /**
   * worldMap getter.
   *
   * @return the world map
   */
  public Map<Integer, Room> getWorldMap() {
    return worldMap;
  }

  // ==== helper ====

  /**
   * Safely parse rooms from root object.
   *
   * @param root the root json object
   * @throws IOException file not found
   */
  private void parseRooms(JsonObject root) throws IOException {
    // Check if the rooms field is included
    if (!root.has("rooms") || !root.get("rooms").isJsonArray()) {
      throw new IOException("Invalid JSON file: Missing 'rooms' field, or the field is not an array!");
    }

    JsonArray roomsArray = root.getAsJsonArray("rooms");

    for (JsonElement element : roomsArray) {
      // Make sure each element is an object
      if (!element.isJsonObject()) {
        throw new IOException("There are illegal elements (not objects) in the rooms array:" + element);
      }

      JsonObject roomObj = element.getAsJsonObject();

      try {
        // Call parseRoom and handle exceptions
        Room room = parseRoom(roomObj);

        if (room == null) {
          throw new IOException("parseRoom returns null, please check the field:" + roomObj);
        }

        int number = room.getRoomNumber();
        if (worldMap.containsKey(number)) {
          throw new IOException("Repeated room numbers: " + number + ". Please check the JSON configuration!");
        }

        worldMap.put(number, room);

      } catch (Exception e) {
        throw new IOException("Failed to parse the room: " + roomObj + ", reason:" + e.getMessage(), e);
      }
    }
  }



  // todo sue
  /**
   * Parse a single Room object from a JSON object.
   * { "room_name":"Courtyard", "room_number": "1",
   *  "description":"A beautiful courtyard with flowers on both sides of the stone walkway. \nThe walkway leads north. A billboard is in the distance.",
   * "N": "2", "S": "0", "E": "0", "W": "0","puzzle": null, "monster": null, "items": "Hair Clippers", "fixtures": "Billboard","picture": "courtyard.png" },
   * }
   */
  // 👇 解析单个 Room 对象 提取基本字段（名字/编号/出口）
  private Room parseRoom(JsonObject obj) {
    // Get the room number from JSON file.
    int num = obj.get("room_number").getAsInt();
    // Get the room name from JSON file.
    String name = obj.get("room_name").getAsString();
    // Create a Room object using room number and name.
    Room r = new Room(num, name);

    // Set the room description if it exists
    if (obj.has("description") && !obj.get("description").isJsonNull()) {
      r.setDescription(obj.get("description").getAsString());
    }

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

  // 提取以逗号分隔的名字列表
  // 你还没实现：需要你去 Room 类存储原始 "Pen, Eraser" 这样的字符串字段
  private List<String> extractNames(Room room, String field) {
    List<String> names = new ArrayList<>();
    String raw = room.getRawField(field);  // gets the raw string like "Key, Lamp"
    if (raw != null && !raw.isBlank()) {
      for (String name : raw.split(",")) { // splits into ["Key", " Lamp"]
        names.add(name.trim());            // trims whitespace: "Key", "Lamp"
      }
    }
    return names;
  }

  // 拷贝物品（避免同一个 item 被多个房间引用） 避免共享引用造成冲突
  private Item cloneItem(Item i) {
    return new Item(
            i.getName(),
            i.getDescription(),    // 2nd argument
            i.getWeight(),         // double
            i.getMaxUses(),
            i.getUsesRemaining(),
            i.getValue(),
            i.getWhenUsed()
    );
  }

  // 解析 room target 格式 “1:RoomName” → 1 只提取前面的房间号
  private int parseRoomNumber(String input) {
    String[] parts = input.split(":");
    return Integer.parseInt(parts[0].trim());
  }
}
