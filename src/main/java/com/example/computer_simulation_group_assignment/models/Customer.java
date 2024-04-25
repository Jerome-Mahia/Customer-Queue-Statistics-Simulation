package com.example.computer_simulation_group_assignment.models;

public class Customer {
    public static int nextCustomerNumber = 1; // Static variable to keep track of the next customer number
    public int customerNumber; // Each customer will have a unique customer number
    public double arrivalTime;
    public double serviceTime;
    public double serviceStartTime;
    public double serviceEndTime;
    public double waitingTime;
    public double timeInSystem;

    // Constructor to assign a unique customer number
    public Customer() {
        this.customerNumber = nextCustomerNumber++;
    }
}
