package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class PlaylistTest {

  @Test
  void testPlaylistConstructor() {
    OutputDevice outputDevice = new OutputDevice();
    Playlist playlist = new Playlist("My Playlist", 1, outputDevice);

    assertEquals("My Playlist", playlist.getName());
    assertEquals(1, playlist.getCreatedByUserID());
    assertNotNull(playlist.getItems());
    assertTrue(playlist.getItems().isEmpty());
  }

  @Test
  void testAddItem() {
    OutputDevice outputDevice = new OutputDevice();
    Playlist playlist = new Playlist("My Playlist", 1, outputDevice);
    Song song = new Song("Song 1", 120, outputDevice);

    playlist.addItem(song);

    List<Playable> items = playlist.getItems();
    assertEquals(1, items.size());
    assertEquals(song, items.get(0));
  }

  @Test
  void testRemoveItem() {
    OutputDevice outputDevice = new OutputDevice();
    Playlist playlist = new Playlist("My Playlist", 1, outputDevice);
    Song song = new Song("Song 1", 120, outputDevice);

    playlist.addItem(song);
    playlist.removeItem(song);

    assertTrue(playlist.getItems().isEmpty());
  }

  @Test
  void testGetDuration() {
    OutputDevice outputDevice = new OutputDevice();
    Playlist playlist = new Playlist("My Playlist", 1, outputDevice);
    playlist.addItem(new Song("Song 1", 120, outputDevice));
    playlist.addItem(new Song("Song 2", 150, outputDevice));

    assertEquals(270, playlist.getDuration());
  }
}
