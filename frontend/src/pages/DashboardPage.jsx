import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { BookOpen, ChevronRight, FileText, GraduationCap, Tag, TrendingUp, Users } from 'lucide-react';
import { categoryApi } from '../api/categoryApi';
import { courseApi } from '../api/courseApi';
import { lessonApi } from '../api/lessonApi';
import { studentApi } from '../api/studentApi';
import { teacherApi } from '../api/teacherApi';
import { ActionButton, Badge, EmptyState, ErrorState, LoadingState, PageHeader, SectionCard, StatCard } from '../components/ui';

function asArray(value) {
  return Array.isArray(value) ? value : [];
}

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
        courses: asArray(coursesResponse.data),
        students: asArray(studentsResponse.data),
        teachers: asArray(teachersResponse.data),
        lessons: asArray(lessonsResponse.data),
        categories: asArray(categoriesResponse.data),
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

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
        <StatCard title="Courses" value={data.courses.length} hint="Published learning programs in the catalog" icon={BookOpen} tone="indigo" />
        <StatCard title="Students" value={data.students.length} hint="Learners currently visible in the workspace" icon={Users} tone="emerald" />
        <StatCard title="Teachers" value={data.teachers.length} hint="Active instructors in the system" icon={GraduationCap} tone="blue" />
        <StatCard title="Lessons" value={data.lessons.length} hint={`${stats.lessonDensity} lessons per course on average`} icon={FileText} tone="violet" />
        <StatCard title="Categories" value={data.categories.length} hint="Catalog groups used to organize courses" icon={Tag} tone="amber" />
      </div>

      <div className="grid gap-6 xl:grid-cols-[1.5fr_1fr]">
        <SectionCard
          title="Core overview"
          subtitle="Only the most important signals for the current LMS state."
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
              <p className="text-sm text-slate-500">Teaching structure</p>
              <p className="mt-2 text-2xl font-semibold text-slate-900">{data.teachers.length} teachers</p>
              <p className="mt-2 text-sm text-slate-500">{data.categories.length} categories configured</p>
            </div>
          </div>

          <div className="mt-6 flex flex-wrap gap-2">
            <Badge tone="indigo">{data.courses.length} courses</Badge>
            <Badge tone="emerald">{data.students.length} students</Badge>
            <Badge tone="blue">{data.teachers.length} teachers</Badge>
            <Badge tone="amber">{data.categories.length} categories</Badge>
          </div>
        </SectionCard>

        <SectionCard
          title="Quick actions"
          subtitle="Small set of useful shortcuts instead of many secondary panels."
        >
          <div className="grid gap-4">
            <Link to="/courses" className="rounded-2xl border border-slate-200 bg-slate-50 p-5 transition hover:border-indigo-200 hover:bg-indigo-50/50">
              <TrendingUp className="h-5 w-5 text-indigo-600" />
              <p className="mt-3 font-semibold text-slate-900">Review courses</p>
              <p className="mt-1 text-sm text-slate-500">Prices, duration and course structure in one place.</p>
            </Link>
            <Link to="/students" className="rounded-2xl border border-slate-200 bg-slate-50 p-5 transition hover:border-emerald-200 hover:bg-emerald-50/50">
              <Users className="h-5 w-5 text-emerald-600" />
              <p className="mt-3 font-semibold text-slate-900">Open students</p>
              <p className="mt-1 text-sm text-slate-500">See learners and their current enrollments.</p>
            </Link>
            <Link to="/lessons" className="rounded-2xl border border-slate-200 bg-slate-50 p-5 transition hover:border-violet-200 hover:bg-violet-50/50">
              <FileText className="h-5 w-5 text-violet-600" />
              <p className="mt-3 font-semibold text-slate-900">Open lessons</p>
              <p className="mt-1 text-sm text-slate-500">Navigate lesson content and linked videos.</p>
            </Link>
            <div className="grid gap-3 sm:grid-cols-2">
              <Link to="/teachers" className="rounded-2xl border border-slate-200 bg-white px-4 py-4 transition hover:border-blue-200 hover:bg-blue-50/50">
                <div className="flex items-center gap-3">
                  <div className="rounded-2xl bg-blue-50 p-2 text-blue-600">
                    <GraduationCap className="h-4 w-4" />
                  </div>
                  <div>
                    <p className="font-semibold text-slate-900">Teachers</p>
                    <p className="text-sm text-slate-500">Manage instructors</p>
                  </div>
                </div>
              </Link>
              <Link to="/categories" className="rounded-2xl border border-slate-200 bg-white px-4 py-4 transition hover:border-amber-200 hover:bg-amber-50/50">
                <div className="flex items-center gap-3">
                  <div className="rounded-2xl bg-amber-50 p-2 text-amber-600">
                    <Tag className="h-4 w-4" />
                  </div>
                  <div>
                    <p className="font-semibold text-slate-900">Categories</p>
                    <p className="text-sm text-slate-500">Manage catalog structure</p>
                  </div>
                </div>
              </Link>
            </div>
          </div>
        </SectionCard>
      </div>

      <SectionCard
        title="Recent courses"
        subtitle="A compact list of the latest visible programs in the catalog."
      >
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
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
                  <Badge tone="indigo">${Number(course.price || 0).toFixed(2)}</Badge>
                </div>
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
    </div>
  );
}
