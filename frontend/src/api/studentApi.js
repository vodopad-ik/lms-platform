import api from './axios';

export const studentApi = {
  getAll: () => api.get('/students'),
  getById: (id) => api.get(`/students/${id}`),
  filter: (params) => api.get('/students/filter', { params }),
  filterNative: (params) => api.get('/students/filter/native', { params }),
  create: (studentData) => api.post('/students', studentData),
  update: (id, studentData) => api.put(`/students/${id}`, studentData),
  delete: (id) => api.delete(`/students/${id}`),
};
