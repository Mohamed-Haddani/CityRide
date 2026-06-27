import api from './axiosClient'

export const bookingApi = {
  create: (payload) => api.post('/bookings', payload).then((r) => r.data),
  getById: (id) => api.get(`/bookings/${id}`).then((r) => r.data),
  mine: () => api.get('/bookings/mine').then((r) => r.data),
  forRide: (rideId) => api.get(`/rides/${rideId}/bookings`).then((r) => r.data),
  accept: (id) => api.patch(`/bookings/${id}/accept`).then((r) => r.data),
  reject: (id) => api.patch(`/bookings/${id}/reject`).then((r) => r.data),
  cancel: (id) => api.patch(`/bookings/${id}/cancel`).then((r) => r.data)
}
