package edu.illinois.cs.cs125.fall2020.mp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.illinois.cs.cs125.fall2020.mp.R;
import edu.illinois.cs.cs125.fall2020.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.fall2020.mp.databinding.ActivityCourseBinding;
import edu.illinois.cs.cs125.fall2020.mp.models.Course;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;
import edu.illinois.cs.cs125.fall2020.mp.network.Client;

/**
 * CourseActivity for displaying specific course's description & title etc.
 */
public class CourseActivity extends MainActivity implements Client.CourseClientCallbacks {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final String TAG = CourseActivity.class.getSimpleName();

  //Bind to the layout activity_course.xml.
  private ActivityCourseBinding binding;

  /**
   * starts onCreate.
   * @param savedInstanceState bundle
   */
  @Override
  protected void onCreate(@Nullable final Bundle savedInstanceState) {
    Log.i("Course Activity", "Course Activity Started");
    super.onCreate(savedInstanceState);

    binding = DataBindingUtil.setContentView(this, R.layout.activity_course);

    Intent intent = getIntent();
    String courseJSON = intent.getStringExtra("COURSE");
    Summary key = null;
    try {
      ObjectNode node = (ObjectNode) MAPPER.readTree(courseJSON);
      key = new Summary(node.get("year").asText(), node.get("semester").asText(),
          node.get("department").asText(), node.get("number").asText(), node.get("title").asText());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    CourseableApplication application = (CourseableApplication) getApplication();
    application.getCourseClient().getCourse(key, this);
    binding.textTitle.setText(key.getTitle());
  }

  /**
   * Callback called when the client has retrieved the course using summary specified.
   *
   * @param summary summary used to retrieve course.
   * @param course the course retrieved.
   */
  @Override
  public void courseResponse(final Summary summary, final Course course) {
    Log.d(TAG, "courseResponse called back");
    Log.d(TAG, "course got is " + course.getTitle());
    binding.textDescription.setText(course.getDescription());
  }
}
