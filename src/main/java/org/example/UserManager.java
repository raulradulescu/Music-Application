package org.example;

import java.sql.*;

public class UserManager {

  //auth function
  public int authenticate(String username, String password) {
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "SELECT id FROM Users WHERE username = ? AND password = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, username);
      stmt.setString(2, password);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt("id"); //return the user ID if successful auth
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1; // Return -1 to indicate authentication failure
  }
  //register function
  public boolean register(String username, String password) {
    try (Connection connection = DatabaseUtil.getConnection()) {
      String query = "INSERT INTO Users (username, password, role) VALUES (?, ?, 'USER')";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, username);
      stmt.setString(2, password);

      stmt.executeUpdate();
      return true;  //registration ok
    } catch (SQLException e) {
      e.printStackTrace();
      return false;  //any errors dont log in
    }
  }
  //admin checker
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
  }

}
