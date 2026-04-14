import { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { lessonApi } from '../api/lessonApi';
import { Plus, Edit, Trash2, Clock, Video, BookOpen, ArrowLeft } from 'lucide-react';

export default function LessonsPage() {
  const [lessons, setLessons] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [editingLesson, setEditingLesson] = useState(null);
  const [filter, setFilter] = useState({ courseId: '', courseTitle: '', title: '' });
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const courseId = searchParams.get('courseId');
    if (courseId) {
      setFilter(f => ({ ...f, courseId }));
      handleFilterWithCourseId(courseId);
    } else {
      fetchLessons();
    }
  }, [searchParams]);

  const fetchLessons = async () => {
    try {
      setLoading(true);
      const response = await lessonApi.getAll();
      setLessons(response.data);
    } catch (err) {
      setError('Failed to fetch lessons');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterWithCourseId = async (courseId) => {
    try {
      setLoading(true);
      const response = await lessonApi.filter({ courseId: parseInt(courseId) });
      setLessons(response.data.content || response.data);
    } catch (err) {
      setError('Failed to filter lessons');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilter = async () => {
    try {
      setLoading(true);
      const params = {};
      if (filter.courseId) params.courseId = parseInt(filter.courseId);
      if (filter.courseTitle) params.courseTitle = filter.courseTitle;
      if (filter.title) params.title = filter.title;
      
      const response = await lessonApi.filter(params);
      setLessons(response.data.content || response.data);
    } catch (err) {
      setError('Failed to filter lessons');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this lesson?')) return;
    
    try {
      await lessonApi.delete(id);
      setLessons(lessons.filter(l => l.id !== id));
    } catch (err) {
      setError('Failed to delete lesson');
      console.error(err);
    }
  };

  const handleEdit = (lesson) => {
    setEditingLesson(lesson);
    setShowModal(true);
  };

  const handleSave = async (lessonData) => {
    try {
      if (editingLesson) {
        const response = await lessonApi.update(editingLesson.id, lessonData);
        setLessons(lessons.map(l => l.id === editingLesson.id ? response.data : l));
      } else {
        const response = await lessonApi.create(lessonData);
        setLessons([...lessons, response.data]);
      }
      setShowModal(false);
      setEditingLesson(null);
    } catch (err) {
      setError('Failed to save lesson');
      console.error(err);
    }
  };

  if (loading) return <div className="text-center py-8">Loading...</div>;
  if (error) return <div className="text-center py-8 text-red-600">{error}</div>;

  const courseId = searchParams.get('courseId');

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div className="flex items-center space-x-4">
          {courseId && (
            <Link to="/courses" className="flex items-center space-x-2 text-gray-600 hover:text-gray-800">
              <ArrowLeft className="w-5 h-5" />
              <span>Back to Courses</span>
            </Link>
          )}
          <h1 className="text-3xl font-bold text-gray-800">Lessons</h1>
        </div>
        <button
          onClick={() => { setEditingLesson(null); setShowModal(true); }}
          className="bg-purple-600 text-white px-4 py-2 rounded-lg hover:bg-purple-700 flex items-center space-x-2"
        >
          <Plus className="w-5 h-5" />
          <span>Add Lesson</span>
        </button>
      </div>

      <div className="bg-white rounded-lg shadow-md p-6 mb-6">
        <h3 className="text-lg font-semibold mb-4">Filter Lessons</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input
            type="number"
            placeholder="Course ID"
            value={filter.courseId}
            onChange={(e) => setFilter({...filter, courseId: e.target.value})}
            className="border rounded-lg px-4 py-2"
          />
          <input
            type="text"
            placeholder="Course Title"
            value={filter.courseTitle}
            onChange={(e) => setFilter({...filter, courseTitle: e.target.value})}
            className="border rounded-lg px-4 py-2"
          />
          <input
            type="text"
            placeholder="Lesson Title"
            value={filter.title}
            onChange={(e) => setFilter({...filter, title: e.target.value})}
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
          onClick={() => { setFilter({ courseId: '', courseTitle: '', title: '' }); fetchLessons(); }}
          className="mt-4 ml-2 bg-gray-400 text-white px-4 py-2 rounded-lg hover:bg-gray-500"
        >
          Reset
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {lessons.map(lesson => (
          <LessonCard
            key={lesson.id}
            lesson={lesson}
            onEdit={() => handleEdit(lesson)}
            onDelete={() => handleDelete(lesson.id)}
          />
        ))}
      </div>

      {showModal && (
        <LessonModal
          lesson={editingLesson}
          onSave={handleSave}
          onClose={() => { setShowModal(false); setEditingLesson(null); }}
        />
      )}
    </div>
  );
}

function LessonCard({ lesson, onEdit, onDelete }) {
  return (
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
      <div className="flex justify-between items-start mb-4">
        <h3 className="text-xl font-semibold text-gray-800">{lesson.title}</h3>
        <div className="flex space-x-2">
          <button onClick={onEdit} className="text-blue-600 hover:text-blue-800">
            <Edit className="w-5 h-5" />
          </button>
          <button onClick={onDelete} className="text-red-600 hover:text-red-800">
            <Trash2 className="w-5 h-5" />
          </button>
        </div>
      </div>
      <p className="text-gray-600 mb-4 line-clamp-3">{lesson.content}</p>
      <div className="space-y-2 text-sm text-gray-500">
        <div className="flex items-center space-x-2">
          <Clock className="w-4 h-4" />
          <span>{lesson.durationMinutes} minutes</span>
        </div>
        {lesson.videoUrl && (
          <div className="flex items-center space-x-2">
            <Video className="w-4 h-4" />
            <a href={lesson.videoUrl} target="_blank" rel="noopener noreferrer" className="text-blue-600 hover:underline">
              Watch Video
            </a>
          </div>
        )}
        {lesson.course && (
          <div className="flex items-center space-x-2">
            <BookOpen className="w-4 h-4" />
            <Link to={`/courses?courseId=${lesson.course.id}`} className="text-indigo-600 hover:text-indigo-800 hover:underline">
              {lesson.course.title}
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}

function LessonModal({ lesson, onSave, onClose }) {
  const [formData, setFormData] = useState(lesson || {
    title: '',
    content: '',
    durationMinutes: '',
    videoUrl: '',
    course: { id: '' }
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave({
      ...formData,
      durationMinutes: parseInt(formData.durationMinutes),
      course: { id: parseInt(formData.course?.id) }
    });
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
        <h2 className="text-2xl font-bold mb-4">{lesson ? 'Edit Lesson' : 'Add Lesson'}</h2>
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
            <label className="block text-sm font-medium mb-1">Content</label>
            <textarea
              required
              value={formData.content}
              onChange={(e) => setFormData({...formData, content: e.target.value})}
              className="w-full border rounded-lg px-3 py-2"
              rows="4"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Duration (minutes)</label>
            <input
              type="number"
              required
              min="1"
              value={formData.durationMinutes}
              onChange={(e) => setFormData({...formData, durationMinutes: e.target.value})}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Video URL (optional)</label>
            <input
              type="url"
              value={formData.videoUrl}
              onChange={(e) => setFormData({...formData, videoUrl: e.target.value})}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Course ID</label>
            <input
              type="number"
              value={formData.course?.id}
              onChange={(e) => setFormData({...formData, course: { id: parseInt(e.target.value) }})}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
          <div className="flex space-x-3">
            <button
              type="submit"
              className="flex-1 bg-purple-600 text-white py-2 rounded-lg hover:bg-purple-700"
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
