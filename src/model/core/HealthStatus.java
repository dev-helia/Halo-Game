package model.core;

/**
 * Enum representing the player's health status.
 * Thresholds:
 *  - SLEEP (<= 0): Player lost all points and go to "SLEEP" mode.
 *  - WOOZY (<40): Player's score lower than 40: Low Health.
 *  - FATIGUED (< 70): Moderate health.
 *  - AWAKE (>= 70): Player's score greater than or equal to 70 but less than 100: Healthy status.
 */
public enum HealthStatus {
  SLEEP(0),
  WOOZY(40),
  FATIGUED(70),
  AWAKE(100);

  // Threshold of each status
  private final int threshold;

  /**
   * Constructor of HealthStatus.
   * @param threshold The minimum health value for each status.
   */
  HealthStatus(int threshold) {
    this.threshold = threshold;
  }

  /**
   * Determines the player's current health status.
   *
   * @param health the player's current health value (0-100).
   * @return the player's current health status.
   */
  public static HealthStatus fromHealth(int health) {
    if (health <= SLEEP.threshold) return SLEEP;
    if (health < WOOZY.threshold) return WOOZY;
    if (health < FATIGUED.threshold) return FATIGUED;
    return AWAKE;
  }
}

