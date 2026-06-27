import api from './axiosClient'

export const paymentApi = {
  pay: (payload) => api.post('/payments', payload).then((r) => r.data),
  mine: () => api.get('/payments/mine').then((r) => r.data)
}
