package me.learning.lmsplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.service.TransactionTestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Transaction tests", description = "Endpoints for transaction behavior checks")
public class TransactionTestController {

    private final TransactionTestService transactionTestService;

    @PostMapping("/no-transaction")
    @Operation(summary = "Run save scenario without @Transactional",
        operationId = "transactionWithoutTransactional")
    public ResponseEntity<Map<String, String>> testNoTransaction() {
        transactionTestService.saveMultipleWithoutTransaction();
        return ResponseEntity.ok(Map.of(
            "message", "Success. Check DB - first record should be saved on failure."));
    }

    @PostMapping("/with-transaction")
    @Operation(summary = "Run save scenario with @Transactional",
        operationId = "transactionWithTransactional")
    public ResponseEntity<Map<String, String>> testWithTransaction() {
        transactionTestService.saveMultipleWithTransaction();
        return ResponseEntity.ok(Map.of(
            "message", "Success. Check DB - all records should be saved."));
    }
}
