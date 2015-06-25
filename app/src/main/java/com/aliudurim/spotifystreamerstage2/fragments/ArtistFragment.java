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
import android.widget.SearchView;
import android.widget.Toast;

import com.aliudurim.spotifystreamerstage2.R;
import com.aliudurim.spotifystreamerstage2.adapters.ListArtistAdapter;
import com.aliudurim.spotifystreamerstage2.callbacks.SearchCallBack;
import com.aliudurim.spotifystreamerstage2.tasks.SearchArtistTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.models.Artist;


public class ArtistFragment extends Fragment implements SearchCallBack {


    @InjectView(R.id.rvListArtist)
    RecyclerView rvListArtist;
    @InjectView(R.id.svArtist)
    SearchView svArtist;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    private ListArtistAdapter listArtistAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Artist> artistArrayList = new ArrayList<>();
    private Toast toast;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View gv = inflater.inflate(R.layout.artist_screen, container, false);

        ButterKnife.inject(this, gv);

        svArtist.setIconified(false);

        rvListArtist.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        rvListArtist.setLayoutManager(mLayoutManager);

        listArtistAdapter = new ListArtistAdapter(getActivity());
        listArtistAdapter.setArtistArrayList(artistArrayList);
        rvListArtist.setAdapter(listArtistAdapter);


        svArtist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                if (query.length() > 0) {
                    if (isNetworkAvailable(getActivity())) {
                        new SearchArtistTask(ArtistFragment.this, true, progressBar).execute(query);
                    } else {
                        showToast("No Internet Connection");
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                if (newText.length() > 0) {
                    if (isNetworkAvailable(getActivity())) {
                        new SearchArtistTask(ArtistFragment.this, false, progressBar).execute(newText);
                    } else {
                        showToast("No Internet Connection");
                    }
                }

                return false;
            }
        });

        return gv;
    }


    @Override
    public void onSearchCallBack(List<Artist> artistArrayList, boolean isArtistExist) {

        this.artistArrayList.clear();
        this.artistArrayList.addAll(artistArrayList);
        listArtistAdapter.setArtistArrayList(this.artistArrayList);


        if (isArtistExist && artistArrayList.size() == 0) {
            showToast("This artist name is not found");
        }

    }

    public void showToast(String text) {
        cancelToast();
        toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void cancelToast() {

        if (toast != null) {
            toast.cancel();
        }
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
