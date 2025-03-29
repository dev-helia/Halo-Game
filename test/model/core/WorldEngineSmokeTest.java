package model.core;

import java.io.IOException;


/**
 * The type World engine smoke test.
 */
public class WorldEngineSmokeTest{
  /**
   * Print the worldMap for the smoke test for testing world engine.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    try {
      // Arrange
      String filePath = "src/resources/maps/Museum_of_Planet_of_the_Apes.json";

      WorldEngine engine = new WorldEngine();

      // Act
      engine.generateWorld(filePath);
      engine.printWorldMap();

    } catch (IOException e) {
      System.err.println("File loading failure: " + e.getMessage());
    }
  }
}


