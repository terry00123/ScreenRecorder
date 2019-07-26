package com.orpheusdroid.screenrecorder.ui.settings;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.adapter.AppsListAdapter;
import com.orpheusdroid.screenrecorder.adapter.models.Apps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppPickerDialog extends PreferenceDialogFragmentCompat implements AppsListAdapter.OnItemClicked {
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<Apps> apps;

    public static AppPickerDialog newInstance(String key) {
        
        Bundle args = new Bundle(1);
        args.putString(ARG_KEY, key);
        
        AppPickerDialog fragment = new AppPickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View onCreateDialogView(Context context) {
        return super.onCreateDialogView(context);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        progressBar = view.findViewById(R.id.appsProgressBar);
        recyclerView = view.findViewById(R.id.appsRecyclerView);

        init();
    }

    private void init() {
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        // Generate list of installed apps and display in dialog
        new GetApps().execute();
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {

    }

    @Override
    public void onItemClick(int position) {
        Log.d(Const.TAG, "Closing dialog. received result. Pos:" + position);
        ((AppPickerPreference)getPreference()).saveString(apps.get(position).getPackageName());
        dismiss();
    }



    class GetApps extends AsyncTask<Void, Void, ArrayList<Apps>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(Const.TAG, "Picked: " + ((AppPickerPreference)getPreference()).getString("none"));
        }

        @Override
        protected void onPostExecute(ArrayList<Apps> apps) {
            super.onPostExecute(apps);

            // Hide progress bar after the apps list has been loaded
            progressBar.setVisibility(View.GONE);
            AppsListAdapter recyclerViewAdapter = new AppsListAdapter(apps);

            // set custom adapter to recycler view
            recyclerView.setAdapter(recyclerViewAdapter);

            // Set recycler view item click listener
            recyclerViewAdapter.setOnClick(AppPickerDialog.this);
        }

        @Override
        protected ArrayList<Apps> doInBackground(Void... voids) {
            PackageManager pm = getContext().getPackageManager();
            apps = new ArrayList<>();

            // Get list of all installs apps including system apps and apps without any launcher activity
            List<PackageInfo> packages = pm.getInstalledPackages(0);

            for (PackageInfo packageInfo : packages) {

                // Check if the app has launcher intent set and exclude our own app
                if (!(getContext().getPackageName().equals(packageInfo.packageName))
                        && !(pm.getLaunchIntentForPackage(packageInfo.packageName) == null)) {

                    Apps app = new Apps(
                            packageInfo.applicationInfo.loadLabel(getContext().getPackageManager()).toString(),
                            packageInfo.packageName,
                            packageInfo.applicationInfo.loadIcon(getContext().getPackageManager())

                    );

                    // Identify the previously selected app
                    app.setSelectedApp(
                            ((AppPickerPreference)AppPickerDialog.this.getPreference()).getString("none")
                                    .equals(packageInfo.packageName)
                    );
                    if (pm.getLaunchIntentForPackage(packageInfo.packageName) == null)
                        Log.d(Const.TAG, packageInfo.packageName);
                    apps.add(app);
                }
                Collections.sort(apps);
            }
            return apps;
        }
    }
}
