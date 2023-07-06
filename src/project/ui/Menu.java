package project.ui;

import project.utils.ConsoleMessage;

import java.util.List;
import java.util.Scanner;

public abstract class Menu {
     protected String instruction;
     protected List<String> options;
     protected Scanner scanner;

//     private Menu() {}

     public Menu(String instruction, List<String> options) {
          this.instruction = instruction;
          this.options = options;
          this.scanner = new Scanner(System.in);
     }

     abstract void handleUserChoice();
     protected abstract String getHeader();

     public void displayMenu() {
          System.out.println(instruction);
          printOptions(options);
          System.out.println("Your choice: ");
     }

     public void displayMenu(String header) {
          printHeader(header);
          printBody();
     }

     public void showMenu() {
          displayMenu();
          handleUserChoice();
     }

     public boolean shouldReturnToMenu(String input) {
          return input.equalsIgnoreCase("exit");
     }

     public void returnToMenu() {
          ConsoleMessage.showInfoMessage("Returning to the previous menu.");
          showMenu();
     }

     public void printOptions(List<String> options) {
          for(int i = 0; i < options.size(); i++) {
               String option = String.format("[%d] %s", i+1, options.get(i));
               System.out.println(option);
          }
     }

     public void printBlankLine() {
          System.out.println();
     }
     public void exit() {
          System.out.println("Quitting...");
          System.exit(0);
     }

     public String getUserInput(String message) {
          Scanner scanner = new Scanner(System.in);
          String input = "";

          while (input.isEmpty()) {
               System.out.print(message);
               input = scanner.nextLine();


               if (input.isEmpty()) {
                    showInvalidInputMessage();
               }
          }
          return input;
     }

     public void showInvalidOptionMessage() {
          ConsoleMessage.showErrorMessage("Invalid option selected. Please try again.");
     }

     public void showInvalidInputMessage() {
          ConsoleMessage.showErrorMessage("Invalid input. Please try again.");
     }

     private void printHeader(String header) {
          System.out.println(header);
          printBlankLine();
     }

     private void printBody() {
          System.out.println(instruction);
          printBlankLine();
          printOptions(options);
          printBlankLine();
          System.out.println("Your choice: ");
     }
}