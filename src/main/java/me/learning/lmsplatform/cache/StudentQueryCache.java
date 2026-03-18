package me.learning.lmsplatform.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import me.learning.lmsplatform.dto.StudentDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StudentQueryCache {

  private final Map<StudentQueryCacheKey, Page<StudentDto>> index = new HashMap<>();

  public Page<StudentDto> getOrLoad(
      StudentQueryCacheKey key,
      Supplier<Page<StudentDto>> loader) {
    synchronized (index) {
      Page<StudentDto> cached = index.get(key);
      if (cached != null) {
        log.info("CACHE_HIT entity=Student keyHash={}", key.hashCode());
        return cached;
      }
    }
    log.info("CACHE_MISS entity=Student keyHash={}", key.hashCode());
    Page<StudentDto> loaded = loader.get();
    synchronized (index) {
      index.put(key, loaded);
    }
    return loaded;
  }

  public void invalidateAll() {
    synchronized (index) {
      log.info("CACHE_INVALIDATE entity=Student size={}", index.size());
      index.clear();
    }
  }
}
