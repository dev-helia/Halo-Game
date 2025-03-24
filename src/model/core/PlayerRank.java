package model.core;

/**
 * Enum representing the player's rank based on their score.
 * Scoring thresholds:
 * - NOVICE (0–59)
 * - INTERMEDIATE (60–119)
 * - EXPERT (120–199)
 * - LEGEND (200+)
 */
public enum PlayerRank {
  NOVICE(0),
  INTERMEDIATE(60),
  EXPERT(120),
  LEGEND(200);

  // Minimum score required for each rank
  private final int threshold;

  /**
   * Constructor to associate a score threshold with each rank.
   *
   * @param threshold The minimum score required for this rank.
   */
  PlayerRank(int threshold) {
    this.threshold = threshold;
  }

  /**
   * Determines the player's rank based on their final score.
   *
   * @param score Total score accumulated by the player.
   * @return The player's rank.
   */
  public static PlayerRank fromScore(int score) {

    if (score >= LEGEND.threshold) return LEGEND;
    if (score >= EXPERT.threshold) return EXPERT;
    if (score >= INTERMEDIATE.threshold) return INTERMEDIATE;
    return NOVICE;
  }
}
