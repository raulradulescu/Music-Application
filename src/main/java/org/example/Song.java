//Song.java (class C)
package org.example;

public class Song implements Playable, Comparable<Song> {
  private String title;
  private int duration;
  private OutputDevice outputDevice;


  public Song( String title, int duration, OutputDevice outputDevice) {
    if (title == null || title.isEmpty()) {
      throw new IllegalArgumentException("Title cannot be null or empty.");
    }
    if (duration <= 0) {
      throw new IllegalArgumentException("Duration must be greater than zero.");
    }
    if (outputDevice == null) {
      throw new NullPointerException("Output device cannot be null.");
    }
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
