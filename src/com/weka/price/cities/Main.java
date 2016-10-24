package com.weka.price.cities;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static int selection;

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
                        System.out.println("Option 1");
                        break;
                    case 2:
                        System.out.println("Option 2");
                        break;
                    case 3:
                        System.out.println("Option 3");
                        break;
                    default:
                        if (userChoice!=4)
                        System.out.println("This option is not available");
                    }

            }
            while (userChoice != 4);
        }

    }
