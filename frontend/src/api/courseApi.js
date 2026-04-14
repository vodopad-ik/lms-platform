import api from './axios';

export const courseApi = {
  getAll: () => api.get('/courses'),
  getById: (id) => api.get(`/courses/${id}`),
  getLessons: (courseId) => api.get(`/courses/${courseId}/lessons`),
  addLesson: (courseId, lessonData) => api.post(`/courses/${courseId}/lessons`, lessonData),
  getByTitle: (title) => api.get('/courses/search', { params: { title } }),
  filter: (params) => api.get('/courses/filter', { params }),
  filterNative: (params) => api.get('/courses/filter/native', { params }),
  create: (courseData) => api.post('/courses', courseData),
  update: (id, courseData) => api.put(`/courses/${id}`, courseData),
  patch: (id, patchData) => api.patch(`/courses/${id}`, patchData),
  delete: (id) => api.delete(`/courses/${id}`),
  addStudent: (courseId, studentId) => api.post(`/courses/${courseId}/students/${studentId}`),
};
