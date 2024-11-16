//InputDevice.java
package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InputDevice {
  // Below we have methods that communicate with the MySQL db to fetch data from there
  public Song fetchSongById(int id, OutputDevice outputDevice) {
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "SELECT title, duration FROM Song WHERE id = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setInt(1, id);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        String title = rs.getString("title");
        int duration = rs.getInt("duration");
        return new Song(title, duration, outputDevice);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public <T extends Person> T fetchPersonById(int id, String tableName, Class<T> clazz) {
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "SELECT name FROM " + tableName + " WHERE id = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setInt(1, id);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        String name = rs.getString("name");
        return clazz.getDeclaredConstructor(String.class).newInstance(name);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public Album fetchAlbumById(int id, OutputDevice outputDevice) {
    try (Connection connection = DatabaseUtil.getConnection()) {
      String albumQuery = "SELECT title, artist_id, producer_id FROM Album WHERE id = ?";
      PreparedStatement albumStmt = connection.prepareStatement(albumQuery);
      albumStmt.setInt(1, id);

      ResultSet albumRs = albumStmt.executeQuery();
      if (albumRs.next()) {
        String title = albumRs.getString("title");
        int artistId = albumRs.getInt("artist_id");
        int producerId = albumRs.getInt("producer_id");

        Artist artist = fetchPersonById(artistId, "Artist", Artist.class);
        Producer producer = fetchPersonById(producerId, "Producer", Producer.class);
        List<Song> songs = fetchSongsByAlbumId(id, outputDevice);
        Song[] songArray = songs.toArray(new Song[0]);

        return new Album(title, artist, producer, songArray, outputDevice);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  private List<Song> fetchSongsByAlbumId(int albumId, OutputDevice outputDevice) {
    List<Song> songs = new ArrayList<>();
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "SELECT id, title, duration FROM Song WHERE album_id = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setInt(1, albumId);

      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        int duration = rs.getInt("duration");
        songs.add(new Song(title, duration, outputDevice));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return songs;
  }

  public List<Song> getAllSongs() {
    List<Song> songs = new ArrayList<>();
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "SELECT id, title, duration FROM Song";
      PreparedStatement stmt = connection.prepareStatement(query);

      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        int duration = rs.getInt("duration");
        songs.add(new Song(title, duration, new OutputDevice())); // Creates a new Song with title and duration
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return songs;
  }

  public List<Album> getAllAlbums() {
    List<Album> albums = new ArrayList<>();
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "SELECT id, title, artist_id, producer_id FROM Album";
      PreparedStatement stmt = connection.prepareStatement(query);

      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        int albumId = rs.getInt("id");
        String title = rs.getString("title");
        int artistId = rs.getInt("artist_id");
        int producerId = rs.getInt("producer_id");

        Artist artist = fetchPersonById(artistId, "Artist", Artist.class);
        Producer producer = fetchPersonById(producerId, "Producer", Producer.class);

        List<Song> songs = fetchSongsByAlbumId(albumId, new OutputDevice());
        Album album = new Album(title, artist, producer, songs.toArray(new Song[0]), new OutputDevice());

        albums.add(album);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return albums;
  }

  public List<Playlist> getPlaylists(int userId, boolean isAdmin, OutputDevice outputDevice) {
    List<Playlist> playlists = new ArrayList<>();
    String query;

    if (isAdmin) {
      query = "SELECT id, name, created_by_user_id FROM Playlist";
    } else {
      query = "SELECT id, name FROM Playlist WHERE created_by_user_id = ?";
    }

    try (Connection connection = DatabaseUtil.getConnection()) {
      PreparedStatement stmt = connection.prepareStatement(query);

      if (!isAdmin) {
        stmt.setInt(1, userId);
      }

      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        int playlistId = rs.getInt("id");
        String playlistName = rs.getString("name");

        int playlistCreator = isAdmin ? rs.getInt("created_by_user_id") : userId;

        Playlist playlist = new Playlist(playlistName, playlistCreator, outputDevice);

        List<Song> songs = fetchSongsByPlaylistId(playlistId, outputDevice);
        songs.forEach(playlist::addItem);

        playlists.add(playlist);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return playlists;
  }

  private List<Song> fetchSongsByPlaylistId(int playlistId, OutputDevice outputDevice) {
    List<Song> songs = new ArrayList<>();
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "SELECT s.id, s.title, s.duration FROM Song s JOIN Playlist_Song ps ON s.id = ps.song_id WHERE ps.playlist_id = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setInt(1, playlistId);

      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        int duration = rs.getInt("duration");
        songs.add(new Song(title, duration, outputDevice));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return songs;
  }

  public static String getLine() {
    return "The quick brown fox jumps over the lazy dog.";
  }
}