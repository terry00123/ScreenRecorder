package com.orpheusdroid.screenrecorder.adapter.models.videolist;


public abstract class VideoListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_VIDEO = 1;

    abstract public int getType();

}