package me.learning.lmsplatform.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import me.learning.lmsplatform.dto.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CategoryQueryCache {

  private final Map<CategoryQueryCacheKey, Page<CategoryDto>> index = new HashMap<>();

  public Page<CategoryDto> getOrLoad(
      CategoryQueryCacheKey key,
      Supplier<Page<CategoryDto>> loader) {
    synchronized (index) {
      Page<CategoryDto> cached = index.get(key);
      if (cached != null) {
        log.info("CACHE_HIT entity=Category keyHash={}", key.hashCode());
        return cached;
      }
    }
    log.info("CACHE_MISS entity=Category keyHash={}", key.hashCode());
    Page<CategoryDto> loaded = loader.get();
    synchronized (index) {
      index.put(key, loaded);
    }
    return loaded;
  }

  public void invalidateAll() {
    synchronized (index) {
      log.info("CACHE_INVALIDATE entity=Category size={}", index.size());
      index.clear();
    }
  }
}

