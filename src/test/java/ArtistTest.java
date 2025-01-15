package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {


  @Test
  void testArtistConstructor() {
    Artist artist = new Artist("Artist Name");

    assertEquals("Artist Name", artist.getName());
  }

  @Test
  void testSetName() {
    Artist artist = new Artist("Old Name");
    artist.setName("New Name");

    assertEquals("New Name", artist.getName());
  }
}
