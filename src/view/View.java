package view;

import model.core.Player;
import model.core.Room;

/**
 * 视图接口：定义所有游戏视图的标准
 */
public interface View {
  void displayMainMenu();

  void renderGame(Player player, Room room);

  void showMessage(String message);
}
