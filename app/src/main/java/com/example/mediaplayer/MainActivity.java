package com.example.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(checkPermission()){
            setSong();
        }
    }

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

    private void setSong() {
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

                ModelSong modelSong = new ModelSong();
                modelSong.setSongTitle(title);
                modelSong.setSongArtist(artist);
                modelSong.setSongDuration(duration);
                modelSong.setSongUri(Uri.parse(url));

                songArrayList.add(modelSong);

            } while (cursor.moveToNext());
        }
    }
}