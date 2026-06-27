import api from './axiosClient'

export const adminApi = {
  users: () => api.get('/admin/users').then((r) => r.data),
  block: (id) => api.patch(`/admin/users/${id}/block`).then((r) => r.data),
  unblock: (id) => api.patch(`/admin/users/${id}/unblock`).then((r) => r.data),
  rides: () => api.get('/admin/rides').then((r) => r.data),
  bookings: () => api.get('/admin/bookings').then((r) => r.data)
}
