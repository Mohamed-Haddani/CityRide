import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import Spinner from './Spinner'

// Protege un groupe de routes. adminOnly=true exige le role ADMIN.
export default function ProtectedRoute({ adminOnly = false }) {
  const { isAuthenticated, isAdmin, loading } = useAuth()
  const location = useLocation()

  if (loading) return <Spinner full />
  if (!isAuthenticated) return <Navigate to="/login" state={{ from: location }} replace />
  if (adminOnly && !isAdmin) return <Navigate to="/dashboard" replace />

  return <Outlet />
}
