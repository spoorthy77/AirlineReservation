package com.mycompany.airlinereservation;

public class SeederRunner {
    public static void main(String[] args) {
        System.out.println("Running DataSeeder...");
        DataSeeder.seedAdmin();
        System.out.println("Done.");
    }
}
