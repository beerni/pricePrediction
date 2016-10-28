package com.weka.price.cities;

import java.io.BufferedReader;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.io.FileReader;

import weka.classifiers.functions.LinearRegression;

import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemoveWithValues;

public class Main {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private static int menu() {
        int selection;
        Scanner input = new Scanner(System.in);
        System.out.println(ANSI_CYAN + "  __             _   ____       _          \n" + ANSI_RESET +
                " / _| ___   ___ | |_|  _ \\ _ __(_) ___ ___ \n" + ANSI_GREEN +
                "| |_ / _ \\ /" + ANSI_YELLOW + " _ \\| " + ANSI_RED + "__| |_) | '__| |/ __/ _ \\\n" + ANSI_RESET +
                "|  _| (_) | " + ANSI_BLACK + "(_) | |_|  __/| |" + ANSI_PURPLE + "  | | (_|  __/\n" + ANSI_RED +
                "|_|  \\___/ \\___/" + ANSI_BLUE + " \\__|_|   |_|  |_|\\___\\___|\n" + ANSI_RESET +
                "                                           \n");
        System.out.println("Select an option of the beyond menu ---> \n");
        System.out.println("1 - Calculates the price equation according to a given position");
        System.out.println("2 - What player do you need?");
        System.out.println("3 - Option 3");
        System.out.println("4 - Quit");
        System.out.println("-------------------------\n");
        try {
            selection = input.nextInt();
            return selection;
        } catch (InputMismatchException ex) {
            System.out.println(ANSI_RED + "\nPlease introduce an integer" + ANSI_RESET);
            return -1;
        }
    }

    private static void buildLinearModel(String indices, String position) throws Exception {
        //Reading whole dataset
        BufferedReader trainingData = new BufferedReader(new FileReader("/Users/bernatmir/Desktop/dataset.arff"));
        Instances train = new Instances(trainingData);
        train.setClassIndex(train.numAttributes() - 1);

        //Deleting  useless attributes
        String[] options = new String[2];
        options[0] = "-R";
        options[1] = "1,2,3,6";
        Remove remove = new Remove();
        remove.setOptions(options);
        remove.setInputFormat(train);

        //Applying  remove filter
        Instances newData = Filter.useFilter(train, remove);
        //Remove with values if is GK, D, M or S
        RemoveWithValues removeWithValues = new RemoveWithValues();
        String[] optionsValues = new String[6];
        optionsValues[0] = "-S";
        optionsValues[1] = "0.0";
        optionsValues[2] = "-C";
        optionsValues[3] = "2";
        optionsValues[4] = "-L";
        optionsValues[5] = indices;
        removeWithValues.setOptions(optionsValues);
        removeWithValues.setInputFormat(newData);

        //Applying  remove with values filter
        Instances newData2 = Filter.useFilter(newData, removeWithValues);


        Instances data = null;

        //If is a GK remove Assists attribute
        if (position.equals("GK")) {
            String[] assists = new String[2];
            assists[0] = "-R";
            assists[1] = "6";
            Remove remove2 = new Remove();
            remove2.setOptions(assists);
            remove2.setInputFormat(newData2);
            data = Filter.useFilter(newData2, remove2);
        }

        //If is a D or S remove AerialsWon attribute
        if (position.equals("D") || position.equals("S")) {
            String[] assists = new String[2];
            assists[0] = "-R";
            assists[1] = "11";
            Remove remove2 = new Remove();
            remove2.setOptions(assists);
            remove2.setInputFormat(newData2);
            data = Filter.useFilter(newData2, remove2);
        }

        //If is a M do not delete anything
        if (position.equals("M")) {
            data = newData2;
        }

        //Train classifier
        LinearRegression lr = new LinearRegression();
        Evaluation eval = new Evaluation(data);
        lr.buildClassifier(data);
        eval.crossValidateModel(lr, data, 10, new Random(1));
        System.out.println(lr);
        System.out.println("Type any to return to the main menu\n");
        Scanner exit = new Scanner(System.in);
        String any = exit.next();


    }

    private static String nominalIndices(String position) {
        String nominalIndices = null;
        if (position.equals(("GK")))
            nominalIndices = "1,2,3";
        else if (position.equals("D"))
            nominalIndices = "1,2,4";
        else if (position.equals("M"))
            nominalIndices = "1,3,4";
        else if (position.equals("S"))
            nominalIndices = "2,3,4";
        return nominalIndices;
    }

    public static void main(String[] args) throws Exception {
        int userChoice;
        Scanner scan = new Scanner(System.in);

        do {

            userChoice = menu();

            switch (userChoice) {
                case -1:
                    break;
                case 1:
                    System.out.println("What position would you like to predict (GK, D, M or S)?\n");
                    String position = scan.next();
                    position = position.toUpperCase();
                    String rep;
                    if (position.equals("GK") || position.equals("D") || position.equals("M") || position.equals("S")) {
                        buildLinearModel(nominalIndices(position), position);
                    } else {
                        System.out.println("Please introduce a valid position or enter exit to skip (GK, D, M or S)?\n");
                        String next = scan.next();
                        next = next.toUpperCase();

                        if (next.equals("EXIT")) {
                            break;
                        } else if (next.equals("GK") || next.equals("D") || next.equals("M") || next.equals("S"))
                            buildLinearModel(nominalIndices(next), next);
                        else {
                            do {
                                System.out.println("Please introduce a valid position or enter exit to skip (GK, D, M or S)?\n");
                                rep = scan.next();
                                rep = rep.toUpperCase();

                                if (rep.equals("EXIT"))
                                    break;
                                buildLinearModel(nominalIndices(rep), rep);
                            }
                            while (!rep.equals("GK") || !rep.equals("D") || !rep.equals("M") || !rep.equals("S"));
                        }
                    }
                    break;
                case 2:
                    System.out.println("Option 2");
                    break;
                case 3:
                    System.out.println("Option 3");
                    break;
                default:
                    if (userChoice != 4)
                        System.out.println("This option is not available");
            }
        }
        while (userChoice != 4);
    }

}
