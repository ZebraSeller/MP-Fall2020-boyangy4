package edu.illinois.cs.cs125.fall2020.mp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.concurrent.CompletableFuture;


import edu.illinois.cs.cs125.fall2020.mp.R;
import edu.illinois.cs.cs125.fall2020.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.fall2020.mp.databinding.ActivityCourseBinding;
import edu.illinois.cs.cs125.fall2020.mp.models.Course;
import edu.illinois.cs.cs125.fall2020.mp.models.Rating;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;
import edu.illinois.cs.cs125.fall2020.mp.network.Client;

/**
 * CourseActivity for displaying specific course's description & title etc.
 */
public class CourseActivity extends MainActivity implements
    Client.CourseClientCallbacks {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final String TAG = CourseActivity.class.getSimpleName();

  //Bind to the layout activity_course.xml.
  private ActivityCourseBinding binding;

  private Summary currentSummary;
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
      currentSummary = key;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    CourseableApplication application = (CourseableApplication) getApplication();
    application.getCourseClient().getCourse(key, this);
    binding.textTitle.setText(key.getTitle());
    String userID = application.getClientID();
    final Summary currentS = key;
    Log.d("rating", "current userID is " + userID);
    application.getCourseClient().getRating(key, userID, this);
    RatingBar bar = binding.rating;
    CompletableFuture<Rating> completableFuture = new CompletableFuture<>();
    bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
      /**
       * Detect when rating changes.
       * @param ratingBar the rating bar that changed.
       * @param v current rating value.
       * @param b whether the change is form user input.
       */
      @Override
      public void onRatingChanged(final RatingBar ratingBar, final float v, final boolean b) {
          Log.d("rating", "Rating is Changed");
          Rating rating = new Rating(userID, v);
        application.getCourseClient().postRating(currentS, rating,
            new Client.CourseClientCallbacks() {
            @Override
            public void yourRating(final Summary summary, final Rating rating) {
                completableFuture.complete(rating);
            }
          });
      }
    });
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
  /**
   * Callback called when the client has retrieved the course using summary specified.
   *
   * @param summary summary used to retrieve course.
   * @param rating the rating retrieved.
   */
  @Override
  public void yourRating(final Summary summary, final Rating rating) {
    Log.d("rating", "yourRating called back");
    Log.d("rating", "with rating" + rating.toString());
    float rate = (float) rating.getRating();
    binding.rating.setRating(rate);
  }
}
