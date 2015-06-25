package com.aliudurim.spotifystreamerstage2.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aliudurim.spotifystreamerstage2.R;
import com.aliudurim.spotifystreamerstage2.object.TopTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by DurimAliu on 22/06/15.
 */
public class DialogPlaying extends DialogFragment implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, View.OnTouchListener {

    public ArrayList<TopTrack> trackArrayList;
    public int position = 0;
    public MediaPlayer mediaPlayer;
    int mediaFileLengthInMilliseconds;
    Handler handler;
    Toast toast;

    @InjectView(R.id.txtArtistDialogName)
    TextView txtArtistDialogName;
    @InjectView(R.id.txtAlbumDialogName)
    TextView txtAlbumDialogName;
    @InjectView(R.id.txtTrackDialogName)
    TextView txtTrackDialogName;
    @InjectView(R.id.txtTrackDurationStart)
    TextView txtTrackDurationStart;
    @InjectView(R.id.txtTrackDurationEnd)
    TextView txtTrackDurationEnd;

    @InjectView(R.id.imgArtWork)
    ImageView imgArtWork;

    @InjectView(R.id.seekBar)
    SeekBar seekBar;

    @InjectView(R.id.imgBtnPrevious)
    ImageButton imgBtnPrevious;
    @InjectView(R.id.imgBtnPlayPause)
    ImageButton imgBtnPlayPause;
    @InjectView(R.id.imgBtnNext)
    ImageButton imgBtnNext;

    public static DialogPlaying newInstance(ArrayList<TopTrack> trackArrayList, int position) {

        DialogPlaying myFragment = new DialogPlaying();

        Bundle args = new Bundle();
        args.putParcelableArrayList("trackArrayList", trackArrayList);
        args.putInt("position", position);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View gv = inflater.inflate(R.layout.dialog_fragment, container, false);
        ButterKnife.inject(this, gv);

        handler = new Handler();

        position = getArguments().getInt("position");
        trackArrayList = getArguments().getParcelableArrayList("trackArrayList");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);

        fillAllFields(position, false);
        seekBar.setOnTouchListener(this);

        return gv;
    }

    public void fillAllFields(final int pos, boolean isPlay) {

        if (!isPlay) {


            imgBtnPlayPause.setImageResource(android.R.drawable.ic_media_pause);

            txtArtistDialogName.setText(trackArrayList.get(pos).artistName);
            txtAlbumDialogName.setText(trackArrayList.get(pos).albumName);
            txtTrackDialogName.setText(trackArrayList.get(pos).name);

            Picasso.with(getActivity()).load(trackArrayList.get(pos).url).resize(400, 400).centerCrop().into(imgArtWork);


            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    try {

                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(trackArrayList.get(pos).trackPreview);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
                        seekBarProgressUpdater();

                    } catch (Exception ex) {

                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {

                    txtTrackDurationEnd.setText(getStringFormat(mediaFileLengthInMilliseconds));
                    txtTrackDurationStart.setText("0:00");

                    super.onPostExecute(aVoid);
                }
            }.execute();


        } else {

            if (mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
                imgBtnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                seekBarProgressUpdater();

            } else {

                mediaPlayer.start();
                imgBtnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                seekBarProgressUpdater();
            }
        }


    }

    private void seekBarProgressUpdater() {

        if (mediaPlayer != null) {
            seekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100));

            if (mediaPlayer.isPlaying()) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null) {
                            seekBarProgressUpdater();
                            txtTrackDurationStart.setText(getStringFormat(mediaPlayer.getCurrentPosition()));
                        }
                    }
                }, 100);
            }
        }
    }

    @OnClick(R.id.imgBtnPrevious)
    public void setOnClickListenerPrevious() {

        position--;
        position = (position < 0) ? trackArrayList.size() - 1 : position;
        fillAllFields(position, false);


        if (!isNetworkAvailable(getActivity())) {
            showToast();
        }
    }

    @OnClick(R.id.imgBtnPlayPause)
    public void setOnClickListenerPlayPause() {
        fillAllFields(position, true);
    }

    @OnClick(R.id.imgBtnNext)
    public void setOnClickListenerNext() {

        position++;
        position = (position == trackArrayList.size()) ? 0 : position;
        fillAllFields(position, false);


        if (!isNetworkAvailable(getActivity())) {
            showToast();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setOnClickListenerNext();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mediaPlayer.isPlaying()) {
            SeekBar sb = (SeekBar) v;
            int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
            mediaPlayer.seekTo(playPositionInMillisecconds);
        }
        return false;
    }

    private String getStringFormat(int milisecond) {
        return String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes(milisecond) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milisecond)),
                TimeUnit.MILLISECONDS.toSeconds(milisecond) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisecond)));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception ex) {

        }
    }

    public boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
}