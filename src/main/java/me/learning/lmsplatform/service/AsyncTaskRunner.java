  package me.learning.lmsplatform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsyncTaskRunner {

  private final StatisticsService statisticsService;
  private final ObjectMapper objectMapper;

  @Async("asyncTaskExecutor")
  public CompletableFuture<Void> run(String taskId, int steps, long delayMs,
                                     AsyncTaskService taskService) {
    Instant started = Instant.now();
    taskService.update(taskId, AsyncTaskStatus.PROCESSING, 0, started, null, null);

    try {
      taskService.update(taskId, AsyncTaskStatus.PROCESSING, 50, started, null, null);

      StatisticsService.LmsStatistics stats = statisticsService.generateStatistics();
      String resultJson = objectMapper.writeValueAsString(stats);

      Instant finished = Instant.now();
      taskService.update(taskId, AsyncTaskStatus.SUCCESS, 100, started, finished, resultJson);
      return CompletableFuture.completedFuture(null);
    } catch (Exception e) {
      Instant finished = Instant.now();
      taskService.update(taskId, AsyncTaskStatus.FAILED, 0, started, finished, e.getMessage());
      return CompletableFuture.failedFuture(e);
    }
  }
}
