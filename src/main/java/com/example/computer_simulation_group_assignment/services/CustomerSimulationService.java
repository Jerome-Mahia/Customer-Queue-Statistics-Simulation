package com.example.computer_simulation_group_assignment.services;

import com.example.computer_simulation_group_assignment.models.Customer;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerSimulationService {
    static Random random = new Random();
    static Queue<Customer> customerQueue = new LinkedList<>(); // Changed to LinkedList for FIFO
    static double clockTime;
    static int numCustomers;
    static int numCustomersInQueue;
    static double totalWaitingTime;
    static double totalServiceTime;
    static double idleTime;
    static double previousIAT;

    public List<String> simulateCustomerArrival(int numCustomersInSystem) {
        resetSimulation(); // Reset simulation parameters before starting
        return runSimulation(numCustomersInSystem); // Start the simulation with specified number of customers
    }

    public void resetSimulation() {
        clockTime = 0.0;
        numCustomers = 0;
        numCustomersInQueue = 0;
        totalWaitingTime = 0.0;
        totalServiceTime = 0.0;
        idleTime = 0.0;
        previousIAT = 0.0;
        customerQueue.clear();
    }

    public List<String> runSimulation(int numCustomersInSystem) {
        List<String> customerDataList = new ArrayList<>();
        while (numCustomers < numCustomersInSystem) { // Simulate until the specified number of customers
            double nextIAT = getRandomExponential(1.93); // Generate random inter-arrival time
            double nextArrivalTime = clockTime + nextIAT; // Add it to the previous arrival time

            idleTime += Math.max(0.0, nextArrivalTime - clockTime);

            if (customerQueue.isEmpty()) {
                clockTime = nextArrivalTime;
            } else {
                numCustomersInQueue++;
            }

            Customer newCustomer = new Customer();
            newCustomer.arrivalTime = clockTime;
            double nextServiceTime = getRandomExponential(1.24); // Replace 1.24 with the mean service time
            newCustomer.serviceTime = nextServiceTime;
            customerQueue.add(newCustomer);
            numCustomers++;

            String customerData = processCustomer(newCustomer);
            if (customerData!= null) {
                customerDataList.add(customerData);
            }

            previousIAT = nextIAT; // Update the previous inter-arrival time
        }
        return customerDataList;
    }

    public String processCustomer(Customer customer) {
        if (!customerQueue.isEmpty()) {
            Customer currentCustomer = customerQueue.poll();
            currentCustomer.serviceStartTime = clockTime;
            double serviceCompletionTime = clockTime + currentCustomer.serviceTime;
            totalServiceTime += currentCustomer.serviceTime;
            currentCustomer.serviceEndTime = serviceCompletionTime;
            currentCustomer.waitingTime = Math.max(0.0, serviceCompletionTime - currentCustomer.arrivalTime);
            currentCustomer.timeInSystem = currentCustomer.waitingTime + currentCustomer.serviceTime;
            totalWaitingTime += currentCustomer.waitingTime;
            clockTime = serviceCompletionTime;

            return printCustomerData(Collections.singletonList(currentCustomer)); // Return individual customer data
        }
        return null;
    }

    public String printCustomerData(List<Customer> customers) {
        StringBuilder stringBuilder = new StringBuilder();
        int numCustomersThatHadToWait = 0;

        for (Customer customer : customers) {
            double idleTimePerCustomer = idleTime / numCustomers;
            int numCustomersInSystem = numCustomers - customerQueue.size();
            int numCustomersInQueue = customerQueue.size();
            double waitingTimeInQueue = totalWaitingTime / numCustomersInSystem;
            double waitingTimeInSystem = totalWaitingTime / numCustomers;
            if (customer.waitingTime > 0) {
                numCustomersThatHadToWait++;
            }

            stringBuilder.append("Customer: ").append(customer.customerNumber)
                    .append(", IAT: ").append(customer.arrivalTime - previousIAT)
                    .append(", Clock Time: ").append(clockTime)
                    .append(", Service Time: ").append(customer.serviceTime)
                    .append(", Service Start Time: ").append(customer.serviceStartTime)
                    .append(", Service End Time: ").append(customer.serviceEndTime)
                    .append(", Number in System: ").append(numCustomersInSystem)
                    .append(", Number in Queue: ").append(numCustomersInQueue)
                    .append(", Waiting Time in Queue: ").append(customer.waitingTime)
                    .append(", Waiting Time in System: ").append(waitingTimeInSystem)
                    .append(", Idle Time: ").append(idleTimePerCustomer)
                    .append("\n");
        }

        // Calculate total statistics
        double totalServiceTime = 0.0;
        int totalCustomersInSystem = 0;
        int totalCustomersInQueue = 0;
        double totalWaitingTimeInQueue = 0.0;
        double totalWaitingTimeInSystem = 0.0;
        double totalIdleTime = 0.0;

        double averageServiceTime = totalServiceTime / numCustomers;
        double averageWaitingTimeInQueue = totalWaitingTimeInQueue / numCustomers;
        double averageWaitingTimeInSystem = totalWaitingTimeInSystem / numCustomers;
        double averageIdleTime = totalIdleTime / numCustomers;
        double probabilityOfWaiting = (double) numCustomersThatHadToWait / numCustomers;

        for (Customer customer : customers) {
            totalServiceTime += customer.serviceTime;
            totalCustomersInSystem++;
            if (customer.waitingTime > 0) {
                totalCustomersInQueue++;
                totalWaitingTimeInQueue += customer.waitingTime;
            }
            totalWaitingTimeInSystem += customer.waitingTime;
            totalIdleTime += idleTime / numCustomers;
        }

        // Append total statistics
        stringBuilder.append("------------------------\n")
                .append("Total: ")
                .append("Service Time: ").append(totalServiceTime).append(", ")
                .append("Number in System: ").append(totalCustomersInSystem).append(", ")
                .append("Number in Queue: ").append(totalCustomersInQueue).append(", ")
                .append("Waiting Time in Queue: ").append(totalWaitingTimeInQueue).append(", ")
                .append("Waiting Time in System: ").append(totalWaitingTimeInSystem).append(", ")
                .append("Idle Time: ").append(totalIdleTime)
                .append("Average Service Time: ").append(averageServiceTime).append(", ")
                .append("Average Waiting Time in Queue: ").append(averageWaitingTimeInQueue).append(", ")
                .append("Average Waiting Time in System: ").append(averageWaitingTimeInSystem).append(", ")
                .append("Average Idle Time: ").append(averageIdleTime).append(", ")
                .append("Probability of Waiting: ").append(probabilityOfWaiting)
                .append("\n");

        return stringBuilder.toString();
    }

    public double getRandomExponential(double mean) {
        return -Math.log(1 - random.nextDouble()) * mean;
    }
}