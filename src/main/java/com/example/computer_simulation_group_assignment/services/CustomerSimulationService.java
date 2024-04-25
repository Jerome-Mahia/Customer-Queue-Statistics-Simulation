package com.example.computer_simulation_group_assignment.services;

import com.example.computer_simulation_group_assignment.models.Customer;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerSimulationService {
    static Random random = new Random();
    static Comparator<Customer> customerComparator = Comparator.comparingDouble(customer -> customer.arrivalTime);
    static Queue<Customer> customerQueue = new PriorityQueue<>(customerComparator);
    static double clockTime = 0.0;
    static int numCustomers = 0;
    static int numCustomersInQueue = 0;
    static double totalWaitingTime = 0.0;
    static double totalServiceTime = 0.0;
    static double idleTime = 0.0;

    public List<String> simulateCustomerArrival() {
        List<String> customerDataList = new ArrayList<>();
        while (numCustomers < 10) { // Simulate 10 customers
            double nextArrivalTime = clockTime + getRandomExponential(1.93); // 1.93 is the mean IAT
            double nextServiceTime = getRandomExponential(1.24); // Replace 1.24 with the mean service time

            idleTime += Math.max(0.0, nextArrivalTime - clockTime);

            if (customerQueue.isEmpty()) {
                clockTime = nextArrivalTime;
            } else {
                numCustomersInQueue++;
            }

            Customer newCustomer = new Customer();
            newCustomer.arrivalTime = clockTime;
            newCustomer.serviceTime = nextServiceTime;
            customerQueue.add(newCustomer);
            numCustomers++;

            String customerData = processCustomer(newCustomer);
            if (customerData != null) {
                customerDataList.add(customerData);
            }
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

            return printCustomerData(currentCustomer);
        }
        return null;
    }

    public String printCustomerData(Customer customer) {
        double idleTimePerCustomer = idleTime / numCustomers;
        int numCustomersInSystem = numCustomers - customerQueue.size();
        int numCustomersInQueue = customerQueue.size();
        double waitingTimeInQueue = totalWaitingTime / numCustomersInSystem;
        double waitingTimeInSystem = totalWaitingTime / numCustomers;

        return "Customer: " + customer.customerNumber +
                ", IAT: " + (customer.arrivalTime - clockTime) +
                ", Clock Time: " + clockTime +
                ", Service Time: " + customer.serviceTime +
                ", Service Start Time: " + customer.serviceStartTime +
                ", Service End Time: " + customer.serviceEndTime +
                ", Number in System: " + numCustomersInSystem +
                ", Number in Queue: " + numCustomersInQueue +
                ", Waiting Time in Queue: " + customer.waitingTime +
                ", Waiting Time in System: " + waitingTimeInSystem +
                ", Idle Time: " + idleTimePerCustomer;
    }

    public double getRandomExponential(double mean) {
        return -Math.log(1 - random.nextDouble()) * mean;
    }
}
