package com.orpheusdroid.screenrecorder.adapter.models;

public class AboutModel {

    public static enum TYPE{
        HEADER(0),
        INFO(1),
        DATA(2);
        private final int value;

        TYPE(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }

        public static TYPE getStatusFromInt(int status) {
            //here return the appropriate enum constant
            for (TYPE oprname : TYPE.values()) {
                if (status == oprname.getValue()) {
                    return oprname;
                }
            }
            return null;
        }
    }

    private String text;
    private String version;
    private boolean isMagisk;
    private boolean hasRoot;
    private String buildType;

    private TYPE type;

    public AboutModel(String text, TYPE type) {
        this.text = text;
        this.type = type;
    }

    public AboutModel(String version, boolean isMagisk, boolean hasRoot, String buildType, TYPE type) {
        this.version = version;
        this.isMagisk = isMagisk;
        this.hasRoot = hasRoot;
        this.buildType = buildType;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isMagisk() {
        return isMagisk;
    }

    public void setMagisk(boolean magisk) {
        isMagisk = magisk;
    }

    public boolean isHasRoot() {
        return hasRoot;
    }

    public void setHasRoot(boolean hasRoot) {
        this.hasRoot = hasRoot;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }
}
