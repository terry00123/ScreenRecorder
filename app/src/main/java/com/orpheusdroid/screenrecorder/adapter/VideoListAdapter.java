package com.orpheusdroid.screenrecorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.adapter.models.videolist.VideoHeader;
import com.orpheusdroid.screenrecorder.adapter.models.videolist.VideoItem;
import com.orpheusdroid.screenrecorder.adapter.models.videolist.VideoListItem;
import com.orpheusdroid.screenrecorder.utils.Log;

import java.util.ArrayList;

public class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<VideoListItem> videos = new ArrayList<>();
    private Context context;

    public VideoListAdapter(ArrayList<VideoListItem> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return videos.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case VideoListItem.TYPE_HEADER:
                View v1 = inflater.inflate(R.layout.item_video_header, parent,
                        false);
                viewHolder = new VideoHeaderHolder(v1);
                break;

            case VideoListItem.TYPE_VIDEO:
                View v2 = inflater.inflate(R.layout.item_video_videolist, parent, false);
                viewHolder = new VideoListHolder(v2);
                break;
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {

            case VideoListItem.TYPE_HEADER:

                VideoHeader headerItem = (VideoHeader) videos.get(position);
                VideoHeaderHolder generalViewHolder = (VideoHeaderHolder) holder;
                generalViewHolder.header.setText(headerItem.getDate());

                break;

            case VideoListItem.TYPE_VIDEO:
                VideoItem videoItem = (VideoItem) videos.get(position);
                VideoListHolder videoViewHolder = (VideoListHolder) holder;

                Log.d(Const.TAG, "Data:" + videoItem.getVideo().getFileName());

                videoViewHolder.fileName.setText(videoItem.getVideo().getFileName());
                videoViewHolder.thumbnail.setImageBitmap(videoItem.getVideo().getThumbnail());
                // Populate date item data here

                break;
        }
    }

    @Override
    public int getItemCount() {
        Log.d(Const.TAG, "Total count: " + videos.size());
        return videos != null ? videos.size() : 0;
    }

    private static final class VideoHeaderHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView header;

        VideoHeaderHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.videoList_header);
        }
    }

    private static final class VideoListHolder extends RecyclerView.ViewHolder {
        private TextView fileName;
        private ImageView thumbnail;

        VideoListHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileName);
            thumbnail = itemView.findViewById(R.id.videoThumb);
        }
    }
}
