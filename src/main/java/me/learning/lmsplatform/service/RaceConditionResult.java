package me.learning.lmsplatform.service;

public record RaceConditionResult(
    int threads,
    int incrementsPerThread,
    int expected,
    int unsafeActual,
    int synchronizedActual,
    int atomicActual
) {
  @Override
  public String toString() {
    return String.format(
        "RaceConditionResult{threads=%d, increments=%d, expected=%d, "
            + "unsafe=%d, synchronized=%d, atomic=%d}",
        threads, incrementsPerThread, expected, unsafeActual, synchronizedActual, atomicActual
    );
  }
}
