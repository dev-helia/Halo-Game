package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import model.elements.Fixture;

public class FixtureParser {
  public static void parseFixtures(JsonObject root, Map<String, Fixture> globalFixtures) {
    // 4.解析 fixtures（用于装入房间）
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
  }
}
