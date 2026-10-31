package app;

import service.RentalSystem;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleMenu menu = new ConsoleMenu(new RentalSystem(), new InputHelper(scanner));
        menu.run();
        scanner.close();
    }
}
