package utils.fileutil;

import java.nio.file.Paths;

/**
 * The type Path utils.
 */
public class PathUtils {

  private static final String MAP_DIR
          = Paths.get(System.getProperty("user.dir"), "src", "resources", "maps").toString();
  private static final String SAVE_DIR
          = Paths.get(System.getProperty("user.dir"), "src", "resources", "saves").toString();;

  /**
   * Concatenate map file paths (default.json suffix)
   * @param fileName Map name without path
   * @return Full path
   */
  public static String getMapPath(String fileName) {
    if (!fileName.endsWith(".json")) {
      fileName += ".json";
    }
    return Paths.get(MAP_DIR, fileName).toString();
  }

  /**
   * Concatenate archive file paths (default.json suffix)
   * @param fileName Map name without path
   * @return Full path
   */
  public static String getSavePath(String fileName) {
    if (!fileName.endsWith(".json")) {
      fileName += ".json";
    }
    return Paths.get(System.getProperty("user.dir"), SAVE_DIR, fileName).toString();
  }
}
