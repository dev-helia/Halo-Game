package utils;

public class RoomNumberParser {
  public static int parseRoomNumber(String input) {
    String[] parts = input.split(":");
    return Integer.parseInt(parts[0].trim());
  }
}
