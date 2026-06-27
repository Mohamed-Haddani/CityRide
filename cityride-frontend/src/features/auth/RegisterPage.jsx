import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../auth/AuthContext'
import { getErrorMessage } from '../../lib/errors'

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()

  const [form, setForm] = useState({
    firstName: '', lastName: '', email: '', password: '', phone: '', city: ''
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await register(form)
      navigate('/dashboard', { replace: true })
    } catch (err) {
      setError(getErrorMessage(err, "Impossible de creer le compte"))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto max-w-md">
      <div className="card">
        <h1 className="text-2xl font-bold text-slate-900">Creer un compte</h1>
        <p className="mt-1 text-sm text-slate-500">Rejoignez la communaute CityRide.</p>

        {error && <div className="mt-4 rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}

        <form onSubmit={onSubmit} className="mt-6 space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label" htmlFor="firstName">Prenom</label>
              <input id="firstName" name="firstName" required value={form.firstName}
                     onChange={onChange} className="input" />
            </div>
            <div>
              <label className="label" htmlFor="lastName">Nom</label>
              <input id="lastName" name="lastName" required value={form.lastName}
                     onChange={onChange} className="input" />
            </div>
          </div>
          <div>
            <label className="label" htmlFor="email">Email</label>
            <input id="email" name="email" type="email" required value={form.email}
                   onChange={onChange} className="input" placeholder="vous@exemple.com" />
          </div>
          <div>
            <label className="label" htmlFor="password">Mot de passe</label>
            <input id="password" name="password" type="password" required minLength={6} value={form.password}
                   onChange={onChange} className="input" placeholder="6 caracteres minimum" />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label" htmlFor="phone">Telephone</label>
              <input id="phone" name="phone" value={form.phone} onChange={onChange} className="input" />
            </div>
            <div>
              <label className="label" htmlFor="city">Ville</label>
              <input id="city" name="city" value={form.city} onChange={onChange} className="input" />
            </div>
          </div>
          <button type="submit" disabled={loading} className="btn-primary w-full">
            {loading ? 'Creation...' : 'Creer mon compte'}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-500">
          Deja un compte ?{' '}
          <Link to="/login" className="font-semibold text-brand-600 hover:text-brand-700">Connectez-vous</Link>
        </p>
      </div>
    </div>
  )
}
