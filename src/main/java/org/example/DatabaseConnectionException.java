//DatabaseConnectionException.java (custom error)
package org.example;

public class DatabaseConnectionException extends RuntimeException {
  public DatabaseConnectionException() {
    super();
  }

  public DatabaseConnectionException(String message) {
    super(message);
  }

  public DatabaseConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}