import api from './axiosClient'

export const reviewApi = {
  create: (payload) => api.post('/reviews', payload).then((r) => r.data),
  forUser: (userId) => api.get(`/users/${userId}/reviews`).then((r) => r.data)
}
