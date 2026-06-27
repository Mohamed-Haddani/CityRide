import api from './axiosClient'

export const notificationApi = {
  mine: () => api.get('/notifications').then((r) => r.data),
  unreadCount: () => api.get('/notifications/unread-count').then((r) => r.data),
  markRead: (id) => api.patch(`/notifications/${id}/read`),
  markAllRead: () => api.patch('/notifications/read-all')
}
