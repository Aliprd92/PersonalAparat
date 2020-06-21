package com.i.personalaparat.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.i.personalaparat.R;
import com.i.personalaparat.utils.AppSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;


public class VideoPlayerActivity extends AppCompatActivity {
    JzvdStd jzvVideoPlayer;
    ImageView imgCloseWebViewVideoPlayer;
    LinearLayout lnrVideoPlayerNoConnection;
    Button btnVideoPlayerTryConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        init();
        getPlayerReady();


    }

    private void getPlayerReady() {
        String Url = "https://www.aparat.com/api/fa/v1/video/video/show/videohash/" + getIntent().getStringExtra("VideoId");

        Response.Listener<JSONObject> objectListener = response -> {
            try {

                JSONObject dataJsonObject = response.getJSONObject("data");
                JSONObject attributesJsonObject = dataJsonObject.getJSONObject("attributes");

                //JzvdStd.startFullscreenDirectly(VideoPlayerActivity.this , JzvdStd.class, attributesJsonObject.getString("file_link"), attributesJsonObject.getString("title") );

                jzvVideoPlayer.setUp(attributesJsonObject.getString("file_link"), attributesJsonObject.getString("title"));
                Glide.with(getApplicationContext()).load(attributesJsonObject.getString("file_link")).into(jzvVideoPlayer.posterImageView);
                jzvVideoPlayer.fullscreenButton.setVisibility(View.GONE);
                jzvVideoPlayer.setScreenFullscreen();

            } catch (JSONException e) {
                e.printStackTrace();
                jzvVideoPlayer.setVisibility(View.GONE);
                lnrVideoPlayerNoConnection.setVisibility(View.VISIBLE);
            }

        };
        Response.ErrorListener errorListener = error -> {
            jzvVideoPlayer.setVisibility(View.GONE);
            lnrVideoPlayerNoConnection.setVisibility(View.VISIBLE);
        };
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url, null, objectListener, errorListener);
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


        imgCloseWebViewVideoPlayer.setOnClickListener(v -> finish());

    }

    private void init() {

        imgCloseWebViewVideoPlayer = findViewById(R.id.imgCloseWebViewVideoPlayer);
        jzvVideoPlayer = findViewById(R.id.jzvVideoPlayer);
        lnrVideoPlayerNoConnection = findViewById(R.id.lnrVideoPlayerNoConnection);
        btnVideoPlayerTryConnection = findViewById(R.id.btnVideoPlayerTryConnection);


        btnVideoPlayerTryConnection.setOnClickListener(v -> {
            getPlayerReady();
            lnrVideoPlayerNoConnection.setVisibility(View.GONE);
            jzvVideoPlayer.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }


}
