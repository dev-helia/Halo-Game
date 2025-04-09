package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.IModel;
import model.core.HealthStatus;
import model.core.Player;
import model.core.PlayerRank;
import model.core.Room;
import model.elements.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.SwingView;

import static org.junit.jupiter.api.Assertions.*;

public class SwingControllerTest {

  // 使用手动模拟的类
  private IModel model;
  private SwingView view;
  private SwingController controller;

  public SwingControllerTest() {
    model = new FakeModel() {
      @Override
      public void generateWorld(String jsonFilePath) throws IOException {

      }

      @Override
      public void initializePlayer(String name) {

      }

      @Override
      public Player getPlayer() {
        return null;
      }

      @Override
      public HealthStatus getHealthStatus() {
        return null;
      }

      @Override
      public Player getPlayerReference() {
        return null;
      }
    };
  }

  @BeforeEach
  public void setup() {
    view = new FakeView();
    controller = new SwingController(model, view);
  }

  @Test
  public void testMove() {
    boolean result = model.movePlayer("N");
    controller.move("N");
    assertTrue(result, "Player should move north.");
  }

  @Test
  public void testTakeItem() {
    String itemName = "Sword";
    boolean result = model.pickItem(itemName);
    controller.takeItem(itemName);
    assertTrue(result, "Item should be picked up.");
  }

  @Test
  public void testExamineItem() {
    String itemName = "Health Potion";
    Room room = new Room(1, "Room Description", String.valueOf(new ArrayList<>()));
    Item item = new Item(
        itemName,
        "Restores 50 health points.",
        0.5,
        5,
        5,
        100,
        "Heals the player."
    );
    room.addItem(item);
    model.setCurrentRoom(room);

    controller.examine(itemName);
    assertEquals("Restores 50 health points.", item.getDescription());
  }

  @Test
  public void testSaveGame() {
    String fileName = "testSave";
    boolean success = model.saveGame("resources/saves/" + fileName + ".json");
    controller.saveGame();
    assertTrue(success, "Game should be saved successfully.");
  }

  @Test
  public void testQuitGame() {
    boolean confirmation = view.promptYesNo("Quit the game?");
    controller.quitGame();
    assertTrue(confirmation, "Player should confirm quitting.");
  }

  abstract class FakeModel implements IModel {
    private Room currentRoom;

    @Override
    public boolean movePlayer(String direction) {
      return true;
    }

    @Override
    public boolean pickItem(String itemName) {
      return true;
    }

    @Override
    public boolean dropItem(String itemName) {
      return true;
    }

    @Override
    public String useItem(String itemName) {
      return "Item used";
    }

    @Override
    public boolean answerPuzzle(String answer) {
      return true;
    }

    @Override
    public boolean saveGame(String filePath) {
      return true;
    }

    @Override
    public boolean loadGame(String filePath) {
      return true;
    }

    @Override
    public Room getCurrentRoom() {
      return currentRoom;
    }

    @Override
    public void setCurrentRoom(Room room) {
      this.currentRoom = room;
    }

    @Override
    public List<Item> getInventory() {
      List<Item> items = List.of(new Item(
          "Sword",
          "A sharp blade",
          0.5,
          10,
          10,
          100,
          "Swing the sword"
      ));
      List<Item> items1 = items;
      return items1;
    }

    @Override
    public double getHealth() {
      return 100;
    }

    @Override
    public double getScore() {
      return 1000;
    }

    @Override
    public PlayerRank getPlayerRank() {
      return PlayerRank.NOVICE;
    }
  }

  // 手动实现 SwingView 的模拟类
  class FakeView extends SwingView {
    @Override
    public void showMessage(String msg) {
      System.out.println(msg);
    }

    @Override
    public void renderRoom(Room room) {
      System.out.println("Rendering Room: " + room.getRoomDescription());
    }

    @Override
    public boolean promptYesNo(String msg) {
      return true;
    }
  }
}
