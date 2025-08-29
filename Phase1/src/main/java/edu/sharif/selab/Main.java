package edu.sharif.selab;

import edu.sharif.selab.core.SmartBuildingSystem;
import edu.sharif.selab.state.ActiveState;
import edu.sharif.selab.state.EcoState;
import edu.sharif.selab.state.ShutdownState;
import edu.sharif.selab.strategy.GreenTariff;
import edu.sharif.selab.strategy.PeakTariff;
import edu.sharif.selab.strategy.StandardTariff;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        SmartBuildingSystem sys = SmartBuildingSystem.defaultSystem();
        Scanner sc = new Scanner(System.in);

        System.out.println("Staring System...");
        while (true) {
            System.out.println("\n---- Menu ----");
            System.out.println("1) View current energy system status");
            System.out.println("2) Calculate cost for consumption amount (with current policy)");
            System.out.println("3) Simulate consumption (raw units -> effective units and cost)");
            System.out.println("4) [Admin] Change cost calculation policy");
            System.out.println("5) [Admin] Change system state");
            System.out.println("0) Exit");
            System.out.println("Choice: ");


            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.println(sys.statusLine());
                }
                case "2" -> {
                    System.out.print("Number of consumption units (integer): ");
                    long units = readLong(sc);
                    long cost = sys.simulateCost(units);
                    System.out.println("Total cost: " + cost + " Toman");
                }
                case "3" -> {
                    System.out.print("Number of consumption units (raw): ");
                    long units = readLong(sc);
                    long adjusted = sys.adjustedUnits(units);
                    long cost = sys.simulateCost(units);
                    System.out.println("Effective units: " + adjusted);
                    System.out.println("Cost: " + cost + " Toman");
                }
                case "4" -> {
                    changeTariff(sc, sys);
                }
                case "5" -> {
                    changeState(sc, sys);
                }
                case "0" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option!");
            }

        }
    }

    private static void changeTariff(Scanner sc, SmartBuildingSystem sys) {
        System.out.println("Tariffs:");
        System.out.println("1) Standard (500/unit)");
        System.out.println("2) Peak Hours (1000/unit)");
        System.out.println("3) Green Mode (300/unit)");
        System.out.print("Choice: ");
        String t = sc.nextLine().trim();
        switch (t) {
            case "1" -> sys.setTariff(new StandardTariff());
            case "2" -> sys.setTariff(new PeakTariff());
            case "3" -> sys.setTariff(new GreenTariff());
            default -> System.out.println("Invalid option!");
        }
        System.out.println("Current tariff: " + sys.getTariff().name());
    }


    private static void changeState(Scanner sc, SmartBuildingSystem sys) {
        System.out.println("States:");
        System.out.println("1) Active");
        System.out.println("2) Eco Mode");
        System.out.println("3) Shutdown");
        System.out.print("Choice: ");
        String s = sc.nextLine().trim();
        switch (s) {
            case "1" -> sys.setState(new ActiveState());
            case "2" -> sys.setState(new EcoState());
            case "3" -> sys.setState(new ShutdownState());
            default -> System.out.println("Invalid option!");
        }
        System.out.println(sys.statusLine());
    }


    private static long readLong(Scanner sc) {
        while (true) {
            try {
                long v = Long.parseLong(sc.nextLine().trim());
                if (v < 0) {
                    System.out.print("Must be non-negative. Try again: ");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Try again: ");
            }
        }
    }

}
