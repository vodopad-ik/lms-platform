package me.learning.lmsplatform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RaceConditionDemoServiceTest {

  private RaceConditionDemoService raceConditionDemoService;
  private CounterService counterService;

  @BeforeEach
  void setUp() {
    counterService = new CounterService();
    raceConditionDemoService = new RaceConditionDemoService(counterService);
  }

  @Test
  void run_withDefaultParameters_shouldCompleteSuccessfully() {
    RaceConditionResult result = raceConditionDemoService.run(10, 100);

    assertEquals(10, result.threads());
    assertEquals(100, result.incrementsPerThread());
    assertEquals(1000, result.expected());
    assertEquals(result.expected(), result.synchronizedActual());
    assertEquals(result.expected(), result.atomicActual());
  }

  @Test
  void run_withSingleThread_shouldIncrementCorrectly() {
    RaceConditionResult result = raceConditionDemoService.run(1, 50);

    assertEquals(1, result.threads());
    assertEquals(50, result.incrementsPerThread());
    assertEquals(50, result.expected());
    assertEquals(50, result.unsafeActual());
    assertEquals(50, result.synchronizedActual());
    assertEquals(50, result.atomicActual());
  }

  @Test
  void run_withZeroOrNegativeParameters_shouldUseMinimumValues() {
    RaceConditionResult result = raceConditionDemoService.run(0, 0);

    assertEquals(1, result.threads());
    assertEquals(1, result.incrementsPerThread());
    assertEquals(1, result.expected());
  }

  @Test
  void run_shouldResetCountersBeforeExecution() {
    counterService.increment(CounterType.UNSAFE);
    counterService.increment(CounterType.SYNCHRONIZED);
    counterService.increment(CounterType.ATOMIC);

    RaceConditionResult result = raceConditionDemoService.run(5, 10);

    assertEquals(50, result.expected());
    assertEquals(result.expected(), result.synchronizedActual());
    assertEquals(result.expected(), result.atomicActual());
  }

  @Test
  void run_withMultipleThreads_shouldDemonstrateRaceCondition() {
    RaceConditionResult result = raceConditionDemoService.run(50, 1000);

    assertEquals(50, result.threads());
    assertEquals(1000, result.incrementsPerThread());
    assertEquals(50000, result.expected());

    assertEquals(result.expected(), result.synchronizedActual());
    assertEquals(result.expected(), result.atomicActual());

    assertTrue(result.unsafeActual() <= result.expected(),
        "Unsafe counter should be less than or equal to expected due to race conditions");
  }
}
