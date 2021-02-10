package com.csarmiento.flixter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.csarmiento.flixter.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {

  TextView tvDetailTitle;
  TextView tvDetailOverview;
  RatingBar ratingBar;
  YouTubePlayerView player;

  public static final String YT_API_KEY = "AIzaSyAQrQGfkVFE09gJUusUTWAawi-_sC8PXi4";
  public static final String VIDEO_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);

    tvDetailTitle = findViewById(R.id.tvDetailTitle);
    tvDetailOverview = findViewById(R.id.tvDetatilOverview);
    ratingBar = findViewById(R.id.ratingBar);
    player = findViewById(R.id.player);

    Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
    tvDetailTitle.setText(movie.getTitle());
    tvDetailOverview.setText(movie.getOverview());
    ratingBar.setRating((float)movie.getRating());

    AsyncHttpClient client = new AsyncHttpClient();
    client.get(String.format(VIDEO_URL, movie.getId()), new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int i, Headers headers, JSON json) {
        try {
          JSONArray results = json.jsonObject.getJSONArray("results");
          if (results.length() == 0) {
            return;
          }
          else {
            String ytUrl = results.getJSONObject(0).getString("key");
            Log.d("DetailActivity", ytUrl);
            initializeYoutube(ytUrl, movie.getRating());
          }

        } catch (JSONException e) {
          Log.e("DetailActivity", "Failed to parse JSON");
        }
      }

      @Override
      public void onFailure(int i, Headers headers, String s, Throwable throwable) {

      }
    });

  }

  private void initializeYoutube(String ytUrl, double rating) {
    player.initialize(YT_API_KEY, new YouTubePlayer.OnInitializedListener() {
      @Override
      public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        Log.d("DetailActivity", "onInitializationSuccess");
        if (rating >= 5) {
          youTubePlayer.loadVideo(ytUrl);
        }
        else {
          youTubePlayer.cueVideo(ytUrl);
        }
      }

      @Override
      public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.d("DetailActivity", "onInitializationFailure");
      }
    });
  }
}