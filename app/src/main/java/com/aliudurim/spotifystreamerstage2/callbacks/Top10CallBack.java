package com.aliudurim.spotifystreamerstage2.callbacks;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by DurimAliu on 07/06/15.
 */
public interface Top10CallBack {
    void onTop10CallBack(List<Track> tracksList);
}
