import { useState } from 'react'
import { reviewApi } from '../api/review.api'
import { getErrorMessage } from '../lib/errors'

// Fenetre modale de notation (1-5 etoiles + commentaire).
export default function ReviewModal({ rideId, reviewee, onClose, onSubmitted }) {
  const [rating, setRating] = useState(5)
  const [hover, setHover] = useState(0)
  const [comment, setComment] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const submit = async () => {
    setLoading(true)
    setError('')
    try {
      await reviewApi.create({ rideId, revieweeId: reviewee.id, rating, comment })
      onSubmitted?.()
      onClose()
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4" onClick={onClose}>
      <div className="w-full max-w-md rounded-2xl bg-white p-6 shadow-xl" onClick={(e) => e.stopPropagation()}>
        <h2 className="text-lg font-bold text-slate-900">Noter {reviewee.name}</h2>

        {error && <div className="mt-3 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

        <div className="mt-4 flex justify-center gap-1">
          {[1, 2, 3, 4, 5].map((n) => (
            <button key={n} type="button" onClick={() => setRating(n)}
                    onMouseEnter={() => setHover(n)} onMouseLeave={() => setHover(0)}
                    className={`text-3xl transition ${(hover || rating) >= n ? 'text-amber-400' : 'text-slate-300'}`}>
              ★
            </button>
          ))}
        </div>

        <textarea value={comment} onChange={(e) => setComment(e.target.value)} rows="3"
                  className="input mt-4" placeholder="Votre commentaire (optionnel)" />

        <div className="mt-4 flex justify-end gap-2">
          <button onClick={onClose} className="btn-secondary">Annuler</button>
          <button onClick={submit} disabled={loading} className="btn-primary">
            {loading ? 'Envoi...' : 'Envoyer'}
          </button>
        </div>
      </div>
    </div>
  )
}
