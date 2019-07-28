package com.orpheusdroid.screenrecorder.adapter.models.videolist;

public class VideoHeader extends VideoListItem {
    private String Date;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

    @Override
    public String toString() {
        return "VideoHeader{" +
                "Date='" + Date + '\'' +
                '}';
    }
}
