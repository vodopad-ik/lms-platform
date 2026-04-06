package me.learning.lmsplatform.service;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class CounterService {

  private int unsafeCounter;
  private int synchronizedCounter;
  private final AtomicInteger atomicCounter = new AtomicInteger(0);

  public int increment(CounterType type) {
    return switch (type) {
      case UNSAFE -> ++unsafeCounter;
      case SYNCHRONIZED -> incrementSynchronized();
      case ATOMIC -> atomicCounter.incrementAndGet();
    };
  }

  public int get(CounterType type) {
    return switch (type) {
      case UNSAFE -> unsafeCounter;
      case SYNCHRONIZED -> getSynchronized();
      case ATOMIC -> atomicCounter.get();
    };
  }

  public void resetAll() {
    unsafeCounter = 0;
    synchronized (this) {
      synchronizedCounter = 0;
    }
    atomicCounter.set(0);
  }

  private synchronized int incrementSynchronized() {
    synchronizedCounter++;
    return synchronizedCounter;
  }

  private synchronized int getSynchronized() {
    return synchronizedCounter;
  }
}
