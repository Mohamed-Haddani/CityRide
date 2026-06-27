import { Link } from 'react-router-dom'
import { formatDateTime, formatPrice } from '../lib/format'

// Badge de couleur selon le score de matching.
function scoreColor(score) {
  if (score >= 80) return 'bg-green-100 text-green-700'
  if (score >= 60) return 'bg-amber-100 text-amber-700'
  return 'bg-slate-100 text-slate-600'
}

const STATUS_LABELS = {
  ACTIVE: { label: 'Actif', cls: 'bg-green-100 text-green-700' },
  FULL: { label: 'Complet', cls: 'bg-amber-100 text-amber-700' },
  CANCELLED: { label: 'Annule', cls: 'bg-red-100 text-red-700' },
  COMPLETED: { label: 'Termine', cls: 'bg-slate-100 text-slate-600' }
}

export default function RideCard({ ride, matchScore, showStatus = false }) {
  const status = STATUS_LABELS[ride.status]
  return (
    <Link to={`/rides/${ride.id}`} className="card block transition hover:shadow-md hover:ring-brand-200">
      <div className="flex items-start justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-lg font-bold text-slate-900">
            <span>{ride.departureCity}</span>
            <span className="text-brand-500">→</span>
            <span>{ride.destinationCity}</span>
          </div>
          <p className="mt-1 text-sm text-slate-500">{formatDateTime(ride.departureTime)}</p>
          <p className="mt-0.5 text-xs text-slate-400">{ride.departurePoint} → {ride.arrivalPoint}</p>
        </div>
        <div className="text-right">
          <p className="text-lg font-bold text-brand-700">{formatPrice(ride.pricePerSeat)}</p>
          <p className="text-xs text-slate-500">{ride.availableSeats} place(s)</p>
        </div>
      </div>

      <div className="mt-4 flex items-center justify-between">
        <div className="flex items-center gap-2 text-sm text-slate-600">
          <span className="grid h-7 w-7 place-items-center rounded-full bg-brand-100 text-xs font-bold text-brand-700">
            {ride.driver?.firstName?.[0] || '?'}
          </span>
          <span>{ride.driver?.firstName}</span>
          <span className="text-amber-500">★ {ride.driver?.ratingAvg?.toFixed(1) ?? '0.0'}</span>
        </div>
        {typeof matchScore === 'number' && (
          <span className={`badge ${scoreColor(matchScore)}`}>Compatibilite {matchScore}%</span>
        )}
        {showStatus && status && <span className={`badge ${status.cls}`}>{status.label}</span>}
      </div>
    </Link>
  )
}
