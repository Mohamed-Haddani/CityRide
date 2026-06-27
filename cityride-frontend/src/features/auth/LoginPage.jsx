import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../auth/AuthContext'
import { getErrorMessage } from '../../lib/errors'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const from = location.state?.from?.pathname || '/dashboard'

  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(form.email, form.password)
      navigate(from, { replace: true })
    } catch (err) {
      setError(getErrorMessage(err, 'Email ou mot de passe incorrect'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto max-w-md">
      <div className="card">
        <h1 className="text-2xl font-bold text-slate-900">Connexion</h1>
        <p className="mt-1 text-sm text-slate-500">Heureux de vous revoir sur CityRide.</p>

        {error && <div className="mt-4 rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}

        <form onSubmit={onSubmit} className="mt-6 space-y-4">
          <div>
            <label className="label" htmlFor="email">Email</label>
            <input id="email" name="email" type="email" required value={form.email}
                   onChange={onChange} className="input" placeholder="vous@exemple.com" />
          </div>
          <div>
            <label className="label" htmlFor="password">Mot de passe</label>
            <input id="password" name="password" type="password" required value={form.password}
                   onChange={onChange} className="input" placeholder="••••••••" />
          </div>
          <button type="submit" disabled={loading} className="btn-primary w-full">
            {loading ? 'Connexion...' : 'Se connecter'}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-500">
          Pas encore de compte ?{' '}
          <Link to="/register" className="font-semibold text-brand-600 hover:text-brand-700">Inscrivez-vous</Link>
        </p>
      </div>
    </div>
  )
}
