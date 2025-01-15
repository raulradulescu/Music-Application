//AdminMenuController.java
package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.util.List;
import java.util.Optional;

public class AdminMenuController {

  private MusicLibraryApplication musicLibraryApplication;

  public void setApplication(MusicLibraryApplication app) {
    System.out.println("Setting musicLibraryApplication in AdminMenuController");
    this.musicLibraryApplication = app;
  }

  @FXML
  private ListView<String> songListView;

  @FXML
  private void handleViewAllSongs(ActionEvent event) {
    System.out.println("View All Songs button clicked.");
    hideAllLists(); // Ensure no overlapping lists are shown

    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }

    // Fetch all songs
    List<Song> songs = musicLibraryApplication.inputDevice.getAllSongs();
    if (songs == null || songs.isEmpty()) {
      System.out.println("No songs found.");
      return;
    }

    // Display songs in the UI
    songListView.getItems().clear();
    for (Song song : songs) {
      songListView.getItems().add(song.getTitle() + " (" + song.getDuration() + " seconds)");
    }
    songListView.setVisible(true);
  }

  @FXML
  private ListView<String> albumListView;

  @FXML
  private void handleViewAllAlbums(ActionEvent event) {
    System.out.println("View All Albums button clicked.");
    hideAllLists(); // Hide other lists

    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }

    // Fetch all albums
    List<Album> albums = musicLibraryApplication.inputDevice.getAllAlbums();
    if (albums == null || albums.isEmpty()) {
      System.out.println("No albums found.");
      return;
    }

    // Display albums in the UI
    albumListView.getItems().clear();
    for (Album album : albums) {
      albumListView.getItems().add("Album: " + album.getTitle() + " by " + album.getArtist().getName());
    }
    albumListView.setVisible(true);
  }

  @FXML
  public void handleListenToSong(ActionEvent event) {
    System.out.println("Listen to Song button clicked.");
    hideAllLists(); // Hide any visible lists

    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }

    Dialog<Integer> dialog = new Dialog<>();
    dialog.setTitle("Listen to Song");
    dialog.setHeaderText("Enter the Song ID:");

    TextField songIdField = new TextField();
    songIdField.setPromptText("Song ID");

    VBox content = new VBox(10, songIdField);
    dialog.getDialogPane().setContent(content);

    ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().add(okButtonType);

    dialog.setResultConverter(button -> {
      if (button == okButtonType) {
        try {
          return Integer.parseInt(songIdField.getText());
        } catch (NumberFormatException e) {
          System.out.println("Invalid song ID. Please enter a valid number.");
        }
      }
      return null;
    });

    Optional<Integer> result = dialog.showAndWait();
    result.ifPresent(songId -> {
      // Fetch the song title before playing (update the logic in MusicLibraryApplication if needed)
      Song song = musicLibraryApplication.inputDevice.fetchSongById(songId, new OutputDevice());
      if (song != null) {
        String songName = song.getTitle();
        System.out.println("Playing song: " + songName);

        // Display the song being played in the UI
        songListView.getItems().clear();
        songListView.getItems().add("Playing song: " + songName);
        songListView.setVisible(true);

        // Play the song
        musicLibraryApplication.listenToSong(songId);
      } else {
        System.out.println("Song not found.");
      }
    });
  }

  @FXML
  public void handleViewAllPlaylists(ActionEvent event) {
    System.out.println("View All Playlists button clicked.");
    hideAllLists(); // Hide other lists

    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }

    // Fetch all playlists
    List<Playlist> playlists = musicLibraryApplication.inputDevice.getPlaylists(0, true, new OutputDevice());
    if (playlists == null || playlists.isEmpty()) {
      System.out.println("No playlists found.");
      return;
    }

    // Display playlists in the UI
    albumListView.getItems().clear();
    for (Playlist playlist : playlists) {
      albumListView.getItems().add("Playlist: " + playlist.getName() + " (Created by User ID: " + playlist.getCreatedByUserID() + ")");
    }
    albumListView.setVisible(true);
  }

  @FXML
  private void handleDeletePlaylist(ActionEvent event) {
    System.out.println("Delete Playlist button clicked.");
    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }
  }

  @FXML
  public void handleAddNewSong(ActionEvent event) {
    System.out.println("Add New Song button clicked.");
    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }

    Dialog<Void> dialog = new Dialog<>();
    dialog.setTitle("Add New Song");
    dialog.setHeaderText("Enter the Song Details:");

    TextField titleField = new TextField();
    titleField.setPromptText("Enter the song title");

    TextField durationField = new TextField();
    durationField.setPromptText("Duration (in seconds)");

    TextField albumIdField = new TextField();
    albumIdField.setPromptText("Album ID (0 for standalone song)");

    VBox content = new VBox(10, titleField, durationField, albumIdField);
    dialog.getDialogPane().setContent(content);

    ButtonType buttonTypeOk = new ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == buttonTypeOk) {
        try {
          // Collect song details
          String title = titleField.getText();
          int duration = Integer.parseInt(durationField.getText());
          int albumId = Integer.parseInt(albumIdField.getText());

          // Save the song using the application logic
          musicLibraryApplication.addNewSong(title, duration, albumId);

          // Confirm success
          System.out.println("Song '" + title + "' added successfully!");
        } catch (NumberFormatException e) {
          System.out.println("Invalid input. Please enter valid numbers for duration and album ID.");
        }
      }
      return null;
    });

    dialog.showAndWait();
  }

  @FXML
  public void handleAddNewAlbum(ActionEvent event) {
    System.out.println("Add New Album button clicked.");
    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }

    Dialog<Void> dialog = new Dialog<>();
    dialog.setTitle("Add New Album");
    dialog.setHeaderText("Enter the Album Details:");

    TextField albumTitleField = new TextField();
    albumTitleField.setPromptText("Album Title");

    TextField artistNameField = new TextField();
    artistNameField.setPromptText("Artist Name");

    TextField producerNameField = new TextField();
    producerNameField.setPromptText("Producer Name (leave blank to be the same as the artist)");

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

    // Load available songs into the ListView
    List<Song> availableSongs = musicLibraryApplication.inputDevice.getAllSongs();
    songListView.getItems().addAll(availableSongs);

    VBox content = new VBox(10, albumTitleField, artistNameField, producerNameField, songListView);
    dialog.getDialogPane().setContent(content);

    ButtonType buttonTypeOk = new ButtonType("OK", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == buttonTypeOk) {
        try {
          //album
          String albumTitle = albumTitleField.getText();
          String artistName = artistNameField.getText();
          String producerName = producerNameField.getText().isEmpty() ? artistName : producerNameField.getText();
          //artist and prod
          Artist artist = new Artist(artistName);
          Producer producer = new Producer(producerName);

          //songs
          List<Song> selectedSongs = songListView.getSelectionModel().getSelectedItems();

          //save album
          musicLibraryApplication.addNewAlbum(albumTitle, artist, producer, selectedSongs);

          //confirm?
          System.out.println("Album '" + albumTitle + "' by " + artistName + " added successfully!");
        } catch (Exception e) {
          System.out.println("Error adding album: " + e.getMessage());
        }
      }
      return null;
    });

    dialog.showAndWait();
  }

  @FXML
  public void handleLogout(ActionEvent event) {
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

  private void hideAllLists() {
    songListView.setVisible(false);
    albumListView.setVisible(false);
  }
}
