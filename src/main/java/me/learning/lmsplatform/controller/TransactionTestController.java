package me.learning.lmsplatform.controller;

import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.service.TransactionTestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TransactionTestController {

    private final TransactionTestService transactionTestService;

    @PostMapping("/no-transaction")
    public String testNoTransaction() {
        try {
            transactionTestService.saveMultipleWithoutTransaction();
        } catch (Exception e) {
            return "Error: " + e.getMessage() + " Check DB - first record should be saved!";
        }
        return "Success";
    }

    @PostMapping("/with-transaction")
    public String testWithTransaction() {
        try {
            transactionTestService.saveMultipleWithTransaction();
        } catch (Exception e) {
            return "Error: " + e.getMessage() + " Check DB - nothing should be saved!";
        }
        return "Success";
    }
}
