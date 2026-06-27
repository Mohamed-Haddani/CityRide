import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { rideApi } from '../../api/ride.api'
import { getErrorMessage } from '../../lib/errors'

const EMPTY = {
  departureCity: '', destinationCity: '', departurePoint: '', arrivalPoint: '',
  departureTime: '', totalSeats: 3, pricePerSeat: '', description: ''
}

export default function CreateRidePage() {
  const navigate = useNavigate()
  const [form, setForm] = useState(EMPTY)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const payload = {
        ...form,
        totalSeats: Number(form.totalSeats),
        pricePerSeat: Number(form.pricePerSeat)
      }
      const created = await rideApi.create(payload)
      navigate(`/rides/${created.id}`)
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Proposer un trajet</h1>

      {error && <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}

      <form onSubmit={onSubmit} className="card space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="label" htmlFor="departureCity">Ville de depart</label>
            <input id="departureCity" name="departureCity" required value={form.departureCity} onChange={onChange} className="input" />
          </div>
          <div>
            <label className="label" htmlFor="destinationCity">Ville d'arrivee</label>
            <input id="destinationCity" name="destinationCity" required value={form.destinationCity} onChange={onChange} className="input" />
          </div>
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="label" htmlFor="departurePoint">Point de depart precis</label>
            <input id="departurePoint" name="departurePoint" required value={form.departurePoint} onChange={onChange} className="input" placeholder="Gare, place..." />
          </div>
          <div>
            <label className="label" htmlFor="arrivalPoint">Point d'arrivee precis</label>
            <input id="arrivalPoint" name="arrivalPoint" required value={form.arrivalPoint} onChange={onChange} className="input" />
          </div>
        </div>
        <div className="grid grid-cols-3 gap-4">
          <div className="col-span-1">
            <label className="label" htmlFor="departureTime">Date et heure</label>
            <input id="departureTime" name="departureTime" type="datetime-local" required value={form.departureTime} onChange={onChange} className="input" />
          </div>
          <div>
            <label className="label" htmlFor="totalSeats">Places</label>
            <input id="totalSeats" name="totalSeats" type="number" min="1" max="8" required value={form.totalSeats} onChange={onChange} className="input" />
          </div>
          <div>
            <label className="label" htmlFor="pricePerSeat">Prix / place (DH)</label>
            <input id="pricePerSeat" name="pricePerSeat" type="number" min="0" step="0.5" required value={form.pricePerSeat} onChange={onChange} className="input" />
          </div>
        </div>
        <div>
          <label className="label" htmlFor="description">Description (optionnel)</label>
          <textarea id="description" name="description" rows="3" value={form.description} onChange={onChange} className="input" />
        </div>
        <button type="submit" disabled={loading} className="btn-primary">
          {loading ? 'Publication...' : 'Publier le trajet'}
        </button>
      </form>
    </div>
  )
}
