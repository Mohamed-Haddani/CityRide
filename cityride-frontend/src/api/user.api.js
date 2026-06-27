import api from './axiosClient'

export const userApi = {
  getPublicProfile: (id) => api.get(`/users/${id}`).then((r) => r.data),
  updateProfile: (payload) => api.put('/users/me', payload).then((r) => r.data)
}
