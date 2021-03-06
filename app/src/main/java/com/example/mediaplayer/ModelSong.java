package com.example.mediaplayer;

import android.net.Uri;

public class ModelSong {

    String songTitle, songArtist, songDuration;
    Uri songUri;
    Uri songCover;

    public Uri getSongCover() { return songCover; }

    public void setSongCover(Uri songCover) { this.songCover = songCover; }

    public String getSongTitle() { return songTitle; }

    public void setSongTitle(String songTitle) { this.songTitle = songTitle; }

    public String getSongArtist() { return songArtist; }

    public void setSongArtist(String songArtist) { this.songArtist = songArtist; }

    public String getSongDuration() { return songDuration; }

    public void setSongDuration(String songDuration) { this.songDuration = songDuration; }

    public Uri getSongUri() { return songUri; }

    public void setSongUri(Uri songUri) { this.songUri = songUri; }
}
