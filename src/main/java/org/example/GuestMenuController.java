//GuestMenuController.java
package org.example;

import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class GuestMenuController {
  private MusicLibraryApplication musicLibraryApplication;
  @FXML
  private ListView<String> songListView; // ListView for songs
  @FXML
  private ListView<String> albumListView; // ListView for albums

  public void setApplication(MusicLibraryApplication app) {
    System.out.println("Setting musicLibraryApplication in GuestMenuController");
    this.musicLibraryApplication = app;
  }

  @FXML
  private void handleBrowseSongsAndAlbums(ActionEvent event) {
    System.out.println("Browse Songs and Albums button clicked.");
    hideAllLists();
    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }

    //fetch the remaining songs
    List<Song> songs = musicLibraryApplication.inputDevice.getAllSongs();
    if (songs != null && !songs.isEmpty()) {
      songListView.getItems().clear();
      for (Song song : songs) {
        songListView.getItems().add(song.getTitle() + " (" + song.getDuration() + " seconds)");
      }
      songListView.setVisible(true);
    } else {
      System.out.println("No songs available.");
    }

    // fetch and display albums
    List<Album> albums = musicLibraryApplication.inputDevice.getAllAlbums();
    if (albums != null && !albums.isEmpty()) {
      albumListView.getItems().clear();
      for (Album album : albums) {
        albumListView.getItems().add(album.getTitle() + " by " + album.getArtist().getName());
      }
      albumListView.setVisible(true);
    } else {
      System.out.println("No albums available.");
    }
  }

  @FXML
  private void handleRegister(ActionEvent event) {
    System.out.println("Register button clicked.");
    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }

    // trigger registration process
    musicLibraryApplication.register();

    // Go back to the main menu
    Stage currentStage = (Stage) songListView.getScene().getWindow(); // Get current stage
    musicLibraryApplication.showLoginMenu(currentStage); // Show main login menu
  }

  @FXML
  private void handleExit(ActionEvent event) {
    System.out.println("Exit button clicked.");
    System.exit(0);
  }

  private void hideAllLists() {
    songListView.setVisible(false);
    albumListView.setVisible(false);
  }
}