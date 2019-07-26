package com.orpheusdroid.screenrecorder.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.adapter.AboutAdapter;
import com.orpheusdroid.screenrecorder.utils.AboutBuilder;
import com.orpheusdroid.screenrecorder.utils.Log;

public class AboutActivity extends BaseActivity {
    private ImageView icon;
    private AppBarLayout appBar;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        appBar = findViewById(R.id.appBarLayout);
        icon = findViewById(R.id.about_icon);
        rv = findViewById(R.id.about_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(recyclerViewLayoutManager);
        AboutAdapter recyclerViewAdapter = new AboutAdapter(this, AboutBuilder.getInstance(this).buildAbout());

        // set custom adapter to recycler view
        rv.setAdapter(recyclerViewAdapter);


        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float alpha = 1.0f - Math.abs(
                    verticalOffset / (float) appBarLayout.getTotalScrollRange()
            );
            icon.setAlpha(alpha);
            //icon.setForegroundGravity(Gravity.BOTTOM);
            icon.scrollTo(verticalOffset, (int) alpha);

            Log.d(Const.TAG, "Scroll alpha: " + (alpha + 1) + ", X length: ");
        });
    }

    public void onChipClick(View view) {
        switch (view.getId()) {
            case R.id.source_chip:
                startActivityForURL("https://gitlab.com/vijai/screenrecorder");
                break;
            case R.id.license_chip:
                startActivityForURL("https://www.gnu.org/licenses/agpl-3.0.en.html");
                break;
            case R.id.support_chip:
                startActivityForURL("https://t.me/joinchat/C_ZSIUKiqUCI5NsPMAv0eA");
                break;
            case R.id.translate_chip:
                startActivityForURL("https://crowdin.com/project/screencam");
                break;
        }
    }

    private void startActivityForURL(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No browser app installed!", Toast.LENGTH_SHORT).show();
        }
    }
}
