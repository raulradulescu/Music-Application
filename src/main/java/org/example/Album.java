//Album.java (class B)
package org.example;

public class Album implements Playable{
  private String title;
  private final Artist artist;
  private final Producer producer;
  private final Song[] songArray;
  private final OutputDevice outputDevice;

  public Album(String tit, Artist art, Producer prod, Song[] songArr, OutputDevice outputDevice) {
    if (tit == null || tit.isEmpty()) {
      throw new IllegalArgumentException("Title cannot be null or empty.");
    }
    if (art == null) {
      throw new NullPointerException("Artist cannot be null.");
    }
    if (prod == null) {
      throw new NullPointerException("Producer cannot be null.");
    }
    if (songArr == null) {
      throw new NullPointerException("Song array cannot be null.");
    }
    this.title = tit;
    this.artist = art;
    this.producer = prod;
    this.songArray = songArr;
    this.outputDevice = outputDevice;
  }

  public void setTitle(String name) {
    this.title = name;
  }

  public String getTitle() {
    return title;
  }

  public Artist getArtist() {
    return artist;
  }

  public Producer getProducer() {
    return producer;
  }

  public Song[] getSongs() {
    return songArray;
  }

  @Override
  public void play() {
    outputDevice.writeMessage("Playing album: " + title);
    for (Song song : songArray) {
      song.play();
    }
  }

  @Override
  public int getDuration() {
    int totalDuration = 0;
    for (Song song : songArray) {
      totalDuration += song.getDuration();
    }
    return totalDuration;
  }
}
