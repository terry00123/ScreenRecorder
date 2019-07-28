package com.orpheusdroid.screenrecorder.adapter.models.videolist;

import android.graphics.Bitmap;

import java.io.File;

public class Video {
    private String FileName;
    private File file;
    private Bitmap thumbnail;
    private String lastModified;
    private boolean isSection = false;
    private boolean isSelected = false;

    public Video(boolean isSection, String lastModified) {
        this.isSection = isSection;
        this.lastModified = lastModified;
    }

    public Video(String fileName, File file, String lastModified) {
        this.FileName = fileName;
        this.file = file;
        this.lastModified = lastModified;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isSection() {
        return isSection;
    }

    public void setSection(boolean section) {
        isSection = section;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "Video{" +
                "FileName='" + FileName + '\'' +
                ", file=" + file +
                ", thumbnail=" + thumbnail +
                ", lastModified=" + lastModified +
                ", isSection=" + isSection +
                ", isSelected=" + isSelected +
                '}';
    }
}
