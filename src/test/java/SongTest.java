package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SongTest {

  @Test
  void testSongConstructor() {
    OutputDevice outputDevice = new OutputDevice();
    Song song = new Song("Test Song", 200, outputDevice);

    assertEquals("Test Song", song.getTitle());
    assertEquals(200, song.getDuration());
  }

  @Test
  void testPlayMethod() {
    OutputDevice outputDevice = new OutputDevice();
    Song song = new Song("Test Song", 200, outputDevice);

    assertDoesNotThrow(song::play);
  }

  @Test
  void testInvalidSongTitle() {
    OutputDevice outputDevice = new OutputDevice();

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new Song("", 120, outputDevice));

    assertEquals("Title cannot be null or empty.", exception.getMessage());
  }

  @Test
  void testInvalidDuration() {
    OutputDevice outputDevice = new OutputDevice();

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new Song("Test Song", 0, outputDevice));

    assertEquals("Duration must be greater than zero.", exception.getMessage());
  }
}
