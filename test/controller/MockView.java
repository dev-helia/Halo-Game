package controller;

import java.util.ArrayList;
import java.util.List;

import model.core.Player;
import model.core.Room;
import view.View;

/**
 * The type Mock view.
 */
public class MockView implements View {
  private final List<String> messages = new ArrayList<>();

  @Override
  public void displayMainMenu() {
    messages.add("[MENU]");
  }

  @Override
  public void showMessage(String message) {
    messages.add(message);
  }

  @Override
  public void renderGame(Player player, Room room) {
    messages.add("Rendered: " + room.getName());
  }

  /**
   * Gets messages.
   *
   * @return the messages
   */
  public List<String> getMessages() {
    return new ArrayList<>(messages); // Return a copy to avoid mutation
  }

  /**
   * Contains message boolean.
   *
   * @param keyword the keyword
   * @return the boolean
   */
  public boolean containsMessage(String keyword) {
    return messages.stream().anyMatch(msg -> msg.contains(keyword));
  }
}
