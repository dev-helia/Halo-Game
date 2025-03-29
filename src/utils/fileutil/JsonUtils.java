package utils.fileutil;

import com.google.gson.*;

import java.io.*;

/**
 * JSON tool class: safely read JSON files
 * and ensure that they are legitimate JsonObject root structure.
 */
public class JsonUtils {

  /**
   * Safely parses JSON files and returns JsonObject.
   * Tries to be tolerant with incomplete structure.
   *
   * @param jsonFilePath file path
   * @return JsonObject Root object (can be empty if invalid)
   * @throws IOException Critical errors (e.g. file not found or broken syntax)
   */
  public static JsonObject safeParseJson(String jsonFilePath) throws IOException {
    try (Reader reader = new FileReader(jsonFilePath)) {
      JsonElement element = JsonParser.parseReader(reader);

      if (!element.isJsonObject()) {
        System.err.println("Warning: Root of JSON file is not an object ({}), returning empty object.");
        return new JsonObject();
      }

      return element.getAsJsonObject();

    } catch (FileNotFoundException e) {
      throw new IOException("JSON file not found: " + jsonFilePath, e);
    } catch (JsonSyntaxException e) {
      throw new IOException("JSON syntax error: " + jsonFilePath, e);
    } catch (IllegalStateException e) {
      throw new IOException("JSON is not a valid object structure: " + jsonFilePath, e);
    }
  }


}
