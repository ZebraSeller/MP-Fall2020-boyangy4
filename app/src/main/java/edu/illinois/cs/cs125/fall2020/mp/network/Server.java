package edu.illinois.cs.cs125.fall2020.mp.network;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.illinois.cs.cs125.fall2020.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.fall2020.mp.models.Rating;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Development course API server.
 *
 * <p>Normally you would run this server on another machine, which the client would connect to over
 * the internet. For the sake of development, we're running the server right alongside the app on
 * the same device. However, all communication between the course API client and course API server
 * is still done using the HTTP protocol. Meaning that eventually it would be straightforward to
 * move this server to another machine where it could provide data for all course API clients.
 *
 * <p>You will need to add functionality to the server for MP1 and MP2.
 */
public final class Server extends Dispatcher {
  @SuppressWarnings({"unused", "RedundantSuppression"})
  private static final String TAG = Server.class.getSimpleName();

  private final Map<String, String> summaries = new HashMap<>();

  private MockResponse getSummary(@NonNull final String path) {
    String[] parts = path.split("/");
    if (parts.length != 2) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    String summary = summaries.get(parts[0] + "_" + parts[1]);
    if (summary == null) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    }
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(summary);
  }

  private String theString = "server: The String";
  private MockResponse testPost(@NonNull final RecordedRequest request) {
    if (request.getMethod().equals("GET")) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(theString);
    } else if (request.getMethod().equals("POST")) {
      theString = request.getBody().readUtf8();
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).setHeader(
          "Location", "/test/"
      ); // this is a redirect ^^.
      //return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK);
    }
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
  }
  //map for storing ratings, string key is course INFo, Rating is value and user ID.
  private Map<String, ArrayList<Rating>> ratings = new HashMap<>();

  private MockResponse getRating(
      @NonNull final RecordedRequest request,
      @NonNull final String path) {
    String userID;
    System.out.println("getRating: path is " + path);
    if (path.contains("/?")) {
      System.out.println("getRating: 400 Bad Request due to: wrong format before UUID");
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }
    if (path.contains("client")) {
      userID = path.split("\\?")[1].replaceFirst("client=", "");
      System.out.println("getRating: userID is " + userID);
    } else {
      System.out.println("getRating: 400 Bad Request due to: no UUID in URL");
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    final int partsLength = 4;
    String[] parts = path.split("/");
    if (parts.length != partsLength) {
      System.out.println("getRating: 404 Not Found due to: Wrong parts length");
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    }
    parts[3] = parts[3].substring(0, 3);
    String key = parts[0] + parts[1] + parts[2] + parts[3]; // this part is to get course info key ready.
    System.out.println("getRating: key is " + key);
    Double ratingGottenFromStorage;
    //following part checks for invalid course info.
    String summary = summaries.get(parts[0] + "_" + parts[1]);
    if (summary != null && summary.contains("\"number\" : \"" + parts[3])) {
      System.out.println("getRating: found the object in map.");
    } else {
      System.out.println("getRating: specified course not found in map");
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    }
    if (ratings.containsKey(key)) {
      for (Rating rating : ratings.get(key)) {
        if (rating.getId().equals(userID)) {
          ratingGottenFromStorage = rating.getRating();
          return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
              .setBody(ratingGottenFromStorage.toString());
        }
      }
      System.out.println("getRating: rating not found");
      ratingGottenFromStorage = -1.0;
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
          .setBody(ratingGottenFromStorage.toString());
    }
    //not entering if statement means not ratings for the course.
    Rating rating = new Rating(userID, Rating.NOT_RATED);
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(rating.toStringJSON());

  }
  private MockResponse setRating(
      @NonNull final RecordedRequest request,
      @NonNull final String path) {
    return null;
  }

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  private final Map<Summary, String> courses = new HashMap<>();

  private MockResponse getCourse(@NonNull final String path) {
    final int partsLength = 4;
    String[] parts = path.split("/");
    if (parts.length != partsLength) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }
//    System.out.println("parts length is " + parts.length);
//    System.out.println(parts[0] + "_" + parts[1] + "_" + parts[2] + "_" + parts[3]);
    Summary keyToGet = new Summary(parts[0], parts[1], parts[2], parts[3], "");
    String course = courses.get(keyToGet);
    if (course == null) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    }
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(course);
  }

  @NonNull
  @Override
  public MockResponse dispatch(@NonNull final RecordedRequest request) {
    try {
      String path = request.getPath();
      if (path == null || request.getMethod() == null) {
        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
      } else if (path.equals("/") && request.getMethod().equalsIgnoreCase("HEAD")) {
        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK);
      } else if (path.startsWith("/summary/")) {
        return getSummary(path.replaceFirst("/summary/", ""));
      } else if (path.startsWith("/course/")) {
        return getCourse(path.replaceFirst("/course/", ""));
      } else if (path.equals("/test/")) {
        return testPost(request);
      } else if (path.startsWith("/rating/")) {
        if (request.getMethod().equals("GET")) {
          return getRating(request, path.replaceFirst("/rating/", ""));
        } else if (request.getMethod().equals("POST")) {
          return setRating(request, path.replaceFirst("/rating/", ""));
        }
      }
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    } catch (Exception e) {
      System.out.println("Error is " + e.toString());
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
    }
  }

  private static boolean started = false;

  /**
   * Start the server if has not already been started.
   *
   * <p>We start the server in a new thread so that it operates separately from and does not
   * interfere with the rest of the app.
   */
  public static void start() {
    if (!started) {
      new Thread(Server::new).start();
      started = true;
    }
  }

  private final ObjectMapper mapper = new ObjectMapper();

  private Server() {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    loadSummary("2020", "fall");
    loadCourses("2020", "fall");

    try {
      MockWebServer server = new MockWebServer();
      server.setDispatcher(this);
      server.start(CourseableApplication.SERVER_PORT);

      String baseUrl = server.url("").toString();
      if (!CourseableApplication.SERVER_URL.equals(baseUrl)) {
        //Log.i("url",CourseableApplication.SERVER_URL.toString());
        throw new IllegalStateException("Bad server URL: " + baseUrl);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  @SuppressWarnings("SameParameterValue")
  private void loadSummary(@NonNull final String year, @NonNull final String semester) {
    String filename = "/" + year + "_" + semester + "_summary.json";
    String json =
        new Scanner(Server.class.getResourceAsStream(filename), "UTF-8").useDelimiter("\\A").next();
    summaries.put(year + "_" + semester, json);
  }

  @SuppressWarnings("SameParameterValue")
  private void loadCourses(@NonNull final String year, @NonNull final String semester) {
    String filename = "/" + year + "_" + semester + ".json";
    String json =
        new Scanner(Server.class.getResourceAsStream(filename), "UTF-8").useDelimiter("\\A").next();
    try {
      JsonNode nodes = mapper.readTree(json);
      for (Iterator<JsonNode> it = nodes.elements(); it.hasNext(); ) {
        JsonNode node = it.next();
        Summary course = mapper.readValue(node.toString(), Summary.class);
        courses.put(course, node.toPrettyString());
      }
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }
}
