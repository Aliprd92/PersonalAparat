package com.i.personalaparat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.i.personalaparat.Activities.VideoPlayerActivity;
import com.i.personalaparat.Models.Video;
import com.i.personalaparat.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;


public class VideoCustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long MONTH_MILLIS = 30L * DAY_MILLIS;
    private static final long YEAR_MILLIS = 365L * DAY_MILLIS;
    Context context;
    List<Video.Videobyuser> videoList;
    boolean isFail;
    onLoadMoreListener onLoadMoreListener;
    View.OnClickListener onRetryListener;
    private int TotalItemCount, LastVisible;
    private boolean isLoading;
    private int Threshold = 2;
    private int VIEW_TYPE_VIDEO_ITEM = 0;
    private int VIEW_TYPE_LOADING_ITEM = 1;
    private long difference;

    public VideoCustomAdapter(Context context, List<Video.Videobyuser> videoList, RecyclerView mRecyclerView) {
        this.context = context;
        this.videoList = videoList;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                TotalItemCount = linearLayoutManager.getItemCount();
                LastVisible = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && TotalItemCount <= (LastVisible + Threshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                    isFail = false;
                }
            }
        });

    }

    public void setOnLoadMoreListener(com.i.personalaparat.Adapters.onLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setFail(boolean fail) {
        isFail = fail;
    }

    public void setOnRetryListener(View.OnClickListener onRetryListener) {
        this.onRetryListener = onRetryListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_VIDEO_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_videos, parent, false);
            return new videoViewHolder(view);
        }
        if (viewType == VIEW_TYPE_LOADING_ITEM) {

            View view = LayoutInflater.from(context).inflate(R.layout.row_loading_video, parent, false);
            return new loadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof videoViewHolder) {
            String videoViewCount = "";
            String videoPublishDate = "";
            String videoDuration = "";

            videoViewHolder mVideoViewHolder = (videoViewHolder) holder;
            final Video.Videobyuser video = videoList.get(position);

            mVideoViewHolder.txtVideoTitle.setText(video.getTitle());
            videoViewCount = video.getVisit_cnt() + " بازدید ";
            mVideoViewHolder.txtVideoViewCount.setText(videoViewCount);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String formatCurrentData = formatter.format(Calendar.getInstance().getTime());

            try {
                Date publishDate = formatter.parse(video.getCreate_date());
                Date currentDate = formatter.parse(formatCurrentData);
                difference = Math.abs(currentDate.getTime() - publishDate.getTime());
            } catch (ParseException e) {
                Log.e("EXP", e.toString());
                e.printStackTrace();
            }

            if (MINUTE_MILLIS < difference && difference < HOUR_MILLIS) {
                difference = difference / MINUTE_MILLIS;
                videoPublishDate = difference + " دقیقه پیش ";
            }
            if (HOUR_MILLIS < difference && difference < DAY_MILLIS) {
                difference = difference / HOUR_MILLIS;
                videoPublishDate = difference + " ساعت پیش ";
            }
            if (DAY_MILLIS < difference && difference < MONTH_MILLIS) {
                difference = difference / DAY_MILLIS;
                videoPublishDate = difference + " روز پیش ";
            }
            if (MONTH_MILLIS < difference && difference < YEAR_MILLIS) {
                difference = difference / MONTH_MILLIS;
                videoPublishDate = difference + " ماه پیش ";
            }
            if (YEAR_MILLIS < difference) {
                difference = difference / YEAR_MILLIS;
                videoPublishDate = difference + " سال پیش ";
            }
            mVideoViewHolder.txtVideoPublishDate.setText(videoPublishDate);
            videoDuration = " مدت ویدیو " + video.getDuration() % 60 + " : " + video.getDuration() / 60;

            mVideoViewHolder.txtVideoDuration.setText(videoDuration);

            if (!video.getBig_poster().equals("")) {
                Picasso.get()
                        .load(video.getBig_poster())
                        .placeholder(R.drawable.image_placeholder)
                        .into(mVideoViewHolder.imgVideoPoster);
            }

            mVideoViewHolder.rltvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(VideoCustomAdapter.this.context, VideoPlayerActivity.class);
                    intent.putExtra("VideoId", video.getUid());
                    context.startActivity(intent);
                }
            });
        }
        if (holder instanceof loadingViewHolder) {
            loadingViewHolder mLoadingViewHolder = (loadingViewHolder) holder;
            mLoadingViewHolder.progressBarLoading.setIndeterminate(true);
            if (isFail) {
                mLoadingViewHolder.progressBarLoading.setVisibility(View.GONE);
                mLoadingViewHolder.btnRetryRow.setVisibility(View.VISIBLE);
                mLoadingViewHolder.btnRetryRow.setOnClickListener(this.onRetryListener);
            } else {
                mLoadingViewHolder.progressBarLoading.setVisibility(View.VISIBLE);
                mLoadingViewHolder.btnRetryRow.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (videoList.get(position) == null) {
            return VIEW_TYPE_LOADING_ITEM;
        } else
            return VIEW_TYPE_VIDEO_ITEM;
    }


    @Override
    public int getItemCount() {
        return videoList == null ? 0 : videoList.size();
    }

    public static class videoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgVideoPoster;
        TextView txtVideoTitle, txtVideoViewCount, txtVideoPublishDate, txtVideoDuration;
        RelativeLayout rltvContainer;

        public videoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVideoPoster = itemView.findViewById(R.id.imgVideoPoster);
            txtVideoTitle = itemView.findViewById(R.id.txtVideoTitle);
            txtVideoViewCount = itemView.findViewById(R.id.txtVideoViewCount);
            txtVideoPublishDate = itemView.findViewById(R.id.txtVideoPublishDate);
            txtVideoDuration = itemView.findViewById(R.id.txtVideoDuration);
            rltvContainer = itemView.findViewById(R.id.rltvContainer);
        }
    }

    public static class loadingViewHolder extends RecyclerView.ViewHolder {

        MaterialProgressBar progressBarLoading;
        Button btnRetryRow;

        public loadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBarLoading = itemView.findViewById(R.id.progressBarLoading);
            btnRetryRow = itemView.findViewById(R.id.btnRetryRow);
        }
    }
}
