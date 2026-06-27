import { createContext, useContext, useEffect, useState } from 'react'
import { tokenStore } from '../api/axiosClient'
import { authApi } from '../api/auth.api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  // Au chargement de l'app : si un token existe, on recharge le profil.
  useEffect(() => {
    async function init() {
      if (tokenStore.getAccess()) {
        try {
          const me = await authApi.me()
          setUser(me)
        } catch {
          tokenStore.clear()
        }
      }
      setLoading(false)
    }
    init()
  }, [])

  const login = async (email, password) => {
    const data = await authApi.login({ email, password })
    tokenStore.set(data.accessToken, data.refreshToken)
    setUser(data.user)
    return data.user
  }

  const register = async (payload) => {
    const data = await authApi.register(payload)
    tokenStore.set(data.accessToken, data.refreshToken)
    setUser(data.user)
    return data.user
  }

  const logout = () => {
    tokenStore.clear()
    setUser(null)
  }

  const value = {
    user,
    setUser,
    loading,
    login,
    register,
    logout,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'ADMIN'
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth doit etre utilise dans un AuthProvider')
  return ctx
}
