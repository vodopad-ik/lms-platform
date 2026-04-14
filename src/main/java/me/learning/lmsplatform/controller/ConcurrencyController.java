package me.learning.lmsplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.service.CounterService;
import me.learning.lmsplatform.service.CounterType;
import me.learning.lmsplatform.service.RaceConditionDemoService;
import me.learning.lmsplatform.service.RaceConditionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/concurrency")
@RequiredArgsConstructor
@Tag(name = "Concurrency demos", description = "Race condition and thread-safe counter demos")
public class ConcurrencyController {

  private final CounterService counterService;
  private final RaceConditionDemoService raceConditionDemoService;

  @GetMapping("/counter")
  @Operation(summary = "Get counter value")
  public ResponseEntity<Map<String, Integer>> getCounter(
      @RequestParam(defaultValue = "ATOMIC") CounterType type) {
    return ResponseEntity.ok(Map.of("value", counterService.get(type)));
  }

  @PostMapping("/counter/increment")
  @Operation(summary = "Increment counter")
  public ResponseEntity<Map<String, Integer>> increment(
      @RequestParam(defaultValue = "ATOMIC") CounterType type,
      @RequestParam(defaultValue = "1") int times) {
    int safeTimes = Math.max(1, times);
    int value = 0;
    for (int i = 0; i < safeTimes; i++) {
      value = counterService.increment(type);
    }
    return ResponseEntity.ok(Map.of("value", value));
  }

  @PostMapping("/race-demo")
  @Operation(summary = "Run race condition demo")
  public ResponseEntity<RaceConditionResult> raceDemo(
      @RequestParam(defaultValue = "10") int threads,
      @RequestParam(defaultValue = "100") int incrementsPerThread) {
    System.out.println("Starting race demo: " + threads + " threads, "
        + incrementsPerThread + " increments each");
    RaceConditionResult result = raceConditionDemoService.run(threads, incrementsPerThread);
    System.out.println("Race demo completed: " + result.toString());
    return ResponseEntity.ok(result);
  }
}
