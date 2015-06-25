package com.aliudurim.spotifystreamerstage2.activitys;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
 * Created by DurimAliu on 23/06/15.
 */
public class DialogPlayingActivity extends ActionBarActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, View.OnTouchListener {

    public ArrayList<TopTrack> trackArrayList;
    public int position = 0;
    public MediaPlayer mediaPlayer;
    int mediaFileLengthInMilliseconds;
    Handler handler;
    Toast toast;

    @InjectView(R.id.txtArtistDialogNameActivity)
    TextView txtArtistDialogNameActivity;
    @InjectView(R.id.txtAlbumDialogNameActivity)
    TextView txtAlbumDialogNameActivity;
    @InjectView(R.id.txtTrackDialogNameActivity)
    TextView txtTrackDialogNameActivity;
    @InjectView(R.id.txtTrackDurationStartActivity)
    TextView txtTrackDurationStartActivity;
    @InjectView(R.id.txtTrackDurationEndActivity)
    TextView txtTrackDurationEndActivity;

    @InjectView(R.id.imgArtWorkActivity)
    ImageView imgArtWorkActivity;

    @InjectView(R.id.seekBarActivity)
    SeekBar seekBarActivity;

    @InjectView(R.id.imgBtnPreviousActivity)
    ImageButton imgBtnPreviousActivity;
    @InjectView(R.id.imgBtnPlayPauseActivity)
    ImageButton imgBtnPlayPauseActivity;
    @InjectView(R.id.imgBtnNextActivity)
    ImageButton imgBtnNextActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity);
        ButterKnife.inject(this);
        handler = new Handler();

        Intent intent = getIntent();
        trackArrayList = intent.getParcelableArrayListExtra("trackArrayList");
        position = intent.getIntExtra("position", 0);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(trackArrayList.get(position).artistName);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(DialogPlayingActivity.this);
        mediaPlayer.setOnCompletionListener(DialogPlayingActivity.this);
        seekBarActivity.setOnTouchListener(DialogPlayingActivity.this);

        fillAllFields(position, false);
    }

    public void fillAllFields(final int pos, boolean isPlay) {

        if (!isPlay) {

            imgBtnPlayPauseActivity.setImageResource(android.R.drawable.ic_media_pause);

            txtArtistDialogNameActivity.setText(trackArrayList.get(pos).artistName);
            txtAlbumDialogNameActivity.setText(trackArrayList.get(pos).albumName);
            txtTrackDialogNameActivity.setText(trackArrayList.get(pos).name);

            Picasso.with(getApplicationContext()).load(trackArrayList.get(pos).url).resize(400, 400).centerCrop().into(imgArtWorkActivity);


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

                    txtTrackDurationEndActivity.setText(getStringFormat(mediaFileLengthInMilliseconds));
                    txtTrackDurationStartActivity.setText("0:00");

                    super.onPostExecute(aVoid);
                }
            }.execute();


        } else {

            if (mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
                imgBtnPlayPauseActivity.setImageResource(android.R.drawable.ic_media_play);
                seekBarProgressUpdater();

            } else {

                mediaPlayer.start();
                imgBtnPlayPauseActivity.setImageResource(android.R.drawable.ic_media_pause);
                seekBarProgressUpdater();
            }
        }


    }

    private void seekBarProgressUpdater() {

        if (mediaPlayer != null) {

            seekBarActivity.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100));

            if (mediaPlayer.isPlaying()) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (mediaPlayer != null) {
                            seekBarProgressUpdater();
                            txtTrackDurationStartActivity.setText(getStringFormat(mediaPlayer.getCurrentPosition()));
                        }

                    }
                }, 100);
            }
        }
    }

    @OnClick(R.id.imgBtnPreviousActivity)
    public void setOnClickListenerPrevious() {

        position--;
        position = (position < 0) ? trackArrayList.size() - 1 : position;
        fillAllFields(position, false);

        if (!isNetworkAvailable(getApplicationContext())) {
            showToast();
        }

    }

    @OnClick(R.id.imgBtnPlayPauseActivity)
    public void setOnClickListenerPlayPause() {
        fillAllFields(position, true);
    }

    @OnClick(R.id.imgBtnNextActivity)
    public void setOnClickListenerNext() {

        position++;
        position = (position == trackArrayList.size()) ? 0 : position;
        fillAllFields(position, false);

        if (!isNetworkAvailable(getApplicationContext())) {
            showToast();
        }

    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBarActivity.setSecondaryProgress(percent);
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

    @Override
    protected void onPause() {
        super.onPause();
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
        toast = Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void cancelToast() {

        if (toast != null) {
            toast.cancel();
        }
    }
}
