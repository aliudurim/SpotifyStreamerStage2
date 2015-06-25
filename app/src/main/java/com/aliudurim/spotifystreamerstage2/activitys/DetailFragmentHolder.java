package com.aliudurim.spotifystreamerstage2.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.aliudurim.spotifystreamerstage2.R;
import com.aliudurim.spotifystreamerstage2.fragments.TracksFragment;

/**
 * Created by DurimAliu on 21/06/15.
 */
public class DetailFragmentHolder extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_fragment_holder);


        Intent intent = getIntent();


        String spotifyId = intent.getStringExtra("spotifyId");
        String nameArtist = intent.getStringExtra("nameArtist");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Top 10 Tracks");
        actionBar.setSubtitle(nameArtist);


        TracksFragment tracksFragment = new TracksFragment();
        Bundle args = new Bundle();
        args.putString("spotifyId", spotifyId);
        tracksFragment.setArguments(args);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, tracksFragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
