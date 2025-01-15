//LoggedInMenuController.java
package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.util.List;
import java.util.Optional;

public class LoggedInMenuController {

  private MusicLibraryApplication musicLibraryApplication;

  public void setApplication(MusicLibraryApplication app) {
    System.out.println("Setting musicLibraryApplication in LoggedInMenuController");
    this.musicLibraryApplication = app;
  }

  private void hideAllLists() {
    songListView.setVisible(false);
    albumListView.setVisible(false);
  }

  @FXML
  private ListView<String> songListView; // Connect ListView from FXML

  @FXML
  private ListView<String> albumListView; // Connect ListView for albums from FXML

  @FXML
  private Label playingSongLabel; // Connect Label for displaying the playing song

  @FXML
  private void handleViewAllSongs(ActionEvent event) {
    System.out.println("View All Songs button clicked.");
    hideAllLists(); // Hide all ListViews

    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }
    // Fetch all songs from the InputDevice
    List<Song> songs = musicLibraryApplication.inputDevice.getAllSongs();
    if (songs == null || songs.isEmpty()) {
      songListView.setVisible(false); // Hide the list if no songs found
      System.out.println("No songs available.");
      return;
    }

    // Populate the ListView
    songListView.getItems().clear();
    for (Song song : songs) {
      songListView.getItems().add(song.getTitle() + " (" + song.getDuration() + " seconds)");
    }
    songListView.setVisible(true); // Show the list
  }

  @FXML
  private void handleViewAllAlbums(ActionEvent event) {
    System.out.println("View All Albums button clicked.");
    hideAllLists(); // Hide all ListViews

    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }

    List<Album> albums = musicLibraryApplication.inputDevice.getAllAlbums();
    if (albums == null || albums.isEmpty()) {
      albumListView.setVisible(false); // Hide the list if no albums found
      System.out.println("No albums available.");
      return;
    }
    // Populate the ListView
    albumListView.getItems().clear();
    for (Album album : albums) {
      albumListView.getItems().add(album.getTitle() + " by " + album.getArtist().getName());
    }
    albumListView.setVisible(true); // Show the list
  }

  @FXML
  private void handleListenToSong(ActionEvent event) {
    System.out.println("Listen to Song button clicked.");
    hideAllLists(); // Hide all ListViews

    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      playingSongLabel.setText("Application not initialized. Please restart.");
      return;
    }

    Dialog<Integer> dialog = new Dialog<>();
    dialog.setTitle("Listen to Song");
    dialog.setHeaderText("Enter the Song ID to listen:");

    Label label = new Label("Song ID:");
    TextField songIdField = new TextField();
    VBox content = new VBox(10, label, songIdField);
    dialog.getDialogPane().setContent(content);

    ButtonType buttonTypeOk = new ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == buttonTypeOk) {
        try {
          return Integer.parseInt(songIdField.getText());
        } catch (NumberFormatException e) {
          System.out.println("Invalid input. Please enter a valid number.");
          playingSongLabel.setText("Invalid input. Please enter a valid number.");
        }
      }
      return null;
    });

    Optional<Integer> result = dialog.showAndWait();
    result.ifPresent(songId -> {
      // Fetch the song from the database
      Song song = musicLibraryApplication.inputDevice.fetchSongById(songId, new OutputDevice());
      if (song != null) {
        playingSongLabel.setText("Playing song: " + song.getTitle());
        System.out.println("Playing song: " + song.getTitle());
      } else {
        playingSongLabel.setText("Song not found. Please try again.");
        System.out.println("Song not found.");
      }
    });
  }

  @FXML
  private void handleViewMyPlaylists(ActionEvent event) {
    System.out.println("View My Playlists button clicked.");
    hideAllLists(); // hide all other ListViews

    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }

    int userId = musicLibraryApplication.getUserId();
    OutputDevice outputDevice = new OutputDevice();
    List<Playlist> playlists = musicLibraryApplication.inputDevice.getPlaylists(userId, false,outputDevice);

    if (playlists == null || playlists.isEmpty()) {
      albumListView.setVisible(false); // Use albumListView for displaying playlists
      System.out.println("No playlists found.");
      return;
    }

    // Populate the ListView with playlists
    albumListView.getItems().clear();
    for (Playlist playlist : playlists) {
      albumListView.getItems().add("Playlist: " + playlist.getName() + " (Songs: " + playlist.getItems().size() + ")");
    }
    albumListView.setVisible(true);
    musicLibraryApplication.viewPlaylists(userId, false);
  }

  @FXML
  private void handleCreatePlaylist(ActionEvent event) {
    System.out.println("Create Playlist button clicked.");
    hideAllLists(); // Hide all other ListViews

    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }


    Dialog<List<Song>> dialog = new Dialog<>();
    dialog.setTitle("Create Playlist");
    dialog.setHeaderText("Enter the Playlist Name and Select Songs:");

    TextField playlistNameField = new TextField();
    playlistNameField.setPromptText("Playlist Name");

    ListView<Song> songListView = new ListView<>();
    songListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    songListView.setCellFactory(param -> new ListCell<>() {
      @Override
      protected void updateItem(Song song, boolean empty) {
        super.updateItem(song, empty);
        if (empty || song == null) {
          setText(null);
        } else {
          setText(song.getTitle());
        }
      }
    });

    VBox content = new VBox(10, playlistNameField, songListView);
    dialog.getDialogPane().setContent(content);

    ButtonType buttonTypeOk = new ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

    // Load available songs
    List<Song> availableSongs = musicLibraryApplication.inputDevice.getAllSongs();
    songListView.getItems().addAll(availableSongs);

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == buttonTypeOk) {
        String playlistName = playlistNameField.getText();
        List<Song> selectedSongs = songListView.getSelectionModel().getSelectedItems();
        return selectedSongs;
      }
      return null;
    });

    Optional<List<Song>> result = dialog.showAndWait();
    result.ifPresent(selectedSongs -> {
      String playlistName = playlistNameField.getText();
      int userId = musicLibraryApplication.getUserId(); // userId from Application
      musicLibraryApplication.createPlaylist(playlistName, userId, selectedSongs);
    });
  }

  @FXML
  private void handleLogout(ActionEvent event) {
    System.out.println("Logout button clicked.");
    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }
    musicLibraryApplication.logOut();
    Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
    musicLibraryApplication.showLoginMenu(currentStage);
  }

  @FXML
  private void handleExit(ActionEvent event) {
    System.out.println("Exit button clicked.");
    System.exit(0);
  }
}
