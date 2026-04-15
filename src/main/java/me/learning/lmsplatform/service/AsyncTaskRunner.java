  package me.learning.lmsplatform.service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AsyncTaskRunner {

  private final StatisticsService statisticsService;

  @Async("asyncTaskExecutor")
  public CompletableFuture<Void> run(String taskId, int steps, long delayMs,
                                     AsyncTaskService taskService) {
    Instant started = Instant.now();
    taskService.update(taskId, AsyncTaskStatus.PROCESSING, 0, started, null, null);

    try {
      int safeSteps = Math.max(1, steps);
      long safeDelay = Math.max(100L, delayMs);

      for (int i = 1; i <= safeSteps; i++) {
        Thread.sleep(safeDelay);
        int progress = (i * 100) / safeSteps;
        taskService.update(taskId, AsyncTaskStatus.PROCESSING, progress, started, null, null);
      }

      StatisticsService.LmsStatistics stats = statisticsService.generateStatistics();
      String result = stats.toString();

      Instant finished = Instant.now();
      taskService.update(taskId, AsyncTaskStatus.SUCCESS, 100, started, finished, result);
      return CompletableFuture.completedFuture(null);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      Instant finished = Instant.now();
      taskService.update(taskId, AsyncTaskStatus.FAILED, 0, started, finished, e.getMessage());
      return CompletableFuture.failedFuture(e);
    } catch (Exception e) {
      Instant finished = Instant.now();
      taskService.update(taskId, AsyncTaskStatus.FAILED, 0, started, finished, e.getMessage());
      return CompletableFuture.failedFuture(e);
    }
  }
}
