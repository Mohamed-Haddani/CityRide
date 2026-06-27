import { useEffect, useState } from 'react'
import { paymentApi } from '../../api/payment.api'
import { getErrorMessage } from '../../lib/errors'
import { formatDateTime } from '../../lib/format'
import Spinner from '../../components/Spinner'

const PAYMENT_STATUS = {
  SUCCEEDED: { label: 'Reussi', cls: 'bg-green-100 text-green-700' },
  PENDING: { label: 'En cours', cls: 'bg-amber-100 text-amber-700' },
  FAILED: { label: 'Echoue', cls: 'bg-red-100 text-red-700' },
  REFUNDED: { label: 'Rembourse', cls: 'bg-slate-100 text-slate-600' }
}

export default function MyPaymentsPage() {
  const [payments, setPayments] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    paymentApi.mine()
      .then(setPayments)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <Spinner full />

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Mes paiements</h1>
      {error && <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}

      {payments.length === 0 ? (
        <p className="text-center text-slate-500">Aucun paiement pour le moment.</p>
      ) : (
        <div className="space-y-3">
          {payments.map((p) => {
            const st = PAYMENT_STATUS[p.status]
            return (
              <div key={p.id} className="card flex items-center justify-between gap-4">
                <div>
                  <p className="font-semibold text-slate-900">{p.label}</p>
                  <p className="text-sm text-slate-500">
                    {formatDateTime(p.paidAt || p.createdAt)} · {p.provider} · {p.providerRef || '—'}
                  </p>
                </div>
                <div className="text-right">
                  <p className="font-bold text-slate-900">{Number(p.amount).toFixed(2)} {p.currency}</p>
                  <span className={`badge ${st?.cls}`}>{st?.label}</span>
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
