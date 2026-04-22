import { AlertCircle, ChevronLeft, ChevronRight, Loader2, RefreshCw } from 'lucide-react';

export function cn(...classes) {
  return classes.filter(Boolean).join(' ');
}

export function PageHeader({ title, description, actions }) {
  return (
    <div className="flex flex-col gap-4 rounded-3xl border border-slate-200/70 bg-white/80 p-6 shadow-sm backdrop-blur sm:flex-row sm:items-center sm:justify-between">
      <div className="space-y-1">
        <h1 className="text-3xl font-semibold tracking-tight text-slate-900 sm:text-4xl">{title}</h1>
        {description ? <p className="max-w-2xl text-sm text-slate-600 sm:text-base">{description}</p> : null}
      </div>
      {actions ? <div className="flex flex-wrap items-center gap-3">{actions}</div> : null}
    </div>
  );
}

export function StatCard({ title, value, hint, icon: Icon, tone = 'indigo' }) {
  const tones = {
    indigo: 'bg-indigo-50 text-indigo-600 ring-indigo-100',
    emerald: 'bg-emerald-50 text-emerald-600 ring-emerald-100',
    blue: 'bg-blue-50 text-blue-600 ring-blue-100',
    amber: 'bg-amber-50 text-amber-600 ring-amber-100',
    rose: 'bg-rose-50 text-rose-600 ring-rose-100',
    violet: 'bg-violet-50 text-violet-600 ring-violet-100',
  };

  return (
    <div className="rounded-3xl border border-slate-200/70 bg-white/90 p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm font-medium text-slate-500">{title}</p>
          <p className="mt-3 text-3xl font-semibold tracking-tight text-slate-900">{value}</p>
        </div>
        {Icon ? (
          <div className={cn('rounded-2xl p-3 ring-1', tones[tone] ?? tones.indigo)}>
            <Icon className="h-5 w-5" />
          </div>
        ) : null}
      </div>
      {hint ? <p className="mt-3 text-sm text-slate-500">{hint}</p> : null}
    </div>
  );
}

export function SectionCard({ title, subtitle, action, children, className }) {
  return (
    <section className={cn('rounded-3xl border border-slate-200/70 bg-white/90 p-6 shadow-sm', className)}>
      {(title || subtitle || action) ? (
        <div className="mb-5 flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
          <div>
            {title ? <h2 className="text-xl font-semibold text-slate-900">{title}</h2> : null}
            {subtitle ? <p className="mt-1 text-sm text-slate-500">{subtitle}</p> : null}
          </div>
          {action ? <div className="flex items-center gap-2">{action}</div> : null}
        </div>
      ) : null}
      {children}
    </section>
  );
}

export function Badge({ children, tone = 'slate' }) {
  const tones = {
    slate: 'bg-slate-100 text-slate-700',
    indigo: 'bg-indigo-100 text-indigo-700',
    emerald: 'bg-emerald-100 text-emerald-700',
    amber: 'bg-amber-100 text-amber-700',
    rose: 'bg-rose-100 text-rose-700',
    blue: 'bg-blue-100 text-blue-700',
  };

  return (
    <span className={cn('inline-flex items-center rounded-full px-2.5 py-1 text-xs font-medium', tones[tone] ?? tones.slate)}>
      {children}
    </span>
  );
}

export function LoadingState({ title = 'Loading data', description = 'Please wait while the latest information is being prepared.' }) {
  return (
    <div className="rounded-3xl border border-slate-200/70 bg-white/90 p-10 text-center shadow-sm">
      <Loader2 className="mx-auto h-10 w-10 animate-spin text-indigo-600" />
      <h2 className="mt-4 text-xl font-semibold text-slate-900">{title}</h2>
      <p className="mt-2 text-sm text-slate-500">{description}</p>
    </div>
  );
}

export function ErrorState({ title = 'Something went wrong', description, onRetry }) {
  return (
    <div className="rounded-3xl border border-rose-200 bg-rose-50 p-8 text-center shadow-sm">
      <AlertCircle className="mx-auto h-10 w-10 text-rose-600" />
      <h2 className="mt-4 text-xl font-semibold text-rose-950">{title}</h2>
      {description ? <p className="mt-2 text-sm text-rose-700">{description}</p> : null}
      {onRetry ? (
        <button
          type="button"
          onClick={onRetry}
          className="mt-5 inline-flex items-center gap-2 rounded-xl bg-rose-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-rose-700"
        >
          <RefreshCw className="h-4 w-4" />
          Retry
        </button>
      ) : null}
    </div>
  );
}

export function EmptyState({ title, description, action, icon: Icon }) {
  return (
    <div className="rounded-3xl border border-dashed border-slate-300 bg-slate-50/80 p-10 text-center">
      {Icon ? <Icon className="mx-auto h-10 w-10 text-slate-400" /> : null}
      <h3 className="mt-4 text-lg font-semibold text-slate-900">{title}</h3>
      {description ? <p className="mt-2 text-sm text-slate-500">{description}</p> : null}
      {action ? <div className="mt-5">{action}</div> : null}
    </div>
  );
}

