package com.aliudurim.spotifystreamerstage2.callbacks;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by DurimAliu on 07/06/15.
 */
public interface SearchCallBack {
    void onSearchCallBack(List<Artist> artistArrayList, boolean isArtistExist);
}
