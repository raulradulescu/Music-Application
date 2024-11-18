//DatabaseUtil.java
package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
  private static final String URL = "jdbc:mysql://localhost:3306/music_db"; //database name
  private static final String USER = "raul"; //MySQL username
  private static final String PASSWORD = "raulraulraul"; //MySQL password

  public static Connection getConnection() throws DatabaseConnectionException {
    try {
      return DriverManager.getConnection(URL, USER, PASSWORD);
    } catch (SQLException e) {
      throw new DatabaseConnectionException("Failed to establish a database connection.", e);
    }
  }  //implements custom exception
}
