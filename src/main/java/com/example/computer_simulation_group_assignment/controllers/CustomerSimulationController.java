package com.example.computer_simulation_group_assignment.controllers;

import com.example.computer_simulation_group_assignment.services.CustomerSimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.lang.Integer.parseInt;

@RestController
@RequestMapping("/simulate")
@RequiredArgsConstructor
public class CustomerSimulationController {
    private final CustomerSimulationService customerSimulationService;

    @GetMapping
    public List<String> simulateQueueStatistics() {
        return customerSimulationService.simulateCustomerArrival();
    }
}
