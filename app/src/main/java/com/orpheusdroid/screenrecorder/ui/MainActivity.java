package com.orpheusdroid.screenrecorder.ui;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orpheusdroid.screenrecorder.Config;
import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.DonateActivity;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.interfaces.IPermissionListener;
import com.orpheusdroid.screenrecorder.services.RecordingService;
import com.orpheusdroid.screenrecorder.ui.settings.fragments.RootSettingsFragment;
import com.orpheusdroid.screenrecorder.utils.Log;
import com.orpheusdroid.screenrecorder.utils.PermissionHelper;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private PermissionHelper permissionHelper;
    private IPermissionListener permissionListener;
    private FloatingActionButton fab;
    private BottomNavigationView navView;
    private BottomAppBar bottomAppBar;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mediaProjection;

    private ArrayList<String> files = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.fragment, new RootSettingsFragment())
                            .commit();
                    fab.show();
                    return true;
                case R.id.navigation_notifications:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.fragment, new VideoListFragment())
                            .commit();
                    fab.hide();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, new RootSettingsFragment())
                .commit();

        navView = findViewById(R.id.bottom_navigation);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        bottomAppBar = findViewById(R.id.bottom_bar);

        //Acquiring media projection service to start screen mirroring
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            /*boolean shouldShowFloatingControl = Config.getInstance(MainActivity.this).isFloatingControls();
            Intent recordingIntent = new Intent(MainActivity.this, RecordingService.class);
            if (shouldShowFloatingControl){
                recordingIntent.setAction(Const.SCREEN_RECORDING_SHOW_FLOATING_ACTIONS);
            } else {
                recordingIntent.setAction(Const.SCREEN_RECORDING_START);
            }
            startService(recordingIntent);*/
            if (mediaProjection == null && !isServiceRunning(RecordingService.class)) {
                //Request Screen recording permission
                startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), Const.SCREEN_RECORD_REQUEST_CODE);
            } else if (isServiceRunning(RecordingService.class)) {
                //stop recording if the service is already active and recording
                Toast.makeText(MainActivity.this, "Screen already recording", Toast.LENGTH_SHORT).show();
            }
        });

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) navView.getParent()).getLayoutParams();
        params.setBehavior(new HideBottomViewOnScrollBehavior());

        permissionHelper = PermissionHelper.getInstance(this);
        init();

        Config config = Config.getInstance(this);
        config.buildConfig();
    }

    private void init() {
        permissionHelper.requestPermissionStorage();
    }

    public void setPermissionListener(IPermissionListener permissionListener) {
        this.permissionListener = permissionListener;
    }

    public void setBottomBarVisibility(boolean isVisible) {
        if (isVisible) {
            navView.setVisibility(View.VISIBLE);
            bottomAppBar.setVisibility(View.VISIBLE);
            //bottomAppBar.animate().translationY(0).alpha(1.0f);
            fab.show();
        } else {
            navView.setVisibility(View.GONE);
            bottomAppBar.setVisibility(View.GONE);
            //bottomAppBar.animate().translationY(bottomAppBar.getHeight()).alpha(0.0f);
            fab.hide();
        }
    }

    private void handleStoragePermission(int[] grantResults) {
        if ((grantResults.length > 0) &&
                (grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            android.util.Log.d(Const.TAG, "write storage Permission Denied");
            /* Disable floating action Button in case write storage permission is denied.
             * There is no use in recording screen when the video is unable to be saved */
            fab.setEnabled(false);
            permissionHelper.showSnackbar();
        } else if ((grantResults.length > 0) &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            /* Since we have write storage permission now, lets create the app directory
             * in external storage*/
            Log.d(Const.TAG, "write storage Permission granted");
            permissionHelper.createDir();
            fab.setEnabled(true);
        }
    }

    /**
     * Method to check if the {@link RecordingService} is running
     *
     * @param serviceClass Collection containing the {@link RecordingService} class
     * @return boolean value representing if the {@link RecordingService} is running
     * @throws NullPointerException May throw NullPointerException
     */
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * onActivityResult method to handle the activity results for floating controls
     * and screen recording permission
     *
     * @param requestCode Unique request code for different startActivityForResult calls
     * @param resultCode  result code representing the user's choice
     * @param data        Extra intent data passed from calling intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String intentAction = getIntent().getAction();

        //Result for system windows permission required to show floating controls
        if (requestCode == Const.FLOATING_CONTROLS_SYSTEM_WINDOWS_CODE || requestCode == Const.CAMERA_SYSTEM_WINDOWS_CODE) {
            //setSystemWindowsPermissionResult(requestCode);
            return;
        }

        //The user has denied permission for screen mirroring. Let's notify the user
        if (resultCode == RESULT_CANCELED && requestCode == Const.SCREEN_RECORD_REQUEST_CODE) {
            /*Toast.makeText(this,
                    getString(R.string.screen_recording_permission_denied), Toast.LENGTH_SHORT).show();
            //Return to home screen if the app was started from app shortcut
            if (intentAction != null && intentAction.equals(getString(R.string.app_shortcut_action)))
                this.finish();*/
            return;

        }

        /*If code reaches this point, congratulations! The user has granted screen mirroring permission
         * Let us set the recorderservice intent with relevant data and start service*/
        Intent recorderService = new Intent(this, RecordingService.class);
        recorderService.setAction(Const.SCREEN_RECORDING_START);
        recorderService.putExtra(Const.RECORDER_INTENT_DATA, data);
        recorderService.putExtra(Const.RECORDER_INTENT_RESULT, resultCode);
        startService(recorderService);

        //if (intentAction != null && intentAction.equals(getString(R.string.app_shortcut_action)))
        //this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("Permission", requestCode + "");
        if (requestCode == Const.EXTDIR_REQUEST_CODE) {
            handleStoragePermission(grantResults);
        }

        if (permissionListener != null)
            permissionListener.onPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.privacy_policy:
                startActivity(new Intent(this, PrivacyPolicy.class));
                return true;
            case R.id.menu_faq:
                startActivity(new Intent(this, FAQActivity.class));
                return true;
            case R.id.donate:
                startActivity(new Intent(this, DonateActivity.class));
                return true;
            case R.id.help:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/joinchat/C_ZSIUKiqUCI5NsPMAv0eA")));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No browser app installed!", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
