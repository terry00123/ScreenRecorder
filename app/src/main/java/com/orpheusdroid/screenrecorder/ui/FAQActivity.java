package com.orpheusdroid.screenrecorder.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.adapter.FAQAdapter;
import com.orpheusdroid.screenrecorder.utils.FAQBuilder;

public class FAQActivity extends AppCompatActivity {
    private RecyclerView FAQView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FAQView = findViewById(R.id.faq_rv);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(this);
        FAQView.setLayoutManager(recyclerViewLayoutManager);
        FAQAdapter recyclerViewAdapter = new FAQAdapter(FAQBuilder.getInstance(this).buildFAQ());

        // set custom adapter to recycler view
        FAQView.setAdapter(recyclerViewAdapter);

        // Set recycler view item click listener
        //recyclerViewAdapter.setOnClick(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //finish this activity and return to parent activity
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
