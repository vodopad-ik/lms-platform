package me.learning.lmsplatform.service;

import java.time.Instant;

public record AsyncTaskInfo(
    String taskId,
    AsyncTaskStatus status,
    int progressPercent,
    Instant createdAt,
    Instant startedAt,
    Instant finishedAt,
    String result,
    String error
) {
}
