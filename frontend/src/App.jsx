import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import AppShell from './components/AppShell';
import CoursesPage from './pages/CoursesPage';
import DashboardPage from './pages/DashboardPage';
import StudentsPage from './pages/StudentsPage';
import TeachersPage from './pages/TeachersPage';
import LessonsPage from './pages/LessonsPage';
import CategoriesPage from './pages/CategoriesPage';

function App() {
  return (
    <Router>
      <AppShell>
        <div className="mx-auto w-full max-w-7xl">
          <Routes>
            <Route path="/" element={<DashboardPage />} />
            <Route path="/courses" element={<CoursesPage />} />
            <Route path="/students" element={<StudentsPage />} />
            <Route path="/teachers" element={<TeachersPage />} />
            <Route path="/lessons" element={<LessonsPage />} />
            <Route path="/categories" element={<CategoriesPage />} />
          </Routes>
        </div>
      </AppShell>
    </Router>
  );
}

export default App;
