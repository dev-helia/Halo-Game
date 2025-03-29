package model.elements;


/**
 * The Item class represents objects in the game that players can pick up, use, or drop.
 */
public class Item extends GameElements {
  private double weight;           // Weight of the item
  private int maxUses;             // Maximum number of uses
  private int usesRemaining;       // Remaining number of uses
  private int value;               // Value of the item
  private String whenUsed;         // Description shown when used

  /**
   * Constructor: Initializes the item's name, description, weight, usage limits, value, and usage description.
   */
  public Item(String name, String description, double weight, int maxUses, int usesRemaining, int value, String whenUsed) {
    super(name, description);  // Call the parent class constructor
    this.weight = weight;
    this.maxUses = maxUses;
    this.usesRemaining = usesRemaining;
    this.value = value;
    this.whenUsed = whenUsed;
  }

  /**
   * Use the item, decrease remaining uses, and return its usage description.
   */
  public String use() {
    if (usesRemaining > 0) {
      usesRemaining--;
      return whenUsed;
    } else {
      return getName() + " has no remaining uses.";
    }
  }

  /**
   * Check whether the item can still be used.
   */
  public boolean isUsable() {
    return usesRemaining > 0;
  }

  /**
   * Drop the item.
   */
  public void dropItem() {
    System.out.println("You dropped the " + getName() + ".");
  }

  /**
   * Display detailed information about the item.
   */
  @Override
  public void displayDetails() {
    System.out.println("Item: " + getName());
    System.out.println("Description: " + getDescription());
    System.out.println("Weight: " + weight + "kg");
    System.out.println("Uses remaining: " + usesRemaining);
    System.out.println("Value: " + value);
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public int getMaxUses() {
    return maxUses;
  }

  public void setMaxUses(int maxUses) {
    this.maxUses = maxUses;
  }

  public int getUsesRemaining() {
    return usesRemaining;
  }

  public void setUsesRemaining(int usesRemaining) {
    this.usesRemaining = usesRemaining;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public String getWhenUsed() {
    return whenUsed;
  }

  public void setWhenUsed(String whenUsed) {
    this.whenUsed = whenUsed;
  }
}
