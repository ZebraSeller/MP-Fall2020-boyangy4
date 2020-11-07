package edu.illinois.cs.cs125.fall2020.mp.models;

/**
 * Course data model, inheriting Summary.
 */
public class Course extends Summary {
  private String description;

  /**
   * Get the description for the course object.
   * @return string containing description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Create a Course with the provided fields.
   *
   * @param setYear         the year for this Course
   * @param setSemester     the semester for this Course
   * @param setDepartment   the department for this Course
   * @param setNumber       the number for this Course
   * @param setTitle        the title for this Course
   * @param setDescription  the description for this Course.
   */
  public Course(
      final String setYear,
      final String setSemester,
      final String setDepartment,
      final String setNumber,
      final String setTitle,
      final String setDescription) {
    super(setYear, setSemester, setDepartment, setNumber, setTitle);
    description = setDescription;
  }

  /**
   * Default Constructor.
   */
  public Course() {
    super();
    description = null;
  }

}
