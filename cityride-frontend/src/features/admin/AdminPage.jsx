import { useEffect, useState } from 'react'
import { adminApi } from '../../api/admin.api'
import { getErrorMessage } from '../../lib/errors'
import { formatDateTime, formatPrice } from '../../lib/format'
import Spinner from '../../components/Spinner'

const TABS = [
  { key: 'users', label: 'Utilisateurs' },
  { key: 'rides', label: 'Trajets' },
  { key: 'bookings', label: 'Reservations' }
]

export default function AdminPage() {
  const [tab, setTab] = useState('users')
  const [data, setData] = useState({ users: [], rides: [], bookings: [] })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const loadAll = async () => {
    setLoading(true)
    try {
      const [users, rides, bookings] = await Promise.all([adminApi.users(), adminApi.rides(), adminApi.bookings()])
      setData({ users, rides, bookings })
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { loadAll() }, [])

  const toggleBlock = async (user) => {
    try {
      if (user.blocked) await adminApi.unblock(user.id)
      else await adminApi.block(user.id)
      loadAll()
    } catch (err) {
      setError(getErrorMessage(err))
    }
  }

  if (loading) return <Spinner full />

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Administration</h1>

      <div className="flex gap-2 border-b border-slate-200">
        {TABS.map((t) => (
          <button key={t.key} onClick={() => setTab(t.key)}
                  className={`-mb-px border-b-2 px-4 py-2 text-sm font-medium transition ${
                    tab === t.key ? 'border-brand-600 text-brand-700' : 'border-transparent text-slate-500 hover:text-slate-800'
                  }`}>
            {t.label} ({data[t.key].length})
          </button>
        ))}
      </div>

      {error && <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}

      {tab === 'users' && (
        <Table headers={['#', 'Nom', 'Email', 'Role', 'Note', 'Statut', 'Action']}>
          {data.users.map((u) => (
            <tr key={u.id} className="border-t border-slate-100">
              <Td>{u.id}</Td>
              <Td>{u.firstName} {u.lastName}</Td>
              <Td>{u.email}</Td>
              <Td>{u.role}</Td>
              <Td>★ {u.ratingAvg?.toFixed(1)}</Td>
              <Td>{u.blocked ? <span className="badge bg-red-100 text-red-700">Bloque</span> : <span className="badge bg-green-100 text-green-700">Actif</span>}</Td>
              <Td>
                {u.role !== 'ADMIN' && (
                  <button onClick={() => toggleBlock(u)} className={u.blocked ? 'btn-secondary' : 'btn-danger'}>
                    {u.blocked ? 'Debloquer' : 'Bloquer'}
                  </button>
                )}
              </Td>
            </tr>
          ))}
        </Table>
      )}

      {tab === 'rides' && (
        <Table headers={['#', 'Trajet', 'Conducteur', 'Date', 'Prix', 'Places', 'Statut']}>
          {data.rides.map((r) => (
            <tr key={r.id} className="border-t border-slate-100">
              <Td>{r.id}</Td>
              <Td>{r.departureCity} → {r.destinationCity}</Td>
              <Td>{r.driver?.firstName} {r.driver?.lastName}</Td>
              <Td>{formatDateTime(r.departureTime)}</Td>
              <Td>{formatPrice(r.pricePerSeat)}</Td>
              <Td>{r.availableSeats}/{r.totalSeats}</Td>
              <Td>{r.status}</Td>
            </tr>
          ))}
        </Table>
      )}

      {tab === 'bookings' && (
        <Table headers={['#', 'Trajet', 'Passager', 'Places', 'Total', 'Statut']}>
          {data.bookings.map((b) => (
            <tr key={b.id} className="border-t border-slate-100">
              <Td>{b.id}</Td>
              <Td>{b.ride?.departureCity} → {b.ride?.destinationCity}</Td>
              <Td>{b.passenger?.firstName} {b.passenger?.lastName}</Td>
              <Td>{b.seatsBooked}</Td>
              <Td>{formatPrice(b.totalPrice)}</Td>
              <Td>{b.status}</Td>
            </tr>
          ))}
        </Table>
      )}
    </div>
  )
}

function Table({ headers, children }) {
  return (
    <div className="overflow-x-auto rounded-xl bg-white ring-1 ring-slate-100">
      <table className="min-w-full text-sm">
        <thead className="bg-slate-50 text-left text-xs uppercase text-slate-500">
          <tr>{headers.map((h) => <th key={h} className="px-4 py-3 font-semibold">{h}</th>)}</tr>
        </thead>
        <tbody>{children}</tbody>
      </table>
    </div>
  )
}

function Td({ children }) {
  return <td className="px-4 py-3 text-slate-700">{children}</td>
}
