import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../../auth/AuthContext'

export default function Navbar() {
  const { isAuthenticated, isAdmin, user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  const linkClass = ({ isActive }) =>
    `px-3 py-2 text-sm font-medium rounded-lg transition ${
      isActive ? 'text-brand-700 bg-brand-50' : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
    }`

  return (
    <header className="sticky top-0 z-40 border-b border-slate-200 bg-white/80 backdrop-blur">
      <nav className="mx-auto flex h-16 max-w-6xl items-center justify-between px-4">
        <Link to="/" className="flex items-center gap-2">
          <span className="grid h-9 w-9 place-items-center rounded-xl bg-brand-600 text-lg font-bold text-white">C</span>
          <span className="text-lg font-extrabold tracking-tight text-slate-900">CityRide</span>
        </Link>

        <div className="flex items-center gap-1">
          <NavLink to="/rides/search" className={linkClass}>Rechercher</NavLink>
          {isAuthenticated && <NavLink to="/rides/new" className={linkClass}>Proposer</NavLink>}
          {isAuthenticated && <NavLink to="/dashboard" className={linkClass}>Tableau de bord</NavLink>}
          {isAdmin && <NavLink to="/admin" className={linkClass}>Admin</NavLink>}
        </div>

        <div className="flex items-center gap-2">
          {isAuthenticated ? (
            <>
              <Link to="/profile" className="hidden text-sm font-medium text-slate-600 hover:text-slate-900 sm:block">
                {user?.firstName}
              </Link>
              <button onClick={handleLogout} className="btn-secondary">Deconnexion</button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn-secondary">Connexion</Link>
              <Link to="/register" className="btn-primary">Inscription</Link>
            </>
          )}
        </div>
      </nav>
    </header>
  )
}
