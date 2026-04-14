import api from './axios';

export const lessonApi = {
  getAll: () => api.get('/lessons'),
  getById: (id) => api.get(`/lessons/${id}`),
  filter: (params) => api.get('/lessons/filter', { params }),
  filterNative: (params) => api.get('/lessons/filter/native', { params }),
  create: (lessonData) => api.post('/lessons', lessonData),
  update: (id, lessonData) => api.put(`/lessons/${id}`, lessonData),
  delete: (id) => api.delete(`/lessons/${id}`),
  bulkCreateNoTransaction: (lessonsData) => api.post('/lessons/bulk/no-transaction', lessonsData),
  bulkCreateWithTransaction: (lessonsData) => api.post('/lessons/bulk/with-transaction', lessonsData),
};
