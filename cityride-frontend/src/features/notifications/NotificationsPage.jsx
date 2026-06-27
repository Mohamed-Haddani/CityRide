import { useEffect, useState } from 'react'
import { notificationApi } from '../../api/notification.api'
import { getErrorMessage } from '../../lib/errors'
import { formatDateTime } from '../../lib/format'
import Spinner from '../../components/Spinner'

export default function NotificationsPage() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = () => {
    setLoading(true)
    notificationApi.mine()
      .then(setItems)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const markAll = async () => {
    await notificationApi.markAllRead()
    load()
  }

  const markOne = async (n) => {
    if (n.read) return
    await notificationApi.markRead(n.id)
    setItems((prev) => prev.map((x) => (x.id === n.id ? { ...x, read: true } : x)))
  }

  if (loading) return <Spinner full />

  const unread = items.filter((n) => !n.read).length

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-slate-900">Notifications {unread > 0 && <span className="text-brand-600">({unread})</span>}</h1>
        {unread > 0 && <button onClick={markAll} className="btn-secondary">Tout marquer comme lu</button>}
      </div>

      {error && <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}

      {items.length === 0 ? (
        <p className="text-center text-slate-500">Aucune notification.</p>
      ) : (
        <div className="space-y-2">
          {items.map((n) => (
            <button key={n.id} onClick={() => markOne(n)}
                    className={`flex w-full items-start gap-3 rounded-xl border p-4 text-left transition ${
                      n.read ? 'border-slate-100 bg-white' : 'border-brand-200 bg-brand-50'
                    }`}>
              <span className={`mt-1.5 h-2 w-2 flex-shrink-0 rounded-full ${n.read ? 'bg-slate-300' : 'bg-brand-600'}`} />
              <div>
                <p className="text-sm text-slate-800">{n.message}</p>
                <p className="mt-0.5 text-xs text-slate-400">{formatDateTime(n.createdAt)}</p>
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  )
}
