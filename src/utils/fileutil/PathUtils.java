package utils.fileutil;

import java.nio.file.Paths;

/**
 * The type Path utils.
 */
public class PathUtils {

  private static final String MAP_DIR
          = Paths.get("resources", "maps").toString();

  private static final String SAVE_DIR
          = Paths.get("resources", "saves").toString();

  private static final String IMAGE_DIR
          = Paths.get("resources", "images").toString();

  /**
   * Get the full path to a map file.
   * @param fileName The filename (e.g., "Museum_of_Planet_of_the_Apes.json")
   * @return Full path
   */
  public static String getMapPath(String fileName) {
    if (!fileName.endsWith(".json")) {
      fileName += ".json";
    }
    return Paths.get(MAP_DIR, fileName).toString();
  }

  /**
   * Get the full path to a save file.
   * @param fileName The save file name
   * @return Full path
   */
  public static String getSavePath(String fileName) {
    if (!fileName.endsWith(".json")) {
      fileName += ".json";
    }
    return Paths.get(SAVE_DIR, fileName).toString();
  }

  /**
   * Get the full path to an image file.
   * @param fileName The image file name (e.g. "north.png")
   * @return Full path
   */
  public static String getImagePath(String fileName) {
    return Paths.get(IMAGE_DIR, fileName).toString();
  }
}
