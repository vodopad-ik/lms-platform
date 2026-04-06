package me.learning.lmsplatform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CounterServiceTest {

  private CounterService counterService;

  @BeforeEach
  void setUp() {
    counterService = new CounterService();
  }

  @Test
  void incrementUnsafe_shouldIncrementCounter() {
    int result1 = counterService.increment(CounterType.UNSAFE);
    int result2 = counterService.increment(CounterType.UNSAFE);

    assertEquals(1, result1);
    assertEquals(2, result2);
    assertEquals(2, counterService.get(CounterType.UNSAFE));
  }

  @Test
  void incrementSynchronized_shouldIncrementCounter() {
    int result1 = counterService.increment(CounterType.SYNCHRONIZED);
    int result2 = counterService.increment(CounterType.SYNCHRONIZED);

    assertEquals(1, result1);
    assertEquals(2, result2);
    assertEquals(2, counterService.get(CounterType.SYNCHRONIZED));
  }

  @Test
  void incrementAtomic_shouldIncrementCounter() {
    int result1 = counterService.increment(CounterType.ATOMIC);
    int result2 = counterService.increment(CounterType.ATOMIC);

    assertEquals(1, result1);
    assertEquals(2, result2);
    assertEquals(2, counterService.get(CounterType.ATOMIC));
  }

  @Test
  void resetAll_shouldResetAllCountersToZero() {
    counterService.increment(CounterType.UNSAFE);
    counterService.increment(CounterType.SYNCHRONIZED);
    counterService.increment(CounterType.ATOMIC);

    counterService.resetAll();

    assertEquals(0, counterService.get(CounterType.UNSAFE));
    assertEquals(0, counterService.get(CounterType.SYNCHRONIZED));
    assertEquals(0, counterService.get(CounterType.ATOMIC));
  }

  @Test
  void get_shouldReturnCurrentCounterValue() {
    assertEquals(0, counterService.get(CounterType.UNSAFE));
    assertEquals(0, counterService.get(CounterType.SYNCHRONIZED));
    assertEquals(0, counterService.get(CounterType.ATOMIC));

    counterService.increment(CounterType.UNSAFE);
    counterService.increment(CounterType.SYNCHRONIZED);
    counterService.increment(CounterType.ATOMIC);

    assertEquals(1, counterService.get(CounterType.UNSAFE));
    assertEquals(1, counterService.get(CounterType.SYNCHRONIZED));
    assertEquals(1, counterService.get(CounterType.ATOMIC));
  }

  @Test
  void multipleIncrements_shouldWorkCorrectly() {
    for (int i = 0; i < 100; i++) {
      counterService.increment(CounterType.ATOMIC);
    }

    assertEquals(100, counterService.get(CounterType.ATOMIC));
  }
}
