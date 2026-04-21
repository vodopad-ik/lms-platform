import { Link, NavLink, useLocation } from 'react-router-dom';
import { BookOpen, ChevronRight, FileText, GraduationCap, Home, Search, Tag, Users } from 'lucide-react';
import { cn } from './ui';

const navItems = [
  { to: '/', label: 'Dashboard', icon: Home },
  { to: '/courses', label: 'Courses', icon: BookOpen },
  { to: '/students', label: 'Students', icon: Users },
  { to: '/teachers', label: 'Teachers', icon: GraduationCap },
  { to: '/lessons', label: 'Lessons', icon: FileText },
  { to: '/categories', label: 'Categories', icon: Tag },
];

const quickActions = [
  { label: 'Open courses', to: '/courses' },
  { label: 'Open students', to: '/students' },
];

export default function AppShell({ children }) {
  const location = useLocation();
  const currentPage = navItems.find((item) => item.to !== '/' && location.pathname.startsWith(item.to))
    ?? navItems.find((item) => item.to === location.pathname)
    ?? navItems[0];

  return (
    <div className="min-h-screen bg-slate-100 text-slate-900">
      <div className="mx-auto grid min-h-screen max-w-[1600px] grid-cols-1 xl:grid-cols-[280px_minmax(0,1fr)]">
        <aside className="border-b border-slate-200 bg-slate-950 px-6 py-8 text-slate-100 xl:sticky xl:top-0 xl:h-screen xl:self-start xl:overflow-y-auto xl:border-b-0 xl:border-r xl:border-slate-800">
          <div className="flex items-center gap-3">
            <div className="rounded-2xl bg-indigo-500/20 p-3 ring-1 ring-indigo-400/20">
              <BookOpen className="h-7 w-7 text-indigo-300" />
            </div>
            <div>
              <Link to="/" className="text-lg font-semibold tracking-tight text-white">LMS Platform</Link>
              <p className="text-sm text-slate-400">Operational workspace</p>
            </div>
          </div>

          <nav className="mt-8 grid gap-2">
            {navItems.map(({ to, label, icon: Icon }) => (
              <NavLink
                key={to}
                to={to}
                className={({ isActive }) => cn(
                  'flex items-center gap-3 rounded-2xl px-4 py-3 text-sm font-medium transition',
                  isActive
                    ? 'bg-white text-slate-950 shadow-sm'
                    : 'text-slate-300 hover:bg-white/5 hover:text-white'
                )}
              >
                <Icon className="h-4 w-4" />
                <span>{label}</span>
              </NavLink>
            ))}
          </nav>

          <div className="mt-8 rounded-3xl border border-white/10 bg-white/5 p-5">
            <h2 className="text-sm font-semibold text-white">Quick access</h2>
            <div className="mt-4 space-y-3">
              {quickActions.map((action) => (
                <Link
                  key={action.label}
                  to={action.to}
                  className="flex items-center justify-between rounded-2xl border border-white/10 px-4 py-3 text-sm text-slate-200 transition hover:border-indigo-400/40 hover:bg-indigo-500/10 hover:text-white"
                >
                  <span>{action.label}</span>
                  <ChevronRight className="h-4 w-4" />
                </Link>
              ))}
            </div>
            <p className="mt-4 text-xs leading-5 text-slate-400">
              Keep all core LMS entities close at hand while you work.
            </p>
          </div>
        </aside>

        <div className="min-w-0">
          <header className="sticky top-0 z-20 border-b border-slate-200/80 bg-white/85 backdrop-blur">
            <div className="flex flex-col gap-4 px-4 py-4 sm:px-6 lg:px-10 xl:flex-row xl:items-center xl:justify-between">
              <div>
                <div className="flex items-center gap-2 text-sm text-slate-500">
                  <span>Workspace</span>
                  <ChevronRight className="h-4 w-4" />
                  <span className="font-medium text-slate-700">{currentPage.label}</span>
                </div>
                <p className="mt-1 text-sm text-slate-500">Manage your learning content, people, and operations from one place.</p>
              </div>
              <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
                <div className="flex min-w-[280px] items-center gap-3 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-500">
                  <Search className="h-4 w-4" />
                  <span>Use navigation to switch between LMS modules</span>
                </div>
              </div>
            </div>
          </header>

          <main className="px-4 py-6 sm:px-6 lg:px-10 lg:py-10">{children}</main>
        </div>
      </div>
    </div>
  );
}
