package com.aliudurim.spotifystreamerstage2.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliudurim.spotifystreamerstage2.R;
import com.aliudurim.spotifystreamerstage2.activitys.DialogPlayingActivity;
import com.aliudurim.spotifystreamerstage2.activitys.FragmentHolder;
import com.aliudurim.spotifystreamerstage2.fragments.DialogPlaying;
import com.aliudurim.spotifystreamerstage2.fragments.TracksFragment;
import com.aliudurim.spotifystreamerstage2.object.TopTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DurimAliu on 07/06/15.
 */
public class TracksArtistAdapter extends RecyclerView.Adapter<TracksArtistAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TopTrack> trackArrayList;
    private TracksFragment tracksFragment;
    private Toast toast;

    public static class ViewHolder extends RecyclerView.ViewHolder {


        @InjectView(R.id.txtTracksName)
        TextView txtTracksName;
        @InjectView(R.id.txtAlbumName)
        TextView txtAlbumName;
        @InjectView(R.id.ivForTracks)
        ImageView ivForTracks;
        @InjectView(R.id.llCellForTracks)
        LinearLayout llCellForTracks;


        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    public TracksArtistAdapter(Context context, TracksFragment tracksFragment) {
        this.context = context;
        this.tracksFragment = tracksFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_tracks_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.txtTracksName.setText("" + trackArrayList.get(position).name);
        holder.txtAlbumName.setText("" + trackArrayList.get(position).albumName);

        Picasso.with(context).load(trackArrayList.get(position).url).resize(dpToPx(60), dpToPx(60)).centerCrop().into(holder.ivForTracks);

        holder.llCellForTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable(context)) {

                    if (FragmentHolder.isTablet) {

                        showDialog(trackArrayList, position);

                    } else {

                        Intent intent = new Intent(context, DialogPlayingActivity.class);
                        intent.putParcelableArrayListExtra("trackArrayList", trackArrayList);
                        intent.putExtra("position", position);
                        context.startActivity(intent);
                    }

                } else {

                    showToast();

                }


            }
        });
    }

    public void setTrackArrayList(ArrayList<TopTrack> trackArrayList) {
        this.trackArrayList = trackArrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.trackArrayList.size();
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    void showDialog(ArrayList<TopTrack> trackArrayList, int position) {

        DialogPlaying newFragment = DialogPlaying.newInstance(trackArrayList, position);
        newFragment.setStyle(DialogPlaying.STYLE_NO_TITLE, 0);
        newFragment.show(tracksFragment.getFragmentManager(), null);

    }

    public boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showToast() {
        cancelToast();
        toast = Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void cancelToast() {

        if (toast != null) {
            toast.cancel();
        }
    }
}
