import api from './axiosClient'

// Retire les valeurs vides avant d'envoyer les parametres de recherche.
function cleanParams(params) {
  const out = {}
  Object.entries(params).forEach(([k, v]) => {
    if (v !== '' && v !== null && v !== undefined) out[k] = v
  })
  return out
}

export const rideApi = {
  search: (params) => api.get('/rides/search', { params: cleanParams(params) }).then((r) => r.data),
  getById: (id) => api.get(`/rides/${id}`).then((r) => r.data),
  create: (payload) => api.post('/rides', payload).then((r) => r.data),
  update: (id, payload) => api.put(`/rides/${id}`, payload).then((r) => r.data),
  cancel: (id) => api.delete(`/rides/${id}`),
  mine: () => api.get('/rides/mine').then((r) => r.data)
}