export function Pagination({ page, pageCount, onChange, pageSize, totalItems, onPageSizeChange, pageSizeOptions = [6, 9, 12, 24] }) {
  if (pageCount <= 1 && !onPageSizeChange) return null;

  const safePage = Math.min(Math.max(1, page), Math.max(1, pageCount));
  const start = totalItems === 0 ? 0 : (safePage - 1) * pageSize + 1;
  const end = Math.min(totalItems, safePage * pageSize);

  const pageNumbers = [];
  const maxVisible = 5;
  let rangeStart = Math.max(1, safePage - Math.floor(maxVisible / 2));
  let rangeEnd = Math.min(pageCount, rangeStart + maxVisible - 1);
  rangeStart = Math.max(1, rangeEnd - maxVisible + 1);
  for (let i = rangeStart; i <= rangeEnd; i++) pageNumbers.push(i);

  return (
    <div className="mt-5 flex flex-col gap-3 border-t border-slate-100 pt-4 sm:flex-row sm:items-center sm:justify-between">
      <p className="text-xs text-slate-500">
        {totalItems > 0
          ? <>Showing <span className="font-medium text-slate-700">{start}-{end}</span> of <span className="font-medium text-slate-700">{totalItems}</span></>
          : 'No items'}
      </p>
      <div className="flex flex-wrap items-center gap-2">
        {onPageSizeChange ? (
          <label className="flex items-center gap-2 text-xs text-slate-500">
            <span>Per page</span>
            <select
              value={pageSize}
              onChange={(event) => onPageSizeChange(Number(event.target.value))}
              className="rounded-lg border border-slate-200 bg-white px-2 py-1 text-xs text-slate-700 outline-none focus:border-indigo-300"
            >
              {pageSizeOptions.map((size) => (<option key={size} value={size}>{size}</option>))}
            </select>
          </label>
        ) : null}
        {pageCount > 1 ? (
          <div className="flex items-center gap-1">
            <button
              type="button"
              onClick={() => onChange(Math.max(1, safePage - 1))}
              disabled={safePage === 1}
              className="rounded-lg border border-slate-200 p-1.5 text-slate-600 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
            >
              <ChevronLeft className="h-4 w-4" />
            </button>
            {rangeStart > 1 ? (
              <>
                <button type="button" onClick={() => onChange(1)} className="rounded-lg px-2.5 py-1 text-xs font-medium text-slate-600 hover:bg-slate-100">1</button>
                {rangeStart > 2 ? <span className="px-1 text-xs text-slate-400">…</span> : null}
              </>
            ) : null}
            {pageNumbers.map((pageNumber) => (
              <button
                key={pageNumber}
                type="button"
                onClick={() => onChange(pageNumber)}
                className={cn(
                  'rounded-lg px-2.5 py-1 text-xs font-medium transition',
                  pageNumber === safePage
                    ? 'bg-indigo-600 text-white shadow-sm'
                    : 'text-slate-600 hover:bg-slate-100',
                )}
              >
                {pageNumber}
              </button>
            ))}
            {rangeEnd < pageCount ? (
              <>
                {rangeEnd < pageCount - 1 ? <span className="px-1 text-xs text-slate-400">…</span> : null}
                <button type="button" onClick={() => onChange(pageCount)} className="rounded-lg px-2.5 py-1 text-xs font-medium text-slate-600 hover:bg-slate-100">{pageCount}</button>
              </>
            ) : null}
            <button
              type="button"
              onClick={() => onChange(Math.min(pageCount, safePage + 1))}
              disabled={safePage === pageCount}
              className="rounded-lg border border-slate-200 p-1.5 text-slate-600 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
            >
              <ChevronRight className="h-4 w-4" />
            </button>
          </div>
        ) : null}
      </div>
    </div>
  );
}

export function ActionButton({ children, tone = 'indigo', className = '', ...props }) {
  const tones = {
    indigo: 'bg-indigo-600 text-white hover:bg-indigo-700',
    emerald: 'bg-emerald-600 text-white hover:bg-emerald-700',
    slate: 'bg-slate-900 text-white hover:bg-slate-800',
    white: 'bg-white text-slate-700 ring-1 ring-slate-200 hover:bg-slate-50',
    rose: 'bg-rose-600 text-white hover:bg-rose-700',
    amber: 'bg-amber-500 text-slate-950 hover:bg-amber-400',
  };

  return (
    <button
      type="button"
      className={cn('inline-flex items-center justify-center gap-2 rounded-xl px-4 py-2.5 text-sm font-medium transition', tones[tone] ?? tones.indigo, className)}
      {...props}
    >
      {children}
    </button>
  );
}
