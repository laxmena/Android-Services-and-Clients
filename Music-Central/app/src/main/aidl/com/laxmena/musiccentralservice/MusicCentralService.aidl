package com.laxmena.musiccentralservice;

interface MusicCentralService {
    Bundle getMusicList();
    Bundle getSongInfo(int songId);
    String getSongUrl(int songId);
    Bundle getSongThumbnail(int songId);
}