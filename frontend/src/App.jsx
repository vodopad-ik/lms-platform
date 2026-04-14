import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { BookOpen, Users, GraduationCap, FileText, Home, Tag } from 'lucide-react';
import CoursesPage from './pages/CoursesPage';
import StudentsPage from './pages/StudentsPage';
import TeachersPage from './pages/TeachersPage';
import LessonsPage from './pages/LessonsPage';
import CategoriesPage from './pages/CategoriesPage';

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-50">
        <nav className="bg-white shadow-lg">
          <div className="max-w-7xl mx-auto px-4">
            <div className="flex justify-between items-center h-16">
              <Link to="/" className="flex items-center space-x-2 text-indigo-600 font-bold text-xl">
                <BookOpen className="w-8 h-8" />
                <span>LMS Platform</span>
              </Link>
              <div className="flex space-x-4">
                <Link to="/" className="flex items-center space-x-1 px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100">
                  <Home className="w-5 h-5" />
                  <span>Dashboard</span>
                </Link>
                <Link to="/courses" className="flex items-center space-x-1 px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100">
                  <BookOpen className="w-5 h-5" />
                  <span>Courses</span>
                </Link>
                <Link to="/students" className="flex items-center space-x-1 px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100">
                  <Users className="w-5 h-5" />
                  <span>Students</span>
                </Link>
                <Link to="/teachers" className="flex items-center space-x-1 px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100">
                  <GraduationCap className="w-5 h-5" />
                  <span>Teachers</span>
                </Link>
                <Link to="/lessons" className="flex items-center space-x-1 px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100">
                  <FileText className="w-5 h-5" />
                  <span>Lessons</span>
                </Link>
                <Link to="/categories" className="flex items-center space-x-1 px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100">
                  <Tag className="w-5 h-5" />
                  <span>Categories</span>
                </Link>
              </div>
            </div>
          </div>
        </nav>

        <main className="max-w-7xl mx-auto px-4 py-8">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/courses" element={<CoursesPage />} />
            <Route path="/students" element={<StudentsPage />} />
            <Route path="/teachers" element={<TeachersPage />} />
            <Route path="/lessons" element={<LessonsPage />} />
            <Route path="/categories" element={<CategoriesPage />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

function Dashboard() {
  return (
    <div className="text-center py-12">
      <h1 className="text-4xl font-bold text-gray-800 mb-4">LMS Platform Dashboard</h1>
      <p className="text-xl text-gray-600 mb-8">Welcome to the Learning Management System</p>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Link to="/courses" className="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition-shadow">
          <BookOpen className="w-12 h-12 text-indigo-600 mx-auto mb-4" />
          <h3 className="text-lg font-semibold mb-2">Courses</h3>
          <p className="text-gray-600">Manage courses, lessons, and enrollments</p>
        </Link>
        <Link to="/students" className="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition-shadow">
          <Users className="w-12 h-12 text-green-600 mx-auto mb-4" />
          <h3 className="text-lg font-semibold mb-2">Students</h3>
          <p className="text-gray-600">Manage student enrollment and progress</p>
        </Link>
        <Link to="/teachers" className="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition-shadow">
          <GraduationCap className="w-12 h-12 text-blue-600 mx-auto mb-4" />
          <h3 className="text-lg font-semibold mb-2">Teachers</h3>
          <p className="text-gray-600">Manage teachers and their courses</p>
        </Link>
        <Link to="/lessons" className="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition-shadow">
          <FileText className="w-12 h-12 text-purple-600 mx-auto mb-4" />
          <h3 className="text-lg font-semibold mb-2">Lessons</h3>
          <p className="text-gray-600">Manage lesson content and materials</p>
        </Link>
      </div>
    </div>
  );
}

export default App;
