//Song.java (class C)
package org.example;

public class Song implements Playable, Comparable<Song> {
  private String title;
  private int duration;
  private OutputDevice outputDevice;


  public Song( String title, int duration, OutputDevice outputDevice) {
    this.title = title;
    this.duration = duration;
    this.outputDevice = outputDevice;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  @Override
  public void play() {
    outputDevice.writeMessage("Playing song: " + title);
  }

  @Override
  public int compareTo(Song o) {
    return this.title.compareTo(o.title);
  }
}
