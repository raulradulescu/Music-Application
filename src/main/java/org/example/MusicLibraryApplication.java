//MusicLibraryApplication.java
package org.example;

import javafx.application.Platform;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

public class MusicLibraryApplication extends javafx.application.Application {
  public final InputDevice inputDevice;
  private final OutputDevice outputDevice;
  private final UserManager userManager;
  //private final Scanner scanner;
  private int userId = 0;
  private final boolean debugMode;

  public MusicLibraryApplication(/*InputDevice inputDevice, OutputDevice outputDevice, boolean debugMode*/) {
    this.inputDevice = new InputDevice();
    this.outputDevice = new OutputDevice();
    this.userManager = new UserManager();
    //this.scanner = new Scanner(System.in);
    this.debugMode = /*debugMode*/ false;
  }

  @Override
  public void start(Stage primaryStage) {
    if (debugMode) {
      handleDebugMode();
    }

    showLoginMenu(primaryStage);
  }

  public static void main(String[] args) {
    // Launch the JavaFX application
    launch(args);
    boolean debugMode = false;
    if (args.length > 0 && "debug".equalsIgnoreCase(args[0])) {
      debugMode = true;
    }
  }

  public void showLoginMenu(Stage primaryStage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
      //LoginController controller = new LoginController(this);
      //loader.setController(controller);
      //Parent root = loader.load();
      LoginController loginController = new LoginController();
      loginController.setApplication(this);
      loader.setController(loginController);

      Scene scene = new Scene(/*root*/ loader.load());
      primaryStage.setScene(scene);
      primaryStage.setTitle("Music Library");
      //primaryStage.setScene(scene);
      primaryStage.show();

    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error while loading MainMenu.fxml:" + e.getMessage());
    }
  }

  public void showGuestMenu(Stage primaryStage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GuestMenu.fxml"));
      GuestMenuController guestMenuController = new GuestMenuController();
      guestMenuController.setApplication(this);
      loader.setController(guestMenuController);
      //loader.setController(controller);
      //Parent root = loader.load();
      Scene scene = new Scene(loader.load());

      primaryStage.setScene(scene);
      primaryStage.setTitle("Guest Menu");
      primaryStage.show();

    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error while loading GuestMenu.fxml: " + e.getMessage());
    }
  }

  public void showLoggedInMenu(Stage primaryStage, int userId) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoggedInMenu.fxml"));
      LoggedInMenuController loggedInMenuController = new LoggedInMenuController();
      loggedInMenuController.setApplication(this);
      loader.setController(loggedInMenuController);
      //loader.setController(controller);
      //Parent root = loader.load();
      Scene scene = new Scene(loader.load());
      primaryStage.setScene(scene);
      primaryStage.setTitle("Logged-In User Menu");
      primaryStage.show();

    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error loading FXML: " + e.getMessage());
    }
  }

  public void showAdminMenu(Stage primaryStage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminMenu.fxml"));
      AdminMenuController adminMenuController = new AdminMenuController();
      adminMenuController.setApplication(this);
      loader.setController(adminMenuController);

      //loader.setController(controller);
      //Parent root = loader.load();

      Scene scene = new Scene(loader.load());
      primaryStage.setScene(scene);
      primaryStage.setTitle("Admin Menu");
      primaryStage.show();

    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error loading FXML: " + e.getMessage());
    }
  }


  public boolean logIn(String username, String password) {
    Task<Boolean> loginTask = new LoginTask(username, password);
    Thread loginThread = new Thread(loginTask);
    loginThread.start();

    try {
      return loginTask.get();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public void register() {
    Task<Void> registerTask = new RegisterTask();
    Thread registerThread = new Thread(registerTask);
    registerThread.start();
  }

  public void logOut() {
    System.out.println("Logging out...");
    userId = 0;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public boolean isAdmin(int userId) {
    return userManager.isAdmin(userId);
  }


  // Methods to view data

  public void showBrowseSongsAndAlbums() {
    Task<Void> handleSongsAndAlbums = new HandleSongsAndAlbums();
    Thread handleSongsAndAlbumsThread = new Thread(handleSongsAndAlbums);
    handleSongsAndAlbumsThread.start();
  }

  public void viewAllSongs() {
    Task<Void> viewSongsTask = new ViewSongsTask();
    Thread viewSongsThread = new Thread(viewSongsTask);
    viewSongsThread.start();
  }

  public void viewAllAlbums() {
    Task<Void> viewAlbumsTask = new ViewAlbumsTask();
    Thread viewAlbumsThread = new Thread(viewAlbumsTask);
    viewAlbumsThread.start();
  }

  public void viewPlaylists(int userId, boolean isAdmin) {
    Task<Void> viewPlaylistsTask = new ViewPlaylistsTask(userId, isAdmin);
    Thread viewPlaylistsThread = new Thread(viewPlaylistsTask);
    viewPlaylistsThread.start();
  }

  public void createPlaylist(String playlistName, int userId, List<Song> selectedSongs) {
    Task<Void> createPlaylistTask = new CreatePlaylistTask(playlistName, userId, selectedSongs);

    createPlaylistTask.setOnSucceeded(event -> {
      System.out.println("Playlist creation succeeded: " + createPlaylistTask.getMessage());
    });
    createPlaylistTask.setOnFailed(event -> {
      System.err.println("Playlist creation failed!");
      createPlaylistTask.getException().printStackTrace();
    });


    Thread createPlaylistThread = new Thread(createPlaylistTask);
    createPlaylistThread.setDaemon(true);  // allows JVM to exit if this is the only thread
    createPlaylistThread.start();
  }

  public void listenToSong(int songId) {
    Task<Void> listenToSongTask = new ListenToSongTask(songId);
    Thread listenToSongThread = new Thread(listenToSongTask);
    listenToSongThread.start();
  }

  public void addNewSong(String title, int duration, int albumId) {
    Task<Void> addNewSongTask = new AddNewSongTask(title, duration, albumId);

    addNewSongTask.setOnSucceeded(event -> {
      System.out.println("Song creation succeeded: " + addNewSongTask.getMessage());
    });
    addNewSongTask.setOnFailed(event -> {
      System.err.println("Song creation failed!");
      addNewSongTask.getException().printStackTrace();
    });

    // statusLabel.textProperty().bind(addNewSongTask.messageProperty());

    Thread addNewSongThread = new Thread(addNewSongTask);
    addNewSongThread.setDaemon(true);
    addNewSongThread.start();
  }

  public void addNewAlbum(String albumTitle, Artist artist, Producer producer, List<Song> albumSongs) {
    Task<Void> addNewAlbumTask = new AddNewAlbumTask(albumTitle, artist, producer, albumSongs);

    addNewAlbumTask.setOnSucceeded(event -> {
      System.out.println("Album creation succeeded: " + addNewAlbumTask.getMessage());
    });
    addNewAlbumTask.setOnFailed(event -> {
      System.err.println("Album creation failed!");
      addNewAlbumTask.getException().printStackTrace();
    });

    // statusLabel.textProperty().bind(addNewAlbumTask.messageProperty());

    Thread addNewAlbumThread = new Thread(addNewAlbumTask);
    addNewAlbumThread.setDaemon(true);
    addNewAlbumThread.start();
  }


  // Task classes
  private class LoginTask extends Task<Boolean> {
    private final String username;
    private final String password;

    public LoginTask(String username, String password) {
      this.username = username;
      this.password = password;
    }

    @Override
    protected Boolean call() throws Exception {
      userId = userManager.authenticate(username, password);
      return userId > 0;
    }
  }

  private class RegisterTask extends Task<Void> {
    @Override
    protected Void call() throws Exception {
      System.out.print("Enter a new username: ");
      String username = new java.util.Scanner(System.in).nextLine();
      System.out.print("Enter a new password: ");
      String password = new java.util.Scanner(System.in).nextLine();

      boolean isRegistered = userManager.register(username, password);
      if (isRegistered) {
        System.out.println("Registration successful! You can now log in.");
      } else {
        System.out.println("Registration failed. Username may already exist.");
      }
      return null;
    }
  }

  private class HandleSongsAndAlbums extends Task<Void> {
    @Override
    protected Void call() throws Exception {
      updateMessage("Fetching all songs...");
      List<Song> songs = inputDevice.getAllSongs();
      updateMessage("Songs in the database:");
      for (Song song : songs) {
        updateMessage("Song " + song.getTitle() + " has the ID " + inputDevice.getSongId(song.getTitle()) + ".");
      }
      updateMessage("Fetching all albums...");
      List<Album> albums = inputDevice.getAllAlbums();
      updateMessage("Albums in the database:");
      for (Album album : albums) {
        updateMessage("Album " + album.getTitle() + " has the ID " + inputDevice.getAlbumId(album.getTitle()) + ".");
      }
      updateMessage(" ");
      return null;
    }
  }

  private class ViewSongsTask extends Task<Void> {
    @Override
    protected Void call() throws Exception {
      updateMessage("Fetching all songs...");
      List<Song> songs = inputDevice.getAllSongs();
      updateMessage("Songs in the database:");
      for (Song song : songs) {
        updateMessage("Song " + song.getTitle() + " has the ID " + inputDevice.getSongId(song.getTitle()) + ".");
      }
        updateMessage("Fetching all albums...");
        List<Album> albums = inputDevice.getAllAlbums();
        updateMessage("Albums in the database:");
        for (Album album : albums) {
          updateMessage("Album " + album.getTitle() + " has the ID " + inputDevice.getAlbumId(album.getTitle()) + ".");
        }
        updateMessage(" ");
        return null;
      }
    }

  private class ViewAlbumsTask extends Task<Void> {
    @Override
    protected Void call() throws Exception {
      updateMessage("Fetching all albums...");
      List<Album> albums = inputDevice.getAllAlbums();
      updateMessage("Albums in the database:");
      for (Album album : albums) {
        updateMessage("Album " + album.getTitle() + " has the ID " + inputDevice.getAlbumId(album.getTitle()) + ".");
      }
      updateMessage(" ");
      return null;
    }
  }

  private class ViewPlaylistsTask extends Task<Void> {
    private final int userId;
    private final boolean isAdmin;

    public ViewPlaylistsTask(int userId, boolean isAdmin) {
      this.userId = userId;
      this.isAdmin = isAdmin;
    }

    @Override
    protected Void call() throws Exception {
      List<Playlist> playlists;
      String message;

      if (!isAdmin) {
        playlists = inputDevice.getPlaylists(userId, isAdmin, outputDevice);
        message = "You haven't created any playlists yet.";
      } else {
        playlists = inputDevice.getPlaylists(userId, isAdmin, outputDevice);
        message = "No playlists found.";
      }

      if (playlists.isEmpty()) {
        updateMessage(message);
      } else {
        updateMessage("Playlists:");
        for (Playlist playlist : playlists) {
          updateMessage("- " + playlist.getName());
        }
      }
      return null;
    }
  }

  private class CreatePlaylistTask extends Task<Void> {
    private final String playlistName;
    private final int userId;             // The current user's ID
    private final List<Song> selectedSongs;

    public CreatePlaylistTask(String playlistName, int userId, List<Song> selectedSongs) {
      this.playlistName = playlistName;
      this.userId = userId;
      this.selectedSongs = selectedSongs;
    }

    @Override
    protected Void call() throws Exception {
      // 1) Create a new Playlist object
      Playlist newPlaylist = new Playlist(playlistName, userId, outputDevice);

      // 2) Add selected songs to the playlist
      for (Song song : selectedSongs) {
        newPlaylist.addItem(song);
      }

      // 3) Save the playlist to the database via OutputDevice
      outputDevice.savePlaylist(newPlaylist);

      // 4) Let any UI binding (like a status Label) know that we are done
      updateMessage("Playlist '" + playlistName + "' created successfully!");
      return null;
    }
  }

  private class ListenToSongTask extends Task<Void> {
    private final int songId;

    // The constructor receives the song ID the user wants to listen to
    public ListenToSongTask(int songId) {
      this.songId = songId;
    }

    @Override
    protected Void call() throws Exception {
      // fetch song
      Song song = inputDevice.fetchSongById(songId, outputDevice);

      // 2) If found, "play" it (the play() method in Song.java could do console print or real audio)
      if (song != null) {
        song.play();
        updateMessage("Now playing: " + song.getTitle());
      } else {
        updateMessage("Song with ID " + songId + " not found in the database.");
      }

      return null;
    }
  }

  private class AddNewSongTask extends Task<Void> {
    private final String title;
    private final int duration;
    private final int albumId; //0 for a  “single” (no album)

    public AddNewSongTask(String title, int duration, int albumId) {
      this.title = title;
      this.duration = duration;
      this.albumId = albumId;
    }

    @Override
    protected Void call() throws Exception {
      // 1) Create new Song
      Song newSong = new Song(title, duration, outputDevice);

      // 2) Save it
      //    albumId == 0 => no album association
      outputDevice.saveSong(newSong, albumId);

      // 3) Update the message for any bound UI label
      updateMessage("Song '" + title + "' added successfully!");
      return null;
    }
  }

  private class AddNewAlbumTask extends Task<Void> {
    private final String albumTitle;
    private final Artist artist;
    private final Producer producer;
    private final List<Song> songsForAlbum;

    // The controller (or other UI logic) will pass in the user’s chosen album title,
    // plus fully constructed Artist and Producer objects
    // and the list of songs for the album.
    public AddNewAlbumTask(String albumTitle, Artist artist, Producer producer, List<Song> songsForAlbum) {
      this.albumTitle = albumTitle;
      this.artist = artist;
      this.producer = producer;
      this.songsForAlbum = songsForAlbum;
    }

    @Override
    protected Void call() throws Exception {

      Album newAlbum = new Album(
              albumTitle,
              artist,
              producer,
              songsForAlbum.toArray(new Song[0]),
              outputDevice
      );

      outputDevice.saveAlbum(newAlbum);

      updateMessage("Album '" + albumTitle + "' by " + artist.getName() + " added successfully!");
      return null;
    }
  }


  public void handleDebugMode() {
    System.out.println("Running in debug mode...");

    try {
      DatabaseUtil.getConnection();
      System.out.println("Database connection established successfully.");
    } catch (DatabaseConnectionException e) {
      System.err.println("Database connection error: " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println("Fetching sample data...");

    List<Song> songs = inputDevice.getAllSongs();
    if (songs.isEmpty()) {
      System.out.println("No songs found in the database.");
    } else {
      System.out.println("Songs in the database:");
      for (Song song : songs) {
        System.out.println("ID: " + inputDevice.getSongId(song.getTitle()) + ", Title: " + song.getTitle() + ", Duration: " + song.getDuration() + " seconds");
      }
    }

    List<Album> albums = inputDevice.getAllAlbums();
    if (albums.isEmpty()) {
      System.out.println("No albums found in the database.");
    } else {
      System.out.println("Albums in the database:");
      for (Album album : albums) {
        System.out.println("ID: " + inputDevice.getAlbumId(album.getTitle()) + ", Title: " + album.getTitle() + ", Artist: " + album.getArtist().getName() + ", Producer: " + album.getProducer().getName());
        System.out.println("Songs in album:");
        for (Song song : album.getSongs()) {
          System.out.println("  - ID: " + inputDevice.getSongId(song.getTitle()) + ", Title: " + song.getTitle() + ", Duration: " + song.getDuration() + " seconds");
        }
      }
    }

    try (Connection connection = DatabaseUtil.getConnection()) {
      String userQuery = "SELECT id, username, role FROM Users";
      PreparedStatement userStmt = connection.prepareStatement(userQuery);
      ResultSet userRs = userStmt.executeQuery();

      if (!userRs.isBeforeFirst()) {
        System.out.println("No users found in the database.");
      } else {
        System.out.println("Users in the database:");
        while (userRs.next()) {
          int userId = userRs.getInt("id");
          String username = userRs.getString("username");
          String role = userRs.getString("role");
          System.out.println("ID: " + userId + ", Username: " + username + ", Role: " + role);
        }
      }
    } catch (DatabaseConnectionException | SQLException e) {
      System.err.println("Error fetching user data: " + e.getMessage());
      e.printStackTrace();
    }

    List<Playlist> playlists = inputDevice.getPlaylists(0, true, outputDevice); // Fetch all playlists as admin
    if (playlists.isEmpty()) {
      System.out.println("No playlists found in the database.");
    } else {
      System.out.println("Playlists in the database:");
      for (Playlist playlist : playlists) {
        System.out.println("ID: " + playlist.getName() + ", Created by User ID: " + playlist.getCreatedByUserID());
        System.out.println("Songs in playlist:");
        for (Playable item : playlist.getItems()) {
          if (item instanceof Song) {
            Song song = (Song) item;
            System.out.println("  - ID: " + inputDevice.getSongId(song.getTitle()) + ", Title: " + song.getTitle() + ", Duration: " + song.getDuration() + " seconds");
          }
        }
      }
    }
    System.out.println("Debug mode tasks completed.");
  }



  /*
  public void runCLI() {
    System.out.println("Welcome to the Music Library");

    if (debugMode) {
      handleDebugMode();
    }

    boolean isLoggedIn = mainMenu();
    if (isLoggedIn) {
      if (userManager.isAdmin(userId)) {
        adminMenu();  //display the admin menu if the user is an admin
      } else {
        loggedInMenu();  //display the menu for logged-in users
      }
    } else {
      guestMenu();  //display the guest menu otherwise
    }
  }  //main method to run the application

  public boolean mainMenu() {
    System.out.println("Please select an option:");
    System.out.println("1. Log In");
    System.out.println("2. Register");
    System.out.println("3. Continue as Guest");
    System.out.println("To exit the program, continue as a guest and exit the program there.");

    int choice = scanner.nextInt();
    scanner.nextLine();  // Clear newline

    switch (choice) {
      case 1:
        return logIn();
      case 2:
        register();
        return mainMenu(); // Ask again after registration
      case 3:
        System.out.println("Continuing as Guest.");
        return false;
      default:
        System.out.println("Invalid choice. Try again.");
        return mainMenu();
    }
  }     //prompt user to log in, register, or continue as guest


  public boolean logIn() {
    System.out.print("Enter username: ");
    String username = scanner.nextLine();
    System.out.print("Enter password: ");
    String password = scanner.nextLine();

    userId = userManager.authenticate(username, password);
    if (userId>0){
      return true;
    } else {
      System.out.println("Invalid credentials. Try again.");
      return false;
    }
  }   //log in method

  public void guestMenu() {
    System.out.println("Guest Menu:");
    System.out.println("1. View Songs and Albums");
    System.out.println("2. Register");
    System.out.println("3. Exit");

    int choice = scanner.nextInt();
    scanner.nextLine();  //clear newline

    switch (choice) {
      case 1:
        browseSongsAndAlbums();
        break;
      case 2:
        register();
        mainMenu();  //return to main menu after registration
        break;
      case 3:
        System.out.println("Exiting...");
        return;
      default:
        System.out.println("Invalid choice.");
        guestMenu();  //re-display the menu if input is invalid
    }
  } //guest menu (for users not logged in)


  public void browseSongsAndAlbums() {
    System.out.println("Browse Songs and Albums:");
    System.out.println("1. View All Songs");
    System.out.println("2. View All Albums");
    System.out.println("3. Return to Guest Menu");

    int choice = scanner.nextInt();
    scanner.nextLine();  // Clear newline

    switch (choice) {
      case 1:
        viewAllSongs();
        break;
      case 2:
        viewAllAlbums();
        break;
      case 3:
        guestMenu();
        return;
      default:
        System.out.println("Invalid choice.");
    }
    browseSongsAndAlbums();  // Return to browsing menu after an action
  }     //method for browsing songs and albums

  public void viewAllSongs() {
    System.out.println("Fetching all songs...");
    // Logic to retrieve and display songs from the database
    inputDevice.getAllSongs().forEach(song -> outputDevice.writeMessage("Song " + song.getTitle() + " has the ID " + inputDevice.getSongId(song.getTitle())+"."));
    System.out.println(" ");
  }   //view all songs

  public void viewAllAlbums() {
    System.out.println("Fetching all albums...");
    // Logic to retrieve and display albums from the database
    inputDevice.getAllAlbums().forEach(album -> outputDevice.writeMessage("Album" + album.getTitle() + " has the ID " + inputDevice.getAlbumId(album.getTitle())+"."));
    System.out.println(" ");
  }   //view all albums

  public void register() {
    System.out.print("Enter a new username: ");
    String username = scanner.nextLine();
    System.out.print("Enter a new password: ");
    String password = scanner.nextLine();

    boolean isRegistered = userManager.register(username, password);
    if (isRegistered) {
      System.out.println("Registration successful! You can now log in.");
    } else {
      System.out.println("Registration failed. Username may already exist.");
    }
  }   //register method


  public void loggedInMenu() {
    System.out.println("Logged-In User Menu:");
    System.out.println("1. View All Songs");
    System.out.println("2. View All Albums");
    System.out.println("3. Listen to a Song by ID");
    System.out.println("4. View My Playlists");
    System.out.println("5. Create Playlist");
    System.out.println("6. Logout");

    int choice = scanner.nextInt();
    scanner.nextLine();  // clear newline

    switch (choice) {
      case 1:
        viewAllSongs();
        break;
      case 2:
        viewAllAlbums();
        break;
      case 3:
        listenToSong();
        break;
      case 4:
        viewPlaylists(userId,false);  //non-admin user views only their playlists
        break;
      case 5:
        createPlaylist();
        break;
      case 6:
        logOut();
        return;
      default:
        System.out.println("Invalid choice.");
    }
    loggedInMenu(); //redisplay the menu after an action
  }   //menu for logged-in users

  public void listenToSong() {
    System.out.print("Enter Song ID to listen: ");
    int songId = scanner.nextInt();
    scanner.nextLine(); //clear newline

    //fetch the song from the database
    Song song = inputDevice.fetchSongById(songId, outputDevice);
    if (song != null) {
      song.play();  // Calls the play() method in Song.java
    } else {
      System.out.println("Song not found.");
    }
  }   //method to listen to playable objects

  public void viewPlaylists(int userId, boolean isAdmin) {
    List<Playlist> playlists;
    String message;

    if (!isAdmin) {
      playlists = inputDevice.getPlaylists(userId,isAdmin,outputDevice);
      message = "You haven't created any playlists yet.";
    } else {
      playlists = inputDevice.getPlaylists(userId,isAdmin,outputDevice);
      message = "No playlists found.";
    }

    if (playlists.isEmpty()) {
      System.out.println(message);
    } else {
      System.out.println("Playlists:");
      for (Playlist playlist : playlists) {
        System.out.println("- " + playlist.getName());
      }
    }
  }  //method to view playlists based on user's role

  public void createPlaylist() {   //change to get the user id
    System.out.print("Enter the name of your new playlist: ");
    String playlistName = scanner.nextLine();

    // Display all songs and let the user select multiple songs by ID
    List<Song> availableSongs = inputDevice.getAllSongs();
    List<Song> selectedSongs = new ArrayList<>();

    System.out.println("Available Songs:");
    for (int i = 0; i < availableSongs.size(); i++) {
      System.out.println((i + 1) + ". " + availableSongs.get(i).getTitle());
    }

    System.out.println("Enter song numbers to add to the playlist (comma-separated), or 0 to finish:");
    String[] songChoices = scanner.nextLine().split(",");

    for (String choice : songChoices) {
      choice = choice.trim();
      if (!choice.equals("0")) {
        try {
          int songIndex = Integer.parseInt(choice) - 1;
          if (songIndex >= 0 && songIndex < availableSongs.size()) {
            selectedSongs.add(availableSongs.get(songIndex));
          } else {
            System.out.println("Invalid song number: " + choice);
          }
        } catch (NumberFormatException e) {
          System.out.println("Invalid input: " + choice);
        }
      }
    }

    // Save the playlist with the selected songs
    Playlist newPlaylist = new Playlist(playlistName, userId, outputDevice);
    selectedSongs.forEach(newPlaylist::addItem);
    outputDevice.savePlaylist(newPlaylist);

    System.out.println("Playlist '" + playlistName + "' created successfully!");
  }   //method to create a playlist

  public void logOut() {
    System.out.println("Logging out...");
    userId = 0;
    runCLI();
  }


  public void adminMenu() {
    System.out.println("Admin Menu:");
    System.out.println("1. View All Songs");
    System.out.println("2. View All Albums");
    System.out.println("3. Listen to a Song by ID");
    System.out.println("4. View All Playlists");
    System.out.println("5. Add New Song");
    System.out.println("6. Add New Album");
    System.out.println("7. Logout");

    int choice = scanner.nextInt();
    scanner.nextLine();  // Clear newline

    switch (choice) {
      case 1:
        viewAllSongs();
        break;
      case 2:
        viewAllAlbums();
        break;
      case 3:
        listenToSong();
        break;
      case 4:
        viewPlaylists(0,true);  //admin views all playlists
        break;
      case 5:
        addNewSong(0);  // Passing null for standalone songs
        break;
      case 6:
        addNewAlbum();
        break;
      case 7:
        System.out.println("Logging out...");
        userId=0;
        runCLI();
        return;
      default:
        System.out.println("Invalid choice.");
    }
    adminMenu(); // Redisplay the menu after an action
  }   //admin menu

  public void addNewSong(int albumId) {
    System.out.print("Enter the title of the new song: ");
    String title = scanner.nextLine();
    System.out.print("Enter the duration of the song (in seconds): ");
    int duration = scanner.nextInt();
    scanner.nextLine();  // Clear newline

    Song newSong = new Song(title, duration, outputDevice);

    outputDevice.saveSong(newSong, albumId);  // Passing the album ID for standalone songs

    System.out.println("Song '" + title + "' added successfully!");
  }   //method to add a new song to the database

  public void addNewAlbum() {
    System.out.print("Enter the title of the new album: ");
    String albumTitle = scanner.nextLine();
    System.out.print("Enter the artist's name: ");
    String artistName = scanner.nextLine();
    //create the artist object
    Artist artist = new Artist(artistName);
    outputDevice.savePerson(artist, "artist");

    System.out.print("Enter the producer's name or leave blank to be the same as the artist: ");
    //create the producer object
    String check = scanner.nextLine();
    String producerName;
    if(check.isEmpty()){
      producerName = artistName;
    }
    else{
      producerName = check;
    }

    Producer producer = new Producer(producerName);
    outputDevice.savePerson(producer, "producer");

    // List of songs for the album
    List<Song> albumSongs = new ArrayList<>();

    // Add songs to the album
    while (true) {
      System.out.print("Enter the title of a song (or type 'done' to finish): ");
      String songTitle = scanner.nextLine();
      if (songTitle.equalsIgnoreCase("done")) {
        break;
      }
      System.out.print("Enter the duration of the song (in seconds): ");
      int songDuration = scanner.nextInt();
      scanner.nextLine();  // Clear newline

      // Create and add the song to the album's song list
      Song song = new Song(songTitle, songDuration, outputDevice);
      albumSongs.add(song);
    }

    // Save the album
    Album newAlbum = new Album(albumTitle, artist,producer, albumSongs.toArray(new Song[0]), outputDevice);
    outputDevice.saveAlbum(newAlbum);

    System.out.println("Album '" + albumTitle + "' by " + artistName + " added successfully!");
  }   //method to add a new album with multiple songs
*/

}

