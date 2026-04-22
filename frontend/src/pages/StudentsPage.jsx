import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { studentApi } from '../api/studentApi';
import { Pagination } from '../components/ui';
import { Plus, Edit, Trash2, Mail, Calendar, BookOpen } from 'lucide-react';

export default function StudentsPage() {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [editingStudent, setEditingStudent] = useState(null);
  const [filter, setFilter] = useState({ name: '', email: '', courseTitle: '' });
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(9);

  useEffect(() => {
    fetchStudents();
  }, []);

  useEffect(() => {
    setPage(1);
  }, [students, pageSize]);

  const fetchStudents = async () => {
    try {
      setLoading(true);
      const response = await studentApi.getAll();
      setStudents(response.data);
    } catch (err) {
      setError('Failed to fetch students');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilter = async () => {
    try {
      setLoading(true);
      const params = {};
      if (filter.name) params.name = filter.name;
      if (filter.email) params.email = filter.email;
      if (filter.courseTitle) params.courseTitle = filter.courseTitle;
      
      const response = await studentApi.filter(params);
      setStudents(response.data.content || response.data);
    } catch (err) {
      setError('Failed to filter students');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this student?')) return;
    
    try {
      await studentApi.delete(id);
      setStudents(students.filter(s => s.id !== id));
    } catch (err) {
      setError('Failed to delete student');
      console.error(err);
    }
  };

  const handleEdit = (student) => {
    setEditingStudent(student);
    setShowModal(true);
  };

  const handleSave = async (studentData) => {
    try {
      if (editingStudent) {
        const response = await studentApi.update(editingStudent.id, studentData);
        setStudents(students.map(s => s.id === editingStudent.id ? response.data : s));
      } else {
        const response = await studentApi.create(studentData);
        setStudents([...students, response.data]);
      }
      setShowModal(false);
      setEditingStudent(null);
    } catch (err) {
      setError('Failed to save student');
      console.error(err);
    }
  };

  const pageCount = Math.max(1, Math.ceil(students.length / pageSize));
  const paginatedStudents = students.slice((page - 1) * pageSize, page * pageSize);

  if (loading) return <div className="text-center py-8">Loading...</div>;
  if (error) return <div className="text-center py-8 text-red-600">{error}</div>;

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Students</h1>
        <button
          onClick={() => { setEditingStudent(null); setShowModal(true); }}
          className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 flex items-center space-x-2"
        >
          <Plus className="w-5 h-5" />
          <span>Add Student</span>
        </button>
      </div>

      <div className="bg-white rounded-lg shadow-md p-6 mb-6">
        <h3 className="text-lg font-semibold mb-4">Filter Students</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input
            type="text"
            placeholder="Name"
            value={filter.name}
            onChange={(e) => setFilter({...filter, name: e.target.value})}
            className="border rounded-lg px-4 py-2"
          />
          <input
            type="email"
            placeholder="Email"
            value={filter.email}
            onChange={(e) => setFilter({...filter, email: e.target.value})}
            className="border rounded-lg px-4 py-2"
          />
          <input
            type="text"
            placeholder="Course Title"
            value={filter.courseTitle}
            onChange={(e) => setFilter({...filter, courseTitle: e.target.value})}
            className="border rounded-lg px-4 py-2"
          />
        </div>
        <button
          onClick={handleFilter}
          className="mt-4 bg-gray-600 text-white px-4 py-2 rounded-lg hover:bg-gray-700"
        >
          Apply Filters
        </button>
        <button
          onClick={() => { setFilter({ name: '', email: '', courseTitle: '' }); fetchStudents(); }}
          className="mt-4 ml-2 bg-gray-400 text-white px-4 py-2 rounded-lg hover:bg-gray-500"
        >
          Reset
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {paginatedStudents.map(student => (
          <StudentCard
            key={student.id}
            student={student}
            onEdit={() => handleEdit(student)}
            onDelete={() => handleDelete(student.id)}
          />
        ))}
      </div>

      <div className="mt-6 rounded-2xl border border-slate-200 bg-white px-5 py-2">
        <Pagination
          page={page}
          pageCount={pageCount}
          onChange={setPage}
          pageSize={pageSize}
          totalItems={students.length}
          onPageSizeChange={setPageSize}
        />
      </div>

      {showModal && (
        <StudentModal
          student={editingStudent}
          onSave={handleSave}
          onClose={() => { setShowModal(false); setEditingStudent(null); }}
        />
      )}
    </div>
  );
}

function StudentCard({ student, onEdit, onDelete }) {
  return (
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
      <div className="flex justify-between items-start mb-4">
        <h3 className="text-xl font-semibold text-gray-800">{student.name}</h3>
        <div className="flex space-x-2">
          <button onClick={onEdit} className="text-blue-600 hover:text-blue-800">
            <Edit className="w-5 h-5" />
          </button>
          <button onClick={onDelete} className="text-red-600 hover:text-red-800">
            <Trash2 className="w-5 h-5" />
          </button>
        </div>
      </div>
      <div className="space-y-2 text-sm text-gray-500">
        <div className="flex items-center space-x-2">
          <Mail className="w-4 h-4" />
          <span>{student.email}</span>
        </div>
        <div className="flex items-center space-x-2">
          <Calendar className="w-4 h-4" />
          <span>Enrolled: {student.enrollmentDate}</span>
        </div>
        {student.courses && student.courses.length > 0 && (
          <div className="flex items-start space-x-2">
            <BookOpen className="w-4 h-4 mt-0.5" />
            <div>
              <span className="font-medium">Courses:</span>
              <ul className="list-disc list-inside">
                {student.courses.map(course => (
                  <li key={course.id}>
                    <Link to={`/courses?studentId=${student.id}`} className="text-indigo-600 hover:text-indigo-800 hover:underline">
                      {course.title}
                    </Link>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

function StudentModal({ student, onSave, onClose }) {
  const [formData, setFormData] = useState(student || {
    name: '',
    email: '',
    enrollmentDate: new Date().toISOString().split('T')[0]
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(formData);
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
        <h2 className="text-2xl font-bold mb-4">{student ? 'Edit Student' : 'Add Student'}</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Name</label>
            <input
              type="text"
              required
              value={formData.name}
              onChange={(e) => setFormData({...formData, name: e.target.value})}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Email</label>
            <input
              type="email"
              required
              value={formData.email}
              onChange={(e) => setFormData({...formData, email: e.target.value})}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Enrollment Date</label>
            <input
              type="date"
              required
              value={formData.enrollmentDate}
              onChange={(e) => setFormData({...formData, enrollmentDate: e.target.value})}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div className="flex space-x-3">
            <button
              type="submit"
              className="flex-1 bg-green-600 text-white py-2 rounded-lg hover:bg-green-700"
            >
              Save
            </button>
            <button
              type="button"
              onClick={onClose}
              className="flex-1 bg-gray-300 text-gray-700 py-2 rounded-lg hover:bg-gray-400"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
