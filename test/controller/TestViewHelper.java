package controller;

import view.View;

import model.core.Player;
import model.core.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * A fake View implementation that captures all messages for testing.
 */
public class TestViewHelper implements View {
  private final List<String> messages = new ArrayList<>();

  @Override
  public void displayMainMenu() {
    messages.add("Main Menu Displayed");
  }

  @Override
  public void showMessage(String message) {
    messages.add(message);
  }

  @Override
  public void renderGame(Player player, Room room) {
    messages.add("Render Game: Player in Room " + room.getRoomNumber());
  }

  // Getter: 用于测试中断言输出内容
  public List<String> getMessages() {
    return messages;
  }

  // Helper: 检查是否包含某条输出（忽略大小写）
  public boolean containsMessage(String keyword) {
    return messages.stream().anyMatch(m -> m.toLowerCase().contains(keyword.toLowerCase()));
  }
  /**
   * Checks if any captured message contains the given partial string.
   */
  public boolean containsPartial(String keyword) {
    return messages.stream().anyMatch(msg -> msg.contains(keyword));
  }

}