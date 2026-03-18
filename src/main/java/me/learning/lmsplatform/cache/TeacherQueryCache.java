package me.learning.lmsplatform.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import me.learning.lmsplatform.dto.TeacherDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TeacherQueryCache {

  private final Map<TeacherQueryCacheKey, Page<TeacherDto>> index = new HashMap<>();

  public Page<TeacherDto> getOrLoad(
      TeacherQueryCacheKey key,
      Supplier<Page<TeacherDto>> loader) {
    synchronized (index) {
      Page<TeacherDto> cached = index.get(key);
      if (cached != null) {
        log.info("CACHE_HIT entity=Teacher keyHash={}", key.hashCode());
        return cached;
      }
    }
    log.info("CACHE_MISS entity=Teacher keyHash={}", key.hashCode());
    Page<TeacherDto> loaded = loader.get();
    synchronized (index) {
      index.put(key, loaded);
    }
    return loaded;
  }

  public void invalidateAll() {
    synchronized (index) {
      log.info("CACHE_INVALIDATE entity=Teacher size={}", index.size());
      index.clear();
    }
  }
}
