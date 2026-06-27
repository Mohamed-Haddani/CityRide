import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { rideApi } from '../../api/ride.api'
import { getErrorMessage } from '../../lib/errors'
import { formatDateTime, formatPrice } from '../../lib/format'
import Spinner from '../../components/Spinner'

const STATUS = {
  ACTIVE: { label: 'Actif', cls: 'bg-green-100 text-green-700' },
  FULL: { label: 'Complet', cls: 'bg-amber-100 text-amber-700' },
  CANCELLED: { label: 'Annule', cls: 'bg-red-100 text-red-700' },
  COMPLETED: { label: 'Termine', cls: 'bg-slate-100 text-slate-600' }
}

export default function MyRidesPage() {
  const [rides, setRides] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = () => {
    setLoading(true)
    rideApi.mine()
      .then(setRides)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const cancel = async (id) => {
    if (!window.confirm('Annuler ce trajet ?')) return
    try {
      await rideApi.cancel(id)
      load()
    } catch (err) {
      setError(getErrorMessage(err))
    }
  }

  if (loading) return <Spinner full />

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-slate-900">Mes trajets</h1>
        <Link to="/rides/new" className="btn-primary">Nouveau trajet</Link>
      </div>

      {error && <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}

      {rides.length === 0 ? (
        <p className="text-center text-slate-500">Vous n'avez pas encore propose de trajet.</p>
      ) : (
        <div className="space-y-3">
          {rides.map((ride) => {
            const st = STATUS[ride.status]
            const active = ride.status === 'ACTIVE' || ride.status === 'FULL'
            return (
              <div key={ride.id} className="card flex flex-wrap items-center justify-between gap-4">
                <div>
                  <Link to={`/rides/${ride.id}`} className="font-semibold text-slate-900 hover:text-brand-700">
                    {ride.departureCity} → {ride.destinationCity}
                  </Link>
                  <p className="text-sm text-slate-500">{formatDateTime(ride.departureTime)} · {formatPrice(ride.pricePerSeat)} · {ride.availableSeats}/{ride.totalSeats} places</p>
                </div>
                <div className="flex items-center gap-3">
                  <span className={`badge ${st?.cls}`}>{st?.label}</span>
                  <Link to={`/rides/${ride.id}/bookings`} className="btn-secondary">Reservations</Link>
                  {active && <button onClick={() => cancel(ride.id)} className="btn-danger">Annuler</button>}
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
