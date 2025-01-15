//LoginController.java
/*
package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

  private final UserManager userManager;

  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  private final MusicLibraryApplication musicLibraryApplication;

  public LoginController(MusicLibraryApplication musicLibraryApplication) {
    this.musicLibraryApplication = musicLibraryApplication;
    this.userManager = new UserManager();
  }

  @FXML
  public void handleLogin(ActionEvent event) {
    String username = usernameField.getText();
    String password = passwordField.getText();
    int userId = userManager.authenticate(username, password);
    if (userId > 0) {
      if (userManager.isAdmin(userId)) {
        musicLibraryApplication.showAdminMenu((Stage) usernameField.getScene().getWindow());
      } else {
        musicLibraryApplication.showLoggedInMenu((Stage) usernameField.getScene().getWindow(), userId);
      }
    } else {
      System.out.println("Invalid credentials. Try again.");
    }
  }

  @FXML
  public void handleRegister(ActionEvent event) {
    musicLibraryApplication.register();
    handleLogin(event);
  }

  @FXML
  public void handleGuest(ActionEvent event) {
    musicLibraryApplication.showGuestMenu((Stage) usernameField.getScene().getWindow());
  }
}
*/

package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
  private final UserManager userManager = new UserManager();

  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  private MusicLibraryApplication musicLibraryApplication;

  public void setApplication(MusicLibraryApplication app) {
    this.musicLibraryApplication = app;
  }

  @FXML
  private Label errorLabel; // Link the error label in the FXML

  @FXML
  public void handleLogin(ActionEvent event) {
    System.out.println("Login button clicked.");
    String username = usernameField.getText();
    String password = passwordField.getText();

    if (username.isEmpty() || password.isEmpty()) {
      errorLabel.setText("Username and password cannot be empty.");
      return;
    }
    if (musicLibraryApplication == null) {
      errorLabel.setText("Application not initialized. Please restart.");
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }

    int userId = userManager.authenticate(username, password);
    if (userId > 0) {
      musicLibraryApplication.setUserId(userId); // Set the logged-in user's ID
      System.out.println("User ID set to: " + userId);

      errorLabel.setText("");
      Stage currentStage = (Stage) usernameField.getScene().getWindow();
      if (userManager.isAdmin(userId)) {
        musicLibraryApplication.showAdminMenu(currentStage);
      } else {
        musicLibraryApplication.showLoggedInMenu(currentStage, userId);
      }
    } else {
      errorLabel.setText("Invalid credentials. Try again.");
      System.out.println("User inputted invalid credentials.");
    }
  }

  @FXML
  public void handleRegister(ActionEvent event) {
    System.out.println("Register button clicked.");
    String username = usernameField.getText();
    String password = passwordField.getText();

    if (musicLibraryApplication == null) {
      errorLabel.setText("Application not initialized. Please restart.");
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }
    if (username.isEmpty() || password.isEmpty()) {
      errorLabel.setText("Username and password cannot be empty.");
      return;
    }

    boolean success = userManager.register(username, password);

    if (success) {
      errorLabel.setText("Registration successful! Please log in.");
    } else {
      errorLabel.setText("Username already taken. Please try a different one.");
    }
  }

  @FXML
  public void handleGuest(ActionEvent event) {
    System.out.println("Guest button clicked.");
    if (musicLibraryApplication == null) {
      System.out.println("Error: musicLibraryApplication is null.");
      return;
    }
    Stage currentStage = (Stage) usernameField.getScene().getWindow();
    musicLibraryApplication.showGuestMenu(currentStage);
  }

  @FXML
  private void handleExit(ActionEvent event) {
    System.out.println("Exit button clicked.");
    System.exit(0);
  }
}