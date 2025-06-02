package com.example.ecommerce.controller;


import com.example.ecommerce.service.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@AllArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;


    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getDashboardStats() {
        Map<String, Long> stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statsSaller/{seller_id}")
    public ResponseEntity<Map<String, Long>> getDashboardStatsSaller(@PathVariable Integer seller_id) {
        Map<String, Long> stats = dashboardService.getDashboardStatsSaller(seller_id);
        return ResponseEntity.ok(stats);
    }


}
