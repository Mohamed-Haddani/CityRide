import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { bookingApi } from '../../api/booking.api'
import { getErrorMessage } from '../../lib/errors'
import { formatDateTime, formatPrice } from '../../lib/format'
import Spinner from '../../components/Spinner'
import ReviewModal from '../../components/ReviewModal'

export const BOOKING_STATUS = {
  PENDING: { label: 'En attente', cls: 'bg-amber-100 text-amber-700' },
  CONFIRMED: { label: 'Confirmee', cls: 'bg-blue-100 text-blue-700' },
  CANCELLED: { label: 'Annulee', cls: 'bg-red-100 text-red-700' },
  PAID: { label: 'Payee', cls: 'bg-green-100 text-green-700' }
}

export default function MyBookingsPage() {
  const [bookings, setBookings] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [review, setReview] = useState(null)

  const load = () => {
    setLoading(true)
    bookingApi.mine()
      .then(setBookings)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const cancel = async (id) => {
    if (!window.confirm('Annuler cette reservation ?')) return
    try {
      await bookingApi.cancel(id)
      load()
    } catch (err) {
      setError(getErrorMessage(err))
    }
  }

  if (loading) return <Spinner full />

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Mes reservations</h1>
      {error && <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}

      {bookings.length === 0 ? (
        <p className="text-center text-slate-500">Vous n'avez aucune reservation.</p>
      ) : (
        <div className="space-y-3">
          {bookings.map((b) => {
            const st = BOOKING_STATUS[b.status]
            return (
              <div key={b.id} className="card flex flex-wrap items-center justify-between gap-4">
                <div>
                  <Link to={`/rides/${b.ride.id}`} className="font-semibold text-slate-900 hover:text-brand-700">
                    {b.ride.departureCity} → {b.ride.destinationCity}
                  </Link>
                  <p className="text-sm text-slate-500">
                    {formatDateTime(b.ride.departureTime)} · {b.seatsBooked} place(s) · {formatPrice(b.totalPrice)}
                  </p>
                </div>
                <div className="flex items-center gap-3">
                  <span className={`badge ${st?.cls}`}>{st?.label}</span>
                  {b.status === 'CONFIRMED' && (
                    <Link to={`/payment/${b.id}`} className="btn-primary">Payer</Link>
                  )}
                  {(b.status === 'PENDING' || b.status === 'CONFIRMED') && (
                    <button onClick={() => cancel(b.id)} className="btn-secondary">Annuler</button>
                  )}
                  {b.status === 'PAID' && (
                    <button
                      onClick={() => setReview({ rideId: b.ride.id, reviewee: { id: b.ride.driver.id, name: b.ride.driver.firstName } })}
                      className="btn-secondary">
                      Noter le conducteur
                    </button>
                  )}
                </div>
              </div>
            )
          })}
        </div>
      )}

      {review && (
        <ReviewModal rideId={review.rideId} reviewee={review.reviewee} onClose={() => setReview(null)} />
      )}
    </div>
  )
}
