package com.orpheusdroid.screenrecorder.utils;

import com.orpheusdroid.screenrecorder.Const;

public class Resolution {
    private int WIDTH;
    private int HEIGHT;
    private int DPI;
    private Const.Orientation orientation;

    public Resolution(int WIDTH, int HEIGHT, Const.Orientation orientation) {
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.orientation = orientation;
    }

    public Resolution(){

    }

    public int getWIDTH() {
        return WIDTH;
    }

    public void setWIDTH(int WIDTH) {
        this.WIDTH = WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public void setHEIGHT(int HEIGHT) {
        this.HEIGHT = HEIGHT;
    }

    public int getDPI() {
        return DPI;
    }

    public void setDPI(int DPI) {
        this.DPI = DPI;
    }

    public Const.Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Const.Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public String toString() {
        return "Resolution{" +
                "WIDTH=" + WIDTH +
                ", HEIGHT=" + HEIGHT +
                ", DPI=" + DPI +
                ", orientation=" + orientation +
                '}';
    }
}
