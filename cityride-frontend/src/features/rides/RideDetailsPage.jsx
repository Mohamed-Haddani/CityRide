import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { rideApi } from '../../api/ride.api'
import { bookingApi } from '../../api/booking.api'
import { useAuth } from '../../auth/AuthContext'
import { getErrorMessage } from '../../lib/errors'
import { formatDateTime, formatPrice } from '../../lib/format'
import Spinner from '../../components/Spinner'

export default function RideDetailsPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user, isAuthenticated } = useAuth()

  const [ride, setRide] = useState(null)
  const [loading, setLoading] = useState(true)
  const [seats, setSeats] = useState(1)
  const [feedback, setFeedback] = useState({ type: '', message: '' })
  const [booking, setBooking] = useState(false)

  useEffect(() => {
    rideApi.getById(id)
      .then(setRide)
      .catch((err) => setFeedback({ type: 'error', message: getErrorMessage(err) }))
      .finally(() => setLoading(false))
  }, [id])

  const reserve = async () => {
    setBooking(true)
    setFeedback({ type: '', message: '' })
    try {
      await bookingApi.create({ rideId: ride.id, seats: Number(seats) })
      setFeedback({ type: 'success', message: 'Reservation envoyee ! Suivez son statut dans vos reservations.' })
      setTimeout(() => navigate('/dashboard/bookings'), 1200)
    } catch (err) {
      setFeedback({ type: 'error', message: getErrorMessage(err) })
    } finally {
      setBooking(false)
    }
  }

  if (loading) return <Spinner full />
  if (!ride) return <p className="text-center text-slate-500">Trajet introuvable.</p>

  const isOwner = user?.id === ride.driver?.id
  const canBook = ride.status === 'ACTIVE' && ride.availableSeats > 0
  const total = (Number(ride.pricePerSeat) * seats).toFixed(2)

  return (
    <div className="mx-auto grid max-w-4xl gap-6 lg:grid-cols-3">
      {/* Details du trajet */}
      <div className="space-y-6 lg:col-span-2">
        <div className="card">
          <div className="flex items-center gap-2 text-2xl font-bold text-slate-900">
            {ride.departureCity} <span className="text-brand-500">→</span> {ride.destinationCity}
          </div>
          <p className="mt-2 text-slate-500">{formatDateTime(ride.departureTime)}</p>

          <div className="mt-6 space-y-3 text-sm">
            <Detail label="Point de depart" value={ride.departurePoint} />
            <Detail label="Point d'arrivee" value={ride.arrivalPoint} />
            <Detail label="Places disponibles" value={`${ride.availableSeats} / ${ride.totalSeats}`} />
            <Detail label="Prix par place" value={formatPrice(ride.pricePerSeat)} />
            {ride.description && <Detail label="Description" value={ride.description} />}
          </div>
        </div>

        <div className="card">
          <h3 className="mb-3 font-semibold text-slate-900">Conducteur</h3>
          <div className="flex items-center gap-3">
            <span className="grid h-12 w-12 place-items-center rounded-full bg-brand-100 text-lg font-bold text-brand-700">
              {ride.driver?.firstName?.[0] || '?'}
            </span>
            <div>
              <p className="font-medium text-slate-900">{ride.driver?.firstName} {ride.driver?.lastName}</p>
              <p className="text-sm text-amber-500">★ {ride.driver?.ratingAvg?.toFixed(1) ?? '0.0'} ({ride.driver?.ratingCount ?? 0} avis)</p>
            </div>
          </div>
        </div>
      </div>

      {/* Reservation */}
      <div className="lg:col-span-1">
        <div className="card sticky top-20">
          <p className="text-2xl font-bold text-brand-700">{formatPrice(ride.pricePerSeat)}</p>
          <p className="text-sm text-slate-500">par place</p>

          {feedback.message && (
            <div className={`mt-4 rounded-lg px-3 py-2 text-sm ${
              feedback.type === 'success' ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'
            }`}>{feedback.message}</div>
          )}

          {!isAuthenticated && (
            <button onClick={() => navigate('/login')} className="btn-primary mt-4 w-full">
              Connectez-vous pour reserver
            </button>
          )}

          {isAuthenticated && isOwner && (
            <p className="mt-4 rounded-lg bg-slate-50 px-3 py-2 text-sm text-slate-600">
              C'est votre trajet. Gerez les reservations depuis « Mes trajets ».
            </p>
          )}

          {isAuthenticated && !isOwner && canBook && (
            <div className="mt-4 space-y-3">
              <div>
                <label className="label" htmlFor="seats">Nombre de places</label>
                <select id="seats" value={seats} onChange={(e) => setSeats(e.target.value)} className="input">
                  {Array.from({ length: ride.availableSeats }, (_, i) => i + 1).map((n) => (
                    <option key={n} value={n}>{n}</option>
                  ))}
                </select>
              </div>
              <div className="flex justify-between text-sm font-medium text-slate-700">
                <span>Total</span><span>{total} DH</span>
              </div>
              <button onClick={reserve} disabled={booking} className="btn-primary w-full">
                {booking ? 'Reservation...' : 'Reserver'}
              </button>
            </div>
          )}

          {isAuthenticated && !isOwner && !canBook && (
            <p className="mt-4 rounded-lg bg-amber-50 px-3 py-2 text-sm text-amber-700">
              Ce trajet n'est plus disponible a la reservation.
            </p>
          )}
        </div>
      </div>
    </div>
  )
}

function Detail({ label, value }) {
  return (
    <div className="flex justify-between gap-4 border-b border-slate-100 pb-2">
      <span className="text-slate-500">{label}</span>
      <span className="text-right font-medium text-slate-800">{value}</span>
    </div>
  )
}
