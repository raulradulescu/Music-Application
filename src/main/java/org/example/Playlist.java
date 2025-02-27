//Playlist.java (Class E)
package org.example;

import java.util.ArrayList;
import java.util.List;

public class Playlist implements Playable {
  //private int id; //this field for playlist ID
  private String name;
  private int createdByUserID;
  private List<Playable> items;
  private final OutputDevice outputDevice;

  public Playlist(String name, int createdByUserID, OutputDevice outputDevice) {
    this.name = name;
    this.createdByUserID = createdByUserID;
    this.items = new ArrayList<>();
    this.outputDevice = outputDevice;
  }

  public void setName(String name) {
    this.name = name;
  }
  public String getName() {
    return name;
  }

  public void setCreatedByUserID(int createdByUserID) {
    this.createdByUserID = createdByUserID;
  }
  public int getCreatedByUserID() {
    return createdByUserID;
  }
/*
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
*/
  public String playlistDescription() {
    return "Playlist ID: "+/* this.getId() +*/ " with name " + this.getName() + "was created by user " + getCreatedByUserID() + " .";
  }

  public void addItem(Playable item) {
    items.add(item);
  }
  public void removeItem(Playable item) {
    items.remove(item);
  }

  public List<Playable> getItems() {
    return items;
  }
  @Override
  public void play() {
    outputDevice.writeMessage("Playing playlist: " + name);
    for (Playable item : items) {
      item.play();
    }
  }

  @Override
  public int getDuration() {
    int totalDuration = 0;
    for (Playable item : items) {
      totalDuration += item.getDuration();
    }
    return totalDuration;
  }
}
