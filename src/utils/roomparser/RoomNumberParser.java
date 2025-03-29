package utils.roomparser;

/**
 * Utility class for parsing room number from string formats like "1:Museum Entrance".
 */
public class RoomNumberParser {
  /**
   * Parses the room number from a string like "1:Room Name".
   *
   * @param input the input string
   * @return the parsed room number (as int)
   * @throws IllegalArgumentException if the input format is invalid
   */
  public static int parseRoomNumber(String input) {

    if (input == null || !input.contains(":")) {
      throw new IllegalArgumentException("Invalid room input.");
    }
    String[] parts = input.split(":");
    return Integer.parseInt(parts[0].trim());
  }
}
