package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import model.elements.Item;

public class ItemParser {
  public static void parseItems(JsonObject root, Map<String, Item> globalItems) {
    if (root.has("items")) {
      JsonArray itemsArray = root.getAsJsonArray("items");
      for (JsonElement element : itemsArray) {
        JsonObject itemObject = element.getAsJsonObject();

        // 从 JSON 中提取每个 item 的相关数据
        String name = itemObject.get("name").getAsString();
        String description = itemObject.get("description").getAsString();
        double weight = itemObject.get("weight").getAsDouble();
        int maxUses = itemObject.get("max_uses").getAsInt();
        int usesRemaining = itemObject.get("uses_remaining").getAsInt();
        int value = itemObject.get("value").getAsInt();
        String whenUsed = itemObject.get("when_used").getAsString();

        // 创建 Item 对象并存入全局的 globalItems Map 中
        globalItems.put(name, new Item(name, description, weight, maxUses, usesRemaining, value, whenUsed));
      }
    }
  }
}
