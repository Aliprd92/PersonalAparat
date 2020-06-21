package com.i.personalaparat.Activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.i.personalaparat.Adapters.VideoCustomAdapter;
import com.i.personalaparat.Models.Video;
import com.i.personalaparat.R;
import com.i.personalaparat.utils.AppSingleton;
import com.i.personalaparat.utils.CircleImageViewBehavior;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView CircleImageChannelProfile;
    CollapsingToolbarLayout mainCollapsingToolbar;
    AppBarLayout mainAppBar;
    TextView txtChannelFollower_cnt, txtVideo_cnt;
    ImageView imgChannelCover, imgShowChannelDescription;
    AlertDialog alertBox;
    String channelName, channelDescription;
    LinearLayout lnrRecyclerNoConnection;
    Button btnRecyclerTryConnection;
    String aparatChannelName = "wia.developers";
    String Url = "https://www.aparat.com/etc/api/videobyuser/username/" + aparatChannelName;
    String nextUrl ;
    ShimmerFrameLayout shimmerLayout;
    private int page = 1 ;


    RecyclerView mainRecyclerView;
    VideoCustomAdapter videoCustomAdapter;
    List<Video.Videobyuser> videosList = new ArrayList<>();

    private int loadingItemIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        init();
        getChannelInfo();
        getVideosContent();
    }

    private void findViews() {
        CircleImageChannelProfile = findViewById(R.id.CircleImageChannelProfile);
        mainCollapsingToolbar = findViewById(R.id.mainCollapsingToolbar);
        mainAppBar = findViewById(R.id.mainAppBar);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        txtChannelFollower_cnt = findViewById(R.id.txtChannelFollower_cnt);
        txtVideo_cnt = findViewById(R.id.txtVideo_cnt);
        imgChannelCover = findViewById(R.id.imgChannelCover);
        imgShowChannelDescription = findViewById(R.id.imgShowChannelDescription);
        lnrRecyclerNoConnection = findViewById(R.id.lnrRecyclerNoConnection);
        btnRecyclerTryConnection = findViewById(R.id.btnRecyclerTryConnection);
        shimmerLayout = findViewById(R.id.shimmerLayout);
    }

    private void init() {
        //Fading circle imageView
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) CircleImageChannelProfile.getLayoutParams();
        params.setBehavior(new CircleImageViewBehavior());

        //Set collapsingToolbar title typeface
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/b_yekan.ttf");
        mainCollapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        mainCollapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        mainCollapsingToolbar.setExpandedTitleTypeface(tf);
        mainCollapsingToolbar.setCollapsedTitleTypeface(tf);

        //no network handling
        btnRecyclerTryConnection.setOnClickListener(v -> {
            videosList.clear();
            videoCustomAdapter.notifyDataSetChanged();
            getChannelInfo();
            getVideosContent();
            mainRecyclerView.setVisibility(View.VISIBLE);
            lnrRecyclerNoConnection.setVisibility(View.GONE);
            shimmerLayout.setVisibility(View.VISIBLE);
            CircleImageChannelProfile.setVisibility(View.VISIBLE);
        });


        //setting adapter
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        videoCustomAdapter = new VideoCustomAdapter(MainActivity.this, videosList, mainRecyclerView);
        mainRecyclerView.setAdapter(videoCustomAdapter);

        videoCustomAdapter.setOnRetryListener(v -> {
            videosList.remove(loadingItemIndex);
            videoCustomAdapter.notifyItemRemoved(loadingItemIndex);
            videoCustomAdapter.setFail(false);
            videosList.add(null);
            loadingItemIndex = videosList.size() - 1;
            videoCustomAdapter.notifyItemInserted(loadingItemIndex);
            getVideosContent();
        });

    }

    private void getVideosContent() {
        Response.Listener<JSONObject> objectListener = response -> {

            Gson gson = new Gson();

            try {
                JSONArray jsonArrayVideo = response.getJSONArray("videobyuser");
                Video.Videobyuser[] videos = gson.fromJson(jsonArrayVideo.toString(), Video.Videobyuser[].class);
                Collections.addAll(videosList, videos);

                JSONObject jsonObjectPage = response.getJSONObject("ui");
                nextUrl = jsonObjectPage.getString("pagingForward");

                if (page != 1) {
                    videosList.remove(loadingItemIndex);
                    videoCustomAdapter.notifyItemRemoved(loadingItemIndex);
                }

                videoCustomAdapter.notifyDataSetChanged();
                videoCustomAdapter.setLoading(false);
                shimmerLayout.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
                if (page == 1) {
                    shimmerLayout.setVisibility(View.GONE);
                    lnrRecyclerNoConnection.setVisibility(View.VISIBLE);
                    mainRecyclerView.setVisibility(View.GONE);
                    CircleImageChannelProfile.setVisibility(View.GONE);
                } else {
                    videosList.remove(loadingItemIndex);
                    videoCustomAdapter.notifyItemRemoved(loadingItemIndex);
                    videoCustomAdapter.setFail(true);
                    videosList.add(null);
                    loadingItemIndex = videosList.size() - 1;
                    videoCustomAdapter.notifyItemInserted(loadingItemIndex);
                }
            }

            videoCustomAdapter.notifyDataSetChanged();

        };

        Response.ErrorListener errorListener = error -> {
            if (page == 1) {
                shimmerLayout.setVisibility(View.GONE);
                lnrRecyclerNoConnection.setVisibility(View.VISIBLE);
                mainRecyclerView.setVisibility(View.GONE);
                CircleImageChannelProfile.setVisibility(View.GONE);
            } else {
                videosList.remove(loadingItemIndex);
                videoCustomAdapter.notifyItemRemoved(loadingItemIndex);
                videoCustomAdapter.setFail(true);
                videosList.add(null);
                loadingItemIndex = videosList.size() - 1;
                videoCustomAdapter.notifyItemInserted(loadingItemIndex);
            }
        };
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url, null, objectListener, errorListener);
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

        videoCustomAdapter.setOnLoadMoreListener(() -> {
            if (!nextUrl.equals("")) {
                Url = nextUrl;
                page ++ ;
                videosList.add(null);
                loadingItemIndex = videosList.size() - 1;
                videoCustomAdapter.notifyItemInserted(loadingItemIndex);
                getVideosContent();
            }
        });
    }

    private void getChannelInfo() {

        String channelUrl = "https://www.aparat.com/etc/api/profile/username/" + aparatChannelName;

        Response.Listener<JSONObject> objectListener = response -> {

            try {
                JSONObject jsonObject = response.getJSONObject("profile");
                channelName = jsonObject.getString("name");
                channelDescription = jsonObject.getString("descr");
                String ChannelFollower_cnt = jsonObject.getString("follower_cnt") + " دنبال کننده ";
                String Video_cnt = jsonObject.getString("video_cnt") + " ویدیو ";
                txtChannelFollower_cnt.setText(ChannelFollower_cnt);
                txtVideo_cnt.setText(Video_cnt);
                mainCollapsingToolbar.setTitle(channelName);
                if (!jsonObject.getString("cover_src").equals("")) {
                    Picasso.get()
                            .load(jsonObject.getString("cover_src"))
                            .placeholder(R.drawable.cover_placeholder)
                            .into(imgChannelCover);
                }
                if (!jsonObject.getString("pic_b").equals("")) {
                    Picasso.get()
                            .load(jsonObject.getString("pic_b"))
                            .placeholder(R.drawable.image_placeholder)
                            .into(CircleImageChannelProfile);
                }


            } catch (JSONException e) {
                e.printStackTrace();
                mainRecyclerView.setVisibility(View.GONE);
                lnrRecyclerNoConnection.setVisibility(View.VISIBLE);
                shimmerLayout.setVisibility(View.GONE);
                CircleImageChannelProfile.setVisibility(View.GONE);
            }

        };

        Response.ErrorListener errorListener = error -> {
            mainRecyclerView.setVisibility(View.GONE);
            lnrRecyclerNoConnection.setVisibility(View.VISIBLE);
            shimmerLayout.setVisibility(View.GONE);
            CircleImageChannelProfile.setVisibility(View.GONE);

        };

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, channelUrl, null, objectListener, errorListener);
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

        imgShowChannelDescription.setOnClickListener(v -> showAlert(channelName, channelDescription));

    }

    private void showAlert(String Title, String Message) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.alert_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(view);
        alertBox = alertDialogBuilder.create();

        TextView txtAlertTitle;
        HtmlTextView txtAlertMessage;
        Button btnAlertPositive;

        txtAlertTitle = view.findViewById(R.id.txtAlertTitle);
        txtAlertMessage = view.findViewById(R.id.txtAlertMessage);
        btnAlertPositive = view.findViewById(R.id.btnAlertPositive);

        txtAlertTitle.setText(Title);
        txtAlertMessage.setHtml(Message);


        btnAlertPositive.setOnClickListener(v -> alertBox.dismiss());

        alertBox.show();
    }
}
