package enginedriver;

import controller.TextController;
import controller.SwingController;
import model.GameModel;
import model.IModel;
import view.ConsoleView;
import view.SwingView;
import view.View;

import javax.swing.*;
import java.io.*;

/**
 * GameEngineApp is the required entry point for the game engine.
 * It sets up the model (GameModel), controller, and view.
 * It supports both interactive and automated (smoke test) input sources.
 * It supports -text, -graphics, and batch file modes.
 */
public class GameEngineApp {

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
    IModel model = new GameModel(); // Shared model for all modes
    model.generateWorld(jsonFile);

    String playerName = "Player";

    if (mode.equals("-graphics")) {
      playerName = JOptionPane.showInputDialog(null, "Enter your name:", "New Game", JOptionPane.PLAIN_MESSAGE);
      if (playerName == null || playerName.isBlank()) playerName = "Player";

      model.initializePlayer(playerName);
      SwingView gui = new SwingView();
      new SwingController(model, gui); // No need to call start — handled by GUI

    } else if (mode.equals("-text")) {
      BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Enter your name: ");
      playerName = input.readLine().trim();
      if (playerName.isBlank()) playerName = "Player";

      model.initializePlayer(playerName);
      View view = new ConsoleView();
      TextController controller = new TextController(model, view, input);
      controller.startGame();

    } else if (mode.equals("-batch") && args.length >= 3) {
      String inputFile = args[2];
      Reader batchInput = new FileReader(inputFile);
      PrintWriter batchOutput = (args.length == 4) ? new PrintWriter(args[3]) : new PrintWriter(System.out);

      model.initializePlayer("BatchPlayer");
      View view = new ConsoleView(batchOutput);
      TextController controller = new TextController(model, view, batchInput);
      controller.startGame();
      batchOutput.flush();

    } else {
      System.out.println("Invalid mode. Use -text, -graphics, or -batch.");
    }
  }
}
