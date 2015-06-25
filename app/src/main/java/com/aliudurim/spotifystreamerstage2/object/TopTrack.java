package com.aliudurim.spotifystreamerstage2.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DurimAliu on 13/06/15.
 */
public class TopTrack implements Parcelable {


    public String artistName, name, albumName, url, trackPreview;

    public TopTrack(String name, String albumName, String url, String artistName, String trackPreview) {
        this.name = name;
        this.albumName = albumName;
        this.url = url;
        this.artistName = artistName;
        this.trackPreview = trackPreview;
    }

    private TopTrack(Parcel in) {
        name = in.readString();
        albumName = in.readString();
        url = in.readString();
        artistName = in.readString();
        trackPreview = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(albumName);
        dest.writeString(url);
        dest.writeString(artistName);
        dest.writeString(trackPreview);
    }

    public static final Creator<TopTrack> CREATOR = new Creator<TopTrack>() {
        public TopTrack createFromParcel(Parcel in) {
            return new TopTrack(in);
        }

        public TopTrack[] newArray(int size) {
            return new TopTrack[size];
        }
    };
}
