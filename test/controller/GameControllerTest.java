package controller;

import model.core.Player;
import model.core.Room;
import model.core.WorldEngine;
import model.elements.Item;
import model.elements.Fixture;
import model.obstacle.Puzzle;
import org.junit.Before;
import org.junit.Test;
import view.View;

import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.*;

public class GameControllerTest {
  private WorldEngine world;
  private TestViewHelper view;
  private Player player;

  @Before
  public void setUp() throws Exception {
    world = new WorldEngine();
    world.generateWorld("src/resources/Museum_of_Planet_of_the_Apes.json");
    view = new TestViewHelper();
    player = new Player("Helia", world.getRoom(1));
  }

  private void simulateGameWithInput(String input) throws Exception {
    GameController controller = new GameController(world, view, new StringReader(input));
    controller.startGame();
  }

  @Test
  public void testMoveNorth() throws Exception {
    simulateGameWithInput("Helia\nN\nQ\n");
    assertTrue(view.containsMessage("You can't move that way.") || view.containsMessage("Health Status"));
  }

  @Test
  public void testInvalidMove() throws Exception {
    simulateGameWithInput("Helia\nW\nQ\n");
    assertTrue(view.containsMessage("You can't move that way."));
  }

  @Test
  public void testTakeItem() throws Exception {
    simulateGameWithInput("Helia\nT Ticket\nQ\n");
    assertTrue(view.containsMessage("You picked up Ticket"));
  }

  @Test
  public void testDropItem() throws Exception {
    simulateGameWithInput("Helia\nT Ticket\nD Ticket\nQ\n");
    assertTrue(view.containsMessage("You dropped Ticket"));
  }

  @Test
  public void testUseItem() throws Exception {
    simulateGameWithInput("Helia\nT Ticket\nU Ticket\nQ\n");
    assertTrue(view.containsPartial("You used the"));
  }

  @Test
  public void testExamineItem() throws Exception {
    simulateGameWithInput("Helia\nX Ticket\nQ\n");
    assertTrue(view.containsPartial("A ticket that grants entry"));
  }

  @Test
  public void testExamineFixture() throws Exception {
    simulateGameWithInput("Helia\nN\nX Computer\nQ\n");
    assertTrue(view.containsPartial("An old computer with a green screen"));
  }

  @Test
  public void testAnswerPuzzle() throws Exception {
    simulateGameWithInput("Helia\nA Gorilla\nQ\n");
    assertTrue(view.containsMessage("Puzzle solved!") || view.containsMessage("That didn't work."));
  }

  @Test
  public void testSaveAndRestore() throws Exception {
    simulateGameWithInput("Helia\nSAVE\nRESTORE\nQ\n");
    assertTrue(view.containsMessage("Game saved."));
    assertTrue(view.containsMessage("Game restored."));
  }

  @Test
  public void testInventoryDisplay() throws Exception {
    simulateGameWithInput("Helia\nT Ticket\nI\nQ\n");
    assertTrue(view.containsPartial("Your Inventory:"));
    assertTrue(view.containsPartial("Ticket"));
  }
}
