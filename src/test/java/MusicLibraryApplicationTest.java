package org.example;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MusicLibraryApplicationTest {

  @Test
  void testCreatePlaylist() {
    // Arrange: Create necessary dependencies
    OutputDevice outputDevice = new OutputDevice();
    InputDevice inputDevice = new InputDevice();

    // Prepare the playlist data
    String playlistName = "Test Playlist";
    int userId = 1; // Simulate a logged-in user with ID 1
    Playlist playlist = new Playlist(playlistName, userId, outputDevice);

    // Act: Save the playlist using the OutputDevice
    int savedPlaylist = outputDevice.savePlaylist(playlist);
    boolean saveSuccess;
    saveSuccess = savedPlaylist > 0;

    // Retrieve playlists from the InputDevice to verify the save
    List<Playlist> retrievedPlaylists = inputDevice.getPlaylists(userId, false, outputDevice);

    // Assert: Check that the playlist was saved successfully
    assertTrue(saveSuccess, "The savePlaylist method should return true.");
    assertNotNull(retrievedPlaylists, "Retrieved playlists list should not be null.");
    assertFalse(retrievedPlaylists.isEmpty(), "Retrieved playlists list should not be empty.");
    assertTrue(
            retrievedPlaylists.stream().anyMatch(p -> p.getName().equals(playlistName)),
            "The playlist should exist in the retrieved playlists list."
    );
  }

  @Test
  void testListenToSong() {
    OutputDevice outputDevice = new OutputDevice();
    InputDevice inputDevice = new InputDevice();

    // Create and save a song
    Song song = new Song("Test Song", 300, outputDevice);
    outputDevice.saveSong(song, 0); // 0 indicates it's a standalone song

    // Fetch the song by ID and play it
    int songId = inputDevice.getSongId("Test Song");
    Song fetchedSong = inputDevice.fetchSongById(songId, outputDevice);

    // Assert the song details are correct
    assertNotNull(fetchedSong);
    assertEquals("Test Song", fetchedSong.getTitle());
    assertEquals(300, fetchedSong.getDuration());

    // Simulate playing the song
    fetchedSong.play();

    // No assertion here, but you can ensure the outputDevice correctly "played" the song
    // (if your OutputDevice has a way to check what was written).
  }

  @Test
  void testDatabaseConnectionFunctionality() {
    System.out.println("Testing Database Connection...");

    try (Connection connection = DatabaseUtil.getConnection()) {
      assertNotNull(connection, "Database connection should not be null.");
      assertFalse(connection.isClosed(), "Database connection should be open.");
      System.out.println("Database connection established successfully.");
    } catch (Exception e) {
      fail("Failed to connect to the database. Error: " + e.getMessage());
    }
  }

  }
