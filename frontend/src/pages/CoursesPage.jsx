import { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { courseApi } from '../api/courseApi';
import { Plus, Edit, Trash2, Users, BookOpen, DollarSign, Clock, PlayCircle } from 'lucide-react';

export default function CoursesPage() {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [editingCourse, setEditingCourse] = useState(null);
  const [filter, setFilter] = useState({ department: '', category: '', minPrice: '', maxPrice: '' });
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const categoryId = searchParams.get('categoryId');
    const teacherId = searchParams.get('teacherId');
    const studentId = searchParams.get('studentId');
    
    if (categoryId) {
      setFilter(f => ({ ...f, category: '' }));
      fetchCoursesByCategory();
    } else if (teacherId) {
      fetchCoursesByTeacher();
    } else if (studentId) {
      fetchCoursesByStudent();
    } else {
      fetchCourses();
    }
  }, [searchParams]);

  const fetchCourses = async () => {
    try {
      setLoading(true);
      const response = await courseApi.getAll();
      setCourses(response.data);
    } catch (err) {
      setError('Failed to fetch courses');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchCoursesByCategory = async () => {
    try {
      setLoading(true);
      const response = await courseApi.getAll();
      setCourses(response.data);
    } catch (err) {
      setError('Failed to fetch courses by category');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchCoursesByTeacher = async () => {
    try {
      setLoading(true);
      const response = await courseApi.getAll();
      setCourses(response.data);
    } catch (err) {
      setError('Failed to fetch courses by teacher');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchCoursesByStudent = async () => {
    try {
      setLoading(true);
      const response = await courseApi.getAll();
      setCourses(response.data);
    } catch (err) {
      setError('Failed to fetch courses by student');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilter = async () => {
    try {
      setLoading(true);
      const params = {};
      if (filter.department) params.department = filter.department;
      if (filter.category) params.category = filter.category;
      if (filter.minPrice) params.minPrice = parseFloat(filter.minPrice);
      if (filter.maxPrice) params.maxPrice = parseFloat(filter.maxPrice);
      
      const response = await courseApi.filter(params);
      setCourses(response.data.content || response.data);
    } catch (err) {
      setError('Failed to filter courses');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this course?')) return;
    
    try {
      await courseApi.delete(id);
      setCourses(courses.filter(c => c.id !== id));
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
        setCourses(courses.map(c => c.id === editingCourse.id ? response.data : c));
      } else {
        const response = await courseApi.create(courseData);
        setCourses([...courses, response.data]);
      }
      setShowModal(false);
      setEditingCourse(null);
    } catch (err) {
      setError('Failed to save course');
      console.error(err);
    }
  };

  if (loading) return <div className="text-center py-8">Loading...</div>;
  if (error) return <div className="text-center py-8 text-red-600">{error}</div>;

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Courses</h1>
        <button
          onClick={() => { setEditingCourse(null); setShowModal(true); }}
          className="bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 flex items-center space-x-2"
        >
          <Plus className="w-5 h-5" />
          <span>Add Course</span>
        </button>
      </div>

      <div className="bg-white rounded-lg shadow-md p-6 mb-6">
        <h3 className="text-lg font-semibold mb-4">Filter Courses</h3>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <input
            type="text"
            placeholder="Department"
            value={filter.department}
            onChange={(e) => setFilter({...filter, department: e.target.value})}
            className="border rounded-lg px-4 py-2"
          />
          <input
            type="text"
            placeholder="Category"
            value={filter.category}
            onChange={(e) => setFilter({...filter, category: e.target.value})}
            className="border rounded-lg px-4 py-2"
          />
          <input
            type="number"
            placeholder="Min Price"
            value={filter.minPrice}
            onChange={(e) => setFilter({...filter, minPrice: e.target.value})}
            className="border rounded-lg px-4 py-2"
          />
          <input
            type="number"
            placeholder="Max Price"
            value={filter.maxPrice}
            onChange={(e) => setFilter({...filter, maxPrice: e.target.value})}
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
          onClick={() => { setFilter({ department: '', category: '', minPrice: '', maxPrice: '' }); fetchCourses(); }}
          className="mt-4 ml-2 bg-gray-400 text-white px-4 py-2 rounded-lg hover:bg-gray-500"
        >
          Reset
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {courses.map(course => (
          <CourseCard
            key={course.id}
            course={course}
            onEdit={() => handleEdit(course)}
            onDelete={() => handleDelete(course.id)}
          />
        ))}
      </div>

      {showModal && (
        <CourseModal
          course={editingCourse}
          onSave={handleSave}
          onClose={() => { setShowModal(false); setEditingCourse(null); }}
        />
      )}
    </div>
  );
}

function CourseCard({ course, onEdit, onDelete }) {
  return (
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
      <div className="flex justify-between items-start mb-4">
        <h3 className="text-xl font-semibold text-gray-800">{course.title}</h3>
        <div className="flex space-x-2">
          <button onClick={onEdit} className="text-blue-600 hover:text-blue-800">
            <Edit className="w-5 h-5" />
          </button>
          <button onClick={onDelete} className="text-red-600 hover:text-red-800">
            <Trash2 className="w-5 h-5" />
          </button>
        </div>
      </div>
      <p className="text-gray-600 mb-4 line-clamp-2">{course.description}</p>
      <div className="space-y-2 text-sm text-gray-500">
        <div className="flex items-center space-x-2">
          <DollarSign className="w-4 h-4" />
          <span>${course.price}</span>
        </div>
        <div className="flex items-center space-x-2">
          <Clock className="w-4 h-4" />
          <span>{course.durationWeeks} weeks</span>
        </div>
        {course.teacher && (
          <div className="flex items-center space-x-2">
            <Users className="w-4 h-4" />
            <Link to={`/teachers?courseId=${course.id}`} className="text-indigo-600 hover:text-indigo-800 hover:underline">
              {course.teacher.name}
            </Link>
          </div>
        )}
        {course.category && (
          <div className="flex items-center space-x-2">
            <BookOpen className="w-4 h-4" />
            <Link to={`/categories?courseId=${course.id}`} className="text-indigo-600 hover:text-indigo-800 hover:underline">
              {course.category.name}
            </Link>
          </div>
        )}
        <Link 
          to={`/lessons?courseId=${course.id}`} 
          className="flex items-center space-x-2 text-indigo-600 hover:text-indigo-800 hover:underline"
        >
          <PlayCircle className="w-4 h-4" />
          <span>View Lessons</span>
        </Link>
      </div>
    </div>
  );
}

function CourseModal({ course, onSave, onClose }) {
  const [formData, setFormData] = useState(course || {
    title: '',
    description: '',
    price: '',
    durationWeeks: '',
    teacher: { id: '' },
    category: { id: '' }
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave({
      ...formData,
      price: parseFloat(formData.price),
      durationWeeks: parseInt(formData.durationWeeks)
    });
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
        <h2 className="text-2xl font-bold mb-4">{course ? 'Edit Course' : 'Add Course'}</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Title</label>
            <input
              type="text"
              required
              value={formData.title}
              onChange={(e) => setFormData({...formData, title: e.target.value})}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Description</label>
            <textarea
              required
              value={formData.description}
              onChange={(e) => setFormData({...formData, description: e.target.value})}
              className="w-full border rounded-lg px-3 py-2"
              rows="3"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Price</label>
            <input
              type="number"
              required
              step="0.01"
              value={formData.price}
              onChange={(e) => setFormData({...formData, price: e.target.value})}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Duration (weeks)</label>
            <input
              type="number"
              required
              value={formData.durationWeeks}
              onChange={(e) => setFormData({...formData, durationWeeks: e.target.value})}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Teacher ID</label>
            <input
              type="number"
              value={formData.teacher?.id}
              onChange={(e) => setFormData({...formData, teacher: { id: parseInt(e.target.value) }})}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Category ID</label>
            <input
              type="number"
              value={formData.category?.id}
              onChange={(e) => setFormData({...formData, category: { id: parseInt(e.target.value) }})}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div className="flex space-x-3">
            <button
              type="submit"
              className="flex-1 bg-indigo-600 text-white py-2 rounded-lg hover:bg-indigo-700"
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
