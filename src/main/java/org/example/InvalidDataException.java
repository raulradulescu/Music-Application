//InvalidDataException.java (custom error)
package org.example;

public class InvalidDataException extends IllegalArgumentException {
  public InvalidDataException() {
    super();
  }

  public InvalidDataException(String message) {
    super(message);
  }

  public InvalidDataException(String message, Throwable cause) {
    super(message, cause);
  }
}