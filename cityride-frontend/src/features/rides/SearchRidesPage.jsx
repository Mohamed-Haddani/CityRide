import { useState } from 'react'
import { rideApi } from '../../api/ride.api'
import { getErrorMessage } from '../../lib/errors'
import RideCard from '../../components/RideCard'
import Spinner from '../../components/Spinner'

export default function SearchRidesPage() {
  const [criteria, setCriteria] = useState({ from: '', to: '', dateTime: '', maxPrice: '', minSeats: 1 })
  const [results, setResults] = useState([])
  const [loading, setLoading] = useState(false)
  const [searched, setSearched] = useState(false)
  const [error, setError] = useState('')

  const onChange = (e) => setCriteria({ ...criteria, [e.target.name]: e.target.value })

  const onSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      const data = await rideApi.search(criteria)
      setResults(data)
      setSearched(true)
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Rechercher un trajet</h1>

      <form onSubmit={onSubmit} className="card grid gap-4 sm:grid-cols-2 lg:grid-cols-5 lg:items-end">
        <div>
          <label className="label" htmlFor="from">Depart</label>
          <input id="from" name="from" value={criteria.from} onChange={onChange} className="input" placeholder="Casablanca" />
        </div>
        <div>
          <label className="label" htmlFor="to">Destination</label>
          <input id="to" name="to" value={criteria.to} onChange={onChange} className="input" placeholder="Rabat" />
        </div>
        <div>
          <label className="label" htmlFor="dateTime">Date / heure souhaitee</label>
          <input id="dateTime" name="dateTime" type="datetime-local" value={criteria.dateTime} onChange={onChange} className="input" />
        </div>
        <div>
          <label className="label" htmlFor="maxPrice">Prix max (DH)</label>
          <input id="maxPrice" name="maxPrice" type="number" min="0" value={criteria.maxPrice} onChange={onChange} className="input" />
        </div>
        <button type="submit" className="btn-primary h-[42px]">Rechercher</button>
      </form>

      {error && <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}

      {loading && <Spinner full />}

      {!loading && searched && (
        results.length === 0
          ? <p className="text-center text-slate-500">Aucun trajet ne correspond a votre recherche.</p>
          : (
            <div className="space-y-4">
              <p className="text-sm text-slate-500">{results.length} trajet(s) trie(s) par compatibilite.</p>
              {results.map((r) => <RideCard key={r.ride.id} ride={r.ride} matchScore={r.matchScore} />)}
            </div>
          )
      )}

      {!searched && !loading && (
        <p className="text-center text-slate-400">Lancez une recherche pour voir les trajets disponibles.</p>
      )}
    </div>
  )
}
