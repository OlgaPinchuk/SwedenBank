package project.utils;

public class ConsoleMessage {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";

    public static void showSuccessMessage(String message) {
        System.out.println(GREEN + message + RESET);
    }

    public static void showInfoMessage(String message) {
        System.out.println(YELLOW + message + RESET);
    }

    public static void showErrorMessage(String message) {
        System.out.println(RED + message + RESET);
    }
}