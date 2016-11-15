package com.weka.price.cities;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.io.FileReader;

import weka.classifiers.functions.LinearRegression;

import weka.core.SystemInfo;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemoveWithValues;
import weka.gui.treevisualizer.*;


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
        double [] lmCoeffs = lr.coefficients();
        System.out.println("\nDo you want to calculate the price of the player? (Y/any)\n");
        Scanner exit = new Scanner(System.in);
        String any = exit.next();
        any = any.toUpperCase();
        double finalPrice;
        if(any.equals("Y")){
            if (position.equals("GK")){
                double mins, redCards, pass, aerialsWon, MoTm;
                Scanner inputs = new Scanner(System.in);
                System.out.println("\nIntroduce the minutes\n");
                mins = inputs.nextDouble();
                System.out.println("\nIntroduce the red cards\n");
                redCards = inputs.nextDouble();
                System.out.println("\nIntroduce the passes\n");
                pass = inputs.nextDouble();
                System.out.println("\nIntroduce the aerials won\n");
                aerialsWon = inputs.nextDouble();
                System.out.println("\nIntroduce the number of Man of the match\n");
                MoTm = inputs.nextDouble();
                finalPrice = (mins * lmCoeffs[5])+ (redCards * lmCoeffs[8])+(pass * lmCoeffs[10])+(aerialsWon * lmCoeffs[11])+(MoTm * lmCoeffs[12]) + lmCoeffs[15];
                System.out.println("\nFinal price: "+Math.abs(finalPrice));

            }
            else if (position.equals("D")){
                double age, apps, assists, redCard, passes, rating;
                Scanner inputs = new Scanner(System.in);
                System.out.println("\nIntroduce the Age\n");
                age = inputs.nextDouble();
                System.out.println("\nIntroduce the apps\n");
                apps = inputs.nextDouble();
                System.out.println("\nIntroduce the assists\n");
                assists = inputs.nextDouble();
                System.out.println("\nIntroduce the red cards \n");
                redCard = inputs.nextDouble();
                System.out.println("\nIntroduce the passes\n");
                passes = inputs.nextDouble();
                System.out.println("\nIntroduce the rating\n");
                rating = inputs.nextDouble();
                finalPrice = (age * lmCoeffs[0])+ (apps * lmCoeffs[4])+(assists * lmCoeffs[7])+(redCard * lmCoeffs[9])+(passes * lmCoeffs[11])+(rating * lmCoeffs[13]) + lmCoeffs[15];
                System.out.println("\nFinal price: "+Math.abs(finalPrice));

            }
            else if (position.equals("M")){
                double ageM, appsM, minsM, AssistsM, yellowM, passM, ratingM;
                Scanner inputs = new Scanner(System.in);
                System.out.println("\nIntroduce the age\n");
                ageM = inputs.nextDouble();
                System.out.println("\nIntroduce the apps\n");
                appsM = inputs.nextDouble();
                System.out.println("\nIntroduce the minutes\n");
                minsM = inputs.nextDouble();
                System.out.println("\nIntroduce the assists\n");
                AssistsM = inputs.nextDouble();
                System.out.println("\nIntroduce the yellow cards\n");
                yellowM = inputs.nextDouble();
                System.out.println("\nIntroduce the passes \n");
                passM = inputs.nextDouble();
                System.out.println("\nIntroduce the rating\n");
                ratingM = inputs.nextDouble();
                finalPrice = (ageM * lmCoeffs[0])+ (appsM * lmCoeffs[4])+(minsM * lmCoeffs[5])+(AssistsM * lmCoeffs[7])+(yellowM * lmCoeffs[8])+(passM * lmCoeffs[11])+(ratingM * lmCoeffs[14]) + lmCoeffs[16];
                System.out.println("\nFinal price: "+Math.abs(finalPrice));

            }
            else if (position.equals("S")){
                double ageS, appsS, minsS, goals, assistsS, passS, motmS, ratingS;
                Scanner inputs = new Scanner(System.in);
                System.out.println("\nIntroduce the age\n");
                ageS = inputs.nextDouble();
                System.out.println("\nIntroduce the apps\n");
                appsS = inputs.nextDouble();
                System.out.println("\nIntroduce the minutes\n");
                minsS = inputs.nextDouble();
                System.out.println("\nIntroduce the goals\n");
                goals = inputs.nextDouble();
                System.out.println("\nIntroduce the assists\n");
                assistsS = inputs.nextDouble();
                System.out.println("\nIntroduce the passes\n");
                passS = inputs.nextDouble();
                System.out.println("\nIntroduce the Man of the matches\n");
                motmS = inputs.nextDouble();
                System.out.println("\nIntroduce the rating\n");
                ratingS = inputs.nextDouble();
                finalPrice = (ageS * lmCoeffs[0])+ (appsS * lmCoeffs[4])+(minsS * lmCoeffs[5])+(goals * lmCoeffs[6])+(assistsS * lmCoeffs[7])+(passS * lmCoeffs[11])+(motmS * lmCoeffs[12])+(ratingS * lmCoeffs[13]) + lmCoeffs[15];
                System.out.println("\nFinal price: "+Math.abs(finalPrice));

            }
        }
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

    private static void findTreePath(Node node, double shots, double goals, double rating, double mins, double pass){
        Edge edgeToChild;
        int i = 0;
        boolean check=false;

        if (node.getChild(0)==null){
            System.out.println("You need the position: " + node.getLabel());
            check=true;
        }
        if(check==false){
            while((edgeToChild=node.getChild(i))!=null){
                if(node.getLabel().equals("Shots")){
                    String[] partes = edgeToChild.getLine(0).split(" ");
                    String[] partes2 = partes[1].split("\\(");
                    if (partes2[1].indexOf('i') >= 0) {
                        String[] particion = partes2[1].split("-");
                        if(particion.length==3){
                            String[] particion2 = particion[2].split("\\]");
                            Double parte = Double.parseDouble(particion2[0]);
                            if(shots<parte){
                                node = edgeToChild.getTarget();
                                i=-1;
                            }
                        }else{
                            Double parte = Double.parseDouble(particion[0]);
                            if(shots>parte){
                                node = edgeToChild.getTarget();
                                i=-1;
                            }
                        }
                    } else {
                        String[] partes3 = partes2[1].split("-");
                        double part = Double.parseDouble(partes3[0]);
                        String[] partes4 = partes3[1].split("\\]'");
                        double part2 = Double.parseDouble(partes4[0]);
                        if (part < shots && shots <= part2) {
                            node = edgeToChild.getTarget();
                            i=-1;
                        }
                    }
                }
                else if(node.getLabel().equals("Goals")) {
                    String[] partes = edgeToChild.getLine(0).split(" ");
                    String[] partes2 = partes[1].split("\\(");
                    if (partes2[1].indexOf('i') >= 0) {
                        String[] particion = partes2[1].split("-");
                        if(particion.length==3){
                            String[] particion2 = particion[2].split("\\]");
                            Double parte = Double.parseDouble(particion2[0]);
                            if(goals<parte){
                                node = edgeToChild.getTarget();
                                i=-1;
                            }
                        }else{
                            Double parte = Double.parseDouble(particion[0]);
                            if(goals>parte){
                                node = edgeToChild.getTarget();
                                i=-1;
                            }
                        }
                    } else {
                        String[] partes3 = partes2[1].split("-");
                        double part = Double.parseDouble(partes3[0]);
                        String[] partes4 = partes3[1].split("\\]'");
                        double part2 = Double.parseDouble(partes4[0]);
                        if (part < goals && goals <= part2) {
                            node = edgeToChild.getTarget();
                            i=-1;
                        }
                    }
                }
                else if(node.getLabel().equals("Rating")){
                    String[] partes = edgeToChild.getLine(0).split(" ");
                    String[] partes2 = partes[1].split("\\(");
                    if (partes2[1].indexOf('i') >= 0) {
                        String[] particion = partes2[1].split("-");
                        if(particion.length==3){
                            String[] particion2 = particion[2].split("\\]");
                            Double parte = Double.parseDouble(particion2[0]);
                            if(rating<parte){
                                node = edgeToChild.getTarget();
                                i=-1;
                            }
                        }else{
                            Double parte = Double.parseDouble(particion[0]);
                            if(rating>parte){
                                node = edgeToChild.getTarget();
                                i=-1;
                            }
                        }
                    } else {
                        String[] partes3 = partes2[1].split("-");
                        double part = Double.parseDouble(partes3[0]);
                        String[] partes4 = partes3[1].split("\\]'");
                        double part2 = Double.parseDouble(partes4[0]);
                        if (part < rating && rating <= part2) {
                            node = edgeToChild.getTarget();
                            i=-1;
                        }
                    }
                }
                else if(node.getLabel().equals("Pass")){
                    String[] partes = edgeToChild.getLine(0).split(" ");
                    String[] partes2 = partes[1].split("\\(");
                    if (partes2[1].indexOf('i') >= 0) {
                        String[] particion = partes2[1].split("-");
                        if(particion.length==3){
                            String[] particion2 = particion[2].split("\\]");
                            Double parte = Double.parseDouble(particion2[0]);
                            if(pass<parte){
                                node = edgeToChild.getTarget();
                                i=-1;
                            }
                        }else{
                            Double parte = Double.parseDouble(particion[0]);
                            if(pass>parte){
                                node = edgeToChild.getTarget();
                                i=-1;
                            }
                        }
                    } else {
                        String[] partes3 = partes2[1].split("-");
                        double part = Double.parseDouble(partes3[0]);
                        String[] partes4 = partes3[1].split("\\]'");
                        double part2 = Double.parseDouble(partes4[0]);
                        if (pass < goals && pass <= part2) {
                            node = edgeToChild.getTarget();
                            i=-1;
                        }
                    }
                }
                else if (node.getLabel().equals("Mins")){
                    String[] partes = edgeToChild.getLine(0).split(" ");
                    String[] partes2 = partes[1].split("\\(");
                    if (partes2[1].indexOf('i') >= 0) {
                        String[] particion = partes2[1].split("-");
                        if(particion.length==3){
                            String[] particion2 = particion[2].split("\\]");
                            Double parte = Double.parseDouble(particion2[0]);
                            if(mins<parte){
                                node = edgeToChild.getTarget();
                                i=-1;
                            }
                        }else{
                            Double parte = Double.parseDouble(particion[0]);
                            if(mins>parte){
                                node = edgeToChild.getTarget();
                                i=-1;
                            }
                        }
                    } else {
                        String[] partes3 = partes2[1].split("-");
                        double part = Double.parseDouble(partes3[0]);
                        String[] partes4 = partes3[1].split("\\]'");
                        double part2 = Double.parseDouble(partes4[0]);
                        if (part < mins && mins <= part2) {
                            node = edgeToChild.getTarget();
                            i=-1;
                        }
                    }
                }
                i++;
            }
            if(node.getChild(0)==null) {
                String[] label = node.getLabel().split(" ");
                System.out.println("You need the position: " + label[0]);
            }
        }

    }

    private static void J48Tree(double shots, double goals, double rating, double mins, double pass) throws Exception {
        BufferedReader trainingData = new BufferedReader(new FileReader("C:/Users/Admin/Desktop/dataset2.arff"));
        Instances train = new Instances(trainingData);
        trainingData.close();
        train.setClassIndex(train.numAttributes() - 1);

        String[] options = new String[2];
        options[0] = "-R";
        options[1] = "1,2,3,6,15, 16";
        Remove remove = new Remove();
        remove.setOptions(options);
        remove.setInputFormat(train);

        Instances finalData = Filter.useFilter(train, remove);

        String[] optionsDis = new String[6];
        optionsDis[0] = "-B";
        optionsDis[1] = "10";
        optionsDis[2] = "-M";
        optionsDis[3] = "-1.0";
        optionsDis[4] = "-R";
        optionsDis[5] = "first-last";
        Discretize discretize = new Discretize();
        discretize.setOptions(optionsDis);
        discretize.setInputFormat(finalData);

        Instances finalData2 = Filter.useFilter(finalData, discretize);

        J48 j48 = new J48();

        j48.buildClassifier(finalData2);

        Reader treeDot = new StringReader(j48.graph());
        TreeBuild treeBuild = new TreeBuild();
        Node treeRoot = treeBuild.create(treeDot);


        findTreePath(treeRoot, shots, goals, rating, mins, pass);

        System.out.println("Type any to return to the main menu\n");
        Scanner exit = new Scanner(System.in);
        String any = exit.next();
        /*
        // display classifier
        final javax.swing.JFrame jf =
                new javax.swing.JFrame("Weka Classifier Tree Visualizer: J48");
        jf.setSize(500,400);
        jf.getContentPane().setLayout(new BorderLayout());
        TreeVisualizer tv = new TreeVisualizer(null,
                j48.graph(),
                new PlaceNode2());
        jf.getContentPane().add(tv, BorderLayout.CENTER);
        jf.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                jf.dispose();
            }
        });

        jf.setVisible(true);
        tv.fitToScreen();
        */
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
                    boolean error=true;
                    double shoots = 0;
                    double gooal = 0;
                    double miins = 0;
                    double raating = 0;
                    double paases = 0;
                    while (error) {
                        System.out.println("How many goals would you like your player do in the future?\n");
                        String goals = scan.next();
                        goals = goals.toUpperCase();
                        try {
                            double goalsInt = Double.parseDouble(goals);
                            if(0<=goalsInt){
                                error = false;
                                gooal = goalsInt;
                            }
                            else{
                                System.out.println("Write a number bigger than 0");
                            }
                        } catch (Exception e) {
                            System.out.println("Please write a valid number\n");
                            error = true;
                        }
                        if(!error) {
                            error=false;
                        }
                    }
                    error=true;
                    while (error) {
                        System.out.println("How many shots would you like your player do in the future?\n");
                        String shots = scan.next();
                        shots = shots.toUpperCase();
                        try {
                            double shotsInt = Double.parseDouble(shots);
                            if(0<=shotsInt){
                                error = false;
                                shoots=shotsInt;
                            }
                            else{
                                System.out.println("Write a number bigger than 0");
                            }
                        } catch (Exception e) {
                            System.out.println("Please write a valid number\n");
                            error = true;
                        }
                        if(!error) {
                            error=false;
                        }
                    }
                    error=true;
                    while (error) {
                        System.out.println("What is the rating you expect from the player?\n");
                        String rating = scan.next();
                        rating = rating.toUpperCase();
                        try {
                            double ratingInt = Double.parseDouble(rating);
                            if(0<=ratingInt){
                                if(ratingInt<=10) {
                                    error = false;
                                    raating = ratingInt;
                                }
                                else{
                                    System.out.println("Write a number smaller than 10");
                                }
                            }
                            else{
                                System.out.println("Write a number bigger than 0");
                            }
                        } catch (Exception e) {
                            System.out.println("Please write a valid number\n");
                            error = true;
                        }
                        if(!error) {
                            error=false;
                        }
                    }
                    error=true;
                    while (error) {
                        System.out.println("How many minutes would you like your player do in the future?\n");
                        String minutes = scan.next();
                        minutes = minutes.toUpperCase();
                        try {
                            double minInt = Double.parseDouble(minutes);
                            if (0 <= minInt) {
                                error = false;
                                miins = minInt;
                            } else {
                                System.out.println("Write a number bigger than 0");
                            }
                        } catch (Exception e) {
                            System.out.println("Please write a valid number\n");
                            error = true;
                        }
                        if (!error) {
                            error = false;
                        }
                    }
                    error=true;
                    while (error) {
                        System.out.println("How many passes would you like your player do in the future?\n");
                        String passes = scan.next();
                        passes = passes.toUpperCase();
                        try {
                            double passInt = Double.parseDouble(passes);
                            if (0 <= passInt) {
                                error = false;
                                paases = passInt;
                            } else {
                                System.out.println("Write a number bigger than 0");
                            }
                        } catch (Exception e) {
                            System.out.println("Please write a valid number\n");
                            error = true;
                        }
                        if (!error) {
                            error = false;
                        }
                    }
                    J48Tree(shoots, gooal, raating, miins,paases);                    break;
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
