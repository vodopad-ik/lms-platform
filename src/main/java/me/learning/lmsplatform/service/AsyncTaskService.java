package me.learning.lmsplatform.service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class AsyncTaskService {

  private final Map<String, AsyncTaskInfo> tasks = new ConcurrentHashMap<>();
  private final AsyncTaskRunner asyncTaskRunner;

  public AsyncTaskService(AsyncTaskRunner asyncTaskRunner) {
    this.asyncTaskRunner = asyncTaskRunner;
  }

  public String startDemoBusinessOperation(int steps, long delayMs) {
    String taskId = UUID.randomUUID().toString();
    Instant now = Instant.now();

    tasks.put(taskId, new AsyncTaskInfo(
        taskId,
        AsyncTaskStatus.PENDING,
        0,
        now,
        null,
        null,
        null,
        null
    ));

    asyncTaskRunner.run(taskId, steps, delayMs, this);
    return taskId;
  }

  public Optional<AsyncTaskInfo> getTask(String taskId) {
    return Optional.ofNullable(tasks.get(taskId));
  }

  void update(String taskId,
              AsyncTaskStatus status,
              int progressPercent,
              Instant startedAt,
              Instant finishedAt,
              String resultOrError) {
    tasks.computeIfPresent(taskId, (id, existing) -> {
      String result = existing.result();
      String error = existing.error();

      if (status == AsyncTaskStatus.SUCCESS) {
        result = resultOrError;
        error = null;
      }
      if (status == AsyncTaskStatus.FAILED) {
        result = null;
        error = resultOrError;
      }

      return new AsyncTaskInfo(
          id,
          status,
          clamp(progressPercent),
          existing.createdAt(),
          startedAt != null ? startedAt : existing.startedAt(),
          finishedAt != null ? finishedAt : existing.finishedAt(),
          result,
          error
      );
    });
  }

  private int clamp(int progressPercent) {
    if (progressPercent < 0) {
      return 0;
    }
    if (progressPercent > 100) {
      return 100;
    }
    return progressPercent;
  }
}
