import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { categoryApi } from '../api/categoryApi';
import { Pagination } from '../components/ui';
import { Plus, Edit, Trash2, Tag, BookOpen } from 'lucide-react';

export default function CategoriesPage() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(9);

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    setPage(1);
  }, [categories, pageSize]);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const response = await categoryApi.getAll();
      setCategories(response.data);
    } catch (err) {
      setError('Failed to fetch categories');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this category?')) return;
    
    try {
      await categoryApi.delete(id);
      setCategories(categories.filter(c => c.id !== id));
    } catch (err) {
      setError('Failed to delete category');
      console.error(err);
    }
  };

  const handleEdit = (category) => {
    setEditingCategory(category);
    setShowModal(true);
  };

  const handleSave = async (categoryData) => {
    try {
      if (editingCategory) {
        const response = await categoryApi.update(editingCategory.id, categoryData);
        setCategories(categories.map(c => c.id === editingCategory.id ? response.data : c));
      } else {
        const response = await categoryApi.create(categoryData);
        setCategories([...categories, response.data]);
      }
      setShowModal(false);
      setEditingCategory(null);
    } catch (err) {
      setError('Failed to save category');
      console.error(err);
    }
  };

  const pageCount = Math.max(1, Math.ceil(categories.length / pageSize));
  const paginatedCategories = categories.slice((page - 1) * pageSize, page * pageSize);

  if (loading) return <div className="text-center py-8">Loading...</div>;
  if (error) return <div className="text-center py-8 text-red-600">{error}</div>;

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Categories</h1>
        <button
          onClick={() => { setEditingCategory(null); setShowModal(true); }}
          className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 flex items-center space-x-2"
        >
          <Plus className="w-5 h-5" />
          <span>Add Category</span>
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {paginatedCategories.map(category => (
          <CategoryCard
            key={category.id}
            category={category}
            onEdit={() => handleEdit(category)}
            onDelete={() => handleDelete(category.id)}
          />
        ))}
      </div>

      <div className="mt-6 rounded-2xl border border-slate-200 bg-white px-5 py-2">
        <Pagination
          page={page}
          pageCount={pageCount}
          onChange={setPage}
          pageSize={pageSize}
          totalItems={categories.length}
          onPageSizeChange={setPageSize}
        />
      </div>

      {showModal && (
        <CategoryModal
          category={editingCategory}
          onSave={handleSave}
          onClose={() => { setShowModal(false); setEditingCategory(null); }}
        />
      )}
    </div>
  );
}

function CategoryCard({ category, onEdit, onDelete }) {
  return (
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
      <div className="flex justify-between items-start mb-4">
        <div className="flex items-center space-x-3">
          <Tag className="w-8 h-8 text-indigo-600" />
          <h3 className="text-xl font-semibold text-gray-800">{category.name}</h3>
        </div>
        <div className="flex space-x-2">
          <button onClick={onEdit} className="text-blue-600 hover:text-blue-800">
            <Edit className="w-5 h-5" />
          </button>
          <button onClick={onDelete} className="text-red-600 hover:text-red-800">
            <Trash2 className="w-5 h-5" />
          </button>
        </div>
      </div>
      <Link 
        to={`/courses?categoryId=${category.id}`} 
        className="flex items-center space-x-2 text-indigo-600 hover:text-indigo-800 hover:underline"
      >
        <BookOpen className="w-4 h-4" />
        <span>View Courses</span>
      </Link>
    </div>
  );
}

function CategoryModal({ category, onSave, onClose }) {
  const [formData, setFormData] = useState(category || { name: '' });

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(formData);
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
        <h2 className="text-2xl font-bold mb-4">{category ? 'Edit Category' : 'Add Category'}</h2>
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
