package com.aliudurim.spotifystreamerstage2.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aliudurim.spotifystreamerstage2.R;
import com.aliudurim.spotifystreamerstage2.activitys.FragmentHolder;
import com.aliudurim.spotifystreamerstage2.adapters.TracksArtistAdapter;
import com.aliudurim.spotifystreamerstage2.callbacks.TabletPosition;
import com.aliudurim.spotifystreamerstage2.callbacks.Top10CallBack;
import com.aliudurim.spotifystreamerstage2.object.TopTrack;
import com.aliudurim.spotifystreamerstage2.tasks.Top10Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by DurimAliu on 07/06/15.
 */
public class TracksFragment extends Fragment implements Top10CallBack {

    public static TabletPosition tabletPosition;

    private TracksArtistAdapter tracksArtistAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String spotifyId = "";
    private ArrayList<TopTrack> trackArrayList = new ArrayList<>();
    private Toast toast;

    @InjectView(R.id.rvTracksOfArtist)
    RecyclerView rvTracksOfArtist;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View gv = inflater.inflate(R.layout.tracks_screen, container, false);

        ButterKnife.inject(this, gv);

        if (!FragmentHolder.isTablet) {
            spotifyId = getArguments().getString("spotifyId");
        }

        rvTracksOfArtist.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        rvTracksOfArtist.setLayoutManager(mLayoutManager);

        tracksArtistAdapter = new TracksArtistAdapter(getActivity(), TracksFragment.this);
        tracksArtistAdapter.setTrackArrayList(trackArrayList);
        rvTracksOfArtist.setAdapter(tracksArtistAdapter);


        if (savedInstanceState == null || !savedInstanceState.containsKey("trackList")) {

            if (isNetworkAvailable(getActivity())) {
                if (spotifyId.length() > 0)
                    new Top10Task(TracksFragment.this, progressBar).execute(spotifyId);
            } else {
                showToast();
            }

        } else {
            trackArrayList = savedInstanceState.getParcelableArrayList("trackList");
            tracksArtistAdapter.setTrackArrayList(trackArrayList);
        }

        tabletPosition = new TabletPosition() {
            @Override
            public void onTabletPosition(String spotifyId) {

                if (isNetworkAvailable(getActivity())) {
                    if (spotifyId.length() > 0)
                        new Top10Task(TracksFragment.this, progressBar).execute(spotifyId);
                } else {
                    showToast();
                }
            }
        };
        return gv;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("trackList", trackArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTop10CallBack(List<Track> tracksList) {

        trackArrayList.clear();
        for (int i = 0; i < tracksList.size(); i++) {
            if (tracksList.get(i).album.images.size() > 0 && tracksList.get(i).artists.size() > 0) {
                trackArrayList.add(new TopTrack(tracksList.get(i).name, tracksList.get(i).album.name, tracksList.get(i).album.images.get(0).url, tracksList.get(i).artists.get(0).name, tracksList.get(i).preview_url));
            }
        }
        tracksArtistAdapter.setTrackArrayList(trackArrayList);
    }

    public void showToast() {
        cancelToast();
        toast = Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void cancelToast() {

        if (toast != null) {
            toast.cancel();
        }
    }

    public boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
