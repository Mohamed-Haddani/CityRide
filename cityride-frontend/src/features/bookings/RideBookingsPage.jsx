import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { bookingApi } from '../../api/booking.api'
import { getErrorMessage } from '../../lib/errors'
import { formatPrice } from '../../lib/format'
import Spinner from '../../components/Spinner'
import ReviewModal from '../../components/ReviewModal'
import { BOOKING_STATUS } from './MyBookingsPage'

export default function RideBookingsPage() {
  const { id } = useParams()
  const [bookings, setBookings] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [review, setReview] = useState(null)

  const load = () => {
    setLoading(true)
    bookingApi.forRide(id)
      .then(setBookings)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  useEffect(load, [id])

  const act = async (fn, bookingId) => {
    try {
      await fn(bookingId)
      load()
    } catch (err) {
      setError(getErrorMessage(err))
    }
  }

  if (loading) return <Spinner full />

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-slate-900">Reservations recues</h1>
        <Link to="/dashboard/rides" className="btn-secondary">Retour</Link>
      </div>
      {error && <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}

      {bookings.length === 0 ? (
        <p className="text-center text-slate-500">Aucune reservation pour ce trajet.</p>
      ) : (
        <div className="space-y-3">
          {bookings.map((b) => {
            const st = BOOKING_STATUS[b.status]
            return (
              <div key={b.id} className="card flex flex-wrap items-center justify-between gap-4">
                <div className="flex items-center gap-3">
                  <span className="grid h-10 w-10 place-items-center rounded-full bg-brand-100 font-bold text-brand-700">
                    {b.passenger?.firstName?.[0] || '?'}
                  </span>
                  <div>
                    <p className="font-semibold text-slate-900">{b.passenger?.firstName} {b.passenger?.lastName}</p>
                    <p className="text-sm text-slate-500">{b.seatsBooked} place(s) · {formatPrice(b.totalPrice)}</p>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <span className={`badge ${st?.cls}`}>{st?.label}</span>
                  {b.status === 'PENDING' && (
                    <>
                      <button onClick={() => act(bookingApi.accept, b.id)} className="btn-primary">Accepter</button>
                      <button onClick={() => act(bookingApi.reject, b.id)} className="btn-danger">Refuser</button>
                    </>
                  )}
                  {(b.status === 'CONFIRMED' || b.status === 'PAID') && (
                    <button
                      onClick={() => setReview({ rideId: Number(id), reviewee: { id: b.passenger.id, name: b.passenger.firstName } })}
                      className="btn-secondary">
                      Noter
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
