package com.example;

import java.util.Scanner;

class MainApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize database connection (optional: ensure it’s working)
        try {
            ConnectionClassForDM.getConnection();

            boolean running = true;
            while (running) {
                System.out.println("\nWelcome to Spotify Console!");
                System.out.println("1. Admin");
                System.out.println("2. User");
                System.out.println("3. Back");

                // Use getIntegerInput() method to ensure valid input
                int choice = getIntegerInput(scanner, "Choose an option: ");

                switch (choice) {
                    case 1:
                        Admin admin = new Admin(choice);
                        // Call loginAdmin() to validate admin credentials before proceeding
                        if (admin.loginAdmin()) {
                            admin.manageSongs(); // Only if login is successful, allow access to manage songs
                        } else {
                            System.out.println("Invalid admin credentials.");
                        }
                        break;
                    case 2:
                        Registration registration = new Registration();
                        boolean isLoggedIn = false;
                        while (!isLoggedIn) {
                            System.out.println("\n1. Register");
                            System.out.println("2. Login");
                            System.out.println("3. Back");

                            int userChoice = getIntegerInput(scanner, "Choose an option: ");

                            switch (userChoice) {
                                case 1:
                                    registration.registerUser();
                                    break;
                                case 2:
                                    isLoggedIn = registration.loginUser();
                                    if (isLoggedIn) {
                                        User loggedInUser = registration.getLoggedInUser();
                                        Playlists menu = new Playlists(loggedInUser);
                                        menu.userMenu();
                                    }
                                    break;
                                case 3:
                                    isLoggedIn = true;
                                    break;
                                default:
                                    System.out.println("Invalid choice. Please try again.");
                            }
                        }
                        break;
                    case 3:
                        running = false;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }

        scanner.close();
    }

    // Method to get valid integer input
    private static int getIntegerInput(Scanner scanner, String prompt) {
        int input;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                scanner.nextLine(); // Consume the newline
                break;
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); // Consume the invalid input
            }
        }
        return input;
    }
}