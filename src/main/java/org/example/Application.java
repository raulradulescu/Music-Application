//Application.java
package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Application {
  private final InputDevice inputDevice;
  private final OutputDevice outputDevice;
  private final UserManager userManager;
  private final Scanner scanner;
  private int userId = 0;

  public Application(InputDevice inputDevice, OutputDevice outputDevice) {
    this.inputDevice = inputDevice;
    this.outputDevice = outputDevice;
    this.userManager = new UserManager();
    this.scanner = new Scanner(System.in);
  }


  public void run() {
    System.out.println("Welcome to the Music Library");

    boolean isLoggedIn = promptLogin();
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


  private boolean promptLogin() {
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
        return promptLogin(); // Ask again after registration
      case 3:
        System.out.println("Continuing as Guest.");
        return false;
      default:
        System.out.println("Invalid choice. Try again.");
        return promptLogin();
    }
  }     //prompt user to log in, register, or continue as guest


  private boolean logIn() {
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

  private void guestMenu() {
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
        promptLogin();  //return to main menu after registration
        break;
      case 3:
        System.out.println("Exiting...");
        return;
      default:
        System.out.println("Invalid choice.");
        guestMenu();  //re-display the menu if input is invalid
    }
  } //guest menu (for users not logged in)

  private void browseSongsAndAlbums() {
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

  private void viewAllSongs() {
    System.out.println("Fetching all songs...");
    // Logic to retrieve and display songs from the database
    inputDevice.getAllSongs().forEach(song -> outputDevice.writeMessage("Song " + song.getTitle() + " has the ID " + outputDevice.getSongId(song.getTitle())+"."));
    System.out.println(" ");
  }   //view all songs

  private void viewAllAlbums() {
    System.out.println("Fetching all albums...");
    // Logic to retrieve and display albums from the database
    inputDevice.getAllAlbums().forEach(album -> outputDevice.writeMessage("Album" + album.getTitle() + " has the ID " + outputDevice.getAlbumId(album.getTitle())+"."));
    System.out.println(" ");
  }   //view all albums

  private void register() {
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


  private void loggedInMenu() {
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
        System.out.println("Logging out...");
        userId = 0;
        run();
        return;
      default:
        System.out.println("Invalid choice.");
    }
    loggedInMenu(); //redisplay the menu after an action
  }   //menu for logged-in users

  private void listenToSong() {
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

  private void viewPlaylists(int userId, boolean isAdmin) {
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

  private void createPlaylist() {   //change to get the user id
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


  private void adminMenu() {
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
        run();
        return;
      default:
        System.out.println("Invalid choice.");
    }
    adminMenu(); // Redisplay the menu after an action
  }   //admin menu

  private void addNewSong(int albumId) {
    System.out.print("Enter the title of the new song: ");
    String title = scanner.nextLine();
    System.out.print("Enter the duration of the song (in seconds): ");
    int duration = scanner.nextInt();
    scanner.nextLine();  // Clear newline

    Song newSong = new Song(title, duration, outputDevice);

    outputDevice.saveSong(newSong, albumId);  // Passing the album ID for standalone songs

    System.out.println("Song '" + title + "' added successfully!");
  }   //method to add a new song to the database

  private void addNewAlbum() {
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
}

