package enginedriver;

import controller.GameController;
import controller.SwingController;
import model.core.Player;
import model.core.Room;
import model.core.WorldEngine;
import utils.fileutil.PathUtils;
import view.ConsoleView;
import view.SwingView;
import view.View;

import javax.swing.*;
import java.io.*;

/**
 * GameEngineApp is the required entry point for the game engine.
 * It supports -text, -graphics, and batch file modes.
 */
public class GameEngineApp {

  /**
   * Main entry point. Accepts:
   *   - hallway.json -text
   *   - hallway.json -graphics
   *   - hallway.json -batch input.txt
   *   - hallway.json -batch input.txt output.txt
   */
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.out.println("Usage:");
      System.out.println("  java -jar game_engine.jar <mapfile>.json -text");
      System.out.println("  java -jar game_engine.jar <mapfile>.json -graphics");
      System.out.println("  java -jar game_engine.jar <mapfile>.json -batch input.txt [output.txt]");
      return;
    }

    String jsonFile = args[0];
    String mode = args[1];

    // === Initialize world and load map ===
    WorldEngine world = new WorldEngine();

    // === Shared name prompt ===
    String playerName = "Player";

    if (mode.equals("-graphics")) {
      playerName = JOptionPane.showInputDialog(null, "Enter your name:", "New Game", JOptionPane.PLAIN_MESSAGE);
      if (playerName == null || playerName.isBlank()) playerName = "Player";

      world.generateWorld(jsonFile);

      Player player = new Player(playerName, world.getRoom(1));
      SwingView gui = new SwingView();
      new SwingController(world, player, gui);
    } else if (mode.equals("-text")) {
      BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Enter your name: ");
      playerName = input.readLine().trim();
      if (playerName.isBlank()) playerName = "Player";
      Player player = new Player(playerName, world.getRoom(1));

      View view = new ConsoleView();
      GameController controller = new GameController(world, view, input);
      controller.setPlayer(player); // in case constructor doesn’t already
      controller.startGame();

    } else if (mode.equals("-batch") && args.length >= 3) {
      String inputFile = args[2];
      Reader batchInput = new FileReader(inputFile);
      PrintWriter batchOutput = (args.length == 4) ? new PrintWriter(args[3]) : new PrintWriter(System.out);

      View view = new ConsoleView(batchOutput);
      Player player = new Player("BatchPlayer", world.getRoom(1));
      GameController controller = new GameController(world, view, batchInput);
      controller.setPlayer(player);
      controller.startGame();
      batchOutput.flush();

    } else {
      System.out.println("Invalid mode. Use -text, -graphics, or -batch.");
    }
  }
}