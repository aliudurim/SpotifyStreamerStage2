package com.aliudurim.spotifystreamerstage2.tasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.aliudurim.spotifystreamerstage2.callbacks.SearchCallBack;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by DurimAliu on 07/06/15.
 */
public class SearchArtistTask extends AsyncTask<String, Void, ArtistsPager> {

    private SearchCallBack searchCallBack;
    private boolean isArtistExist = false;
    private ProgressBar progressBar;

    public SearchArtistTask(SearchCallBack searchCallBack, boolean isArtistExist, ProgressBar progressBar) {
        this.searchCallBack = searchCallBack;
        this.isArtistExist = isArtistExist;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        this.progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected ArtistsPager doInBackground(String... params) {

        SpotifyApi api = new SpotifyApi();
        try {
            SpotifyService spotify = api.getService();
            return spotify.searchArtists(params[0]);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArtistsPager artistsPager) {
        this.progressBar.setVisibility(View.GONE);
        if (artistsPager != null)
            searchCallBack.onSearchCallBack(artistsPager.artists.items, isArtistExist);
    }
}
