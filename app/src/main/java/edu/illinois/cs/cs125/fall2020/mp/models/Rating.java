package edu.illinois.cs.cs125.fall2020.mp.models;

/**
 * Model holding the course Rating information.
 */
public class Rating {
  /** Rating indicating that course isn't rated yet. */
  public static final double NOT_RATED = -1.0;
  /** constructor of Rating.
   * @param setID sets the ID.
   * @param setRating sets the Rating.
   */
  public Rating(final String setID, final double setRating) {
    clientID = setID;
    rating = setRating;
  }
  /** empty constructor of Rating.
   */
  public Rating() {}

  /**
   * stores uuid.
   */
  private String clientID;
  /** returns the ID.
   * @return the ID
  */
  public String getId() {
    return clientID;
  }
  /**
   * stores rating.
   */
  private Double rating;
  /** returns the rating.
   * @return the rating.
   */
  public double getRating() {
    if (rating.equals(null)) {
      return NOT_RATED;
    } else {
      double rate = rating;
      return rate;
    }
  }
  /**
   * toString method to deserialize to JSON.
   * @return string
   */
  @Override
  public String toString() {
    return clientID + "," + getRating();
  }
  /**
   * toString method to deserialize to JSON.
   * @return JSON style string
   */
  public String toStringJSON() {
    return "{" + "\"clientID\":\"" + clientID + "\",\"rating\":\"" + getRating() + "\"}";
  }
}
