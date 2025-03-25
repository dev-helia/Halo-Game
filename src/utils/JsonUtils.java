package utils;

import com.google.gson.*;

import java.io.*;

/**
 * JSON tool class: safely read JSON files
 * and ensure that they are legitimate JsonObject root structure.
 */
public class JsonUtils {

  /**
   * Safely parses JSON files and returns JsonObject
   *
   * @param jsonFilePath file path
   * @return JsonObject Root object
   * @throws IOException All kinds of exception packages are thrown by IOException
   */
  public static JsonObject safeParseJson(String jsonFilePath) throws IOException {
    try {
      Reader reader = new FileReader(jsonFilePath);
      JsonElement element = JsonParser.parseReader(reader);

      if (!element.isJsonObject()) {
        throw new IOException("Invalid JSON: The root element is not an object {}, but " + element);
      }

      return element.getAsJsonObject();

    } catch (FileNotFoundException e) {
      throw new IOException("JSON file not found：" + jsonFilePath, e);
    } catch (JsonSyntaxException e) {
      throw new IOException("JSON file format error (syntax error): " + jsonFilePath, e);
    } catch (IllegalStateException e) {
      throw new IOException("The JSON structure does not meet expectations: not an object {}", e);
    }
  }
}
