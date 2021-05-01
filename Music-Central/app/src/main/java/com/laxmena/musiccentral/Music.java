package com.laxmena.musiccentral;

public class Music {
    String title;
    String artistName;
    String musicUrl;
    int thumbnail;

    public Music(String title, String artistName, String musicUrl, int thumbnail) {
        this.title = title;
        this.artistName = artistName;
        this.musicUrl = musicUrl;
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getThumbnail() {
        return thumbnail;
    }
}
