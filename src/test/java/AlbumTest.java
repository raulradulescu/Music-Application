package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {

  @Test
  void testAlbumConstructor() {
    Artist artist = new Artist("Artist Name");
    Producer producer = new Producer("Producer Name");
    Song[] songs = {};
    OutputDevice outputDevice = new OutputDevice();

    Album album = new Album("Album Title", artist, producer, songs, outputDevice);

    assertEquals("Album Title", album.getTitle());
    assertEquals(artist, album.getArtist());
    assertEquals(producer, album.getProducer());
  }

  @Test
  void testGetDuration() {
    Artist artist = new Artist("Artist Name");
    Producer producer = new Producer("Producer Name");
    Song[] songs = {
            new Song("Song 1", 120, new OutputDevice()),
            new Song("Song 2", 180, new OutputDevice())
    };
    OutputDevice outputDevice = new OutputDevice();

    Album album = new Album("Album Title", artist, producer, songs, outputDevice);

    assertEquals(300, album.getDuration());
  }

  @Test
  void testPlayMethod() {
    Artist artist = new Artist("Artist Name");
    Producer producer = new Producer("Producer Name");
    Song[] songs = {new Song("Song 1", 120, new OutputDevice())};
    OutputDevice outputDevice = new OutputDevice();

    Album album = new Album("Album Title", artist, producer, songs, outputDevice);

    assertDoesNotThrow(album::play);
  }

  @Test
  void testInvalidArtist() {
    Producer producer = new Producer("Producer Name");
    Song[] songs = {};
    OutputDevice outputDevice = new OutputDevice();

    NullPointerException exception = assertThrows(NullPointerException.class, () ->
            new Album("Album Title", null, producer, songs, outputDevice));

    assertEquals("Artist cannot be null.", exception.getMessage());
  }
}
