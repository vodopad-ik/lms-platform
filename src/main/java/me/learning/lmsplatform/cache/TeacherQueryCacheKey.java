package me.learning.lmsplatform.cache;

import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Pageable;

public final class TeacherQueryCacheKey {

  private final QueryMode queryMode;
  private final String nameFilter;
  private final String departmentFilter;
  private final String courseCategory;
  private final int page;
  private final int size;
  private final List<String> sortOrders;

  private TeacherQueryCacheKey(
      QueryMode queryMode,
      String nameFilter,
      String departmentFilter,
      String courseCategory,
      int page,
      int size,
      List<String> sortOrders) {
    this.queryMode = Objects.requireNonNull(queryMode, "queryMode");
    this.nameFilter = nameFilter;
    this.departmentFilter = departmentFilter;
    this.courseCategory = courseCategory;
    this.page = page;
    this.size = size;
    this.sortOrders = sortOrders == null ? List.of() : List.copyOf(sortOrders);
  }

  public static TeacherQueryCacheKey from(
      QueryMode queryMode,
      String nameFilter,
      String departmentFilter,
      String courseCategory,
      Pageable pageable) {
    int page = pageable == null ? 0 : pageable.getPageNumber();
    int size = pageable == null ? 20 : pageable.getPageSize();
    return new TeacherQueryCacheKey(
        queryMode,
        nameFilter,
        departmentFilter,
        courseCategory,
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
    if (!(o instanceof TeacherQueryCacheKey that)) {
      return false;
    }
    return page == that.page
        && size == that.size
        && queryMode == that.queryMode
        && Objects.equals(nameFilter, that.nameFilter)
        && Objects.equals(departmentFilter, that.departmentFilter)
        && Objects.equals(courseCategory, that.courseCategory)
        && Objects.equals(sortOrders, that.sortOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        queryMode,
        nameFilter,
        departmentFilter,
        courseCategory,
        page,
        size,
        sortOrders);
  }
}
