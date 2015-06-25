package com.aliudurim.spotifystreamerstage2.activitys;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.aliudurim.spotifystreamerstage2.R;
import com.aliudurim.spotifystreamerstage2.callbacks.UpDateActionBar;
import com.aliudurim.spotifystreamerstage2.fragments.TracksFragment;

/**
 * Created by DurimAliu on 21/06/15.
 */
public class FragmentHolder extends ActionBarActivity {

    public static boolean isTablet = false;
    public static UpDateActionBar upDateActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_holder);

        final ActionBar actionBar = getSupportActionBar();

        if (findViewById(R.id.container) != null) {

            isTablet = true;


            upDateActionBar = new UpDateActionBar() {
                @Override
                public void onUpDateActionBar(String artistName) {
                    actionBar.setSubtitle(artistName);
                }
            };

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new TracksFragment())
                        .commit();
            }

        } else {

            isTablet = false;

        }
    }
}
