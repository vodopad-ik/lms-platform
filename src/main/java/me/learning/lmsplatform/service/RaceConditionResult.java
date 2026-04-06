package me.learning.lmsplatform.service;

public record RaceConditionResult(
    int threads,
    int incrementsPerThread,
    int expected,
    int unsafeActual,
    int synchronizedActual,
    int atomicActual
) {
}
