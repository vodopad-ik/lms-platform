package me.learning.lmsplatform.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import me.learning.lmsplatform.dto.CourseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CourseQueryCache {

  private final Map<CourseQueryCacheKey, Page<CourseDto>> index = new HashMap<>();

  public Page<CourseDto> getOrLoad(CourseQueryCacheKey key, Supplier<Page<CourseDto>> loader) {
    synchronized (index) {
      Page<CourseDto> cached = index.get(key);
      if (cached != null) {
        log.info("CACHE_HIT entity=Course keyHash={}", key.hashCode());
        return cached;
      }
    }
    log.info("CACHE_MISS entity=Course keyHash={}", key.hashCode());
    Page<CourseDto> loaded = loader.get();
    synchronized (index) {
      index.put(key, loaded);
    }
    return loaded;
  }

  public void invalidateAll() {
    synchronized (index) {
      log.info("CACHE_INVALIDATE entity=Course size={}", index.size());
      index.clear();
    }
  }
}
