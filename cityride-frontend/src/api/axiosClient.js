import axios from 'axios'

const ACCESS_KEY = 'cityride_access'
const REFRESH_KEY = 'cityride_refresh'

// Petit utilitaire de stockage des tokens (localStorage).
export const tokenStore = {
  getAccess: () => localStorage.getItem(ACCESS_KEY),
  getRefresh: () => localStorage.getItem(REFRESH_KEY),
  set: (access, refresh) => {
    if (access) localStorage.setItem(ACCESS_KEY, access)
    if (refresh) localStorage.setItem(REFRESH_KEY, refresh)
  },
  clear: () => {
    localStorage.removeItem(ACCESS_KEY)
    localStorage.removeItem(REFRESH_KEY)
  }
}

const api = axios.create({ baseURL: '/api' })

// Ajoute le token d'acces a chaque requete.
api.interceptors.request.use((config) => {
  const token = tokenStore.getAccess()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Sur une 401, tente UNE fois de rafraichir le token puis rejoue la requete.
let refreshPromise = null
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config
    const status = error.response?.status

    if (status === 401 && !original?._retry && tokenStore.getRefresh()) {
      original._retry = true
      try {
        // axios "brut" (sans interceptor) pour eviter une boucle infinie
        refreshPromise =
          refreshPromise ||
          axios.post('/api/auth/refresh', { refreshToken: tokenStore.getRefresh() })
        const { data } = await refreshPromise
        refreshPromise = null
        tokenStore.set(data.accessToken, data.refreshToken)
        original.headers.Authorization = `Bearer ${data.accessToken}`
        return api(original)
      } catch (refreshError) {
        refreshPromise = null
        tokenStore.clear()
        if (window.location.pathname !== '/login') {
          window.location.href = '/login'
        }
        return Promise.reject(refreshError)
      }
    }
    return Promise.reject(error)
  }
)

export default api
