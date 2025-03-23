package model.core;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import model.elements.*;
import model.obstacle.*;

import java.io.*;
import java.util.*;

/**
 * WorldEngine：负责加载 JSON 数据、生成游戏地图、提供世界状态
 */
// Json object 需要导入
public class WorldEngine {
  // 可选：用于生成不同地图（暂不实现）
  private int seed;

  // 整个房间地图，房间编号 -> Room 对象
  private Map<Integer, Room> worldMap;

  // 构造器（无 seed 默认构建）
  public WorldEngine() {
    this.worldMap = new HashMap<>();
  }

  /**
   * 从 JSON 文件生成世界（地图 + 元素）
   */
  public void generateWorld(String jsonFilePath) throws IOException {
    // 读取 JSON 内容
    Reader reader = new FileReader(jsonFilePath);
    JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

    // 先解析房间
    JsonArray roomsArray = root.getAsJsonArray("rooms");
    for (JsonElement element : roomsArray) {
      JsonObject roomObj = element.getAsJsonObject();
      Room room = parseRoom(roomObj);
      worldMap.put(room.getRoomNumber(), room);
    }

    // 解析 items（用于与房间匹配）
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

    // 解析 fixtures（用于装入房间）
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

    // 解析 puzzles（用于匹配房间障碍）
    if (root.has("puzzles")) {
      JsonArray puzzlesArray = root.getAsJsonArray("puzzles");
      for (JsonElement element : puzzlesArray) {
        JsonObject p = element.getAsJsonObject();
        String name = p.get("name").getAsString();
        boolean active = p.get("active").getAsBoolean();
        boolean affectsTarget = p.get("affects_target").getAsBoolean();
        boolean affectsPlayer = p.get("affects_player").getAsBoolean();
        String solution = p.get("solution").getAsString();
        int value = p.get("value").getAsInt();
        String effects = p.get("effects").getAsString();
        int targetRoom = parseRoomNumber(p.get("target").getAsString());

        Puzzle puzzle = new Puzzle(name, name, active, value, solution, affectsTarget, affectsPlayer, effects, targetRoom);

        Room r = worldMap.get(targetRoom);
        if (r != null) r.setObstacle(puzzle);
      }
    }

    // TODO: 可选解析怪物 monster（方式类似 Puzzle）

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

    reader.close();
  }

  /**
   * 获取某个房间
   */
  public Room getRoom(int roomNumber) {
    return worldMap.get(roomNumber);
  }

  /**
   * 获取完整地图
   */
  public Map<Integer, Room> getWorldMap() {
    return worldMap;
  }

  // ==== 辅助方法区 ====

  // 👇 解析单个 Room 对象
  private Room parseRoom(JsonObject obj) {
    int num = obj.get("room_number").getAsInt();
    String name = obj.get("room_name").getAsString();
    Room r = new Room(num, name);

    // 设置出口
    for (String dir : List.of("N", "S", "E", "W")) {
      if (obj.has(dir)) {
        r.setExit(dir, obj.get(dir).getAsInt());
      }
    }

    // 临时保存 items / fixtures 字符串（之后处理）
    if (obj.has("items") && !obj.get("items").isJsonNull()) {
      r.getItems().addAll(new ArrayList<>()); // 后续填充
    }

    if (obj.has("fixtures") && !obj.get("fixtures").isJsonNull()) {
      r.getFixtures().addAll(new ArrayList<>()); // 后续填充
    }

    return r;
  }

  // 提取以逗号分隔的名字列表
  private List<String> extractNames(Room room, String field) {
    List<String> names = new ArrayList<>();
    // 我们需要 room 对应字段的“原始字符串”，你可以设计一个 fieldToString() 方法来存储原始数据
    return names;
  }

  // 拷贝物品（避免同一个 item 被多个房间引用）
  private Item cloneItem(Item i) {
    return new Item(i.getName(), i.getWeight(), i.getMaxUses(), i.getUsesRemaining(), i.getValue(), i.getWhenUsed());
  }

  // 解析 room target 格式 “1:RoomName” → 1
  private int parseRoomNumber(String input) {
    String[] parts = input.split(":");
    return Integer.parseInt(parts[0].trim());
  }
}
