import { useState } from 'react'
import { useAuth } from '../../auth/AuthContext'
import { userApi } from '../../api/user.api'
import { getErrorMessage } from '../../lib/errors'

export default function ProfilePage() {
  const { user, setUser } = useAuth()
  const [form, setForm] = useState({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    phone: user?.phone || '',
    city: user?.city || '',
    photoUrl: user?.photoUrl || ''
  })
  const [status, setStatus] = useState({ type: '', message: '' })
  const [loading, setLoading] = useState(false)

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const onSubmit = async (e) => {
    e.preventDefault()
    setStatus({ type: '', message: '' })
    setLoading(true)
    try {
      const updated = await userApi.updateProfile(form)
      setUser(updated)
      setStatus({ type: 'success', message: 'Profil mis a jour avec succes.' })
    } catch (err) {
      setStatus({ type: 'error', message: getErrorMessage(err) })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Mon profil</h1>

      <div className="card flex items-center gap-4">
        <div className="grid h-16 w-16 place-items-center overflow-hidden rounded-full bg-brand-100 text-xl font-bold text-brand-700">
          {form.photoUrl
            ? <img src={form.photoUrl} alt="" className="h-full w-full object-cover" />
            : (user?.firstName?.[0] || '?')}
        </div>
        <div>
          <p className="font-semibold text-slate-900">{user?.firstName} {user?.lastName}</p>
          <p className="text-sm text-slate-500">{user?.email}</p>
          <p className="mt-1 text-sm text-amber-600">★ {user?.ratingAvg?.toFixed(1) ?? '0.0'} ({user?.ratingCount ?? 0} avis)</p>
        </div>
      </div>

      {status.message && (
        <div className={`rounded-lg px-4 py-3 text-sm ${
          status.type === 'success' ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'
        }`}>{status.message}</div>
      )}

      <form onSubmit={onSubmit} className="card space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="label" htmlFor="firstName">Prenom</label>
            <input id="firstName" name="firstName" required value={form.firstName} onChange={onChange} className="input" />
          </div>
          <div>
            <label className="label" htmlFor="lastName">Nom</label>
            <input id="lastName" name="lastName" required value={form.lastName} onChange={onChange} className="input" />
          </div>
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
        <div>
          <label className="label" htmlFor="photoUrl">URL de la photo de profil</label>
          <input id="photoUrl" name="photoUrl" value={form.photoUrl} onChange={onChange} className="input"
                 placeholder="https://..." />
        </div>
        <button type="submit" disabled={loading} className="btn-primary">
          {loading ? 'Enregistrement...' : 'Enregistrer'}
        </button>
      </form>
    </div>
  )
}
