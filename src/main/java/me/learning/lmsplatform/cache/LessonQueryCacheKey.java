package me.learning.lmsplatform.cache;

import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Pageable;

public final class LessonQueryCacheKey {

  private final QueryMode queryMode;
  private final Long courseId;
  private final String courseTitle;
  private final String titleFilter;
  private final int page;
  private final int size;
  private final List<String> sortOrders;

  private LessonQueryCacheKey(
      QueryMode queryMode,
      Long courseId,
      String courseTitle,
      String titleFilter,
      int page,
      int size,
      List<String> sortOrders) {
    this.queryMode = Objects.requireNonNull(queryMode, "queryMode");
    this.courseId = courseId;
    this.courseTitle = courseTitle;
    this.titleFilter = titleFilter;
    this.page = page;
    this.size = size;
    this.sortOrders = sortOrders == null ? List.of() : List.copyOf(sortOrders);
  }

  public static LessonQueryCacheKey from(
      QueryMode queryMode,
      Long courseId,
      String courseTitle,
      String titleFilter,
      Pageable pageable) {
    int page = pageable == null ? 0 : pageable.getPageNumber();
    int size = pageable == null ? 20 : pageable.getPageSize();
    return new LessonQueryCacheKey(
        queryMode,
        courseId,
        courseTitle,
        titleFilter,
        page,
        size,
        buildSortOrders(pageable));
  }

  private static List<String> buildSortOrders(Pageable pageable) {
    if (pageable == null) {
      return List.of();
    }
    return pageable.getSort().stream()
        .map(order -> order.getProperty() + ":" + order.getDirection().name())
        .toList();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LessonQueryCacheKey that)) {
      return false;
    }
    return page == that.page
        && size == that.size
        && queryMode == that.queryMode
        && Objects.equals(courseId, that.courseId)
        && Objects.equals(courseTitle, that.courseTitle)
        && Objects.equals(titleFilter, that.titleFilter)
        && Objects.equals(sortOrders, that.sortOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryMode, courseId, courseTitle, titleFilter, page, size, sortOrders);
  }
}
