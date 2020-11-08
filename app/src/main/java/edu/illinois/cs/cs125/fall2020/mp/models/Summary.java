package edu.illinois.cs.cs125.fall2020.mp.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Model holding the course summary information shown in the course list.
 *
 * <p>You will need to complete this model for MP0.
 */
public class Summary implements SortedListAdapter.ViewModel {
  private String year;

  /**
   * Get the year for this Summary.
   *
   * @return the year for this Summary
   */
  public final String getYear() {
    return year;
  }

  private String semester;

  /**
   * Get the semester for this Summary.
   *
   * @return the semester for this Summary
   */
  public final String getSemester() {
    return semester;
  }

  private String department;

  /**
   * Get the department for this Summary.
   *
   * @return the department for this Summary
   */
  public final String getDepartment() {
    return department;
  }

  private String number;

  /**
   * Get the number for this Summary.
   *
   * @return the number for this Summary
   */
  public final String getNumber() {
    return number;
  }

  private String title;

  /**
   * Get the title for this Summary.
   *
   * @return the title for this Summary
   */
  public final String getTitle() {
    return title;
  }
  /**
   * Get the UI String.
   *
   * @return the UI String in format required.
   */
  public String getUIString() {
    return department + " " + number + ": " + title;
  }
  /**
   * Create an empty Summary.
   */
  @SuppressWarnings({"unused", "RedundantSuppression"})
  public Summary() {}

  /**
   * Create a Summary with the provided fields.
   *
   * @param setYear       the year for this Summary
   * @param setSemester   the semester for this Summary
   * @param setDepartment the department for this Summary
   * @param setNumber     the number for this Summary
   * @param setTitle      the title for this Summary
   */
  public Summary(
      final String setYear,
      final String setSemester,
      final String setDepartment,
      final String setNumber,
      final String setTitle) {
    year = setYear;
    semester = setSemester;
    department = setDepartment;
    number = setNumber;
    title = setTitle;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof Summary)) {
      return false;
    }
    Summary course = (Summary) o;
    return Objects.equals(year, course.year)
        && Objects.equals(semester, course.semester)
        && Objects.equals(department, course.department)
        && Objects.equals(number, course.number);
  }

  /**
   * {@inheritDoc}
   */
  @NotNull
  @Override
  public String toString() {
    return year + "/" + semester + "/" + department + "/" + number + "/" + title;
  }

  /**
   * Summary to String, No title.
   * @return Summary to String, No title.
   */
  public String toStringJSON() {
    String result = null;
    try {
      JSONObject jSONString = new JSONObject()
          .put("year", year)
          .put("semester", semester)
          .put("department", department)
          .put("number", number)
          .put("title", title);
      result = jSONString.toString();
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(year, semester, department, number);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> boolean isSameModelAs(@NonNull final T model) {
    return equals(model);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> boolean isContentTheSameAs(@NonNull final T model) {
    return equals(model);
  }

  /** Comparator.
   * Compares courseModel1 & courseModel2 by 1. dept. 2. number. 3. title.
   */
  public static final Comparator<Summary> COMPARATOR = (courseModel1, courseModel2) -> {
    if (courseModel1.department.compareTo(courseModel2.department) > 0) {
      return 1;
    } else if (courseModel1.department.compareTo(courseModel2.department) < 0) {
      return -1;
    } else if (courseModel1.department.compareTo(courseModel2.department) == 0) {
      if (courseModel1.number.compareTo(courseModel2.number) > 0) {
        return 1;
      } else if (courseModel1.number.compareTo(courseModel2.number) < 0) {
        return -1;
      } else if (courseModel1.number.compareTo(courseModel2.number) == 0) {
        if (courseModel1.title.compareTo(courseModel2.title) > 0) {
          return 1;
        } else if (courseModel1.title.compareTo(courseModel2.title) < 0) {
          return -1;
        } else if (courseModel1.title.compareTo(courseModel2.title) == 0) {
          return 0;
        }
      }
    }
    Log.wtf("Error", "Comparator Failed, returning 0");
    return 0;
  };
  /**Filter.
   * Filters out summary objects that do not match text in search bar.
   * @param courses list of all courses.
   * @param text text in search bar to compare with.
   * @return filtered list of courses.
   * ok
   */
  public static List<Summary> filter(
      @NonNull final List<Summary> courses, @NonNull final String text) {
    String lCText = text.toLowerCase();
    List<Summary> result = new ArrayList<>();
    for (Summary course : courses) {
      String combinedString = course.department + " " +  course.number + ": " + course.title;
      if (course.title.toLowerCase().contains(lCText)
              || course.department.toLowerCase().contains(lCText)
              || course.number.toLowerCase().contains(lCText)
              || course.year.toLowerCase().contains(lCText)
              || course.semester.toLowerCase().contains(lCText)
              || combinedString.toLowerCase().contains(lCText)) {
        result.add(course);
      }
    }
    return result;
  }
}

