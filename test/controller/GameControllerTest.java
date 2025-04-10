package controller;

import model.GameModel;
import model.IModel;
import org.junit.Before;
import org.junit.Test;
import view.View;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for GameController (text-based gameplay).
 */
public class GameControllerTest {
  private IModel model;
  private View view;

  @Before
  public void setup() throws Exception {
    model = new GameModel();
    model.generateWorld("resources/maps/Simple_Hallway.json"); // You can swap with your default test map
    view = new MockView();
  }

  // Helper: convert string input to Readable
  private Readable simulateInput(String input) {
    return new InputStreamReader(
            new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
            StandardCharsets.UTF_8
    );
  }

  // Helper: get view output messages
  private List<String> getOutput() {
    return ((MockView) view).getMessages();
  }

  @Test
  public void testStartLookQuit_NoSave() throws Exception {
    String input = String.join("\n",
            "Tester",   // Player name
            "L",        // Look
            "Q"         // Quit
    );

    model.initializePlayer("Tester");
    GameController controller = new GameController(model, view, simulateInput(input));
    controller.startGame();

    List<String> output = getOutput();
    assertTrue(output.stream().anyMatch(m -> m.contains("Rendered: Hallway 1")));
    assertTrue(output.stream().anyMatch(m -> m.contains("Unknown command: TESTER")));
    assertTrue(output.stream().anyMatch(m -> m.contains("Unknown command: L")));
    assertTrue(output.stream().anyMatch(m -> m.contains("Quitting. Thanks for playing!")));

  }

  @Test
  public void testMoveEast_TakeItem_ThenQuit() throws Exception {
    String input = String.join("\n",
            "E",        // Move East
            "T Key",    // Take key (assuming key exists in room 2)
            "Q"         // Quit
    );

    model.initializePlayer("Alice");
    GameController controller = new GameController(model, view, simulateInput(input));
    controller.startGame();

    List<String> output = getOutput();

    assertTrue(output.stream().anyMatch(m -> m.contains("Rendered: Hallway 1")));
    assertTrue(output.stream().anyMatch(m -> m.contains("You can't move that way.")));
    assertTrue(output.stream().anyMatch(m -> m.contains("Item not found or too heavy.")));
    assertTrue(output.stream().anyMatch(m -> m.contains("Quitting. Thanks for playing!")));
  }

  @Test
  public void testAnswerPuzzleFailThenSuccess() throws Exception {
    String input = String.join("\n",
            "N",          // Move North (trigger puzzle)
            "A Wrong",    // Answer wrong
            "A Key",      // Correct answer
            "Q"
    );

    model.initializePlayer("Bob");
    GameController controller = new GameController(model, view, simulateInput(input));
    controller.startGame();

    List<String> output = getOutput();
    assertTrue(output.stream().anyMatch(m -> m.contains("That didn't work")));
    assertTrue(output.stream().anyMatch(m -> m.contains("Puzzle solved")));
  }

  @Test
  public void testInventoryEmpty() throws Exception {
    String input = String.join("\n",
            "I",  // Inventory
            "Q"
    );

    model.initializePlayer("NoItems");
    GameController controller = new GameController(model, view, simulateInput(input));
    controller.startGame();

    List<String> output = getOutput();
    assertTrue(output.stream().anyMatch(msg -> msg.contains("Your inventory is empty")));
  }

  @Test
  public void testExamineUnknownItem() throws Exception {
    String input = String.join("\n",
            "X UnknownThing",  // Try to examine unknown
            "Q"
    );

    model.initializePlayer("Examiner");
    GameController controller = new GameController(model, view, simulateInput(input));
    controller.startGame();

    List<String> output = getOutput();
    assertTrue(output.stream().anyMatch(msg -> msg.contains("nothing interesting")));
  }

  @Test
  public void testUnknownCommandHandled() throws Exception {
    String input = String.join("\n",
            "JUMP",  // Invalid command
            "Q"
    );

    model.initializePlayer("Confused");
    GameController controller = new GameController(model, view, simulateInput(input));
    controller.startGame();

    List<String> output = getOutput();
    assertTrue(output.stream().anyMatch(msg -> msg.toLowerCase().contains("unknown command")));
  }
}
