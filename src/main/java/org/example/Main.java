//Main.java
package org.example;
import javafx.stage.Stage;

public class Main {

  public static void main(String[] args) {
    boolean debugMode = false;
    if (args.length > 0 && "debug".equalsIgnoreCase(args[0])) {
      debugMode = true;
    }

    Stage stage = new Stage();
    InputDevice inputDevice = new InputDevice();
    OutputDevice outputDevice = new OutputDevice();
    MusicLibraryApplication musicLibraryApplication = new MusicLibraryApplication(/*inputDevice, outputDevice, debugMode*/);
    //application.runCLI();

    musicLibraryApplication.start(stage);
  }
}
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
//TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
// to see how IntelliJ IDEA suggests fixing it.
//TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
// for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.