import api from './axios';

export const teacherApi = {
  getAll: () => api.get('/teachers'),
  getById: (id) => api.get(`/teachers/${id}`),
  filter: (params) => api.get('/teachers/filter', { params }),
  filterNative: (params) => api.get('/teachers/filter/native', { params }),
  create: (teacherData) => api.post('/teachers', teacherData),
  update: (id, teacherData) => api.put(`/teachers/${id}`, teacherData),
  delete: (id) => api.delete(`/teachers/${id}`),
};
