import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { bookingApi } from '../../api/booking.api'
import { paymentApi } from '../../api/payment.api'
import { getErrorMessage } from '../../lib/errors'
import { formatDateTime, formatPrice } from '../../lib/format'
import Spinner from '../../components/Spinner'

export default function CheckoutPage() {
  const { bookingId } = useParams()
  const navigate = useNavigate()
  const [booking, setBooking] = useState(null)
  const [loading, setLoading] = useState(true)
  const [paying, setPaying] = useState(false)
  const [feedback, setFeedback] = useState({ type: '', message: '' })

  useEffect(() => {
    bookingApi.getById(bookingId)
      .then(setBooking)
      .catch((err) => setFeedback({ type: 'error', message: getErrorMessage(err) }))
      .finally(() => setLoading(false))
  }, [bookingId])

  const pay = async () => {
    setPaying(true)
    setFeedback({ type: '', message: '' })
    try {
      const result = await paymentApi.pay({ bookingId: Number(bookingId) })
      if (result.status === 'SUCCEEDED') {
        setFeedback({ type: 'success', message: 'Paiement reussi ! Redirection...' })
        setTimeout(() => navigate('/dashboard/payments'), 1200)
      } else {
        setFeedback({ type: 'error', message: 'Le paiement a echoue. Veuillez reessayer.' })
      }
    } catch (err) {
      setFeedback({ type: 'error', message: getErrorMessage(err) })
    } finally {
      setPaying(false)
    }
  }

  if (loading) return <Spinner full />
  if (!booking) return <p className="text-center text-slate-500">Reservation introuvable.</p>

  const payable = booking.status === 'CONFIRMED'

  return (
    <div className="mx-auto max-w-md space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Paiement</h1>

      <div className="card space-y-4">
        <div>
          <p className="font-semibold text-slate-900">{booking.ride.departureCity} → {booking.ride.destinationCity}</p>
          <p className="text-sm text-slate-500">{formatDateTime(booking.ride.departureTime)}</p>
        </div>
        <div className="flex justify-between border-t border-slate-100 pt-3 text-sm">
          <span className="text-slate-500">Places</span><span className="font-medium">{booking.seatsBooked}</span>
        </div>
        <div className="flex justify-between text-lg font-bold">
          <span>Total</span><span className="text-brand-700">{formatPrice(booking.totalPrice)}</span>
        </div>

        {feedback.message && (
          <div className={`rounded-lg px-3 py-2 text-sm ${
            feedback.type === 'success' ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'
          }`}>{feedback.message}</div>
        )}

        {booking.status === 'PAID' && (
          <p className="rounded-lg bg-green-50 px-3 py-2 text-sm text-green-700">Cette reservation est deja payee.</p>
        )}
        {!payable && booking.status !== 'PAID' && (
          <p className="rounded-lg bg-amber-50 px-3 py-2 text-sm text-amber-700">
            La reservation doit etre confirmee par le conducteur avant le paiement.
          </p>
        )}

        {payable && (
          <>
            <p className="text-xs text-slate-400">
              Paiement simule pour la demo (aucune carte requise). L'architecture est prete pour Stripe.
            </p>
            <button onClick={pay} disabled={paying} className="btn-primary w-full">
              {paying ? 'Paiement en cours...' : `Payer ${formatPrice(booking.totalPrice)}`}
            </button>
          </>
        )}
      </div>
    </div>
  )
}
