package com.weka.price.cities;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static int selection;

    private static int menu() {

        Scanner input = new Scanner(System.in);

        System.out.println("Choose from these choices");
        System.out.println("-------------------------\n");
        System.out.println("1 - Option 1");
        System.out.println("2 - Option 2");
        System.out.println("3 - Option 3");
        System.out.println("4 - Quit");
        System.out.println("-------------------------\n");
        try {
            selection = input.nextInt();
            return selection;
        }
          catch (InputMismatchException ex) {
            System.out.println(ANSI_RED + "Please introduce an integer" + ANSI_RESET);
              return -1;
        }
    }

    public static void main(String[] args) {
            int userChoice;
            do {

                userChoice = menu();

                switch (userChoice) {
                    case -1:
                        break;
                    case 1:
                        // Perform "original number" case.
                        System.out.println("Option 1");
                        break;
                    case 2:
                        // Perform "encrypt number" case.
                        System.out.println("Option 2");
                        break;
                    case 3:
                        // Perform "decrypt number" case.
                        System.out.println("Option 3");
                        break;
                    default:
                        System.out.println("This option is not available");
                    }

            }
            while (userChoice != 4);
        }

    }
