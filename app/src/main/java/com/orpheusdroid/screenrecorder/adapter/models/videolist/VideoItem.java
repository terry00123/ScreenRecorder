package com.orpheusdroid.screenrecorder.adapter.models.videolist;

public class VideoItem extends VideoListItem {

    private Video video;

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    @Override
    public int getType() {
        return TYPE_VIDEO;
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "video=" + video +
                '}';
    }
}
