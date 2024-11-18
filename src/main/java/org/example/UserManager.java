package org.example;

import java.sql.*;

public class UserManager {

  public int authenticate(String username, String password) {
    if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
      throw new InvalidDataException("Username and password are required.");
    }
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "SELECT id FROM Users WHERE username = ? AND password = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, username);
      stmt.setString(2, password);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt("id");
      }
    } catch (DatabaseConnectionException | SQLException e) {
      e.printStackTrace();
    }
    return -1;
  }   //auth function throws invalid data exception

  public boolean register(String username, String password) {
    if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
      throw new InvalidDataException("Username and password are required.");
    }
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "INSERT INTO Users (username, password, role) VALUES (?, ?, 'USER')";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, username);
      stmt.setString(2, password);
      stmt.executeUpdate();
      return true;
    } catch (DatabaseConnectionException | SQLException e) {
      e.printStackTrace();
      return false;
    }
  }   //register function throws invalid data exception

  public boolean isAdmin(int userId) {
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "SELECT role FROM Users WHERE id = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setInt(1, userId);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        String role = rs.getString("role");
        return "ADMIN".equalsIgnoreCase(role);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;  //false if user is not found or if any error occurs
  }  //admin checker

}
