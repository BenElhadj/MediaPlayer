package com.example.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Currency;

public class MainActivity extends AppCompatActivity {

    /** Variable global**/

    private static final int PERMISSION_READ = 0;

    MediaPlayer mediaPlayer;
    private static final String TAG = "Media Player";
    RecyclerView recyclerView;
    TextView tvSongTitle, tvCurrentPos, tvTotalDuration;
    SeekBar sbPosition;
    ImageView btnPrev, btnPlay, btnNext;
    ArrayList<ModelSong> songArrayList;
    double currentPos, totalDuration;
    int audioIndex;

    /** La Methode d'inisialisation des composants**/

    public void init(){
        recyclerView = findViewById(R.id.recycleView);
        tvSongTitle = findViewById(R.id.tvSongView);
        tvCurrentPos = findViewById(R.id.tvCurrentPos);
        tvTotalDuration = findViewById(R.id.tvTotalDuration);
        sbPosition = findViewById(R.id.sbPosition);
        btnPrev = findViewById(R.id.btnPrevius);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);

        // Init des element du code
        mediaPlayer = new MediaPlayer();
        songArrayList = new ArrayList<>();
        /** a regarder VERTICAL si besoin de la faire en Horizontal**/
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        audioIndex = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(checkPermission()){
            setSong();
        }
    }
    /** debut de la partie la plus importante pour les autorisation**/

    public boolean checkPermission() {
        int READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if ((READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getApplicationContext(), "Please allow storage permission", Toast.LENGTH_LONG).show();
                    } else {
                       setSong();
                    }
                }
            }
        }
    }

    /**
     * sans oublier cette ligne au debut de AndroidManifest.xml
     *
     *  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
     *
     * fin de la partie la plus importante pour les autorisation
     **/

    private void setSong() {
        init();
        getAudioFile();

        sbPosition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentPos = seekBar.getProgress();
                mediaPlayer.seekTo((int) currentPos);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                audioIndex++;
                if(audioIndex < (songArrayList.size())){
                    playSong(audioIndex);
                } else {
                    audioIndex = 0;
                    playSong(audioIndex);
                }
            }
        });

        if(!songArrayList.isEmpty()) {
            playSong(audioIndex);
            prvSong();
            nextSong();
            setPause();
        }
    }

    private void playSong(int pos) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, songArrayList.get(pos).getSongUri());
            mediaPlayer.prepare();
            mediaPlayer.start();

            btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_48);
            tvSongTitle.setText(songArrayList.get(pos).getSongTitle());

            audioIndex = pos;

        } catch (Exception e){
            e.printStackTrace();
        }
        setSongProgress();
    }

    public void setSongProgress(){
        currentPos = mediaPlayer.getCurrentPosition();
        totalDuration = mediaPlayer.getDuration();

        tvTotalDuration.setText(timerConvertion((long) totalDuration));
        tvCurrentPos.setText(timerConvertion((long) currentPos));

        sbPosition.setMax((int) totalDuration);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    currentPos = mediaPlayer.getCurrentPosition();
                    tvCurrentPos.setText(timerConvertion((long) currentPos));
                    sbPosition.setProgress((int) currentPos);
                    handler.postDelayed(this, 1000);
                } catch (IllegalStateException e){
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    public String timerConvertion(long value){
        String songDuration;
        int dur = (int) value;// La duree en millisecondes
        int hrs = dur / 3600000;
        int mns = (dur / 60000) % 60000;
        int scs = dur % 6000 / 1000;

        if (hrs > 0 ){
            songDuration = String.format("%02d:%02d:%02d",hrs,mns,scs);
        }
        else{
            songDuration = String.format("%02d:%02d",mns,scs);
        }
        return songDuration;
    }




    private void getAudioFile(){
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri,null,null,null,null);

        if(cursor != null && cursor.moveToFirst()){
            do{
                //Récupérer les informations de chaques chansons
                String title = cursor.getString((int)cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString((int)cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String duration = cursor.getString((int)cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String url = cursor.getString((int)cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                long albumId = cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));

                Uri coverFolder = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(coverFolder, albumId);

                ModelSong modelSong = new ModelSong();
                modelSong.setSongTitle(title);
                modelSong.setSongArtist(artist);
                modelSong.setSongDuration(duration);
                modelSong.setSongUri(Uri.parse(url));
                modelSong.setSongCover(albumArtUri);

                songArrayList.add(modelSong);

            } while (cursor.moveToNext());
        }
        AdapterSong adapterSong= new AdapterSong(this, songArrayList);
        recyclerView.setAdapter(adapterSong);

        adapterSong.setOnItemClickListener(new AdapterSong.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                playSong(pos);
            }
        });
    }

    public void prvSong(){
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audioIndex > 0){
                    audioIndex--;
                } else {
                    audioIndex = songArrayList.size() -1;

                }
                playSong(audioIndex);
            }
        });
    }
    public void nextSong(){
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audioIndex < (songArrayList.size() -1)){
                    audioIndex++;
                } else {
                    audioIndex = 0;

                }
                playSong(audioIndex);
            }
        });
    }

    public void setPause(){
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.ic_play_circle_filled_48_w);
                } else {
                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_48);
                }
            }
        });
    }

}