package me.learning.lmsplatform.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import me.learning.lmsplatform.dto.LessonDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LessonQueryCache {

  private final Map<LessonQueryCacheKey, Page<LessonDto>> index = new HashMap<>();

  public Page<LessonDto> getOrLoad(
      LessonQueryCacheKey key,
      Supplier<Page<LessonDto>> loader) {
    synchronized (index) {
      Page<LessonDto> cached = index.get(key);
      if (cached != null) {
        log.info("CACHE_HIT entity=Lesson keyHash={}", key.hashCode());
        return cached;
      }
    }
    log.info("CACHE_MISS entity=Lesson keyHash={}", key.hashCode());
    Page<LessonDto> loaded = loader.get();
    synchronized (index) {
      index.put(key, loaded);
    }
    return loaded;
  }

  public void invalidateAll() {
    synchronized (index) {
      log.info("CACHE_INVALIDATE entity=Lesson size={}", index.size());
      index.clear();
    }
  }
}
