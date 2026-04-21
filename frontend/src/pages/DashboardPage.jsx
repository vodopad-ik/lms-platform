import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { BookOpen, ChevronRight, FileText, GraduationCap, Tag, TrendingUp, Users } from 'lucide-react';
import { categoryApi } from '../api/categoryApi';
import { courseApi } from '../api/courseApi';
import { lessonApi } from '../api/lessonApi';
import { studentApi } from '../api/studentApi';
import { teacherApi } from '../api/teacherApi';
import { ActionButton, Badge, EmptyState, ErrorState, LoadingState, PageHeader, SectionCard, StatCard } from '../components/ui';

export default function DashboardPage() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [data, setData] = useState({
    courses: [],
    students: [],
    teachers: [],
    lessons: [],
    categories: [],
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      setError('');

      const [coursesResponse, studentsResponse, teachersResponse, lessonsResponse, categoriesResponse] = await Promise.all([
        courseApi.getAll(),
        studentApi.getAll(),
        teacherApi.getAll(),
        lessonApi.getAll(),
        categoryApi.getAll(),
      ]);

      setData({
        courses: coursesResponse.data ?? [],
        students: studentsResponse.data ?? [],
        teachers: teachersResponse.data ?? [],
        lessons: lessonsResponse.data ?? [],
        categories: categoriesResponse.data ?? [],
      });
    } catch (err) {
      setError('Failed to load dashboard data');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const stats = useMemo(() => {
    const totalRevenue = data.courses.reduce((sum, course) => sum + (Number(course.price) || 0), 0);
    const avgDuration = data.courses.length
      ? Math.round(data.courses.reduce((sum, course) => sum + (Number(course.durationWeeks) || 0), 0) / data.courses.length)
      : 0;
    const lessonDensity = data.courses.length ? (data.lessons.length / data.courses.length).toFixed(1) : '0.0';

    return {
      totalRevenue,
      avgDuration,
      lessonDensity,
    };
  }, [data]);

  const latestCourses = useMemo(() => [...data.courses].slice(-4).reverse(), [data.courses]);
  const topTeachers = useMemo(
    () => [...data.teachers]
      .sort((a, b) => (b.courses?.length ?? 0) - (a.courses?.length ?? 0))
      .slice(0, 4),
    [data.teachers]
  );
  const categoryHighlights = useMemo(
    () => data.categories.map((category) => ({
      ...category,
      courseCount: data.courses.filter((course) => course.category?.id === category.id).length,
    })).sort((a, b) => b.courseCount - a.courseCount).slice(0, 5),
    [data.categories, data.courses]
  );

  if (loading) {
    return <LoadingState title="Preparing dashboard" description="Loading the latest courses, lessons, students, and teachers." />;
  }

  if (error) {
    return <ErrorState title="Dashboard unavailable" description={error} onRetry={fetchDashboardData} />;
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Learning operations dashboard"
        description="Get a live overview of your LMS catalog, teaching staff, students, and content health from one place."
        actions={(
          <>
            <ActionButton tone="white" onClick={fetchDashboardData}>Refresh data</ActionButton>
            <Link to="/courses">
              <ActionButton tone="indigo">Open course manager</ActionButton>
            </Link>
          </>
        )}
      />

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <StatCard title="Courses" value={data.courses.length} hint="Published learning programs in the catalog" icon={BookOpen} tone="indigo" />
        <StatCard title="Students" value={data.students.length} hint="Learners currently visible in the workspace" icon={Users} tone="emerald" />
        <StatCard title="Teachers" value={data.teachers.length} hint="Instructors available for course delivery" icon={GraduationCap} tone="blue" />
        <StatCard title="Lessons" value={data.lessons.length} hint={`${stats.lessonDensity} lessons per course on average`} icon={FileText} tone="violet" />
      </div>

      <div className="grid gap-6 xl:grid-cols-[1.4fr_1fr]">
        <SectionCard
          title="Program performance"
          subtitle="A quick snapshot of course catalog depth and commercial potential."
        >
          <div className="grid gap-4 md:grid-cols-3">
            <div className="rounded-2xl bg-slate-50 p-5">
              <p className="text-sm text-slate-500">Total catalog value</p>
              <p className="mt-2 text-2xl font-semibold text-slate-900">${stats.totalRevenue.toFixed(2)}</p>
            </div>
            <div className="rounded-2xl bg-slate-50 p-5">
              <p className="text-sm text-slate-500">Average duration</p>
              <p className="mt-2 text-2xl font-semibold text-slate-900">{stats.avgDuration} weeks</p>
            </div>
            <div className="rounded-2xl bg-slate-50 p-5">
              <p className="text-sm text-slate-500">Categories</p>
              <p className="mt-2 text-2xl font-semibold text-slate-900">{data.categories.length}</p>
            </div>
          </div>

          <div className="mt-6 grid gap-4 md:grid-cols-2">
            {latestCourses.length ? latestCourses.map((course) => (
              <Link
                key={course.id}
                to="/courses"
                className="group rounded-2xl border border-slate-200 p-5 transition hover:border-indigo-200 hover:bg-indigo-50/50"
              >
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <h3 className="text-lg font-semibold text-slate-900">{course.title}</h3>
                    <p className="mt-2 line-clamp-2 text-sm text-slate-600">{course.description}</p>
                  </div>
                  <Badge tone="indigo">${Number(course.price || 0).toFixed(2)}</Badge>
                </div>
                <div className="mt-4 flex items-center justify-between text-sm text-slate-500">
                  <span>{course.durationWeeks} weeks</span>
                  <span className="inline-flex items-center gap-1 font-medium text-indigo-600">
                    Open courses
                    <ChevronRight className="h-4 w-4 transition group-hover:translate-x-0.5" />
                  </span>
                </div>
              </Link>
            )) : <EmptyState title="No courses yet" description="Create your first course to populate this dashboard." icon={BookOpen} />}
          </div>
        </SectionCard>

        <SectionCard
          title="Top instructors"
          subtitle="Sorted by number of linked courses."
          action={<Link to="/teachers" className="text-sm font-medium text-indigo-600 hover:text-indigo-700">Manage teachers</Link>}
        >
          <div className="space-y-3">
            {topTeachers.length ? topTeachers.map((teacher) => (
              <div key={teacher.id} className="flex items-center justify-between rounded-2xl border border-slate-200 px-4 py-3">
                <div>
                  <p className="font-medium text-slate-900">{teacher.name}</p>
                  <p className="text-sm text-slate-500">{teacher.department || 'No department'}</p>
                </div>
                <div className="text-right">
                  <p className="text-sm font-semibold text-slate-900">{teacher.courses?.length ?? 0} courses</p>
                  <p className="text-xs text-slate-500">{teacher.experienceYears ?? 0} years exp.</p>
                </div>
              </div>
            )) : <EmptyState title="No teachers available" description="Add instructors to see workload insights here." icon={GraduationCap} />}
          </div>
        </SectionCard>
      </div>

      <div className="grid gap-6 lg:grid-cols-[1fr_1fr]">
        <SectionCard
          title="Category distribution"
          subtitle="See which subjects dominate your catalog."
          action={<Link to="/categories" className="text-sm font-medium text-indigo-600 hover:text-indigo-700">Manage categories</Link>}
        >
          <div className="space-y-3">
            {categoryHighlights.length ? categoryHighlights.map((category) => (
              <div key={category.id} className="rounded-2xl border border-slate-200 p-4">
                <div className="flex items-center justify-between gap-3">
                  <div className="flex items-center gap-3">
                    <div className="rounded-2xl bg-indigo-50 p-2 text-indigo-600">
                      <Tag className="h-4 w-4" />
                    </div>
                    <div>
                      <p className="font-medium text-slate-900">{category.name}</p>
                      <p className="text-sm text-slate-500">{category.courseCount} linked courses</p>
                    </div>
                  </div>
                  <Link to={`/courses?categoryId=${category.id}`} className="text-sm font-medium text-indigo-600 hover:text-indigo-700">Explore</Link>
                </div>
              </div>
            )) : <EmptyState title="No categories found" description="Create categories to organize your learning catalog." icon={Tag} />}
          </div>
        </SectionCard>

        <SectionCard
          title="Operational shortcuts"
          subtitle="Jump straight into the most common management flows."
        >
          <div className="grid gap-4 sm:grid-cols-2">
            <Link to="/courses" className="rounded-2xl border border-slate-200 bg-slate-50 p-5 transition hover:border-indigo-200 hover:bg-indigo-50/50">
              <TrendingUp className="h-5 w-5 text-indigo-600" />
              <p className="mt-3 font-semibold text-slate-900">Review pricing</p>
              <p className="mt-1 text-sm text-slate-500">Filter and sort courses by price and duration.</p>
            </Link>
            <Link to="/students" className="rounded-2xl border border-slate-200 bg-slate-50 p-5 transition hover:border-emerald-200 hover:bg-emerald-50/50">
              <Users className="h-5 w-5 text-emerald-600" />
              <p className="mt-3 font-semibold text-slate-900">Manage enrollments</p>
              <p className="mt-1 text-sm text-slate-500">Browse learners and drill into their active courses.</p>
            </Link>
            <Link to="/lessons" className="rounded-2xl border border-slate-200 bg-slate-50 p-5 transition hover:border-violet-200 hover:bg-violet-50/50">
              <FileText className="h-5 w-5 text-violet-600" />
              <p className="mt-3 font-semibold text-slate-900">Content operations</p>
              <p className="mt-1 text-sm text-slate-500">Navigate lesson library, video links, and course content.</p>
            </Link>
            <Link to="/teachers" className="rounded-2xl border border-slate-200 bg-slate-50 p-5 transition hover:border-blue-200 hover:bg-blue-50/50">
              <GraduationCap className="h-5 w-5 text-blue-600" />
              <p className="mt-3 font-semibold text-slate-900">Instructor coverage</p>
              <p className="mt-1 text-sm text-slate-500">Review who teaches what and balance course ownership.</p>
            </Link>
          </div>
        </SectionCard>
      </div>
    </div>
  );
}
