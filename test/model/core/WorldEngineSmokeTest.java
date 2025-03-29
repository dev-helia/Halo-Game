package model.core;

import java.io.IOException;

import static utils.fileutil.PathUtils.getMapPath;


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
      WorldEngine engine = new WorldEngine();
      engine.generateWorld(getMapPath("Museum_of_Planet_of_the_Apes"));

      // Act
      engine.printWorldMap();

    } catch (IOException e) {
      System.err.println("File loading failure: " + e.getMessage());
    }
  }
}


