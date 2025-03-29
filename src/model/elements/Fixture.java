package model.elements;


/**
 * The Fixture class represents immovable objects in the game.
 */
public class Fixture extends GameElements {
  private double weight;

  /**
   * Constructor: Initializes the name, description, and weight of the fixture.
   */
  public Fixture(String name, String description, double weight) {
    super(name, description);  // Call the constructor of the superclass
    this.weight = weight;
  }

  /**
   * Gets the weight of the fixture.
   */
  public double getWeight() {
    return weight;
  }

  /**
   * Displays the detailed information of the fixture.
   */
  @Override
  public void displayDetails() {
    System.out.println("Fixture: " + getName());
    System.out.println("Description: " + getDescription());
    System.out.println("Weight: " + getWeight() + "kg");
  }
}
