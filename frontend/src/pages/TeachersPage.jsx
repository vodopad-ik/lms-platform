import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { teacherApi } from '../api/teacherApi';
import { Pagination } from '../components/ui';
import { Plus, Edit, Trash2, Mail, Briefcase, BookOpen, Award } from 'lucide-react';

export default function TeachersPage() {
  const [teachers, setTeachers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [editingTeacher, setEditingTeacher] = useState(null);
  const [filter, setFilter] = useState({ name: '', department: '', courseCategory: '' });
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(9);

  useEffect(() => {
    fetchTeachers();
  }, []);

  useEffect(() => {
    setPage(1);
  }, [teachers, pageSize]);

  const fetchTeachers = async () => {
    try {
      setLoading(true);
      const response = await teacherApi.getAll();
      setTeachers(response.data);
    } catch (err) {
      setError('Failed to fetch teachers');
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
      if (filter.department) params.department = filter.department;
      if (filter.courseCategory) params.courseCategory = filter.courseCategory;
      
      const response = await teacherApi.filter(params);
      setTeachers(response.data.content || response.data);
    } catch (err) {
      setError('Failed to filter teachers');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this teacher?')) return;
    
    try {
      await teacherApi.delete(id);
      setTeachers(teachers.filter(t => t.id !== id));
    } catch (err) {
      setError('Failed to delete teacher');
      console.error(err);
    }
  };

  const handleEdit = (teacher) => {
    setEditingTeacher(teacher);
    setShowModal(true);
  };

  const handleSave = async (teacherData) => {
    try {
      if (editingTeacher) {
        const response = await teacherApi.update(editingTeacher.id, teacherData);
        setTeachers(teachers.map(t => t.id === editingTeacher.id ? response.data : t));
      } else {
        const response = await teacherApi.create(teacherData);
        setTeachers([...teachers, response.data]);
      }
      setShowModal(false);
      setEditingTeacher(null);
    } catch (err) {
      setError('Failed to save teacher');
      console.error(err);
    }
  };

  const pageCount = Math.max(1, Math.ceil(teachers.length / pageSize));
  const paginatedTeachers = teachers.slice((page - 1) * pageSize, page * pageSize);

  if (loading) return <div className="text-center py-8">Loading...</div>;
  if (error) return <div className="text-center py-8 text-red-600">{error}</div>;

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Teachers</h1>
        <button
          onClick={() => { setEditingTeacher(null); setShowModal(true); }}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 flex items-center space-x-2"
        >
          <Plus className="w-5 h-5" />
          <span>Add Teacher</span>
        </button>
      </div>

      <div className="bg-white rounded-lg shadow-md p-6 mb-6">
        <h3 className="text-lg font-semibold mb-4">Filter Teachers</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input
            type="text"
            placeholder="Name"
            value={filter.name}
            onChange={(e) => setFilter({...filter, name: e.target.value})}
            className="border rounded-lg px-4 py-2"
          />
          <input
            type="text"
            placeholder="Department"
            value={filter.department}
            onChange={(e) => setFilter({...filter, department: e.target.value})}
            className="border rounded-lg px-4 py-2"
          />
          <input
            type="text"
            placeholder="Course Category"
            value={filter.courseCategory}
            onChange={(e) => setFilter({...filter, courseCategory: e.target.value})}
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
          onClick={() => { setFilter({ name: '', department: '', courseCategory: '' }); fetchTeachers(); }}
          className="mt-4 ml-2 bg-gray-400 text-white px-4 py-2 rounded-lg hover:bg-gray-500"
        >
          Reset
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {paginatedTeachers.map(teacher => (
          <TeacherCard
            key={teacher.id}
            teacher={teacher}
            onEdit={() => handleEdit(teacher)}
            onDelete={() => handleDelete(teacher.id)}
          />
        ))}
      </div>

      <div className="mt-6 rounded-2xl border border-slate-200 bg-white px-5 py-2">
        <Pagination
          page={page}
          pageCount={pageCount}
          onChange={setPage}
          pageSize={pageSize}
          totalItems={teachers.length}
          onPageSizeChange={setPageSize}
        />
      </div>

      {showModal && (
        <TeacherModal
          teacher={editingTeacher}
          onSave={handleSave}
          onClose={() => { setShowModal(false); setEditingTeacher(null); }}
        />
      )}
    </div>
  );
}

function TeacherCard({ teacher, onEdit, onDelete }) {
  return (
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
      <div className="flex justify-between items-start mb-4">
        <h3 className="text-xl font-semibold text-gray-800">{teacher.name}</h3>
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
          <span>{teacher.email}</span>
        </div>
        <div className="flex items-center space-x-2">
          <Briefcase className="w-4 h-4" />
          <span>{teacher.department}</span>
        </div>
        <div className="flex items-center space-x-2">
          <Award className="w-4 h-4" />
          <span>{teacher.experienceYears} years experience</span>
        </div>
        {teacher.courses && teacher.courses.length > 0 && (
          <div className="flex items-start space-x-2">
            <BookOpen className="w-4 h-4 mt-0.5" />
            <div>
              <span className="font-medium">Courses:</span>
              <ul className="list-disc list-inside">
                {teacher.courses.map(course => (
                  <li key={course.id}>
                    <Link to={`/courses?teacherId=${teacher.id}`} className="text-indigo-600 hover:text-indigo-800 hover:underline">
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

function TeacherModal({ teacher, onSave, onClose }) {
  const [formData, setFormData] = useState(teacher || {
    name: '',
    email: '',
    department: '',
    experienceYears: ''
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave({
      ...formData,
      experienceYears: parseInt(formData.experienceYears)
    });
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
        <h2 className="text-2xl font-bold mb-4">{teacher ? 'Edit Teacher' : 'Add Teacher'}</h2>
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
            <label className="block text-sm font-medium mb-1">Department</label>
            <input
              type="text"
              required
              value={formData.department}
              onChange={(e) => setFormData({...formData, department: e.target.value})}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Experience (years)</label>
            <input
              type="number"
              required
              min="0"
              value={formData.experienceYears}
              onChange={(e) => setFormData({...formData, experienceYears: e.target.value})}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div className="flex space-x-3">
            <button
              type="submit"
              className="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700"
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
