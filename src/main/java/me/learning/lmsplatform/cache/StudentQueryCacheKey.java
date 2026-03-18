package me.learning.lmsplatform.cache;

import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Pageable;

public final class StudentQueryCacheKey {

  private final QueryMode queryMode;
  private final String nameFilter;
  private final String emailFilter;
  private final String courseTitle;
  private final int page;
  private final int size;
  private final List<String> sortOrders;

  private StudentQueryCacheKey(
      QueryMode queryMode,
      String nameFilter,
      String emailFilter,
      String courseTitle,
      int page,
      int size,
      List<String> sortOrders) {
    this.queryMode = Objects.requireNonNull(queryMode, "queryMode");
    this.nameFilter = nameFilter;
    this.emailFilter = emailFilter;
    this.courseTitle = courseTitle;
    this.page = page;
    this.size = size;
    this.sortOrders = sortOrders == null ? List.of() : List.copyOf(sortOrders);
  }

  public static StudentQueryCacheKey from(
      QueryMode queryMode,
      String nameFilter,
      String emailFilter,
      String courseTitle,
      Pageable pageable) {
    int page = pageable == null ? 0 : pageable.getPageNumber();
    int size = pageable == null ? 20 : pageable.getPageSize();
    return new StudentQueryCacheKey(
        queryMode,
        nameFilter,
        emailFilter,
        courseTitle,
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
    if (!(o instanceof StudentQueryCacheKey that)) {
      return false;
    }
    return page == that.page
        && size == that.size
        && queryMode == that.queryMode
        && Objects.equals(nameFilter, that.nameFilter)
        && Objects.equals(emailFilter, that.emailFilter)
        && Objects.equals(courseTitle, that.courseTitle)
        && Objects.equals(sortOrders, that.sortOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryMode, nameFilter, emailFilter, courseTitle, page, size, sortOrders);
  }
}
