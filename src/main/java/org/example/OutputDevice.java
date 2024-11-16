//OutputDevice.java
package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OutputDevice {
  public void writeMessage(String mess) {
    System.out.println(mess);
  }

  public void savePerson(Person person, String tableName) {
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "INSERT INTO " + tableName + " (name) VALUES (?)";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, person.getName());
      stmt.executeUpdate();
      writeMessage(tableName + " saved: " + person.getName());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public int savePlaylist(Playlist playlist) {
    try (Connection connection = DatabaseUtil.getConnection()) {
      // Insert playlist
      String playlistQuery = "INSERT INTO Playlist (name, created_by_user_id) VALUES (?, ?)";
      PreparedStatement playlistStmt = connection.prepareStatement(playlistQuery, PreparedStatement.RETURN_GENERATED_KEYS);
      playlistStmt.setString(1, playlist.getName());
      playlistStmt.setInt(2, playlist.getCreatedByUserID());
      playlistStmt.executeUpdate();

      // Get generated playlist ID
      ResultSet rs = playlistStmt.getGeneratedKeys();
      int playlistId = -1;
      if (rs.next()) {
        playlistId = rs.getInt(1);
      }

      // Insert playlist-song associations
      String playlistSongQuery = "INSERT INTO Playlist_Song (playlist_id, song_id) VALUES (?, ?)";
      PreparedStatement playlistSongStmt = connection.prepareStatement(playlistSongQuery);

      for (Playable item : playlist.getItems()) {
        if (item instanceof Song) {
          Song song = (Song) item;
          int songId = getSongId(song.getTitle());
          if (songId != -1) {
            playlistSongStmt.setInt(1, playlistId);
            playlistSongStmt.setInt(2, songId);
            playlistSongStmt.addBatch();
          }
        }
      }
      playlistSongStmt.executeBatch();

      writeMessage("Playlist '" + playlist.getName() + "' saved with ID: " + playlistId);
      return playlistId;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1; // Return -1 in case of error
  }

  public int getSongId(String title) {
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "SELECT id FROM Song WHERE title = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, title);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt("id");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;  // Return -1 if song not found
  }

  public void saveSong(Song song, Integer albumId) {
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "INSERT INTO Song (title, duration, album_id) VALUES (?, ?, ?)";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, song.getTitle());
      stmt.setInt(2, song.getDuration());

      if (albumId == 0) {
        stmt.setNull(3, java.sql.Types.INTEGER); // Album ID is null for standalone singles
        System.out.println("Setting album_id to NULL"); // Debugging output
      } else {
        stmt.setInt(3, albumId);
        System.out.println("Setting album_id to " + albumId); // Debugging output
      }

      stmt.executeUpdate();
      writeMessage("Song saved: " + song.getTitle());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void saveAlbum(Album album) {
    try (Connection connection = DatabaseUtil.getConnection()) {
      // Save album information
      String albumQuery = "INSERT INTO Album (title, artist_id, producer_id) VALUES (?, " +
              "(SELECT id FROM Artist WHERE name = ?), " +
              "(SELECT id FROM Producer WHERE name = ?))";
      PreparedStatement albumStmt = connection.prepareStatement(albumQuery, PreparedStatement.RETURN_GENERATED_KEYS);
      albumStmt.setString(1, album.getTitle());
      albumStmt.setString(2, album.getArtist().getName());
      albumStmt.setString(3, album.getProducer().getName());
      albumStmt.executeUpdate();

      // Retrieve generated album ID
      ResultSet rs = albumStmt.getGeneratedKeys();
      int albumId = -1;
      if (rs.next()) {
        albumId = rs.getInt(1);
      }

      // Save songs in the album
      for (Song song : album.getSongs()) {
        saveSong(song, albumId); // Pass the correct album ID
      }

      writeMessage("Album '" + album.getTitle() + "' saved with ID: " + albumId);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public int getAlbumId(String title) {
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "SELECT id FROM Album WHERE title = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, title);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt("id");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;  // Return -1 if song not found
  }
}