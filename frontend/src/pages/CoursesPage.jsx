import { useEffect, useMemo, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { BookOpen, Clock, DollarSign, Edit, Filter, GraduationCap, Plus, PlayCircle, Search, SlidersHorizontal, Trash2, Users } from 'lucide-react';
import { courseApi } from '../api/courseApi';
import { categoryApi } from '../api/categoryApi';
import { teacherApi } from '../api/teacherApi';
import { studentApi } from '../api/studentApi';
import { ActionButton, Badge, EmptyState, ErrorState, LoadingState, PageHeader, Pagination, SectionCard, StatCard } from '../components/ui';

const defaultFilter = { department: '', category: '', minPrice: '', maxPrice: '' };

export default function CoursesPage() {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingCourse, setEditingCourse] = useState(null);
  const [filter, setFilter] = useState(defaultFilter);
  const [quickSearch, setQuickSearch] = useState('');
  const [sortBy, setSortBy] = useState('title-asc');
  const [searchParams] = useSearchParams();
  const [teachers, setTeachers] = useState([]);
  const [categories, setCategories] = useState([]);
  const [studentContext, setStudentContext] = useState(null);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(9);

  useEffect(() => {
    fetchCourses();
  }, []);

  useEffect(() => {
    Promise.all([teacherApi.getAll(), categoryApi.getAll()])
      .then(([teachersResponse, categoriesResponse]) => {
        setTeachers(teachersResponse.data ?? []);
        setCategories(categoriesResponse.data ?? []);
      })
      .catch((err) => console.error('Failed to load references', err));
  }, []);

  useEffect(() => {
    const studentId = searchParams.get('studentId');
    if (!studentId) {
      setStudentContext(null);
      return;
    }
    studentApi.getById(studentId)
      .then((response) => setStudentContext(response.data))
      .catch((err) => {
        console.error('Failed to load student context', err);
        setStudentContext(null);
      });
  }, [searchParams]);

  useEffect(() => {
    setPage(1);
  }, [searchParams, quickSearch, sortBy, pageSize]);

  const fetchCourses = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await courseApi.getAll();
      setCourses(response.data ?? []);
    } catch (err) {
      setError('Failed to fetch courses');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilter = async () => {
    try {
      setLoading(true);
      setError('');
      const params = {};
      if (filter.department) params.department = filter.department;
      if (filter.category) params.category = filter.category;
      if (filter.minPrice) params.minPrice = Number(filter.minPrice);
      if (filter.maxPrice) params.maxPrice = Number(filter.maxPrice);

      const response = await courseApi.filter(params);
      setCourses(response.data.content || response.data || []);
    } catch (err) {
      setError('Failed to filter courses');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const resetFilters = () => {
    setFilter(defaultFilter);
    setQuickSearch('');
    setSortBy('title-asc');
    fetchCourses();
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this course?')) return;

    try {
      await courseApi.delete(id);
      setCourses((current) => current.filter((course) => course.id !== id));
    } catch (err) {
      setError('Failed to delete course');
      console.error(err);
    }
  };

  const handleEdit = (course) => {
    setEditingCourse(course);
    setShowModal(true);
  };

  const handleSave = async (courseData) => {
    try {
      if (editingCourse) {
        const response = await courseApi.update(editingCourse.id, courseData);
        setCourses((current) => current.map((course) => (course.id === editingCourse.id ? response.data : course)));
      } else {
        const response = await courseApi.create(courseData);
        setCourses((current) => [response.data, ...current]);
      }
      setShowModal(false);
      setEditingCourse(null);
      setError('');
    } catch (err) {
      setError('Failed to save course');
      console.error(err);
    }
  };

  const contextInfo = useMemo(() => {
    const categoryId = searchParams.get('categoryId');
    const teacherId = searchParams.get('teacherId');
    const studentId = searchParams.get('studentId');

    if (categoryId) {
      const category = categories.find((item) => String(item.id) === String(categoryId));
      return { tone: 'indigo', label: `Category: ${category?.name ?? `#${categoryId}`}` };
    }
    if (teacherId) {
      const teacher = teachers.find((item) => String(item.id) === String(teacherId));
      return { tone: 'blue', label: `Teacher: ${teacher?.name ?? `#${teacherId}`}` };
    }
    if (studentId) {
      return { tone: 'emerald', label: `Student: ${studentContext?.name ?? `#${studentId}`}` };
    }
    return null;
  }, [searchParams, categories, teachers, studentContext]);

  const visibleCourses = useMemo(() => {
    const needle = quickSearch.trim().toLowerCase();
    const categoryId = searchParams.get('categoryId');
    const teacherId = searchParams.get('teacherId');
    const studentId = searchParams.get('studentId');
    const studentCourseIds = studentId && studentContext?.courses
      ? new Set(studentContext.courses.map((course) => String(course.id)))
      : null;

    const searched = courses.filter((course) => {
      if (categoryId && String(course.category?.id) !== String(categoryId)) return false;
      if (teacherId && String(course.teacher?.id) !== String(teacherId)) return false;
      if (studentCourseIds && !studentCourseIds.has(String(course.id))) return false;
      if (!needle) return true;

      return [
        course.title,
        course.description,
        course.teacher?.name,
        course.teacher?.department,
        course.category?.name,
      ].filter(Boolean).some((value) => String(value).toLowerCase().includes(needle));
    });

    const sorted = [...searched].sort((left, right) => {
      switch (sortBy) {
        case 'price-desc':
          return Number(right.price || 0) - Number(left.price || 0);
        case 'price-asc':
          return Number(left.price || 0) - Number(right.price || 0);
        case 'duration-desc':
          return Number(right.durationWeeks || 0) - Number(left.durationWeeks || 0);
        case 'duration-asc':
          return Number(left.durationWeeks || 0) - Number(right.durationWeeks || 0);
        case 'title-desc':
          return String(right.title || '').localeCompare(String(left.title || ''));
        default:
          return String(left.title || '').localeCompare(String(right.title || ''));
      }
    });

    return sorted;
  }, [courses, quickSearch, sortBy, searchParams, studentContext]);

  const pageCount = Math.max(1, Math.ceil(visibleCourses.length / pageSize));
  const paginatedCourses = useMemo(
    () => visibleCourses.slice((page - 1) * pageSize, page * pageSize),
    [visibleCourses, page, pageSize],
  );

  const stats = useMemo(() => {
    const totalValue = visibleCourses.reduce((sum, course) => sum + Number(course.price || 0), 0);
    const avgDuration = visibleCourses.length
      ? Math.round(visibleCourses.reduce((sum, course) => sum + Number(course.durationWeeks || 0), 0) / visibleCourses.length)
      : 0;
    const categorized = visibleCourses.filter((course) => course.category?.name).length;

    return { totalValue, avgDuration, categorized };
  }, [visibleCourses]);

  if (loading) {
    return <LoadingState title="Loading courses" description="Preparing your course catalog and connected metadata." />;
  }

  if (error && !courses.length) {
    return <ErrorState title="Unable to load courses" description={error} onRetry={fetchCourses} />;
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Course management"
        description="Control your catalog, pricing, duration, instructors, categories, and lesson navigation from one workspace."
        actions={(
          <>
            <ActionButton tone="white" onClick={fetchCourses}>Refresh</ActionButton>
            <ActionButton tone="indigo" onClick={() => { setEditingCourse(null); setShowModal(true); }}>
              <Plus className="h-4 w-4" />
              Add course
            </ActionButton>
          </>
        )}
      />

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <StatCard title="Visible courses" value={visibleCourses.length} hint="After active filters and search" icon={BookOpen} tone="indigo" />
        <StatCard title="Catalog value" value={`$${stats.totalValue.toFixed(2)}`} hint="Sum of visible course prices" icon={DollarSign} tone="emerald" />
        <StatCard title="Average duration" value={`${stats.avgDuration} weeks`} hint="Estimated delivery length" icon={Clock} tone="amber" />
        <StatCard title="Categorized" value={stats.categorized} hint="Courses already linked to categories" icon={Filter} tone="blue" />
      </div>

      <SectionCard
        title="Discover and filter"
        subtitle="Combine backend filtering with local search and sorting for faster catalog operations."
        action={contextInfo ? <Badge tone={contextInfo.tone}>{contextInfo.label}</Badge> : null}
      >
        <div className="grid gap-4 lg:grid-cols-[1.5fr_1fr]">
          <div className="space-y-4">
            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
              <label className="rounded-2xl border border-slate-200 px-4 py-3">
                <span className="mb-2 block text-xs font-medium uppercase tracking-wide text-slate-500">Department</span>
                <input
                  type="text"
                  placeholder="e.g. Engineering"
                  value={filter.department}
                  onChange={(event) => setFilter({ ...filter, department: event.target.value })}
                  className="w-full bg-transparent text-sm text-slate-900 outline-none"
                />
              </label>
              <label className="rounded-2xl border border-slate-200 px-4 py-3">
                <span className="mb-2 block text-xs font-medium uppercase tracking-wide text-slate-500">Category</span>
                <input
                  type="text"
                  placeholder="e.g. Development"
                  value={filter.category}
                  onChange={(event) => setFilter({ ...filter, category: event.target.value })}
                  className="w-full bg-transparent text-sm text-slate-900 outline-none"
                />
              </label>
              <label className="rounded-2xl border border-slate-200 px-4 py-3">
                <span className="mb-2 block text-xs font-medium uppercase tracking-wide text-slate-500">Min price</span>
                <input
                  type="number"
                  placeholder="0"
                  value={filter.minPrice}
                  onChange={(event) => setFilter({ ...filter, minPrice: event.target.value })}
                  className="w-full bg-transparent text-sm text-slate-900 outline-none"
                />
              </label>
              <label className="rounded-2xl border border-slate-200 px-4 py-3">
                <span className="mb-2 block text-xs font-medium uppercase tracking-wide text-slate-500">Max price</span>
                <input
                  type="number"
                  placeholder="999"
                  value={filter.maxPrice}
                  onChange={(event) => setFilter({ ...filter, maxPrice: event.target.value })}
                  className="w-full bg-transparent text-sm text-slate-900 outline-none"
                />
              </label>
            </div>

            <div className="flex flex-wrap items-center gap-3">
              <ActionButton tone="slate" onClick={handleFilter}>
                <Filter className="h-4 w-4" />
                Apply backend filters
              </ActionButton>
              <ActionButton tone="white" onClick={resetFilters}>Reset all</ActionButton>
            </div>
          </div>

          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-1">
            <label className="flex items-center gap-3 rounded-2xl border border-slate-200 px-4 py-3">
              <Search className="h-4 w-4 text-slate-400" />
              <input
                type="text"
                placeholder="Search title, description, teacher, category..."
                value={quickSearch}
                onChange={(event) => setQuickSearch(event.target.value)}
                className="w-full bg-transparent text-sm text-slate-900 outline-none"
              />
            </label>
            <label className="flex items-center gap-3 rounded-2xl border border-slate-200 px-4 py-3">
              <SlidersHorizontal className="h-4 w-4 text-slate-400" />
              <select
                value={sortBy}
                onChange={(event) => setSortBy(event.target.value)}
                className="w-full bg-transparent text-sm text-slate-900 outline-none"
              >
                <option value="title-asc">Title A-Z</option>
                <option value="title-desc">Title Z-A</option>
                <option value="price-desc">Price high to low</option>
                <option value="price-asc">Price low to high</option>
                <option value="duration-desc">Duration long to short</option>
                <option value="duration-asc">Duration short to long</option>
              </select>
            </label>
          </div>
        </div>
      </SectionCard>

      {error ? <ErrorState title="Course action failed" description={error} onRetry={fetchCourses} /> : null}

      <SectionCard
        title="Catalog"
        subtitle={`${visibleCourses.length} courses currently visible`}
      >
        {visibleCourses.length ? (
          <>
            <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
              {paginatedCourses.map((course) => (
                <CourseCard
                  key={course.id}
                  course={course}
                  onEdit={() => handleEdit(course)}
                  onDelete={() => handleDelete(course.id)}
                />
              ))}
            </div>
            <Pagination
              page={page}
              pageCount={pageCount}
              onChange={setPage}
              pageSize={pageSize}
              totalItems={visibleCourses.length}
              onPageSizeChange={setPageSize}
            />
          </>
        ) : (
          <EmptyState
            title="No courses match the current view"
            description="Adjust filters, clear the search input, or create a new course to populate the catalog."
            icon={BookOpen}
            action={
              <ActionButton tone="indigo" onClick={() => { setEditingCourse(null); setShowModal(true); }}>
                <Plus className="h-4 w-4" />
                Create course
              </ActionButton>
            }
          />
        )}
      </SectionCard>

      {showModal ? (
        <CourseModal
          course={editingCourse}
          teachers={teachers}
          categories={categories}
          onSave={handleSave}
          onClose={() => { setShowModal(false); setEditingCourse(null); }}
        />
      ) : null}
    </div>
  );
}

function CourseCard({ course, onEdit, onDelete }) {
  return (
    <article className="group rounded-3xl border border-slate-200/80 bg-white p-6 shadow-sm transition hover:-translate-y-1 hover:shadow-lg">
      <div className="flex items-start justify-between gap-4">
        <div className="space-y-3">
          <div className="flex flex-wrap items-center gap-2">
            {course.category?.name ? <Badge tone="indigo">{course.category.name}</Badge> : <Badge tone="slate">Uncategorized</Badge>}
            {course.teacher?.name ? <Badge tone="blue">{course.teacher.name}</Badge> : null}
          </div>
          <div>
            <h3 className="text-xl font-semibold text-slate-900">{course.title}</h3>
            <p className="mt-2 line-clamp-3 text-sm leading-6 text-slate-600">{course.description || 'No description provided.'}</p>
          </div>
        </div>
        <div className="flex items-center gap-1 opacity-100 transition group-hover:opacity-100">
          <button onClick={onEdit} className="rounded-xl p-2 text-slate-500 transition hover:bg-slate-100 hover:text-blue-700">
            <Edit className="h-4 w-4" />
          </button>
          <button onClick={onDelete} className="rounded-xl p-2 text-slate-500 transition hover:bg-rose-50 hover:text-rose-700">
            <Trash2 className="h-4 w-4" />
          </button>
        </div>
      </div>

      <div className="mt-6 grid gap-3 sm:grid-cols-2">
        <div className="rounded-2xl bg-slate-50 p-4">
          <div className="flex items-center gap-2 text-sm text-slate-500">
            <DollarSign className="h-4 w-4" />
            Price
          </div>
          <p className="mt-2 text-lg font-semibold text-slate-900">${Number(course.price || 0).toFixed(2)}</p>
        </div>
        <div className="rounded-2xl bg-slate-50 p-4">
          <div className="flex items-center gap-2 text-sm text-slate-500">
            <Clock className="h-4 w-4" />
            Duration
          </div>
          <p className="mt-2 text-lg font-semibold text-slate-900">{course.durationWeeks || 0} weeks</p>
        </div>
      </div>

      <div className="mt-6 space-y-3 text-sm text-slate-600">
        {course.teacher ? (
          <Link to={`/teachers?courseId=${course.id}`} className="flex items-center justify-between rounded-2xl border border-slate-200 px-4 py-3 transition hover:border-blue-200 hover:bg-blue-50/40">
            <span className="inline-flex items-center gap-2"><GraduationCap className="h-4 w-4 text-blue-600" /> Teacher details</span>
            <span className="font-medium text-slate-900">{course.teacher.name}</span>
          </Link>
        ) : null}
        <Link to={`/lessons?courseId=${course.id}`} className="flex items-center justify-between rounded-2xl border border-slate-200 px-4 py-3 transition hover:border-indigo-200 hover:bg-indigo-50/40">
          <span className="inline-flex items-center gap-2"><PlayCircle className="h-4 w-4 text-indigo-600" /> Lesson library</span>
          <span className="font-medium text-indigo-700">Open</span>
        </Link>
        <Link to={`/students?courseId=${course.id}`} className="flex items-center justify-between rounded-2xl border border-slate-200 px-4 py-3 transition hover:border-emerald-200 hover:bg-emerald-50/40">
          <span className="inline-flex items-center gap-2"><Users className="h-4 w-4 text-emerald-600" /> Students</span>
          <span className="font-medium text-emerald-700">Browse</span>
        </Link>
      </div>
    </article>
  );
}

function CourseModal({ course, teachers = [], categories = [], onSave, onClose }) {
  const [formData, setFormData] = useState(course || {
    title: '',
    description: '',
    price: '',
    durationWeeks: '',
    teacher: { id: '' },
    category: { id: '' },
  });

  const handleSubmit = (event) => {
    event.preventDefault();
    onSave({
      ...formData,
      price: Number(formData.price),
      durationWeeks: Number(formData.durationWeeks),
      teacher: formData.teacher?.id ? { id: Number(formData.teacher.id) } : null,
      category: formData.category?.id ? { id: Number(formData.category.id) } : null,
    });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-4 backdrop-blur-sm">
      <div className="w-full max-w-2xl rounded-3xl border border-slate-200 bg-white p-6 shadow-2xl sm:p-8">
        <div className="flex items-start justify-between gap-4">
          <div>
            <h2 className="text-2xl font-semibold text-slate-900">{course ? 'Edit course' : 'Create new course'}</h2>
            <p className="mt-1 text-sm text-slate-500">Update the catalog item, delivery details, and linked entities.</p>
          </div>
          <Badge tone="indigo">Course form</Badge>
        </div>

        <form onSubmit={handleSubmit} className="mt-6 space-y-5">
          <div className="grid gap-5 sm:grid-cols-2">
            <label className="block sm:col-span-2">
              <span className="mb-2 block text-sm font-medium text-slate-700">Title</span>
              <input
                type="text"
                required
                value={formData.title}
                onChange={(event) => setFormData({ ...formData, title: event.target.value })}
                className="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-indigo-300 focus:ring-4 focus:ring-indigo-100"
              />
            </label>

            <label className="block sm:col-span-2">
              <span className="mb-2 block text-sm font-medium text-slate-700">Description</span>
              <textarea
                required
                rows="4"
                value={formData.description}
                onChange={(event) => setFormData({ ...formData, description: event.target.value })}
                className="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-indigo-300 focus:ring-4 focus:ring-indigo-100"
              />
            </label>

            <label className="block">
              <span className="mb-2 block text-sm font-medium text-slate-700">Price</span>
              <input
                type="number"
                required
                step="0.01"
                min="0"
                value={formData.price}
                onChange={(event) => setFormData({ ...formData, price: event.target.value })}
                className="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-indigo-300 focus:ring-4 focus:ring-indigo-100"
              />
            </label>

            <label className="block">
              <span className="mb-2 block text-sm font-medium text-slate-700">Duration in weeks</span>
              <input
                type="number"
                required
                min="1"
                value={formData.durationWeeks}
                onChange={(event) => setFormData({ ...formData, durationWeeks: event.target.value })}
                className="w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none transition focus:border-indigo-300 focus:ring-4 focus:ring-indigo-100"
              />
            </label>

            <label className="block">
              <span className="mb-2 block text-sm font-medium text-slate-700">Teacher</span>
              <select
                value={formData.teacher?.id ?? ''}
                onChange={(event) => setFormData({ ...formData, teacher: event.target.value ? { id: event.target.value } : { id: '' } })}
                className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none transition focus:border-indigo-300 focus:ring-4 focus:ring-indigo-100"
              >
                <option value="">Unassigned</option>
                {teachers.map((teacher) => (
                  <option key={teacher.id} value={teacher.id}>
                    {teacher.name}{teacher.department ? ` — ${teacher.department}` : ''}
                  </option>
                ))}
              </select>
            </label>

            <label className="block">
              <span className="mb-2 block text-sm font-medium text-slate-700">Category</span>
              <select
                value={formData.category?.id ?? ''}
                onChange={(event) => setFormData({ ...formData, category: event.target.value ? { id: event.target.value } : { id: '' } })}
                className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none transition focus:border-indigo-300 focus:ring-4 focus:ring-indigo-100"
              >
                <option value="">Uncategorized</option>
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>{category.name}</option>
                ))}
              </select>
            </label>
          </div>

          <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
            <ActionButton tone="white" onClick={onClose}>Cancel</ActionButton>
            <button type="submit" className="inline-flex items-center justify-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-medium text-white transition hover:bg-indigo-700">
              {course ? 'Save changes' : 'Create course'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
