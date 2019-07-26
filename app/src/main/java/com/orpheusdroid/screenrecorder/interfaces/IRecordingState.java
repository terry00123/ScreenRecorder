package com.orpheusdroid.screenrecorder.interfaces;

public interface IRecordingState {
    void onRecordingStarted();
    void onRecordingPaused();
    void onRecordingResumed();
    void onRecordingStopped();
}
