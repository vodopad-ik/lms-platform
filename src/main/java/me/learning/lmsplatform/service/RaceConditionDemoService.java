package me.learning.lmsplatform.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import me.learning.lmsplatform.exception.RaceConditionTimeoutException;
import org.springframework.stereotype.Service;

@Service
public class RaceConditionDemoService {

  private final CounterService counterService;

  public RaceConditionDemoService(CounterService counterService) {
    this.counterService = counterService;
  }

  public RaceConditionResult run(int threads, int incrementsPerThread) {
    int safeThreads = Math.max(1, threads);
    int safeIncrements = Math.max(1, incrementsPerThread);

    counterService.resetAll();

    try (ExecutorService executor = Executors.newFixedThreadPool(safeThreads)) {
      CountDownLatch latch = new CountDownLatch(safeThreads);

      for (int t = 0; t < safeThreads; t++) {
        executor.execute(() -> {
          try {
            for (int i = 0; i < safeIncrements; i++) {
              counterService.increment(CounterType.UNSAFE);
              counterService.increment(CounterType.SYNCHRONIZED);
              counterService.increment(CounterType.ATOMIC);
            }
          } finally {
            latch.countDown();
          }
        });
      }

      boolean completed = false;
      try {
        completed = latch.await(60, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      if (!completed) {
        throw new RaceConditionTimeoutException("Timeout waiting for threads to complete");
      }

      int expected = safeThreads * safeIncrements;
      return new RaceConditionResult(
          safeThreads,
          safeIncrements,
          expected,
          counterService.get(CounterType.UNSAFE),
          counterService.get(CounterType.SYNCHRONIZED),
          counterService.get(CounterType.ATOMIC)
      );
    }
  }
}
